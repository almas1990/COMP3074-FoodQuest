# Verification

## November 8, 2026 Maps setup verification

The locally saved Maps key was detected without printing its value. The debug
app rebuilt successfully, unit tests passed, and lint passed. All seven emulator
tests then passed, including the new map workflow (eleven tests total).

The live map test verified:

- Google map tiles loaded and a quest marker appeared at the geocoded address
  `200 King Street East, Toronto, ON`.
- The full-screen map loaded tiles at the same quest location and closed normally.
- Both map views produced nonblank SDK snapshots, which were visually inspected.
- With location permissions granted in the emulator, the Show my location action
  enabled the map's location layer.

This verifies map rendering, geocoding and permission-enabled location display
configuration. It does not verify real GPS accuracy on a physical phone or that
external mail/SMS/directions applications complete their workflows.

The first map run was interrupted by an Android System UI not-responding dialog.
After clearing that emulator dialog, the map and regression tests passed; the
location-layer check was then added and the complete suite passed again.

`local.properties` remains ignored by Git. The key is not included in tracked
source files. The repository changes are still local; publishing, the team video
and D2L submission remain pending.

## November 8, 2026 initial recheck (before Maps setup)

Both commands completed successfully using JDK 21 and the Pixel 4 API 32 emulator:

```powershell
./gradlew.bat assembleDebug testDebugUnitTest lintDebug assembleDebugAndroidTest
./gradlew.bat connectedDebugAndroidTest
```

Four unit tests and six emulator tests passed. Lint reported zero errors and
17 non-blocking warnings. The two additional emulator tests check that email
retains its recipient, subject and multiline body, and that SMS, dialer and
directions requests retain their destinations. They capture outgoing intents;
they do not send messages, place calls, or prove that an external composer works.

The email URI construction was corrected: appending query parameters with a
hierarchical URI builder could discard the opaque mailto recipient.

The Maps API key is still absent from local.properties. Embedded maps, GPS,
geocoding and external applications remain unverified. These checks, the team
video, publishing the repository changes, and the D2L submission are still pending.

## October 31, 2026 checks

Environment: Windows, JDK 21, Gradle 8.9, Android Gradle Plugin 8.7.3,
Pixel 4 emulator running Android 12 / API 32.

Command completed successfully:

```powershell
./gradlew.bat assembleDebug testDebugUnitTest lintDebug connectedDebugAndroidTest
```

- Debug APK built successfully.
- Four unit tests passed: name/tag filtering, difficulty search, tag normalization,
  and points calculation.
- Three on-device repository tests passed: quest CRUD/rating/completion across
  database reopening, team edits across reopening, and invalid point rejection.
- One on-device UI workflow test passed: add a quest, retain form fields through
  activity recreation, save, search, open details, rate, complete, and verify the
  saved record through a reopened database.
- Lint passed with zero errors. Non-blocking warnings remain, including pinned
  dependency versions, KAPT usage and untranslated strings.
- The UI test exposed a disabled-completion-button bug after rating. The button
  now observes busy state, and the full test suite passed after the fix.

Earlier emulator attempts failed because of a frozen System UI snapshot and an
installation attempted before Android storage finished starting. The successful
run used a cold-booted emulator after its boot completed.

Not verified: embedded/full-screen Google Maps rendering, real GPS location,
geocoding with a valid configured key, or the external email/SMS/phone/directions
applications. These require team setup and device checks. API 26 and API 35
devices have not been tested. Do not describe these checks as completed in a video.

Changes are local and have not been committed or pushed by this verification step.
