package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DreamViewModel
import com.example.ui.components.AudioWaveform
import com.example.ui.components.RecordPulseButton
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.CelestialGoldLight
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.EmeraldEncrypted
import com.example.ui.theme.OnGold
import com.example.ui.theme.PropheticViolet
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordModalSheet(
    viewModel: DreamViewModel,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    val isListening by viewModel.voiceManager.isListening.collectAsState()
    val transcribedText by viewModel.voiceManager.transcribedText.collectAsState()
    val soundLevel by viewModel.voiceManager.soundLevel.collectAsState()
    val errorMessage by viewModel.voiceManager.errorMessage.collectAsState()

    val isInterpreting by viewModel.isInterpreting.collectAsState()
    val progressText by viewModel.interpretationProgressText.collectAsState()

    var manualText by remember(transcribedText) { mutableStateOf(transcribedText) }
    var isManualEditMode by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DeepNavy,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .width(48.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(SurfaceCardBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CelestialGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = CelestialGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Enregistrement Vocal",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = EmeraldEncrypted,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Confidentiel • Chiffré de bout en bout",
                                color = EmeraldEncrypted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_record_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fermer",
                        tint = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isInterpreting) {
                // Sacred AI Interpretation Loading View
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        CelestialGold.copy(alpha = 0.25f),
                                        PropheticViolet.copy(alpha = 0.1f),
                                        Color.Transparent
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = CelestialGold,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(64.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = CelestialGoldLight,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Révélation en cours",
                        color = TextPrimary,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = progressText,
                        color = CelestialGoldLight,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Perspective biblique & Sagesse africaine",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            } else {
                // Recording / Dictation State
                Text(
                    text = if (isListening) "Parlez clairement, racontez votre songe…" else "Appuyez sur le micro pour démarrer",
                    color = if (isListening) CelestialGold else TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Pulsing Center Button
                RecordPulseButton(
                    isRecording = isListening,
                    soundLevel = soundLevel,
                    onClick = {
                        if (isListening) {
                            viewModel.voiceManager.stopListening()
                        } else {
                            viewModel.voiceManager.startListening()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Animated Waveform
                AudioWaveform(
                    isRecording = isListening,
                    soundLevel = soundLevel,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFFCA5A5),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Transcribed or Editable Text Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceCard)
                        .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Paroles transcrites :",
                                color = TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    isManualEditMode = !isManualEditMode
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Éditer",
                                    tint = CelestialGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isManualEditMode) "Valider le texte" else "Modifier / Saisir",
                                    color = CelestialGold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isManualEditMode) {
                            OutlinedTextField(
                                value = manualText,
                                onValueChange = {
                                    manualText = it
                                    viewModel.voiceManager.setManualText(it)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("manual_text_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = CelestialGold,
                                    unfocusedBorderColor = SurfaceCardBorder,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                placeholder = {
                                    Text(
                                        "Ex: J'ai rêvé que je marchais près d'un grand cours d'eau sous un ciel étoilé...",
                                        color = TextMuted,
                                        fontSize = 13.sp
                                    )
                                },
                                maxLines = 6
                            )
                        } else {
                            val displayText = manualText.ifBlank { transcribedText }
                            if (displayText.isNotBlank()) {
                                Text(
                                    text = displayText,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp
                                )
                            } else {
                                Text(
                                    text = "Votre voix sera convertie ici en texte en temps réel...",
                                    color = TextMuted,
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }

                        // Quick suggestion pills if empty
                        if (manualText.isBlank() && transcribedText.isBlank() && !isListening) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Exemples rapides :",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF202A40))
                                        .clickable {
                                            val sample = "J'ai vu en songe un aigle majestueux survoler un fleuve calme et se poser sur ma maison."
                                            manualText = sample
                                            viewModel.voiceManager.setManualText(sample)
                                        }
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "🦅 Aigle & Fleuve",
                                        color = CelestialGoldLight,
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF202A40))
                                        .clickable {
                                            val sample = "Une parole prophétique est venue à moi : 'La saison des larmes prend fin, prépare tes greniers'."
                                            manualText = sample
                                            viewModel.voiceManager.setManualText(sample)
                                        }
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "🌾 Nouvelle saison",
                                        color = PropheticViolet,
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Interpret with AI Action Button
                val canSubmit = (manualText.isNotBlank() || transcribedText.isNotBlank())
                Button(
                    onClick = {
                        val finalMsg = if (manualText.isNotBlank()) manualText else transcribedText
                        viewModel.interpretSpiritualSpeech(finalMsg)
                    },
                    enabled = canSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("submit_interpret_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CelestialGold,
                        contentColor = OnGold,
                        disabledContainerColor = SurfaceCardBorder,
                        disabledContentColor = TextMuted
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Interpréter (Biblique & Africain)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
