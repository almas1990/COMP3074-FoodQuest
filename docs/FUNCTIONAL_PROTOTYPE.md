# Functional prototype - Team P23

## What you need to submit

Submit the existing GitHub repository link and a short progress video to D2L.
This guide is team documentation, not an additional required upload. It can stay
in the repository so teammates can run and understand the project.

Before recording, configure your Maps key using the steps below and verify a
real address on the map. Then demonstrate the app, review the architecture as a
team, and publish the tested changes to the same repository. Every member needs
their own real contribution and commits. The video and D2L submission still need
to be completed by the team; the guide does not replace them.

## Architecture

`MainActivity` hosts AndroidX Fragments and the navigation controls. Each Fragment
owns a screen and observes an activity-scoped `QuestViewModel`. The ViewModel
loads and changes data on an IO dispatcher, then publishes the saved state using
LiveData. `QuestRepository` validates saved records and calls `QuestDao`.
Room generates the DAO implementation and verifies SQL at compile time.
`FoodQuestDatabase` defines the entities and initial team records. `QuestRules`
contains the pure search, tag and points calculations. Cuisine is represented by
user-defined tags; difficulty is an explicit Easy / Medium / Hard field.

The XML layouts define the activity frame, searchable quest list, recycled quest
rows, and the shared scrolling screen layout. QuestAdapter uses ListAdapter and
DiffUtil, so list updates do not rebuild the search box or every visible row.
Shared typography and buttons use XML styles. Screen-specific fields and actions
are built by each Fragment using small view helpers in `ScreenFragment`.

### Example: completing a quest

1. QuestDetailFragment calls `model.complete(id)`.
2. The ViewModel runs the repository update off the UI thread.
3. The database sets `completed` to 1 only if it was 0.
4. The ViewModel reloads quests and publishes them.
5. Details and achievements observe that state. Points are derived from completed
   quests, so pressing complete twice cannot award points twice.

There is no server, account login, dependency injection framework or separate
use-case class for each button. This is a small standalone application.

## Requirements and limitations

| Requirement | Implementation / verification needed |
| --- | --- |
| Improved organization | Separate navigation, screens, state, rules and persistence |
| Data access layer | Room / SQLite, entities, DAO and repository; no database access in Fragments |
| Quest list / name, cuisine, difficulty and tag search | QuestListFragment; dynamic tags; cuisine stored as a tag |
| Add / edit / remove quests | Validated form and confirmed deletion |
| Rate quests | Details rating selector, saved to SQLite |
| Complete quests / achievements | Saved completion; derived points and badges |
| Splash and About | Logo and registered P23 names / IDs |
| Team add / view / edit | Stored name, student ID, email and phone |
| Contact / email share | Opens installed mail, SMS or dialer applications |
| Map | Google Maps SDK; requires your own configured API key and network |
| Current location | Optional approximate / precise permission for map location layer |
| Directions | Opens Google Maps directions; origin is selected by Maps |
| Full-screen map | Full-screen map dialog; requires configured key and device verification |
| Contact without leaving the application | Not fulfilled literally: composers / dialer are external |

Do not describe external contact composers or external directions as an in-app
messaging system or an in-app route engine. Confirm the instructor accepts these
Android intent workflows. Do not claim 80% completion solely from the number of
screen classes: demonstrate each required workflow in the running app.

## Google Maps setup

Follow Google's official guide:
https://developers.google.com/maps/documentation/android-sdk/get-api-key

1. Create or select a Google Cloud project. Review its billing requirements before
   enabling any paid service.
2. Enable **Maps SDK for Android** and create an API key.
3. Restrict the key to Android application package `com.comp3074.foodquest` and
   the SHA-1 signing certificate shown by `gradlew.bat signingReport`.
4. Restrict the API to Maps SDK for Android.
5. Add `MAPS_API_KEY=your_key_here` as a new line in the root `local.properties`.
   That file is ignored by Git. Do not add the key to a tracked file or video.
6. Sync and rebuild. Use a Google APIs / Google Play emulator or supported device.
7. Add a quest with a complete real address and open its map. Confirm a marker
   appears, then check location permission granted and denied.

An Android API key is packaged in the APK; application/API restrictions are still
necessary. Without a key the app displays an unavailable state and retains the
external directions button. That state is not evidence of a working embedded map.

## Verification before recording

Instrumentation tests use a separate `foodquest-test.db` database.

1. Run `gradlew.bat assembleDebug testDebugUnitTest lintDebug` using JDK 17 or 21.
2. On a test emulator, run `gradlew.bat connectedDebugAndroidTest`.
3. Add two quests with different names and tags. Check required-field and points
   validation. Search several letters and confirm the keyboard stays usable.
4. Edit one quest; rate it; complete it. Reopen achievements and check the total.
5. Close and reopen the application. Verify the edited quest and completion remain.
6. Rotate while entering a quest; check that the unsaved fields remain.
7. Delete a quest; check its removal and the updated points total.
8. Edit a member's contact details; reopen the app and verify them. Test mail,
   SMS and phone on a device with suitable applications. Cancel each composer.
9. Configure and test maps as above. Test no network and denied permission.

## Team understanding and submission

Each member needs real contributions and commits from their own Git account.
Do not invent past contributions. Everyone should be able to explain the full
path from a screen action to the database and back, including the other members'
code. Keep a record of which workflows each person implemented and tested.

Submit the repository link and a short progress video. In the video demonstrate
the architecture improvement, saved data after restarting, CRUD, search, ratings,
points, and the features actually verified. State any remaining limitations.
