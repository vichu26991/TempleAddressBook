package com.snuggy.templeaddressbook.ui.contacts

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayInputStream
import java.io.File

@Immutable
data class ContactRecord(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val primaryPhone: String,
    val phoneLabel: String,
    val villageTown: String,
    val district: String,
    val state: String,
    val country: String,
    val tags: List<String>,
    val isFavorite: Boolean,
    val photoUri: String? = null
) {
    val fullName: String = listOf(firstName.trim(), lastName.trim())
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank { "Unnamed Contact" }

    val locationLine: String = listOf(villageTown.trim(), district.trim())
        .filter { it.isNotBlank() }
        .joinToString(" • ")

    val initials: String = fullName
        .split(" ")
        .filter { it.isNotBlank() }
        .let { parts ->
            when {
                parts.isEmpty() -> "?"
                parts.size == 1 -> parts.first().take(2).uppercase()
                else -> "${parts.first().first()}${parts.last().first()}".uppercase()
            }
        }
}

data class ContactDraft(
    val firstName: String,
    val lastName: String,
    val primaryPhone: String,
    val phoneLabel: String,
    val villageTown: String,
    val district: String,
    val state: String,
    val country: String,
    val tags: List<String>,
    val isFavorite: Boolean,
    val photoUri: String? = null
)

data class ContactFilterOptions(
    val countries: List<String>,
    val states: List<String>,
    val districts: List<String>,
    val villageTowns: List<String>,
    val tags: List<String>
)

data class AppliedContactFilters(
    val countries: Set<String> = emptySet(),
    val states: Set<String> = emptySet(),
    val districts: Set<String> = emptySet(),
    val villageTowns: Set<String> = emptySet(),
    val tags: Set<String> = emptySet()
) {
    fun isEmpty(): Boolean = countries.isEmpty() &&
            states.isEmpty() &&
            districts.isEmpty() &&
            villageTowns.isEmpty() &&
            tags.isEmpty()

    fun selectedCount(): Int = listOf(countries, states, districts, villageTowns, tags)
        .sumOf { it.size }
}

data class SmartGroupRecord(
    val id: Long,
    val name: String,
    val filters: AppliedContactFilters,
    val createdAt: Long
)

@Immutable
data class PhotoRenderSpec(
    val source: String,
    val scale: Float = 1f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f
)

private const val PHOTO_SPEC_SEPARATOR = "#TABPHOTO#"

fun encodePhotoSpec(
    source: String,
    scale: Float = 1f,
    offsetX: Float = 0f,
    offsetY: Float = 0f
): String = listOf(source, scale.toString(), offsetX.toString(), offsetY.toString())
    .joinToString(PHOTO_SPEC_SEPARATOR)

fun decodePhotoSpec(raw: String?): PhotoRenderSpec? {
    if (raw.isNullOrBlank()) return null
    val parts = raw.split(PHOTO_SPEC_SEPARATOR)
    return if (parts.size == 4) {
        PhotoRenderSpec(
            source = parts[0],
            scale = parts[1].toFloatOrNull() ?: 1f,
            offsetX = parts[2].toFloatOrNull() ?: 0f,
            offsetY = parts[3].toFloatOrNull() ?: 0f
        )
    } else {
        PhotoRenderSpec(source = raw)
    }
}



fun normalizedPhotoOffset(value: Float): Float = if (kotlin.math.abs(value) > 3f) value / 220f else value

fun photoTranslationForSize(offset: Float, sizePx: Float): Float = normalizedPhotoOffset(offset) * sizePx

fun loadContactPhotoBitmap(context: Context, specString: String?): ImageBitmap? {
    val spec = decodePhotoSpec(specString) ?: return null
    val uri = Uri.parse(spec.source)
    return runCatching {
        val bytes = if (uri.scheme == "file") {
            val path = uri.path ?: return@runCatching null
            File(path).takeIf { it.exists() }?.readBytes()
        } else {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } ?: return@runCatching null

        val rawBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return@runCatching null
        rotateBitmapIfRequired(rawBitmap, bytes).asImageBitmap()
    }.getOrNull()
}

private fun rotateBitmapIfRequired(bitmap: Bitmap, bytes: ByteArray): Bitmap {
    val orientation = runCatching {
        ExifInterface(ByteArrayInputStream(bytes)).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
    }.getOrDefault(ExifInterface.ORIENTATION_NORMAL)

    val matrix = Matrix().apply {
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> preScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> preScale(1f, -1f)
        }
    }
    if (matrix.isIdentity) return bitmap
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}
