package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CloneEntity
import com.example.ui.components.SpaceCard

@Composable
fun HomeScreen(
    clones: List<CloneEntity>,
    maxSpaces: Int,
    onAppClick: (CloneEntity) -> Unit,
    onAppLongClick: (CloneEntity) -> Unit,
    onAddAppClick: (Int) -> Unit,
    onDeleteSpace: (Int) -> Unit,
    onAddSpaceClick: () -> Unit,
    onOpenSpeedBoost: () -> Unit,
    onOpenVirtualRoot: () -> Unit,
    onOpenGameGuardian: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val distinctSpaces = remember(clones, maxSpaces) {
        val presentSpaces = clones.map { it.spaceIndex }.toSet()
        val total = maxOf(maxSpaces, (presentSpaces.maxOrNull() ?: 1))
        (1..total).toList()
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Stylish title matching Screenshot 1: MULTI CLONER in bold serif style
                Text(
                    text = "MULTI CLONER",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 1.sp,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.testTag("app_title")
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Golden Broom RAM Cleaner icon matching Screenshot 1!
                    Box(
                        modifier = Modifier
                            .clickable(onClick = onOpenSpeedBoost)
                            .padding(6.dp)
                            .testTag("broom_cleaner_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = "RAM Speed Booster",
                            tint = Color(0xFFF59E0B), // Golden Broom
                            modifier = Modifier.size(24.dp)
                        )
                        // Tiny boost spark badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 3.dp, y = (-3).dp)
                                .size(7.dp)
                                .background(Color(0xFFEF4444), CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Overflow Menu
                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.testTag("menu_more_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu Options",
                                tint = Color(0xFF1E293B)
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = null,
                                            tint = Color(0xFF10B981),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Virtual Root & /proc Manager", fontWeight = FontWeight.SemiBold)
                                    }
                                },
                                onClick = {
                                    menuExpanded = false
                                    onOpenVirtualRoot()
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Speed,
                                            contentDescription = null,
                                            tint = Color(0xFF2563EB),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("GameGuardian Memory Tool", fontWeight = FontWeight.SemiBold)
                                    }
                                },
                                onClick = {
                                    menuExpanded = false
                                    onOpenGameGuardian()
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CleaningServices,
                                            contentDescription = null,
                                            tint = Color(0xFFF59E0B),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("RAM Speed Booster")
                                    }
                                },
                                onClick = {
                                    menuExpanded = false
                                    onOpenSpeedBoost()
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = Color(0xFF6366F1),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Add New Space")
                                    }
                                },
                                onClick = {
                                    menuExpanded = false
                                    onAddSpaceClick()
                                }
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddAppClick(1) },
                containerColor = Color(0xFF2563EB),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_add_clone")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Clone Any App",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        containerColor = Color(0xFFF8FAFC),
        modifier = modifier.testTag("home_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Virtual Root Active Banner (Subtle and informative)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0FDF4))
                    .clickable { onOpenVirtualRoot() }
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF10B981), CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Virtual Root: uid=0 (Active) • GameGuardian hooks enabled",
                    color = Color(0xFF15803D),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Settings ›",
                    color = Color(0xFF16A34A),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Spaces List (matching Screenshot 1: Space 1, Space 2, Space 3, Space 4...)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(distinctSpaces) { spaceIdx ->
                    val spaceApps = clones.filter { it.spaceIndex == spaceIdx }
                    SpaceCard(
                        spaceIndex = spaceIdx,
                        apps = spaceApps,
                        onAppClick = onAppClick,
                        onAppLongClick = onAppLongClick,
                        onAddAppClick = onAddAppClick,
                        onDeleteSpace = onDeleteSpace
                    )
                }

                // Add New Space card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = 1.5.dp,
                                color = Color(0xFFCBD5E1),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable(onClick = onAddSpaceClick)
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Add New Multi Space",
                                color = Color(0xFF475569),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(72.dp)) // padding for FAB
                }
            }
        }
    }
}
