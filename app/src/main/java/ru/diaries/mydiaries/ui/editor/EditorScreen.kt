package ru.diaries.mydiaries.ui.editor

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import ru.diaries.mydiaries.R
import ru.diaries.mydiaries.data.model.Photo
import ru.diaries.mydiaries.ui.components.DiaryTextField
import ru.diaries.mydiaries.ui.theme.DateHeaderStyle
import ru.diaries.mydiaries.ui.theme.InkBlue
import ru.diaries.mydiaries.ui.theme.LavenderMist
import ru.diaries.mydiaries.ui.theme.SageGreen
import ru.diaries.mydiaries.ui.theme.Terracotta
import ru.diaries.mydiaries.ui.theme.WarmBrown
import java.io.File
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    entryId: String?,
    viewModel: EditorViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showPhotoSourceSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val titleFocusRequester = remember { FocusRequester() }

    var cameraPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(
            maxItems = EditorState.MAX_PHOTOS
        )
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            uris.forEach { uri ->
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: Exception) {
                }
            }
            viewModel.handleIntent(EditorIntent.AddPhotos(uris))
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraPhotoUri?.let { uri ->
                viewModel.handleIntent(EditorIntent.AddPhotos(listOf(uri)))
            }
        }
        cameraPhotoUri = null
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createImageUri(context)
            cameraPhotoUri = uri
            cameraLauncher.launch(uri)
        }
    }

    fun launchCamera() {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                val uri = createImageUri(context)
                cameraPhotoUri = uri
                cameraLauncher.launch(uri)
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    fun launchGallery() {
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    LaunchedEffect(entryId) {
        viewModel.handleIntent(EditorIntent.LoadEntry(entryId))
    }

    // Auto-focus title field when creating new entry
    LaunchedEffect(state.isLoading) {
        if (!state.isLoading && state.entryId == null) {
            titleFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is EditorEffect.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    if (showPhotoSourceSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPhotoSourceSheet = false },
            sheetState = sheetState
        ) {
            PhotoSourceBottomSheet(
                onCameraClick = {
                    scope.launch {
                        sheetState.hide()
                        showPhotoSourceSheet = false
                        launchCamera()
                    }
                },
                onGalleryClick = {
                    scope.launch {
                        sheetState.hide()
                        showPhotoSourceSheet = false
                        launchGallery()
                    }
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(SageGreen, LavenderMist)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.surface
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (state.entryId == null)
                                stringResource(R.string.new_entry)
                            else
                                stringResource(R.string.edit_entry),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleIntent(EditorIntent.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    // Save button with gradient background when enabled
                    val saveEnabled = state.title.isNotBlank() && !state.isSaving
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (saveEnabled)
                                    Brush.linearGradient(listOf(WarmBrown, Terracotta))
                                else
                                    Brush.linearGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                        )
                                    )
                            )
                            .clickable(enabled = saveEnabled) {
                                viewModel.handleIntent(EditorIntent.SaveEntry)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.save),
                            tint = if (saveEnabled)
                                MaterialTheme.colorScheme.surface
                            else
                                MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Date with decorative element
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(WarmBrown, Terracotta.copy(alpha = 0.5f))
                            )
                        )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = state.currentDate.format(dateFormatter),
                    style = DateHeaderStyle,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            DiaryTextField(
                value = state.title,
                onValueChange = { viewModel.handleIntent(EditorIntent.TitleChanged(it)) },
                label = stringResource(R.string.title_label),
                placeholder = stringResource(R.string.title_placeholder),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.titleMedium,
                focusRequester = titleFocusRequester
            )

            Spacer(modifier = Modifier.height(20.dp))

            DiaryTextField(
                value = state.content,
                onValueChange = { viewModel.handleIntent(EditorIntent.ContentChanged(it)) },
                label = stringResource(R.string.content_label),
                placeholder = stringResource(R.string.content_placeholder),
                modifier = Modifier.fillMaxWidth(),
                minLines = 8,
                textStyle = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(20.dp))

            PhotosSection(
                photos = state.photos,
                canAddMore = state.canAddMorePhotos,
                onAddPhotosClick = { showPhotoSourceSheet = true },
                onRemovePhoto = { photoId ->
                    viewModel.handleIntent(EditorIntent.RemovePhoto(photoId))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun createImageUri(context: android.content.Context): Uri {
    val imagesDir = File(context.cacheDir, "images")
    imagesDir.mkdirs()
    val imageFile = File(imagesDir, "photo_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}

@Composable
private fun PhotoSourceBottomSheet(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Decorative header icon
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = CircleShape,
                    spotColor = WarmBrown.copy(alpha = 0.2f)
                )
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PhotoLibrary,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.add_photo),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Camera option
        PhotoSourceOptionCard(
            icon = Icons.Default.CameraAlt,
            title = stringResource(R.string.take_photo),
            subtitle = stringResource(R.string.take_photo_description),
            gradientColors = listOf(InkBlue, LavenderMist),
            onClick = onCameraClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Gallery option
        PhotoSourceOptionCard(
            icon = Icons.Default.PhotoLibrary,
            title = stringResource(R.string.choose_from_gallery),
            subtitle = stringResource(R.string.choose_from_gallery_description),
            gradientColors = listOf(SageGreen, Terracotta),
            onClick = onGalleryClick
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PhotoSourceOptionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    gradientColors: List<androidx.compose.ui.graphics.Color>,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_scale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = WarmBrown.copy(alpha = 0.1f)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = true,
                    color = gradientColors.first()
                ),
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(colors = gradientColors)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.surface
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PhotosSection(
    photos: List<Photo>,
    canAddMore: Boolean,
    onAddPhotosClick: () -> Unit,
    onRemovePhoto: (String) -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.photos_count, photos.size, EditorState.MAX_PHOTOS),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (photos.isEmpty()) {
            // Empty state - attractive add photos card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onAddPhotosClick),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)
                        )
                    )
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Terracotta.copy(alpha = 0.2f),
                                        SageGreen.copy(alpha = 0.2f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.add_photos),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.add_photos_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(photos, key = { it.id }) { photo ->
                    PhotoItem(
                        photo = photo,
                        onRemove = { onRemovePhoto(photo.id) }
                    )
                }

                if (canAddMore) {
                    item {
                        AddPhotoButton(onClick = onAddPhotosClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoItem(
    photo: Photo,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = WarmBrown.copy(alpha = 0.15f)
            )
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photo.uri.toUri())
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.photo),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
        )

        // Remove button with gradient
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .size(26.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = CircleShape,
                    spotColor = MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                )
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.errorContainer)
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.remove_photo),
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun AddPhotoButton(
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Terracotta.copy(alpha = 0.4f),
                    SageGreen.copy(alpha = 0.4f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Terracotta.copy(alpha = 0.15f),
                                SageGreen.copy(alpha = 0.15f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_photo),
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.add),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
