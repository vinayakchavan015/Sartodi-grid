const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_operator_1";
const BOB_UID = "bob_operator_2";
const SUB_ID = "sub_284619";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function tsNow(offsetMs = -2000) {
  return new Date(Date.now() + offsetMs);
}

function validSubstationPayload(ts = tsNow()) {
  return {
    name: "33/11 kV सरतोडी उपकेंद्र",
    code: "284619",
    division: "सफाळा शाखा, पालघर विभाग",
    switches: { goCouplerClosed: false, pt33GodClosed: true },
    lossMf: {},
    createdAt: ts,
    updatedAt: ts,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) await testEnv.cleanup();
});

beforeEach(async () => {
  if (testEnv) await testEnv.clearFirestore();
});

test("Unauthenticated user: denied read and write on substations and subcollections", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("substations").get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("substations").doc(SUB_ID).set(validSubstationPayload())
  );
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("substations").doc(SUB_ID).collection("trips").get()
  );
});

test("Owner can create, read, list, and partially update substation switches & MF", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const subRef = aliceDb.collection("users").doc(ALICE_UID).collection("substations").doc(SUB_ID);
  const created = tsNow(-5000);

  await assertSucceeds(subRef.set(validSubstationPayload(created)));
  await assertSucceeds(subRef.get());
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("substations").get());

  // Partial update of switches + updatedAt
  await assertSucceeds(
    subRef.update({
      switches: { goCouplerClosed: true, pt33GodClosed: true },
      updatedAt: tsNow(-1000),
    })
  );

  // Reject mutating createdAt
  await assertFails(
    subRef.update({
      createdAt: tsNow(-500),
      updatedAt: tsNow(-500),
    })
  );

  // Reject shadow/ghost field
  await assertFails(
    subRef.update({
      isAdmin: true,
      updatedAt: tsNow(-500),
    })
  );

  // Reject future timestamp
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("substations").doc("sub_future").set(
      validSubstationPayload(tsNow(60000))
    )
  );
});

test("Cross-user isolation: Bob cannot read, write, or delete Alice's substation or registers", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("substations").doc(SUB_ID).set(validSubstationPayload())
  );

  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("substations").doc(SUB_ID).get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("substations").get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("substations").doc(SUB_ID).update({
      name: "Hijacked",
      updatedAt: tsNow(-1000),
    })
  );
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("substations").doc(SUB_ID).delete());
});

test("Subcollections: Parent gate enforced; valid Kotlin payloads succeed across all registers", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const subRef = aliceDb.collection("users").doc(ALICE_UID).collection("substations").doc(SUB_ID);
  const ts = tsNow(-3000);

  // Orphan write before parent substation exists must fail
  await assertFails(
    subRef.collection("trips").doc("tr_1").set({
      date: "2026-11-18",
      type: "TP - Tripping",
      feeder: "11 K.V. SAPHALA FEEDER (205)",
      offTime: "08:15",
      onTime: "08:23",
      duration: "8 मिनिटे",
      durationMin: 8,
      relay: "O/C R-Phase",
      remark: "Test",
      createdAt: ts,
      updatedAt: ts,
    })
  );

  // Create parent substation first
  await assertSucceeds(subRef.set(validSubstationPayload(ts)));

  // Trips
  await assertSucceeds(
    subRef.collection("trips").doc("tr_1").set({
      date: "2026-11-18",
      type: "TP - Tripping",
      feeder: "11 K.V. SAPHALA FEEDER (205)",
      offTime: "08:15",
      onTime: "08:23",
      duration: "8 मिनिटे",
      durationMin: 8,
      relay: "O/C R-Phase",
      remark: "Test",
      createdAt: ts,
      updatedAt: ts,
    })
  );
  await assertSucceeds(subRef.collection("trips").get());

  // Reject @DocumentId 'id' inside payload
  await assertFails(
    subRef.collection("trips").doc("tr_2").set({
      id: "tr_2",
      date: "2026-11-18",
      type: "TP",
      feeder: "205",
      offTime: "08:15",
      onTime: "08:23",
      duration: "8m",
      durationMin: 8,
      relay: "O/C",
      remark: "",
      createdAt: ts,
      updatedAt: ts,
    })
  );

  // Energy (enum check)
  await assertSucceeds(
    subRef.collection("energy").doc("en_1").set({
      date: "2026-11-18",
      meter: "33 KV INCOMMER",
      category: "33kV_IN",
      openReading: 1420.5,
      closeReading: 1474.5,
      mf: 600,
      units: 32400,
      createdAt: ts,
      updatedAt: ts,
    })
  );
  await assertFails(
    subRef.collection("energy").doc("en_bad").set({
      date: "2026-11-18",
      meter: "33 KV INCOMMER",
      category: "INVALID_CAT",
      openReading: 10,
      closeReading: 20,
      mf: 600,
      units: 6000,
      createdAt: ts,
      updatedAt: ts,
    })
  );

  // Daily Losses
  await assertSucceeds(
    subRef.collection("dailyLosses").doc("2026-11-18").set({
      date: "2026-11-18",
      finalReadings: { hv1: 930.0, inc: 1474.5 },
      initialOverrides: { hv1: 910.0 },
      createdAt: ts,
      updatedAt: ts,
    })
  );
  await assertSucceeds(
    subRef.collection("dailyLosses").doc("2026-11-18").update({
      finalReadings: { hv1: 932.0, inc: 1475.0 },
      updatedAt: tsNow(-1000),
    })
  );

  // MinMax
  await assertSucceeds(
    subRef.collection("minmax").doc("2026-11-18").set({
      date: "2026-11-18",
      entries: { inc33: { mx: "240", mxT: "10:00", mn: "60", mnT: "03:00" } },
      createdAt: ts,
      updatedAt: ts,
    })
  );

  // Faults (create + status update)
  await assertSucceeds(
    subRef.collection("faults").doc("fm_1").set({
      date: "2026-11-18",
      equip: "Power T/F No. 1",
      bay: "Bay-1",
      faultDesc: "Oil leak observed",
      actionTaken: "Informed AE",
      status: "बिघाड (Open)",
      restoredDate: "",
      oldSn: "T-01",
      newSn: "",
      refNo: "REF-1",
      remark: "",
      createdAt: ts,
      updatedAt: ts,
    })
  );
  await assertSucceeds(
    subRef.collection("faults").doc("fm_1").update({
      status: "दुरुस्त झाले",
      restoredDate: "2026-11-19",
      updatedAt: tsNow(-1000),
    })
  );

  // Nameplates
  await assertSucceeds(
    subRef.collection("nameplates").doc("tf_1").set({
      category: "transformers",
      bay: "Power T/F Bay-1",
      name: "5 MVA Power Transformer No. 1",
      status: "In Service (ON)",
      make: "CGL",
      serialNo: "T-5MVA-284619-01",
      year: "2018",
      specs: { capacity: "5 MVA", impedance: "7.15 %" },
      createdAt: ts,
      updatedAt: ts,
    })
  );

  // Monthly Records
  await assertSucceeds(
    subRef.collection("monthlyRecords").doc("2026-10").set({
      year: 2026,
      monthIdx: 9,
      monthName: "OCTOBER",
      incomerKwh: 2314800,
      outgoingKwh: 2296400,
      diffKwh: 18400,
      lossPct: 0.795,
      drivePdfId: "",
      feeders: [{ feederIdx: 0, feeder: "33 KV INCOMMER", tp: 8 }],
      createdAt: ts,
      updatedAt: ts,
    })
  );
});
