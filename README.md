# MonashMP
## Google Sign-In Setup Note
This project uses Firebase Authentication with Google Sign-In.
Since the Firebase project is maintained under my personal account, each developer or tester must provide their SHA-1 certificate fingerprint to enable Google Sign-In on their own devices.

### If Google Sign-In fails (e.g., the login screen doesn't appear or closes immediately), please follow the steps below:
Run the following command in your terminal to retrieve your debug SHA-1:

macOS/Linux:

```
keytool -list -v -alias androiddebugkey -keystore ~/.android/debug.keystore -storepass android -keypass android
```
Windows:
```
keytool -list -v -alias androiddebugkey -keystore "%USERPROFILE%\.android\debug.keystore" -storepass android -keypass android
```

Look for the line:
```
SHA1: XX:XX:...
```
Send your SHA-1 to the project owner (me):

📧 Email: [yzha1113@student.monash.edu]

💬 Or via your preferred communication tool (WeChat / Slack / etc.)

I will add your SHA-1 fingerprint to the Firebase console. After that, Google Sign-In will work on your device.

- Alternative Option (for reviewers or instructors)
If you are reviewing this project and encounter issues with Google Sign-In, feel free to contact me. I can either:

Add your device’s SHA-1 to Firebase, or

Provide a test account for demo purposes.
