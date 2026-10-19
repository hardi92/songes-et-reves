package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DreamRecord
import com.example.ui.DreamViewModel
import com.example.ui.theme.AfricanOchre
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.CelestialGoldLight
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.EmeraldEncrypted
import com.example.ui.theme.MidnightObsidian
import com.example.ui.theme.PropheticViolet
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamDetailScreen(
    record: DreamRecord,
    viewModel: DreamViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val isVoiceGenerating by viewModel.isVoiceGenerating.collectAsState()
    val voiceStatusText by viewModel.voiceStatusText.collectAsState()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val isProphecy = record.category.contains("PROPHET", ignoreCase = true)
    val accentColor = if (isProphecy) PropheticViolet else CelestialGold
    val dateFormat = SimpleDateFormat("dd MMMM yyyy à HH:mm", Locale.FRENCH)
    val formattedDate = dateFormat.format(Date(record.timestamp))

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isProphecy) "Prophétie & Vision" else "Interprétation du Songe",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formattedDate,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    // Audio AI Voice Playback button
                    IconButton(
                        onClick = {
                            val textToRead = "${record.title}. Perspective biblique : ${record.biblicalInterpretation}. Perspective africaine : ${record.africanPerspective}. Conseils spirituels : ${record.spiritualCounsel}"
                            viewModel.speakWithHumanAiVoice(textToRead)
                        },
                        modifier = Modifier.testTag("detail_tts_button")
                    ) {
                        if (isVoiceGenerating) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = CelestialGold,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.StopCircle else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Écouter avec voix IA humaine",
                                tint = if (isSpeaking) CelestialGoldLight else TextSecondary
                            )
                        }
                    }

                    // Favorite toggle
                    IconButton(
                        onClick = { viewModel.toggleFavorite(record) },
                        modifier = Modifier.testTag("detail_favorite_button")
                    ) {
                        Icon(
                            imageVector = if (record.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Favori",
                            tint = if (record.isFavorite) CelestialGold else TextSecondary
                        )
                    }

                    // Share button
                    IconButton(
                        onClick = {
                            val shareBody = """
                                🕊️ ${record.title} (${record.subCategory})
                                
                                🎙️ Paroles transcrites :
                                "${record.rawAudioText}"
                                
                                📖 Perspective Biblique :
                                ${record.biblicalInterpretation}
                                
                                📜 Versets clés :
                                ${record.biblicalVerses}
                                
                                🌍 Perspective Culturelle Africaine :
                                ${record.africanPerspective}
                                
                                🛡️ Conseils Spirituels & Prières :
                                ${record.spiritualCounsel}
                                
                                ${record.prayersAndActions}
                                
                                — Enregistré avec Songes & Prophéties
                            """.trimIndent()
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareBody)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Partager l'interprétation"))
                        },
                        modifier = Modifier.testTag("detail_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Partager",
                            tint = TextSecondary
                        )
                    }

                    // Delete button
                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.testTag("detail_delete_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = TextMuted
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MidnightObsidian
                )
            )
        },
        containerColor = MidnightObsidian
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Category & Subcategory Banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isProphecy) Icons.Default.AutoAwesome else Icons.Default.Bedtime,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isProphecy) "PROPHÉTIE / VISION" else "SONGE DIVIN",
                            color = accentColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = record.subCategory,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Title
            Text(
                text = record.title,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Dedicated AI Human Voice Player Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                CelestialGold.copy(alpha = 0.15f),
                                PropheticViolet.copy(alpha = 0.12f)
                            )
                        )
                    )
                    .border(1.dp, CelestialGold.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .clickable {
                        val textToRead = "${record.title}. Perspective biblique : ${record.biblicalInterpretation}. Perspective africaine : ${record.africanPerspective}. Conseils spirituels : ${record.spiritualCounsel}"
                        viewModel.speakWithHumanAiVoice(textToRead)
                    }
                    .padding(14.dp)
                    .testTag("ai_voice_player_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(CelestialGold),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isVoiceGenerating) {
                                androidx.compose.material3.CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = com.example.ui.theme.OnGold,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = if (isSpeaking) Icons.Default.StopCircle else Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = null,
                                    tint = com.example.ui.theme.OnGold,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isSpeaking) "Lecture en cours…" else "Écouter avec la Voix IA Humaine",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = voiceStatusText ?: "Voix claire, posée et chaleureuse (IA Céleste)",
                                color = CelestialGoldLight,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = CelestialGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section 1: Transcribed User Speech
            SpiritualSectionCard(
                title = "Paroles enregistrées par la voix",
                icon = Icons.Default.RecordVoiceOver,
                iconTint = CelestialGoldLight
            ) {
                Text(
                    text = "« ${record.rawAudioText} »",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Section 2: Key Symbols Detected
            if (record.keySymbols.isNotBlank()) {
                SpiritualSectionCard(
                    title = "Symboles & Clés spirituelles",
                    icon = Icons.Default.Lightbulb,
                    iconTint = CelestialGold
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val symbols = record.keySymbols.split(",")
                        for (symbol in symbols) {
                            val clean = symbol.trim()
                            if (clean.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF222B42))
                                        .border(1.dp, SurfaceCardBorder, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = clean,
                                        color = CelestialGoldLight,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Section 3: Biblical Interpretation & Scriptures
            SpiritualSectionCard(
                title = "Perspective Biblique & Écritures",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                iconTint = CelestialGold
            ) {
                Text(
                    text = record.biblicalInterpretation,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    lineHeight = 23.sp
                )

                if (record.biblicalVerses.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF131A2B))
                            .border(1.dp, CelestialGold.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                            .padding(14.dp)
                    ) {
                        Row {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = null,
                                tint = CelestialGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = record.biblicalVerses,
                                color = CelestialGoldLight,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Section 4: African Cultural & Ancestral Wisdom Perspective
            SpiritualSectionCard(
                title = "Perspective Culturelle Africaine",
                icon = Icons.Default.AutoAwesome,
                iconTint = AfricanOchre
            ) {
                Text(
                    text = record.africanPerspective,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    lineHeight = 23.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Section 5: Personalized Spiritual Counsel
            SpiritualSectionCard(
                title = "Conseils Spirituels Personnalisés",
                icon = Icons.Default.SelfImprovement,
                iconTint = PropheticViolet
            ) {
                Text(
                    text = record.spiritualCounsel,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    lineHeight = 23.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Section 6: Warfare Prayers & Prophetic Declarations
            SpiritualSectionCard(
                title = "Prières d'Autorité & Décrets",
                icon = Icons.Default.CheckCircle,
                iconTint = EmeraldEncrypted
            ) {
                Text(
                    text = record.prayersAndActions,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    lineHeight = 23.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Security seal badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F1726))
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = EmeraldEncrypted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Chiffré AES-256 localement • Confidentialité absolue garantie",
                        color = EmeraldEncrypted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Supprimer cette révélation ?") },
            text = { Text("Ce songe sera définitivement supprimé de votre journal spirituel.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        viewModel.deleteRecord(record)
                    }
                ) {
                    Text("Supprimer", color = Color(0xFFEF4444))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Annuler", color = TextSecondary)
                }
            },
            containerColor = DeepNavy
        )
    }
}

@Composable
private fun SpiritualSectionCard(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(17.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    color = iconTint,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}
