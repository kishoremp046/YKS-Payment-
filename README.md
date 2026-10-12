# YKS Payments

A shared expense tracker for Yashwanth, Kishore, and Surya. Anyone enters a
purchase (date, who bought it, item, amount) and it instantly syncs to
everyone else's phone via Firebase Firestore. The Summary tab shows each
person's total, the group's grand total, and exactly who owes whom.

## What you get in this project
- **Add** tab — form to log a purchase (date picker, person dropdown, item, amount)
- **Entries** tab — live list of every purchase, swipe/tap to delete, updates in real time on every device
- **Summary** tab — total spent by each person + grand total + minimal settle-up instructions (e.g. "Kishore → Surya : ₹150.00")

Real-time sync works because every phone reads/writes the **same** Firestore
collection (`entries`) in your Firebase project — there is no login required,
so keep this in mind: anyone who installs the app can add/see/delete entries.
That's fine for 3 trusted people sharing daily expenses, but don't publish
this app publicly as-is.

---

## Step 1 — Create your free Firebase project (10 minutes)

1. Go to https://console.firebase.google.com and click **Add project**.
   Name it e.g. "YKS Payments" and finish the wizard (Google Analytics is optional).
2. Inside the project, click the **Android icon** to add an Android app.
   - **Android package name:** `com.saaman.app`  (must match exactly)
   - Nickname / SHA-1: optional, can skip.
3. Download the **`google-services.json`** file it offers you.
4. Copy that file into this project at:
   ```
   app/google-services.json
   ```
   (replace/remove the placeholder file `app/google-services.json.SAMPLE`)
5. In the left sidebar of the Firebase console, go to **Build → Firestore Database**
   → **Create database** → start in **test mode** (fine for 3 known users) → pick any region close to you.

   Test mode rules expire after 30 days. Before that, replace the rules with
   this (still no login, but at least restricted to your document shape) under
   **Firestore Database → Rules**:
   ```
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /entries/{entryId} {
         allow read, write: if true;
       }
     }
   }
   ```
   (If you'd like real per-person login + stricter rules later, that's a
   natural next upgrade — Firebase Auth can be added on top of this project.)

## Step 2 — Open the project in Android Studio

1. Install **Android Studio** (free): https://developer.android.com/studio
2. Open Android Studio → **Open** → select this project's root folder (the one containing this `README.md`).
3. Let it **Sync Gradle** (bottom status bar). First sync downloads dependencies and can take a few minutes.
   - If Android Studio says the Gradle wrapper is missing, that's fine — accept its prompt to use the bundled Gradle, or run **File → Sync Project with Gradle Files**. It will regenerate the wrapper for you automatically.
4. Make sure `app/google-services.json` (your real one from Step 1) is present, then click **Run ▶** with a phone/emulator connected.

## Step 3 — Install on all three phones

- Easiest: in Android Studio, **Build → Generate Signed Bundle / APK → APK**,
  or just **Build → Build APK(s)** for a quick debug APK, then share the
  resulting `app-debug.apk` file (via WhatsApp/Drive/USB) to Yashwanth's,
  Kishore's, and Surya's phones and install it (enable "install from unknown
  sources" if prompted).
- All three installs point at the same Firebase project, so they all share
  the same live data automatically — no extra setup needed per phone.

---

## How the settlement math works

Every purchase is assumed to be **shared equally** by all three people,
no matter who physically paid for it. So:

```
fair share per person = grand total ÷ 3
balance = amount that person paid − fair share
```

- Positive balance → that person overpaid → they should **get money back**
- Negative balance → that person underpaid → they **owe money**

The Summary tab then works out the minimum number of payments needed to
settle everyone up (e.g. instead of 3 separate transfers, it might reduce
to a single "Surya pays Kishore ₹150" if that clears all balances).

## Updating the app after a code change

1. In Android Studio: **Build → Build App Bundle(s) / APK(s) → Build APK(s)**
2. Share the new `app-debug.apk` (from `app/build/outputs/apk/debug/`) to all three phones again
3. They install it over the old one (tap the file → Install) — this **keeps** all existing data, since everything lives in Firestore, not on the phone

## Project structure

```
app/src/main/java/com/saaman/app/
  MainActivity.kt        bottom-nav host, switches between the 3 tabs
  AddEntryFragment.kt     the "Add" form
  EntriesFragment.kt      live list + delete
  SummaryFragment.kt      totals + settle-up
  Entry.kt                data model + the 3 fixed names (Yashwanth, Kishore, Surya)
  FirebaseRepository.kt   all Firestore read/write/listen calls live here
  Settlement.kt           the balance & settle-up math
  EntryAdapter.kt         RecyclerView adapter for the Entries list
```

To change the 3 people's names, edit the `NAMES` list in `Entry.kt` — nothing
else needs to change.
