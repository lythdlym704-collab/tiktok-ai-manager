package com.lythdlym.tiktokaimanager

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.widget.*
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private lateinit var storyText: EditText
    private lateinit var status: TextView
    private var storyIndex = 0

    private val stories = listOf(
        Pair("مصباح القرية", "في قرية صغيرة تحاصرها العواصف، انطفأت المصابيح واحداً تلو الآخر، وخاف الناس من الظلام. لكن طفلة صغيرة قررت أن تشعل مصباحها الوحيد وتضعه قرب النافذة. رآها جارها فأشعل مصباحه، ثم فعل آخرون مثل ذلك، حتى أضاءت نوافذ الطريق كلها. لم تستطع الطفلة إيقاف العاصفة، لكنها استطاعت أن تمنح الناس أملاً. وفي الصباح اكتشف الجميع أن شجاعتها الصغيرة جمعت القرية كلها. أحياناً لا نحتاج إلى قوة كبيرة لنصنع فرقاً؛ نحتاج فقط إلى خطوة صادقة، ثم نترك أثرها ينتقل من قلب إلى قلب."),
        Pair("الساعة القديمة", "وجد سامر ساعة قديمة في صندوق جده. كانت متوقفة عند السادسة تماماً. حاول إصلاحها فلم تعمل، فسأل جده عن سرها. ابتسم الجد وقال: هذه الساعة تذكرني بيوم ساعدني فيه صديق، ومنذ ذلك اليوم تعلمت أن الوقت الذي نعطيه للآخرين لا يضيع أبداً. فكر سامر في كلامه، وقرر أن يزور جاره المسن ويساعده في ترتيب حديقته. وعندما عاد، شعر أن يومه أصبح أجمل. فهم أن قيمة الوقت ليست في عدد الدقائق، بل في الخير الذي نضعه داخلها. ومن يومها صار يخصص كل أسبوع وقتاً لشخص يحتاج إلى المساعدة."),
        Pair("العصفور الصغير", "سقط عصفور صغير من عشه في صباح بارد. مرّت به الحيوانات، لكن كل واحد ظن أن غيره سيساعده. توقفت سلحفاة بطيئة، واقتربت منه بهدوء، ثم نادت بقية الحيوانات. أحضر الأرنب بعض القش، وجلبت السنجابة أوراقاً ناعمة، وساعدهم عصفور كبير على إعادته إلى عشه. شكرهم العصفور الصغير بصوت خافت. أدركت الحيوانات أن انتظار شخص آخر قد يطيل الألم، وأن المبادرة البسيطة قد تنقذ حياة. ومنذ ذلك اليوم اتفقت على ألا تترك محتاجاً وحده. فالتعاون لا يحتاج إلى بطل واحد، بل إلى قلوب تقرر أن تعمل معاً."),
        Pair("البذرة العنيدة", "زرعت مريم بذرة صغيرة في أصيص قرب نافذتها، وسقتها كل يوم. مر أسبوع ولم يظهر شيء، فكادت تفقد الأمل. قالت لها أمها إن بعض الأشياء الجميلة تحتاج وقتاً أطول مما نتوقع. واصلت مريم العناية بالتربة، وبعد أيام ظهر برعم أخضر صغير. فرحت كثيراً، لكنها لم تتوقف عن السقي. كبرت النبتة وأزهرت، فوضعتها مريم أمام باب البيت لتفرح الجيران. تعلمت أن النجاح لا يأتي دائماً بسرعة، وأن الاستمرار في الخطوات الصغيرة يصنع نتائج كبيرة. فليس المهم أن نرى الثمرة في اليوم الأول، بل أن نعتني بما بدأناه.")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(12, 16, 35)
        window.navigationBarColor = Color.rgb(12, 16, 35)
        tts = TextToSpeech(this, this)
        buildNativeUi()
    }

    private fun buildNativeUi() {
        val bg = Color.rgb(12, 16, 35)
        val panel = Color.rgb(26, 33, 58)
        val white = Color.rgb(245, 247, 255)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 24, 22, 28)
            setBackgroundColor(bg)
        }
        val scroll = ScrollView(this)
        val body = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val heading = TextView(this).apply {
            text = "TikTok AI Manager"
            textSize = 27f
            setTextColor(white)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 8)
        }
        body.addView(heading)
        body.addView(label("مدير القصص العربية — تطبيق Android", white, 15f))
        body.addView(label("أنشئ مسودة قصة، استمع إليها بصوت جهازك، ثم شاركها يدوياً.", Color.LTGRAY, 14f))
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 18, 18, 18)
            setBackgroundColor(panel)
        }
        card.addView(label("إعداد القصة", white, 21f, true))
        card.addView(label("نوع المحتوى: قصص وحكايات قصيرة", Color.LTGRAY, 14f))
        storyText = EditText(this).apply {
            setTextColor(white)
            setHintTextColor(Color.LTGRAY)
            hint = "ستظهر القصة هنا..."
            textSize = 18f
            minLines = 10
            gravity = Gravity.TOP or Gravity.RIGHT
            setPadding(14, 14, 14, 14)
            setBackgroundColor(Color.rgb(13, 19, 39))
        }
        card.addView(storyText, LinearLayout.LayoutParams(-1, -2).apply { topMargin = 16 })
        val generate = button("إنشاء قصة جديدة") {
            val s = stories[storyIndex % stories.size]
            storyIndex++
            storyText.setText(s.first + "\n\n" + s.second)
            status.text = "تم إنشاء مسودة قصة. راجع النص قبل استخدامه."
        }
        card.addView(generate)
        val speak = button("استمع إلى القصة") {
            val text = storyText.text.toString().trim()
            if (text.isEmpty()) status.text = "أنشئ قصة أولاً."
            else if (ttsReady) {
                tts?.setSpeechRate(0.88f)
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "arabic-story")
                status.text = "يجري تشغيل الصوت العربي المتاح على جهازك."
            } else status.text = "الصوت العربي غير متاح. ثبّت صوتاً عربياً من إعدادات تحويل النص إلى كلام."
        }
        card.addView(speak)
        card.addView(button("إيقاف الصوت") {
            tts?.stop()
            status.text = "تم إيقاف الصوت."
        })
        card.addView(button("مشاركة النص") {
            val text = storyText.text.toString().trim()
            if (text.isEmpty()) status.text = "أنشئ قصة أولاً."
            else {
                val i = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                startActivity(Intent.createChooser(i, "مشاركة القصة"))
            }
        })
        body.addView(card)
        status = label("ملاحظة: هذه نسخة أولية تعمل دون اشتراك. لا تنشئ فيديو MP4 ولا تنشر تلقائياً على TikTok بعد.", Color.rgb(130, 220, 190), 13f)
        body.addView(status)
        body.addView(label("TikTok: لا تدخل كلمة مرورك هنا. تسجيل الدخول والنشر الآلي الرسمي يحتاجان OAuth وموافقة TikTok على صلاحيات النشر.", Color.LTGRAY, 13f))
        scroll.addView(body)
        root.addView(scroll)
        setContentView(root)
    }

    private fun label(value: String, color: Int, size: Float, bold: Boolean = false): TextView =
        TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            gravity = Gravity.RIGHT
            setPadding(4, 9, 4, 9)
            if (bold) typeface = Typeface.DEFAULT_BOLD
        }

    private fun button(value: String, action: () -> Unit): Button =
        Button(this).apply {
            text = value
            isAllCaps = false
            textSize = 16f
            setOnClickListener { action() }
            setPadding(8, 8, 8, 8)
        }

    override fun onInit(statusCode: Int) {
        if (statusCode == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("ar", "SA"))
            ttsReady = result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
