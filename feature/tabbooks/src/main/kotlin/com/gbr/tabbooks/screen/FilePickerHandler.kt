package com.gbr.tabbooks.screen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Composable that handles file picking for Gitabase files.
 * Uses the system file picker to select .db files from local storage.
 */
@Composable
fun FilePickerHandler(
    onFileSelected: (String) -> Unit
) {
    val context = LocalContext.current
    
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            uri?.let { selectedUri ->
                // Get the file path from the URI
                val filePath = getFilePathFromUri(context, selectedUri)
                filePath?.let { path ->
                    onFileSelected(path)
                }
            }
        }
    }
    
    // Launch file picker when this composable is first composed
    LaunchedEffect(Unit) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/x-sqlite3", "application/octet-stream"))
        }
        filePickerLauncher.launch(intent)
    }
}

/**
 * Gets the file path from a URI.
 * Preserves the original filename when possible.
 */
private fun getFilePathFromUri(context: android.content.Context, uri: Uri): String? {
    return try {
        // For file:// URIs, we can get the path directly
        if (uri.scheme == "file") {
            uri.path
        } else {
            // For content:// URIs, we need to copy the file to a temporary location
            // Try to get the original filename from the URI
            val originalFileName = getOriginalFileName(context, uri)
            val tempFile = if (originalFileName != null) {
                java.io.File(context.cacheDir, originalFileName)
            } else {
                java.io.File.createTempFile("gitabase_", ".db", context.cacheDir)
            }
            
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            tempFile.absolutePath
        }
    } catch (e: Exception) {
        null
    }
}

/**
 * Attempts to get the original filename from a content URI.
 */
private fun getOriginalFileName(context: android.content.Context, uri: Uri): String? {
    return try {
        // Try to get filename from content resolver
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    it.getString(nameIndex)
                } else null
            } else null
        }
    } catch (e: Exception) {
        null
    }
}
