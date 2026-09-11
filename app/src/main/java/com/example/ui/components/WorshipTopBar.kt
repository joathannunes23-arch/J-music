package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JMusicBlue
import com.example.ui.theme.JMusicBlueLight
import com.example.ui.theme.JMusicCardBorder
import com.example.ui.theme.JMusicCyan
import com.example.ui.theme.JMusicDarkBg
import com.example.ui.theme.JMusicSurface
import com.example.ui.theme.JMusicSurfaceElevated
import com.example.ui.theme.JMusicTextMuted
import com.example.ui.theme.JMusicTextPrimary
import com.example.ui.theme.JMusicTextSecondary

@Composable
fun WorshipTopBar(
    songsCount: Int,
    exportedCount: Int,
    onOpenDrawer: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenExport: () -> Unit,
    onOpenSetlists: () -> Unit = {},
    setlistsCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = JMusicDarkBg,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.verticalGradient(listOf(Color.Transparent, JMusicCardBorder))
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // J-MUSIC Branding (Clean Modern Blue Studio)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF2979FF), Color(0xFF0D47A1))
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(1.dp, JMusicBlueLight.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "J-MUSIC Logo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "J-MUSIC",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            color = JMusicTextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = Color(0xFF0F264A),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, JMusicBlue.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "STUDIO",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = JMusicCyan,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = "Mixer de Stems • Equalizador • Letra & Tom",
                        fontSize = 10.sp,
                        color = JMusicTextSecondary
                    )
                }
            }

            // Action Buttons: "Músicas", "Galeria", "Exportar"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Minhas Músicas Button
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onOpenDrawer() }
                        .testTag("menu_minhas_musicas_button"),
                    color = Color(0xFF101928),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, JMusicCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LibraryMusic,
                            contentDescription = "Minhas Músicas",
                            tint = JMusicBlueLight,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Músicas",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = JMusicTextPrimary
                        )
                    }
                }

                // Setlists Button
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onOpenSetlists() }
                        .testTag("menu_setlists_button"),
                    color = Color(0xFF0F1E2E),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A5F))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatListNumbered,
                            contentDescription = "Setlists",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Setlist",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF00E5FF)
                        )
                        if (setlistsCount > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                color = Color(0xFF0D47A1),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "$setlistsCount",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }

                // Galeria de Exportações Button
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onOpenGallery() }
                        .testTag("menu_galeria_exportados_button"),
                    color = Color(0xFF101B2E),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E355B))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PermMedia,
                            contentDescription = "Galeria",
                            tint = JMusicCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Galeria",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = JMusicCyan
                        )
                        if (exportedCount > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                color = JMusicBlue,
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "$exportedCount",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }

                // Exportar Button
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onOpenExport() }
                        .testTag("botao_exportar_topbar"),
                    color = Color(0xFF0F264A),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, JMusicBlue.copy(alpha = 0.7f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.IosShare,
                            contentDescription = "Exportar",
                            tint = JMusicBlueLight,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Exportar",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
