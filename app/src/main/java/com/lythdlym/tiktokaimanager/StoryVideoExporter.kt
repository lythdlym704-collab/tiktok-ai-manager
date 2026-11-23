package com.lythdlym.tiktokaimanager

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Environment
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Local 30-second H.264 storyboard renderer. Visual scenes only; no AI footage or audio track. */
class StoryVideoExporter(private val context: Context) {
    @Volatile private var muxerState: Pair<Boolean, Int> = Pair(false, -1)

    fun export(title: String, story: String, is4k: Boolean, onProgress: (String) -> Unit, onDone: (File?) -> Unit) {
        Thread {
            var encoder: MediaCodec? = null
            var muxer: MediaMuxer? = null
            var inputSurface: android.view.Surface? = null
            var muxerStarted = false
            var output: File? = null
            try {
                val width = if (is4k) 2160 else 1080
                val height = if (is4k) 3840 else 1920
                val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "Hikaya")
                if (!dir.exists()) dir.mkdirs()
                val stamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
                output = File(dir, "hikaya-" + stamp + ".mp4")
                val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height).apply {
                    setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
                    setInteger(MediaFormat.KEY_BIT_RATE, if (is4k) 16_000_000 else 6_000_000)
                    setInteger(MediaFormat.KEY_FRAME_RATE, 30)
                    setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
                }
                encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
                encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
                inputSurface = encoder.createInputSurface()
                muxer = MediaMuxer(output.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
                encoder.start()
                val info = MediaCodec.BufferInfo()
                val paragraphs = story.split(Regex("\\n\\s*\\n")).map { it.trim() }.filter { it.isNotEmpty() }.ifEmpty { listOf(story) }
                val totalFrames = 30 * 30
                var frame = 0
                var outputDone = false
                while (frame < totalFrames) {
                    val scene = minOf(paragraphs.size - 1, (frame / 150) % paragraphs.size)
                    val canvas = inputSurface.lockCanvas(null)
                    try {
                        drawFrame(canvas, width, height, title, paragraphs[scene], scene + 1, paragraphs.size, frame, totalFrames)
                    } finally {
                        inputSurface.unlockCanvasAndPost(canvas)
                    }
                    Thread.sleep(33L)
                    frame++
                    if (frame % 90 == 0) onProgress("إنشاء الفيديو: " + (frame * 100 / totalFrames) + "٪")
                    outputDone = drainEncoder(encoder, muxer, info, muxerStarted, false)
                    muxerStarted = muxerState.first
                    if (outputDone) break
                }
                encoder.signalEndOfInputStream()
                while (!outputDone) {
                    outputDone = drainEncoder(encoder, muxer, info, muxerStarted, true)
                    muxerStarted = muxerState.first
                }
                onDone(output)
            } catch (e: Exception) {
                output?.delete()
                onProgress("تعذر إنشاء الفيديو: " + (e.message ?: "خطأ غير معروف"))
                onDone(null)
            } finally {
                try { inputSurface?.release() } catch (_: Exception) {}
                try { encoder?.stop() } catch (_: Exception) {}
                try { encoder?.release() } catch (_: Exception) {}
                try { if (muxerStarted) muxer?.stop() } catch (_: Exception) {}
                try { muxer?.release() } catch (_: Exception) {}
            }
        }.start()
    }

    private fun drainEncoder(codec: MediaCodec, muxer: MediaMuxer, info: MediaCodec.BufferInfo,
                             started: Boolean, end: Boolean): Boolean {
        var muxerStarted = started
        while (true) {
            val index = codec.dequeueOutputBuffer(info, if (end) 10_000L else 0L)
            when {
                index == MediaCodec.INFO_TRY_AGAIN_LATER -> {
                    muxerState = Pair(muxerStarted, muxerState.second)
                    if (!end) return false
                    return false
                }
                index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                    if (muxerStarted) throw IllegalStateException("تغير تنسيق الفيديو بعد بدء الحفظ")
                    val track = muxer.addTrack(codec.outputFormat)
                    muxer.start()
                    muxerStarted = true
                    muxerState = Pair(true, track)
                }
                index >= 0 -> {
                    val buffer = codec.getOutputBuffer(index) ?: throw IllegalStateException("تعذر قراءة إطار الفيديو")
                    if (info.size > 0) {
                        if (!muxerStarted) throw IllegalStateException("لم يبدأ حفظ MP4")
                        buffer.position(info.offset)
                        buffer.limit(info.offset + info.size)
                        muxer.writeSampleData(muxerState.second, buffer, info)
                    }
                    val eos = (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0
                    codec.releaseOutputBuffer(index, false)
                    if (eos) {
                        muxerState = Pair(muxerStarted, muxerState.second)
                        return true
                    }
                }
            }
        }
    }

    private fun drawFrame(canvas: Canvas, width: Int, height: Int, title: String, scene: String,
                          sceneNumber: Int, sceneCount: Int, frame: Int, totalFrames: Int) {
        canvas.drawColor(Color.rgb(11, 16, 34))
        val scale = width / 1080f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(105, 126, 255); style = Paint.Style.FILL }
        canvas.drawRoundRect(RectF(54f * scale, 110f * scale, width - 54f * scale, height - 110f * scale),
            42f * scale, 42f * scale, paint)
        paint.color = Color.rgb(24, 34, 67)
        canvas.drawRoundRect(RectF(68f * scale, 124f * scale, width - 68f * scale, height - 124f * scale),
            34f * scale, 34f * scale, paint)
        paint.color = Color.WHITE
        paint.textSize = 52f * scale
        paint.typeface = Typeface.create("sans-serif", Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("حكاية", width / 2f, 250f * scale, paint)
        paint.color = Color.rgb(255, 211, 130)
        paint.textSize = 44f * scale
        drawParagraph(canvas, title, paint, width, (330f * scale).toInt(), (width - 180f * scale).toInt(), 3)
        paint.color = Color.WHITE
        paint.textSize = 50f * scale
        paint.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
        drawParagraph(canvas, scene, paint, width, (height * 0.34f).toInt(), (width - 190f * scale).toInt(), 9)
        paint.color = Color.rgb(177, 195, 255)
        paint.textSize = 30f * scale
        canvas.drawText("المشهد " + sceneNumber + " من " + sceneCount, width / 2f, height - 250f * scale, paint)
        paint.color = Color.rgb(90, 105, 150)
        canvas.drawRoundRect(RectF(140f * scale, height - 190f * scale, width - 140f * scale, height - 174f * scale),
            8f * scale, 8f * scale, paint)
        paint.color = Color.rgb(129, 160, 255)
        val progressWidth = (width - 280f * scale) * (frame.toFloat() / totalFrames)
        canvas.drawRoundRect(RectF(140f * scale, height - 190f * scale, 140f * scale + progressWidth, height - 174f * scale),
            8f * scale, 8f * scale, paint)
    }

    private fun drawParagraph(canvas: Canvas, value: String, paint: Paint, width: Int, top: Int, textWidth: Int, maxLines: Int) {
        if (value.isBlank()) return
        val layout = StaticLayout.Builder.obtain(value, 0, value.length, paint, textWidth)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setTextDirection(TextDirectionHeuristics.RTL)
            .setIncludePad(false)
            .setMaxLines(maxLines)
            .build()
        canvas.save()
        canvas.translate((width - textWidth) / 2f, top.toFloat())
        layout.draw(canvas)
        canvas.restore()
    }
}
