# Camel Health Union (CHU) Android application.
Two issues can cause http request failures:
1. The fitbitAccessToken located in HomeFramework needs to be refreshed every 8 hours. https://dev.fitbit.com/build/reference/web-api/troubleshooting-guide/oauth2-tutorial/
2. If https://chu-server.onrender.com/ is running, all that is needed is to download these files, open in Android Studio, then transfer to your phone.
- If https://chu-server.onrender.com/ is not running or has high latency, then the recommended approach is to download CHU_server, deploy in your own Render instance with your own MongoDB connection string, then run in Android Studio emulator.

Issues will remain until: 1. function to refresh the fitbitAccessToken is made and 2. direct client to database connection is made

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
