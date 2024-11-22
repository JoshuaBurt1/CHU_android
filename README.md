# Camel Health Union (CHU) Android application.
Two issues can cause http request failures:
1. The fitbitAccessToken located in HomeFramework needs to be refreshed every 8 hours. https://dev.fitbit.com/build/reference/web-api/troubleshooting-guide/oauth2-tutorial/
Plan: To login with Fitbit: Enter client ID (required - this is the users name/password), then a function to get a new fitbitAccessToken on login - Toast fitbitAccessToken obtained, data updated. If the user is still on the app for a long time, a refresh of the fitbitAccessToken must occur within 8 hours.
2. If https://chu-server.onrender.com/ is running, all that is needed is to download these files, open in Android Studio, then transfer to your phone.
- If https://chu-server.onrender.com/ is not running or has high latency, then the recommended approach is to download CHU_server, deploy in your own Render instance with your own MongoDB connection string, then run in Android Studio emulator.
Plan: direct client to database connection is made using Firestore or similar tech.

# Setup
1. Download the file and open in Android Studio
2. To run in Android Studio: build the code and click the green triangle to run an emulated android device.
3. To run on your Android device:
- On your device, tap build number 7 times to enable Developer Options.
- Go to Settings -> System -> Developer options, then click enable USB debugging.
- Connect your Android device to the computer via USB.
- In Android Studio, click the + symbol at the top right and select your device.
- In Android Studio, click the green triangle to run.
- The app should install on your connected device, and be visible on the emulation screen.
