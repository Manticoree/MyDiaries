package ru.diaries.mydiaries.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ru.diaries.mydiaries.R
import ru.diaries.mydiaries.data.model.DiaryEntry
import ru.diaries.mydiaries.ui.theme.DiaryTitleStyle
import ru.diaries.mydiaries.ui.theme.GoldenHoney
import ru.diaries.mydiaries.ui.theme.Terracotta
import ru.diaries.mydiaries.ui.theme.WarmBrown
import java.time.format.DateTimeFormatter

private const val MAX_PREVIEW_PHOTOS = 2

@Composable
fun DiaryEntryCard(
    entry: DiaryEntry,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    val previewPhotos = entry.photos.take(MAX_PREVIEW_PHOTOS)
    val remainingPhotosCount = entry.photos.size - MAX_PREVIEW_PHOTOS

    // Gradient for bookmark stripe
    val bookmarkGradient = Brush.verticalGradient(
        colors = listOf(Terracotta, GoldenHoney)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = WarmBrown.copy(alpha = 0.15f),
                ambientColor = WarmBrown.copy(alpha = 0.08f)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Gradient bookmark stripe on the left
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(if (previewPhotos.isEmpty()) 120.dp else 180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                    .background(bookmarkGradient)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 8.dp, top = 16.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        // Title with custom style
                        Text(
                            text = entry.title,
                            style = DiaryTitleStyle,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Date with decorative line
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = entry.date.format(dateFormatter),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Decorative line
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                                MaterialTheme.colorScheme.outline.copy(alpha = 0f)
                                            )
                                        )
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Content preview
                        Text(
                            text = entry.content,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Soft delete button
                    IconButton(
                        onClick = onDelete,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                            contentColor = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = stringResource(R.string.delete_entry),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Photo previews
                if (previewPhotos.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 16.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        previewPhotos.forEachIndexed { index, photo ->
                            val isLastPhoto = index == previewPhotos.lastIndex
                            val showOverlay = isLastPhoto && remainingPhotosCount > 0

                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .shadow(
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        spotColor = WarmBrown.copy(alpha = 0.2f)
                                    )
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(photo.uri.toUri())
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = stringResource(R.string.photo),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                if (showOverlay) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                WarmBrown.copy(alpha = 0.7f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "+$remainingPhotosCount",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.inverseOnSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
