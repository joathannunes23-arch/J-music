package com.example.ui.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class HapticFeedbackHelper(private val context: Context) {
    private val vibrator: Vibrator? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    } catch (_: Exception) {
        null
    }

    /**
     * Feedback suave e profissional de clique (para botões, mute, solo, steps)
     */
    fun performClick() {
        try {
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(12L)
                }
            }
        } catch (_: Exception) {}
    }

    /**
     * Feedback de 'tick' sutil para arraste de sliders e faders do mixer
     */
    fun performTick() {
        try {
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(8L)
                }
            }
        } catch (_: Exception) {}
    }

    /**
     * Feedback tátil para o metrônomo (pulso rítmico no tempo da música)
     */
    fun performBeatPulse(isDownbeat: Boolean) {
        try {
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val effect = if (isDownbeat) {
                        VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                    } else {
                        VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                    }
                    vibrator.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(if (isDownbeat) 35L else 12L)
                }
            }
        } catch (_: Exception) {}
    }
}

@Composable
fun rememberHapticFeedbackHelper(): HapticFeedbackHelper {
    val context = LocalContext.current
    return remember(context) { HapticFeedbackHelper(context) }
}
