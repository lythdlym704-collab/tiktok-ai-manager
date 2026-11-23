# بناء APK لنظام Android

المستودع يحتوي تطبيق Android أصلياً (Kotlin)، وليس صفحة ويب.

## البناء والتنزيل
1. افتح تبويب Actions في GitHub.
2. افتح أحدث عملية باسم Build Android APK.
3. انتظر حتى تصبح جميع خطوات البناء خضراء.
4. افتح ملخص العملية ثم قسم Artifacts.
5. نزّل TikTok-AI-Manager-APK.zip وافتحه؛ الملف المطلوب داخله هو app-debug.apk. لا تثبّت artifact.tar أو github-pages.zip.

## الموجود في الإصدار الحالي
- إنشاء قصتين محلياً كل يوم، مع حفظ معرفات التركيبات لتقليل التكرار.
- عرض القصة الأولى والثانية.
- تقسيم النص إلى مشاهد نصية.
- Android TextToSpeech باللغة العربية حسب الأصوات المثبتة على الهاتف.
- حفظ المسودة ونسخ النص.

## حدود مهمة
هذا ليس بعد نظام توليد ذكاء اصطناعي سحابياً. كما أنه لا يصدر فيديو MP4 أو 4K، ولا يولّد مشاهد مرئية أو موسيقى. صوت Android TTS اصطناعي؛ اختيار صوت عربي عالي الجودة من إعدادات الهاتف قد يحسّن النطق لكنه لا يحوله إلى تسجيل بشري.

## ربط TikTok والنشر
- سجّل التطبيق في https://developers.tiktok.com/
- أضف Login Kit واضبط Android package name وApp Link/Redirect URI وبصمة توقيع التطبيق.
- استخدم OAuth/OpenSDK؛ لا تطلب كلمة مرور TikTok داخل التطبيق ولا تضع client_secret أو refresh token داخل APK.
- النشر المباشر يتطلب Content Posting API وتفعيل Direct Post وموافقة TikTok على video.publish، كما تتطلب إزالة وضع الخصوصية التجريبي مراجعة التطبيق.
- بعد موافقة TikTok، يلزم خادم آمن لتبادل رمز التفويض وتجديد الرموز وإرسال طلبات النشر.

روابط رسمية:
- Login Kit Android: https://developers.tiktok.com/docs/en/login-kit-android-quickstart-v2
- Direct Post: https://developers.tiktok.com/docs/en/content-posting-api-get-started
