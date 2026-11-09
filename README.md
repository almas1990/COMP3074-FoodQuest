# Food Quest

COMP3074 functional prototype for Team P23. A standalone Android game for
discovering food places through quests, ratings and points.

## Team

- Almas Sultana - 101568934
- Arcadio Vega - 101569135
- Juela Topi - 101087887

Instructor: Przemyslaw Pawluk.

## Run

1. Open this folder in Android Studio.
2. Set the Gradle JDK to 17 or 21; sync the project.
3. Run the app on an Android 8.0+ device or emulator.
4. Add a quest with a complete address. New installations start with an empty
   quest list and the three registered team members.

Google Maps needs your own API key. Add `MAPS_API_KEY=your_key` to the ignored
`local.properties` file, then rebuild. See [setup and verification](docs/FUNCTIONAL_PROTOTYPE.md).
Without a key the app can open external directions, but does not display a map.

## Structure

- `MainActivity`: activity layout and Fragment navigation.
- `ui/`: individual screens and lifecycle-aware QuestViewModel.
- `data/`: Room entities, DAO, database, tag converters and repository.
- `domain/QuestRules`: searching, tags and points.
- `res/layout/` and `res/values/styles.xml`: shared layouts and styling.

Quest and team records are stored locally in Room / SQLite. Database operations
run off the UI thread. There is no backend or multi-device synchronization.

## Features

- Add, view, edit and delete quests; validate required fields and point values.
- Search names, difficulty and tags; use cuisine names as tags.
- Rate quests, mark them complete, and track points and badges.
- Persist quests, ratings, completion and team contact details across restarts.
- Open an embedded or full-screen Google map after key configuration.
- Open Google Maps directions and email/SMS/dialer applications.
- Splash logo and About screen with registered team names and IDs.

Contact actions use installed applications. They do not send messages or place
calls automatically. Embedded/full-screen maps and the permission-enabled location
layer were verified on API 32 with the team's locally configured key. Real GPS
accuracy and external application workflows still need physical-device checks.

## Checks

```powershell
./gradlew.bat assembleDebug testDebugUnitTest lintDebug
./gradlew.bat connectedDebugAndroidTest
```

The instrumentation suite uses a separate database for repository tests. Its UI
test adds a temporary quest to the application database and removes it on success.
The map test requires a configured key and network access; it is skipped when no
key is configured. It adds and removes its own temporary quest.
See [recorded verification results](docs/VERIFICATION.md) for the checks performed
and the features that still need device testing.

## Submission

Submit the repository link and a short progress video, as required by the course.
See [functional prototype notes](docs/FUNCTIONAL_PROTOTYPE.md) for the architecture,
requirements, setup, remaining limitations and manual verification steps.

Every member must make real contributions and commits and understand the entire
system. Describe only workflows your team has tested in the submission video.

## References

- [Android ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [Room persistence](https://developer.android.com/training/data-storage/room)
- [Google Maps SDK setup](https://developers.google.com/maps/documentation/android-sdk/start)

