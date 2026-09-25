package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BrandStyle(
    val backgroundBrush: Brush,
    val textColor: Color,
    val textGlyph: String? = null,
    val iconVector: androidx.compose.ui.graphics.vector.ImageVector? = null
)

fun getBrandStyle(key: String, packageName: String): BrandStyle {
    val k = key.lowercase()
    val p = packageName.lowercase()

    return when {
        k.contains("gameguardian") || p.contains("catch_.me") -> BrandStyle(
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF263238), Color(0xFF1E88E5))),
            textColor = Color(0xFFFFD54F),
            textGlyph = "GG",
            iconVector = Icons.Default.Security
        )
        k.contains("bing") || p.contains("bing") -> BrandStyle(
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF0078D4), Color(0xFF00C7FD))),
            textColor = Color.White,
            textGlyph = "b"
        )
        k.contains("deepseek") || p.contains("deepseek") -> BrandStyle(
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))),
            textColor = Color.White,
            textGlyph = "🐋"
        )
        k.contains("figma") || p.contains("figma") -> BrandStyle(
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B))),
            textColor = Color(0xFFF24E1E),
            textGlyph = "❖"
        )
        k.contains("discord") || p.contains("discord") -> BrandStyle(
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF5865F2), Color(0xFF4752C4))),
            textColor = Color.White,
            textGlyph = "👾"
        )
        k.contains("whatsapp") || p.contains("whatsapp") -> BrandStyle(
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF25D366), Color(0xFF128C7E))),
            textColor = Color.White,
            iconVector = Icons.Default.Chat
        )
        k.contains("facebook") || p.contains("katana") -> BrandStyle(
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF1877F2), Color(0xFF0B57CF))),
            textColor = Color.White,
            textGlyph = "f"
        )
        k.contains("messenger") || p.contains("orca") -> BrandStyle(
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFFFF007A), Color(0xFF9E00FF), Color(0xFF00C6FF))),
            textColor = Color.White,
            textGlyph = "⚡"
        )
        k.contains("instagram") || p.contains("instagram") -> BrandStyle(
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFFCB045))),
            textColor = Color.White,
            textGlyph = "📷"
        )
        k.contains("telegram") || p.contains("telegram") -> BrandStyle(
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF2AABEE), Color(0xFF229ED9))),
            textColor = Color.White,
            textGlyph = "✈"
        )
        k.contains("youtube") || p.contains("youtube") -> BrandStyle(
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFFFF0000), Color(0xFFCC0000))),
            textColor = Color.White,
            iconVector = Icons.Default.PlayArrow
        )
        k.contains("wechat") || p.contains("tencent.mm") -> BrandStyle(
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF07C160), Color(0xFF059648))),
            textColor = Color.White,
            textGlyph = "💬"
        )
        k.contains("qq") || p.contains("mobileqq") -> BrandStyle(
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF12B7F5), Color(0xFF0099FF))),
            textColor = Color.White,
            textGlyph = "🐧"
        )
        k.contains("pokemon") || p.contains("pokemongo") -> BrandStyle(
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFFFF3D00), Color(0xFFFF9100))),
            textColor = Color.White,
            textGlyph = "⚡"
        )
        k.contains("freefire") || p.contains("freefire") -> BrandStyle(
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFFFF5722), Color(0xFFFFC107))),
            textColor = Color.White,
            iconVector = Icons.Default.Games
        )
        k.contains("chrome") || p.contains("chrome") -> BrandStyle(
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFFEA4335), Color(0xFFFBBC05), Color(0xFF34A853), Color(0xFF4285F4))),
            textColor = Color.White,
            iconVector = Icons.Default.Language
        )
        k.contains("gmail") || p.contains("gm") -> BrandStyle(
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFFEA4335), Color(0xFFD93025))),
            textColor = Color.White,
            iconVector = Icons.Default.Email
        )
        k.contains("playstore") || p.contains("vending") -> BrandStyle(
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF01875F), Color(0xFF006747))),
            textColor = Color.White,
            iconVector = Icons.Default.ShoppingBag
        )
        k.contains("lucky") || p.contains("luckypatcher") -> BrandStyle(
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFFFFB300), Color(0xFFFF6F00))),
            textColor = Color.White,
            textGlyph = "☺"
        )
        else -> BrandStyle(
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF4F46E5), Color(0xFF7C3AED))),
            textColor = Color.White,
            iconVector = Icons.Default.Smartphone
        )
    }
}

@Composable
fun AppBrandLogo(
    iconKey: String,
    packageName: String,
    appName: String,
    modifier: Modifier = Modifier
) {
    val style = getBrandStyle(iconKey, packageName)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(style.backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        if (style.textGlyph != null) {
            Text(
                text = style.textGlyph,
                color = style.textColor,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp
            )
        } else if (style.iconVector != null) {
            Icon(
                imageVector = style.iconVector,
                contentDescription = appName,
                tint = style.textColor,
                modifier = Modifier.size(26.dp)
            )
        } else {
            Text(
                text = appName.take(1).uppercase(),
                color = style.textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}
