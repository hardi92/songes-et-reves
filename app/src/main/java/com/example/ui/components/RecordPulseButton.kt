package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.CelestialGoldLight
import com.example.ui.theme.OnGold
import com.example.ui.theme.PropheticViolet

@Composable
fun RecordPulseButton(
    isRecording: Boolean,
    soundLevel: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.25f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val audioReaction = if (isRecording) (1f + soundLevel * 0.4f) else 1f

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size * 1.5f)
            .testTag("record_pulse_button_container")
    ) {
        // Outer glowing halo
        if (isRecording) {
            Box(
                modifier = Modifier
                    .size(size * pulseScale * audioReaction)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                CelestialGold.copy(alpha = 0.35f),
                                PropheticViolet.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }

        // Inner glowing core button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .scale(if (isRecording) (pulseScale * 0.95f) else 1f)
                .background(
                    brush = if (isRecording) {
                        Brush.linearGradient(
                            listOf(CelestialGoldLight, CelestialGold)
                        )
                    } else {
                        Brush.linearGradient(
                            listOf(CelestialGold, PropheticViolet)
                        )
                    },
                    shape = CircleShape
                )
                .border(2.dp, CelestialGoldLight.copy(alpha = 0.6f), CircleShape)
                .clickable { onClick() }
                .testTag("record_pulse_button")
        ) {
            Icon(
                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = if (isRecording) "Arrêter l'enregistrement" else "Démarrer l'enregistrement",
                tint = OnGold,
                modifier = Modifier.size(size * 0.45f)
            )
        }
    }
}
