BEST SARKARI RESULT - NATIVE LITE ANDROID APP

Version 3.4 में OneSignal Android Push और notification Deep Link जुड़ा है। App खुलते समय logo, welcome message और loading screen दिखाई जाती है।

यह Expo वाला 69 MB project नहीं है। यह हल्का native Android WebView project है।

मुख्य बातें:
- अनुमानित release APK: लगभग 10-20 MB (असली size build के बाद पता चलेगा)
- Android 6.0 और उसके बाद के सामान्य phones
- 32-bit और 64-bit के लिए एक universal APK
- ऊपर status bar और नीचे navigation bar safe-area fix
- Android 13+ notification permission
- OneSignal App ID app में पहले से जोड़ा गया है
- WordPress की नई post का OneSignal notification browser और Android app दोनों पर भेजा जा सकता है
- Website बदलने पर app में वही नया content दिखेगा

APK बनाने का आसान तरीका:
1. Android Studio install करें।
2. इस पूरे folder को Open करें।
3. Gradle Sync पूरा होने दें।
4. Build > Generate Signed Bundle / APK > APK चुनें।
5. अपना keystore बनाएँ और release APK build करें।

OneSignal जरूरी:
1. OneSignal Dashboard > Settings > Push & In-App में Google Android (FCM) पूरा configure करें।
2. Android package name com.bestsarkariresult.app ही रखें।
3. OneSignal App ID: 383f59a5-fb0c-4c8d-bd9c-2dbe4b81482c
4. WordPress OneSignal plugin में यही App ID और सुरक्षित App API Key save रखें।
5. App install/open करके notification permission पर Allow दबाएँ।

ANDROID STUDIO के बिना GitHub से APK:
1. GitHub पर नया repository बनाकर इस folder की सभी files upload करें।
2. Repository का Actions tab खोलें।
3. Build Installable Lite APK workflow खोलकर Run workflow दबाएँ।
4. Build पूरा होने पर Artifacts में Best-Sarkari-Result-Lite-APK download करें।
5. ZIP खोलने पर Best-Sarkari-Result-Lite.apk मिलेगी; यह installable debug-signed APK है।
TEST:
OneSignal Dashboard > Audience > Subscriptions में app वाला device Subscribed दिखना चाहिए।

Notification को app में खोलने के लिए WordPress Code Snippets में यह filter रखें:

add_filter('onesignal_send_notification', function ($fields) {
    if (!is_array($fields)) return $fields;
    $post_url = !empty($fields['url']) ? $fields['url'] : '';
    $fields['isAndroid'] = true;
    $fields['isAnyWeb'] = true;
    if ($post_url) {
        $fields['web_url'] = $post_url;
        $fields['app_url'] = 'bestsarkariresult://open?url=' . rawurlencode($post_url);
        unset($fields['url']);
    }
    return $fields;
}, 999, 4);

इससे browser notification वेबसाइट में और Android notification इसी app के WebView में खुलेगा।
फिर Messages > Push > New Push से test notification भेजें।
