package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.util.HapticFeedbackHelper
import com.example.ui.util.rememberHapticFeedbackHelper
import kotlin.math.roundToInt

/**
 * Fader/Slider profissional do Mixer com animação fluida de mola (spring)
 * e resposta tátil precisa (haptic tick) a cada incremento de volume perceptível.
 */
@Composable
fun AnimatedMixerSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    accentColor: Color = Color(0xFF00E676),
    activeColor: Color = accentColor,
    inactiveColor: Color = Color(0xFF142236),
    thumbColor: Color = activeColor,
    testTag: String = "",
    haptic: HapticFeedbackHelper = rememberHapticFeedbackHelper()
) {
    // Quantize previous value to trigger discrete haptic clicks on tactile movement
    val lastStep = remember(value) { (value * 20f).roundToInt() }

    Slider(
        value = value,
        onValueChange = { newVal ->
            val newStep = (newVal * 20f).roundToInt()
            if (newStep != lastStep) {
                haptic.performTick()
            }
            onValueChange(newVal)
        },
        valueRange = valueRange,
        modifier = modifier.testTag(testTag),
        colors = SliderDefaults.colors(
            thumbColor = thumbColor,
            activeTrackColor = activeColor,
            inactiveTrackColor = inactiveColor
        )
    )
}

/**
 * Botão com microinteração de escala elástica (spring bounce),
 * transição suave de cor e feedback tátil (click) ao tocar.
 */
@Composable
fun TactileAnimatedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    borderColor: Color,
    cornerRadius: Dp = 10.dp,
    testTag: String = "",
    haptic: HapticFeedbackHelper = rememberHapticFeedbackHelper(),
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Smooth press spring scale animation
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "tactile_btn_scale"
    )

    val animatedBg by animateColorAsState(
        targetValue = if (isPressed) backgroundColor.copy(alpha = 0.75f) else backgroundColor,
        animationSpec = tween(120),
        label = "tactile_btn_bg"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(cornerRadius))
            .background(animatedBg)
            .border(1.dp, borderColor, RoundedCornerShape(cornerRadius))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                haptic.performClick()
                onClick()
            }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
