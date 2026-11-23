package com.lythdlym.tiktokaimanager

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.Gravity
import android.widget.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private lateinit var storyText: EditText
    private lateinit var titleText: EditText
    private lateinit var scenesText: TextView
    private lateinit var status: TextView
    private lateinit var dailyCount: TextView
    private lateinit var durationText: TextView

    private val starts = listOf("في قرية هادئة قرب النهر", "في مدينة قديمة تزدحم بالأزقة", "في صباح شتوي هادئ", "على أطراف صحراء واسعة", "في حي صغير يعرف سكانه بعضهم بعضاً", "في جزيرة تحيط بها المياه الزرقاء", "في مدرسة تطل على حديقة خضراء", "في محطة قطار لا تنام")
    private val heroes = listOf("ليان", "سليم", "مريم", "آدم", "نور", "ياسر", "هند", "كريم")
    private val objects = listOf("مفتاحاً نحاسياً", "دفتر ملاحظات قديماً", "مصباحاً صغيراً", "بذرة نادرة", "رسالة بلا توقيع", "صندوقاً خشبياً", "بوصلة متوقفة", "جرساً فضياً")
    private val problems = listOf("لاحظ أن الناس فقدوا الأمل بعد أن تعطلت بئر القرية، ولم يعرف أحد من أين يبدأ.", "سمع أن صديقاً له يمر بوقت صعب، لكنه لم يعرف كيف يفتح معه الحديث.", "رأى حديقة مهملة كانت تمنح الحي جماله، وقرر ألا يتركها تختفي.", "اكتشف أن خلافاً صغيراً فرّق بين جارين كانا صديقين منذ سنوات.", "وجد عصفوراً مصاباً قرب الطريق، بينما كان الجميع مستعجلاً إلى أعماله.", "لاحظ أن الأطفال لا يجدون مكاناً آمناً للقراءة واللعب بعد المدرسة.", "رأى رجلاً مسناً يحمل أكياساً ثقيلة، ولم يعرض أحد عليه المساعدة.", "اكتشف أن رسالة قديمة أخفت حقيقة طيبة كان يجب أن يعرفها الجميع.")
    private val actions = listOf("بدلاً من انتظار شخص آخر، جمع جيرانه وطلب من كل واحد أن يقدم ما يستطيع، ولو كان شيئاً بسيطاً.", "جلس بهدوء، واستمع جيداً، ثم عرض مساعدة عملية من دون أن يجرح كرامة أحد.", "بدأ وحده بتنظيف المكان، وبعد قليل انضم إليه طفل ثم أسرة كاملة.", "بحث عن الحقيقة، وتحدث مع الطرفين، ثم ساعدهما على رؤية ما يجمعهما لا ما يفرقهما.", "حمله برفق إلى مكان آمن، واتصل بمن يعرف كيف يعتني بالحيوانات.", "جمع كتباً مستعملة ورتب زاوية صغيرة، وجعلها مفتوحة لكل من يحب التعلم.", "اقترب منه وعرض حمل الأكياس، ثم رافقه حتى وصل إلى منزله.", "قرأ الرسالة بصوت واضح أمام الجميع، فاختفى سوء الفهم وعادت الابتسامة.")
    private val endings = listOf("لم تتغير الأمور في لحظة، لكن الخطوة الأولى فتحت باباً لم يكن أحد يراه.", "عاد الهدوء إلى المكان، وشعر الجميع أن التعاون أقوى من الخوف والتردد.", "ابتسم البطل، لأنه فهم أن قيمة العمل ليست في حجمه بل في أثره.", "ومنذ ذلك اليوم صار أهل الحي يبادرون إلى الخير قبل أن يطلبه أحد.", "تعلم أن الشجاعة ليست غياب الخوف، بل اختيار التصرف الصحيح رغم وجوده.", "وهكذا تحولت مشكلة صغيرة إلى فرصة جمعت القلوب من جديد.")
    private val morals = listOf("العبرة أن المعروف الصغير قد يصنع فرقاً كبيراً حين نبدأ به.", "العبرة أن الإصغاء الصادق قد يكون أعظم هدية نقدمها لإنسان.", "العبرة أن الأمل لا ينتظر الظروف المثالية، بل يبدأ بخطوة.", "العبرة أن التفاهم يحتاج إلى قلب هادئ وكلمة صادقة.", "العبرة أن الرحمة فعل يومي، وليست مجرد كلمات جميلة.", "العبرة أن مشاركة المعرفة تفتح أبواباً للجميع.")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(12, 16, 35)
        window.navigationBarColor = Color.rgb(12, 16, 35)
        tts = TextToSpeech(this, this)
        buildUi()
        restoreDraft()
        updateDailyCount()
    }

    private fun buildUi() {
        val bg = Color.rgb(12, 16, 35)
        val panel = Color.rgb(26, 33, 58)
        val white = Color.rgb(245, 247, 255)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(20, 22, 20, 26); setBackgroundColor(bg) }
        val scroll = ScrollView(this)
        val body = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        body.addView(label("TikTok AI Manager", white, 27f, true, Gravity.CENTER))
        body.addView(label("استوديو القصص العربية", Color.rgb(185, 198, 255), 17f, true, Gravity.CENTER))
        body.addView(label("أنشئ قصتين يومياً، كل قصة أطول من 30 ثانية، مع تقسيمها إلى مشاهد وتعليق صوتي عربي من محرك هاتفك.", Color.LTGRAY, 14f))
        dailyCount = label("", Color.rgb(130, 220, 190), 14f, true)
        body.addView(dailyCount)

        val card = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(16, 16, 16, 16); setBackgroundColor(panel) }
        card.addView(label("إنشاء القصص", white, 21f, true))
        card.addView(label("المولد المحلي يركّب قصصاً جديدة من عناصر متعددة ويحفظ سجلّاً لتقليل التكرار. لا يحتاج اشتراكاً، لكنه ليس نموذجاً سحابياً.", Color.LTGRAY, 13f))
        card.addView(button("إنشاء قصتي اليوم") { generateDailyStories() })
        card.addView(button("عرض القصة الثانية") { showSecondStory() })
        titleText = EditText(this).apply { setTextColor(white); setHintTextColor(Color.LTGRAY); hint = "عنوان القصة"; textSize = 18f; setSingleLine(true); gravity = Gravity.RIGHT; setPadding(12, 12, 12, 12); setBackgroundColor(Color.rgb(13, 19, 39)) }
        card.addView(titleText)
        storyText = EditText(this).apply { setTextColor(white); setHintTextColor(Color.LTGRAY); hint = "سيظهر نص القصة هنا..."; textSize = 18f; minLines = 9; gravity = Gravity.TOP or Gravity.RIGHT; setPadding(12, 12, 12, 12); setBackgroundColor(Color.rgb(13, 19, 39)) }
        card.addView(storyText)
        durationText = label("المدة التقديرية: ستظهر بعد إنشاء القصة.", Color.LTGRAY, 12f)
        card.addView(durationText)
        card.addView(button("استمع إلى القصة") { speakStory() })
        card.addView(button("تصدير التعليق الصوتي WAV") { exportVoice() })
        card.addView(button("إنشاء فيديو MP4 عمودي 1080p (30 ثانية)") { exportStoryVideo(false) })
        card.addView(button("إنشاء فيديو MP4 عمودي 4K (30 ثانية)") { exportStoryVideo(true) })
        card.addView(button("إيقاف الصوت") { tts?.stop(); status.text = "تم إيقاف الصوت." })
        card.addView(button("نسخ نص القصة") { copyStory() })
        card.addView(button("حفظ المسودة") { saveDraft() })
        body.addView(card)

        val scenes = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(16, 16, 16, 16); setBackgroundColor(panel) }
        scenes.addView(label("مخطط المشاهد", white, 20f, true))
        scenesText = label("أنشئ قصة أولاً لعرض تقسيمها إلى مشاهد.", Color.LTGRAY, 14f)
        scenes.addView(scenesText)
        body.addView(scenes)

        val tiktok = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(16, 16, 16, 16); setBackgroundColor(panel) }
        tiktok.addView(label("ربط حساب TikTok", white, 20f, true))
        tiktok.addView(label("تسجيل الدخول الحقيقي يحتاج إنشاء تطبيق في بوابة TikTok للمطورين، وتفعيل Login Kit، ثم إضافة Client Key وRedirect URI وتوقيع نسخة التطبيق. لا تضع كلمة مرورك هنا.", Color.LTGRAY, 13f))
        tiktok.addView(button("فتح بوابة مطوري TikTok") { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://developers.tiktok.com/"))) })
        tiktok.addView(label("النشر التلقائي يتطلب موافقة Content Posting API على صلاحية video.publish ومراجعة التطبيق. لذلك لن ندّعي أن النشر الآلي يعمل قبل تفعيل الموافقة والربط الآمن بالخادم.", Color.rgb(255, 211, 145), 13f))
        body.addView(tiktok)

        status = label("النسخة الحالية تنشئ نصوصاً ومخطط مشاهد وتشغّل صوت Android TTS. لا تصدّر MP4 أو 4K ولا تنشر تلقائياً بعد.", Color.rgb(130, 220, 190), 13f)
        body.addView(status)
        body.addView(label("ملاحظة الصوت: جودة النطق تعتمد على الصوت العربي المثبّت في إعدادات تحويل النص إلى كلام على هاتفك؛ هذا ليس تسجيلاً بشرياً.", Color.LTGRAY, 12f))
        scroll.addView(body)
        root.addView(scroll)
        setContentView(root)
    }

    private fun generateDailyStories() {
        val day = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val prefs = getSharedPreferences("story_manager", MODE_PRIVATE)
        if (prefs.getString("generated_day", "") == day && !prefs.getString("daily_story_1", "").isNullOrBlank() && !prefs.getString("daily_story_2", "").isNullOrBlank()) {
            loadStory(prefs.getString("daily_title_1", "القصة الأولى")!!, prefs.getString("daily_story_1", "")!!)
            status.text = "قصتا اليوم جاهزتان مسبقاً. لن نعيد توليدهما عند الضغط مرة أخرى."
            updateDailyCount()
            return
        }
        val used = prefs.getStringSet("used_combinations", emptySet())?.toMutableSet() ?: mutableSetOf()
        val first = makeUniqueStory(used, day, 1)
        val second = makeUniqueStory(used, day, 2)
        prefs.edit().putString("generated_day", day).putString("daily_title_1", first.first).putString("daily_story_1", first.second)
            .putString("daily_title_2", second.first).putString("daily_story_2", second.second).putStringSet("used_combinations", used).apply()
        loadStory(first.first, first.second)
        status.text = "تم إنشاء قصتي اليوم. استخدم زر حفظ المسودة للاحتفاظ بالقصة المختارة."
        updateDailyCount()
    }

    private fun showSecondStory() {
        val prefs = getSharedPreferences("story_manager", MODE_PRIVATE)
        val story = prefs.getString("daily_story_2", null)
        val title = prefs.getString("daily_title_2", null)
        if (story.isNullOrBlank() || title.isNullOrBlank()) { status.text = "أنشئ قصتي اليوم أولاً."; return }
        loadStory(title, story)
        status.text = "هذه القصة الثانية لهذا اليوم."
    }

    private fun makeUniqueStory(used: MutableSet<String>, day: String, slot: Int): Pair<String, String> {
        var attempt = 0
        while (attempt < 100) {
            val random = Random(day.hashCode() xor (slot * 7919) xor (attempt * 104729))
            val a = random.nextInt(starts.size); val h = random.nextInt(heroes.size); val o = random.nextInt(objects.size)
            val p = random.nextInt(problems.size); val ac = random.nextInt(actions.size); val e = random.nextInt(endings.size); val m = random.nextInt(morals.size)
            val id = listOf(a, h, o, p, ac, e, m).joinToString("-")
            if (used.add(id)) {
                val title = "حكاية " + heroes[h] + " و" + objects[o]
                val story = starts[a] + "، كان " + heroes[h] + " يحتفظ بـ" + objects[o] + ".\n\n" +
                    problems[p] + " شعر في البداية بالحيرة، لكنه تذكر أن انتظار الحل لا يغيّر شيئاً. قرر ألا يكتفي بالمشاهدة. " + actions[ac] + "\n\n" + "لم يكن الطريق سهلاً، واحتاج إلى الصبر والتعاون. ومع كل محاولة كان يكتشف أن الناس مستعدون للخير متى وجدوا من يبدأ. " + endings[e] + " " + morals[m]
                return Pair(title, story)
            }
            attempt++
        }
        return Pair("خطوة من أجل الخير", "في صباح جميل، قرر طفل أن يساعد جيرانه. بدأ بخطوة بسيطة، ثم انضم إليه الآخرون. لم يتغير كل شيء فوراً، لكن التعاون جعل الطريق أسهل للجميع. العبرة أن المبادرة الصادقة تصنع فرقاً.")
    }

    private fun loadStory(title: String, story: String) {
        titleText.setText(title); storyText.setText(story)
        val words = story.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
        val seconds = (words * 60.0 / 125.0).toInt().coerceAtLeast(1)
        durationText.text = "عدد الكلمات: $words • المدة المتوقعة: نحو ${seconds / 60} دقيقة و${seconds % 60} ثانية"
        scenesText.text = story.split("\n\n").mapIndexed { index, part -> "المشهد " + (index + 1) + ": " + part.trim() }.joinToString("\n\n")
    }

    private fun speakStory() {
        val text = storyText.text.toString().trim()
        if (text.isEmpty()) { status.text = "أنشئ قصة أولاً."; return }
        if (!ttsReady) { status.text = "الصوت العربي غير جاهز. ثبّت صوتاً عربياً من إعدادات تحويل النص إلى كلام."; return }
        tts?.setSpeechRate(0.88f); tts?.setPitch(1.0f)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "arabic-story")
        status.text = "يجري تشغيل الصوت العربي المثبّت على جهازك."
    }


    private fun exportVoice() {
        val text = storyText.text.toString().trim()
        if (text.isEmpty()) { status.text = "أنشئ قصة أولاً."; return }
        if (!ttsReady) { status.text = "الصوت العربي غير جاهز. ثبّت صوتاً عربياً من إعدادات تحويل النص إلى كلام."; return }
        val directory = getExternalFilesDir(Environment.DIRECTORY_MUSIC) ?: filesDir
        if (!directory.exists()) directory.mkdirs()
        val output = File(directory, "hikaya-${System.currentTimeMillis()}.wav")
        val utteranceId = "voice-export-${System.currentTimeMillis()}"
        tts?.setSpeechRate(0.88f)
        tts?.setPitch(1.0f)
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(id: String?) {}
            override fun onDone(id: String?) {
                runOnUiThread { status.text = "تم حفظ التعليق الصوتي WAV داخل مجلد ملفات التطبيق: ${output.name}" }
            }
            override fun onError(id: String?) {
                runOnUiThread { status.text = "تعذر تصدير الصوت. جرّب اختيار محرك أو صوت عربي آخر." }
            }
        })
        val result = tts?.synthesizeToFile(text, Bundle(), output, utteranceId)
        if (result == TextToSpeech.SUCCESS) status.text = "جارٍ إنشاء ملف التعليق الصوتي..."
        else status.text = "محرك الصوت لا يدعم تصدير الملف على هذا الجهاز."
    }

    private fun exportStoryVideo(is4k: Boolean) {
        val story = storyText.text.toString().trim()
        if (story.isEmpty()) { status.text = "أنشئ قصة أولاً."; return }
        status.text = "بدأ تجهيز الفيديو. قد يستغرق التصدير وقتاً، خصوصاً بدقة 4K..."
        StoryVideoExporter(this).export(titleText.text.toString().ifBlank { "حكاية عربية" }, story, is4k,
            { message -> runOnUiThread { status.text = message } },
            { file -> runOnUiThread {
                if (file != null && file.exists() && file.length() > 0L) {
                    status.text = "تم إنشاء فيديو MP4 صامت بنجاح: " + file.absolutePath + " • الحجم " + (file.length() / 1024) + " KB"
                } else {
                    status.text = "لم يكتمل إنشاء الفيديو. جرّب 1080p وتأكد من توفر مساحة كافية."
                }
            } })
    }

    private fun copyStory() {
        val text = titleText.text.toString() + "\n\n" + storyText.text.toString()
        if (storyText.text.isNullOrBlank()) { status.text = "لا يوجد نص لنسخه."; return }
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("قصة عربية", text))
        status.text = "تم نسخ القصة."
    }

    private fun saveDraft() {
        val title = titleText.text.toString().trim(); val story = storyText.text.toString().trim()
        if (story.isEmpty()) { status.text = "أنشئ قصة أولاً."; return }
        getSharedPreferences("story_manager", MODE_PRIVATE).edit().putString("last_title", title).putString("last_story", story).apply()
        status.text = "تم حفظ المسودة على هاتفك."
    }

    private fun restoreDraft() {
        val prefs = getSharedPreferences("story_manager", MODE_PRIVATE)
        val title = prefs.getString("last_title", null); val story = prefs.getString("last_story", null)
        if (!title.isNullOrBlank() && !story.isNullOrBlank()) loadStory(title, story)
    }

    private fun updateDailyCount() {
        val day = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val prefs = getSharedPreferences("story_manager", MODE_PRIVATE)
        dailyCount.text = "قصص اليوم: " + (if (prefs.getString("generated_day", "") == day) "2 / 2" else "0 / 2")
    }

    private fun label(value: String, color: Int, size: Float, bold: Boolean = false, gravityValue: Int = Gravity.RIGHT): TextView =
        TextView(this).apply { text = value; textSize = size; setTextColor(color); gravity = gravityValue; setPadding(4, 9, 4, 9); if (bold) typeface = Typeface.DEFAULT_BOLD }

    private fun button(value: String, action: () -> Unit): Button =
        Button(this).apply { text = value; isAllCaps = false; textSize = 16f; setOnClickListener { action() }; setPadding(8, 8, 8, 8) }

    override fun onInit(statusCode: Int) {
        if (statusCode == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("ar", "SA"))
            ttsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    override fun onDestroy() {
        tts?.stop(); tts?.shutdown(); super.onDestroy()
    }
}
