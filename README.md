# Call screen notification

## Implements 3 notification types:
- Custom expanded notification with accept and decline buttons (when calling notification "hands-up" when the device is unlocked, or in the notification panel if pressing the expanded button)
- Custom collapsed notification (in notification panel)
- Full-screen intent (when a call comes and the device is locked) 


> `Flashlights is optional, because on the emulator it crashes the app so be careful`



### Usage
You can use local schedule push notifications or real. For creating local notifications just press the button in the main activity. For real push notifications create a Firebase project, then add google-service.json to the project. Get the device test token and now you can send push, but remember, FirebaseConsole can send only Display Messages, but you need Data Messages. because only this type trigger the onMessageReceived() callback even if your app is in foreground/background/killed. So for sending Data Messages use Postman with this structure (in data you can put any keys)
```json
{
    "priority": "high",
    "data": {
        "title": "sender_name",
     },
    "registration_ids": [
        "eCAfI2WHTpe8NPM10nngUF:APA91bF151kIpDdwM681wZgBlSDCj2ukZHBB_NJuUdkKlKNiCl24-TEGxWZxhj-xvcsi0Om7xjVAm9Si_21CwueAUpatCvrxeot_Fw3-9LSgUJYrzrAz7Ag_seb5CAWAd0mNx74P4Bgv"
        ]
}
```
And add these Headers
```
Content-Type: application/json
Authorization: key=YOUR_SERVER_KEY
```
YOUR_SERVER_KEY you can get in Firebase Console, here is instruction: https://documentation.onesignal.com/docs/generate-a-google-server-api-key

---

### Trouble with notification receiving
Unfortunately, a notification on some devices will not invoke your service because the system just kills one. For more details see  [`Don't kill my app`](https://dontkillmyapp.com/)
Excepted for pure Android, I implemented support for Xiaomi devices:
- Checks permission and if they are denied, the user will see a popup that asks to navigate to the additional notification setting and accept show notification in wake lock, and background process for showing full-screen intent when the device is locked
- Checks autostart permission and if it is denied, the user will see a popup that asks to navigate to autostart settings and enable autostart for this app for service of listening push notification will not be killed. Also a possible way for Xiaomi to use com.google.android.c2dm.permission.SEND with a broadcast receiver, but I prefer to autostart 
