package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CloneEntity

@Composable
fun AppIconWithBadge(
    clone: CloneEntity,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "iconScale"
    )

    // Border style depending on clone number
    val hasBadge = clone.cloneNumber > 1 || clone.badgeText.isNotEmpty()
    val borderColor = when (clone.cloneNumber) {
        1 -> Color.Transparent
        2 -> Color(0xFF0284C7) // Sky Blue
        3 -> Color(0xFF06B6D4) // Cyan
        else -> Color(0xFFF97316) // Vibrant Orange
    }

    val badgeGradient = when (clone.cloneNumber) {
        2 -> Brush.horizontalGradient(listOf(Color(0xFF0284C7), Color(0xFF2563EB)))
        3 -> Brush.horizontalGradient(listOf(Color(0xFF06B6D4), Color(0xFF0EA5E9)))
        else -> Brush.horizontalGradient(listOf(Color(0xFFF97316), Color(0xFFEF4444)))
    }

    val displayBadgeText = when {
        clone.badgeText.isNotEmpty() -> clone.badgeText
        clone.cloneNumber == 2 -> "2nd"
        clone.cloneNumber == 3 -> "3rd"
        clone.cloneNumber > 3 -> "unlimited nd"
        else -> ""
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(76.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier.size(60.dp),
            contentAlignment = Alignment.Center
        ) {
            // Main app icon container
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .then(
                        if (hasBadge) {
                            Modifier.border(
                                width = 1.5.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(14.dp)
                            )
                        } else {
                            Modifier
                        }
                    )
            ) {
                AppBrandLogo(
                    iconKey = clone.iconKey,
                    packageName = clone.packageName,
                    appName = clone.originalAppName,
                    modifier = Modifier.size(54.dp)
                )
            }

            // Root Privilege Shield indicator (for root apps or enabled root)
            if (clone.isRootEnabled) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = (-2).dp, y = (-2).dp)
                        .size(16.dp)
                        .background(Color(0xFF10B981), CircleShape)
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Virtual Root Enabled",
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }

            // Badge badge at bottom right / bottom center (matching screenshot 3)
            if (hasBadge && displayBadgeText.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 4.dp, y = 4.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeGradient)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = displayBadgeText,
                        color = Color.White,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = clone.customCloneName,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1E293B),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 2.dp)
        )
    }
}
