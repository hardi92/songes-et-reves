package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.PropheticViolet

@Composable
fun AudioWaveform(
    isRecording: Boolean,
    soundLevel: Float,
    modifier: Modifier = Modifier
) {
    val barCount = 18
    val transition = rememberInfiniteTransition(label = "waveform")

    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phase"
    )

    Row(
        modifier = modifier.height(48.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val offset = kotlin.math.sin((i.toFloat() / barCount * Math.PI.toFloat()) + (phase * 2f))
            val heightFraction = if (isRecording) {
                ((soundLevel * 0.7f) + (offset * 0.3f).coerceAtLeast(0f) + 0.15f).coerceIn(0.1f, 1f)
            } else {
                0.15f
            }

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height((heightFraction * 44).dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                CelestialGold,
                                PropheticViolet
                            )
                        )
                    )
            )
        }
    }
}
