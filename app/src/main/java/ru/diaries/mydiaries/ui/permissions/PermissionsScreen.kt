package ru.diaries.mydiaries.ui.permissions

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

data class PermissionItem(
    val permission: String,
    val icon: ImageVector,
    val title: String,
    val description: String,
    val isGranted: Boolean
)

@Composable
fun PermissionsScreen(
    onAllPermissionsGranted: () -> Unit,
    onSkip: () -> Unit
) {
    val context = LocalContext.current
    val primaryColor = Color(0xFF6C63FF)
    val secondaryColor = Color(0xFF8B85FF)

    var permissionStates by remember { mutableStateOf(getRequiredPermissions(context)) }
    var showContent by remember { mutableStateOf(false) }
    var hasRequestedOnce by remember { mutableStateOf(false) }

    val allGranted = permissionStates.all { it.isGranted }

    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }

    LaunchedEffect(allGranted) {
        if (allGranted) {
            delay(500)
            onAllPermissionsGranted()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        permissionStates = permissionStates.map { item ->
            item.copy(isGranted = results[item.permission] == true || item.isGranted)
        }
        hasRequestedOnce = true
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { -40 },
                    animationSpec = spring(stiffness = Spring.StiffnessLow)
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Header icon
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(primaryColor, secondaryColor)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Security,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Разрешения",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Для полноценной работы приложению нужны следующие разрешения",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Permission items
            permissionStates.forEachIndexed { index, item ->
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(
                        animationSpec = spring(stiffness = Spring.StiffnessLow)
                    ) + slideInVertically(
                        initialOffsetY = { 40 * (index + 1) },
                        animationSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        )
                    )
                ) {
                    PermissionItemCard(
                        item = item,
                        accentColor = primaryColor
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Grant button
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { 60 },
                    animationSpec = spring(stiffness = Spring.StiffnessLow)
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (allGranted) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF4CAF50).copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Все разрешения получены!",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        val notGrantedPermissions = permissionStates
                            .filter { !it.isGranted }
                            .map { it.permission }
                            .toTypedArray()

                        Button(
                            onClick = {
                                permissionLauncher.launch(notGrantedPermissions)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryColor
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Предоставить разрешения",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Show "Open Settings" if permissions were denied
                        if (hasRequestedOnce && notGrantedPermissions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", context.packageName, null)
                                    }
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Settings,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Открыть настройки")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Если разрешения были отклонены, предоставьте их в настройках",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = onSkip,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Пропустить")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Вы сможете предоставить разрешения позже",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PermissionItemCard(
    item: PermissionItem,
    accentColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (item.isGranted) {
            Color(0xFF4CAF50).copy(alpha = 0.1f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (item.isGranted) {
                            Color(0xFF4CAF50).copy(alpha = 0.2f)
                        } else {
                            accentColor.copy(alpha = 0.15f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (item.isGranted) Icons.Outlined.Check else item.icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (item.isGranted) Color(0xFF4CAF50) else accentColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (item.isGranted) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF4CAF50).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "OK",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

private fun getRequiredPermissions(context: android.content.Context): List<PermissionItem> {
    val permissions = mutableListOf<PermissionItem>()

    // Camera
    permissions.add(
        PermissionItem(
            permission = Manifest.permission.CAMERA,
            icon = Icons.Outlined.CameraAlt,
            title = "Камера",
            description = "Для фотографирования еды и записи видео",
            isGranted = context.checkSelfPermission(Manifest.permission.CAMERA) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    )

    // Location
    permissions.add(
        PermissionItem(
            permission = Manifest.permission.ACCESS_FINE_LOCATION,
            icon = Icons.Outlined.LocationOn,
            title = "Геолокация",
            description = "Для записи маршрутов на карте",
            isGranted = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    )

    // Microphone
    permissions.add(
        PermissionItem(
            permission = Manifest.permission.RECORD_AUDIO,
            icon = Icons.Outlined.Mic,
            title = "Микрофон",
            description = "Для записи звука в видео",
            isGranted = context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    )

    // Activity recognition (Android 10+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        permissions.add(
            PermissionItem(
                permission = Manifest.permission.ACTIVITY_RECOGNITION,
                icon = Icons.AutoMirrored.Outlined.DirectionsWalk,
                title = "Физическая активность",
                description = "Для подсчёта шагов",
                isGranted = context.checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) ==
                        android.content.pm.PackageManager.PERMISSION_GRANTED
            )
        )
    }

    // Media permissions (Android 13+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(
            PermissionItem(
                permission = Manifest.permission.READ_MEDIA_IMAGES,
                icon = Icons.Outlined.PhotoLibrary,
                title = "Фото",
                description = "Для выбора фотографий из галереи",
                isGranted = context.checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) ==
                        android.content.pm.PackageManager.PERMISSION_GRANTED
            )
        )

        permissions.add(
            PermissionItem(
                permission = Manifest.permission.READ_MEDIA_VIDEO,
                icon = Icons.Outlined.PhotoLibrary,
                title = "Видео",
                description = "Для выбора видео из галереи",
                isGranted = context.checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) ==
                        android.content.pm.PackageManager.PERMISSION_GRANTED
            )
        )

        permissions.add(
            PermissionItem(
                permission = Manifest.permission.POST_NOTIFICATIONS,
                icon = Icons.Outlined.Notifications,
                title = "Уведомления",
                description = "Для напоминаний о записях",
                isGranted = context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                        android.content.pm.PackageManager.PERMISSION_GRANTED
            )
        )
    } else {
        // Pre-Android 13: use READ_EXTERNAL_STORAGE
        @Suppress("DEPRECATION")
        permissions.add(
            PermissionItem(
                permission = Manifest.permission.READ_EXTERNAL_STORAGE,
                icon = Icons.Outlined.PhotoLibrary,
                title = "Медиафайлы",
                description = "Для доступа к фото и видео из галереи",
                isGranted = context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        android.content.pm.PackageManager.PERMISSION_GRANTED
            )
        )
    }

    return permissions
}

fun checkAllPermissionsGranted(context: android.content.Context): Boolean {
    return getRequiredPermissions(context).all { it.isGranted }
}
