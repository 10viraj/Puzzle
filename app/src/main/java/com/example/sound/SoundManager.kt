package com.example.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class SoundManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var isSoundEnabled: Boolean = true
    var isHapticsEnabled: Boolean = true

    fun playTap() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(frequency = 520.0, durationMs = 35, volume = 0.4f, decay = 0.08f)
        }
    }

    fun playSuccessMove(pitchStep: Int = 0) {
        if (!isSoundEnabled) return
        scope.launch {
            // Uplifting slide chord note based on combo/pitch step
            val baseFreq = 440.0 * Math.pow(2.0, ((pitchStep % 8) * 2) / 12.0)
            playSweep(startFreq = baseFreq * 0.8, endFreq = baseFreq * 1.3, durationMs = 120, volume = 0.6f)
        }
    }

    fun playBlocked() {
        if (!isSoundEnabled) return
        scope.launch {
            // Low thud/buzz sound
            playTone(frequency = 130.0, durationMs = 90, volume = 0.55f, decay = 0.05f)
        }
    }

    fun playHint() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(frequency = 880.0, durationMs = 60, volume = 0.4f, decay = 0.02f)
            kotlinx.coroutines.delay(70)
            playTone(frequency = 1174.0, durationMs = 100, volume = 0.45f, decay = 0.03f)
        }
    }

    fun playUndo() {
        if (!isSoundEnabled) return
        scope.launch {
            playSweep(startFreq = 600.0, endFreq = 300.0, durationMs = 90, volume = 0.4f)
        }
    }

    fun playVictoryFanfare() {
        if (!isSoundEnabled) return
        scope.launch {
            val notes = listOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
            for (note in notes) {
                playTone(frequency = note, durationMs = 140, volume = 0.65f, decay = 0.02f)
                kotlinx.coroutines.delay(100)
            }
            playTone(frequency = 1318.51, durationMs = 350, volume = 0.7f, decay = 0.01f)
        }
    }

    fun vibrateShort() {
        if (!isHapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(25)
            }
        } catch (_: Exception) {}
    }

    fun vibrateBlocked() {
        if (!isHapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 30, 40, 30)
                val amplitudes = intArrayOf(0, 180, 0, 220)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(70)
            }
        } catch (_: Exception) {}
    }

    fun vibrateSuccess() {
        if (!isHapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(45, 160))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(45)
            }
        } catch (_: Exception) {}
    }

    fun vibrateVictory() {
        if (!isHapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 60, 60, 80, 60, 140)
                val amplitudes = intArrayOf(0, 120, 0, 180, 0, 255)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(300)
            }
        } catch (_: Exception) {}
    }

    private fun playTone(frequency: Double, durationMs: Int, volume: Float, decay: Float = 0.05f) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val envelope = exp(-i.toDouble() * (decay / 10.0))
                val sample = (sin(2.0 * PI * frequency * t) * envelope * volume * Short.MAX_VALUE).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            scope.launch {
                kotlinx.coroutines.delay(durationMs.toLong() + 50)
                try {
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }

    private fun playSweep(startFreq: Double, endFreq: Double, durationMs: Int, volume: Float) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                val currentFreq = startFreq + (endFreq - startFreq) * progress
                val t = i.toDouble() / sampleRate
                val envelope = sin(progress * PI) // fade in and fade out smoothly
                val sample = (sin(2.0 * PI * currentFreq * t) * envelope * volume * Short.MAX_VALUE).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            scope.launch {
                kotlinx.coroutines.delay(durationMs.toLong() + 50)
                try {
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }
}
