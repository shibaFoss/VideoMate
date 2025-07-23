@file:Suppress("DEPRECATION")

package libs.files

import android.content.ContentResolver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
import android.net.Uri
import android.provider.OpenableColumns.DISPLAY_NAME
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import core.bases.GlobalApplication.Companion.APP_INSTANCE
import libs.files.FileExtensions.ARCHIVE_EXTENSIONS
import libs.files.FileExtensions.DOCUMENT_EXTENSIONS
import libs.files.FileExtensions.IMAGE_EXTENSIONS
import libs.files.FileExtensions.MUSIC_EXTENSIONS
import libs.files.FileExtensions.PROGRAM_EXTENSIONS
import libs.files.FileExtensions.VIDEO_EXTENSIONS
import libs.texts.CommonTextUtility.getText
import net.base.R
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.OutputStream
import java.net.URLDecoder.decode
import java.nio.charset.StandardCharsets.UTF_8
import java.util.Locale

object FileUtility {

    /**
     * Parses the "Content-Disposition" HTTP header to extract the filename parameter.
     *
     * This is commonly used when downloading files via HTTP, where the server specifies
     * the suggested filename in the header like: `Content-Disposition: attachment; filename="example.txt"`
     *
     * @param contentDisposition The content disposition header value (can be null).
     * @return The extracted filename, or null if not present or invalid.
     *
     * Example:
     *     getNameFromContentDisposition("attachment; filename=\"report.pdf\"") returns "report.pdf"
     */
    @JvmStatic
    fun getNameFromContentDisposition(contentDisposition: String?): String? {
        if (!contentDisposition.isNullOrEmpty()) {
            val filenameRegex = """(?i)filename=["']?([^";]+)""".toRegex()
            val filenameMatch = filenameRegex.find(contentDisposition)
            if (filenameMatch != null) {
                val filename = filenameMatch.groupValues[1]
                return filename
            }
        }
        return null
    }

    /**
     * Decodes a percent-encoded filename string from a URL, such as "hello%20world.txt".
     *
     * Uses UTF-8 encoding to properly handle all Unicode characters.
     *
     * @param encodedString The encoded file name from a URL.
     * @return The decoded string if successful, or the original string if decoding fails.
     *
     * Common usage:
     *     decodeURLFileName("file%20name.pdf") returns "file name.pdf"
     */
    @JvmStatic
    fun decodeURLFileName(encodedString: String): String {
        return try {
            val decodedFileName = decode(encodedString, UTF_8.name())
            decodedFileName
        } catch (error: Exception) {
            error.printStackTrace()
            encodedString
        }
    }

    /**
     * Retrieves the display name of a file from a URI, handling both "content://" and "file://" schemes.
     *
     * - For content URIs, it queries the ContentResolver to get DISPLAY_NAME.
     * - For file URIs, it extracts the filename from the path.
     *
     * @param context The Android context (used to query ContentResolver).
     * @param uri The file or content URI pointing to a file.
     * @return The file name if available, or null if not resolvable.
     *
     * Note: Always handle null values when calling this method.
     */
    @JvmStatic
    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        try {
            var fileName: String? = null
            if ("content" == uri.scheme) {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(DISPLAY_NAME)
                        if (nameIndex != -1) {
                            fileName = cursor.getString(nameIndex)
                        }
                    }
                    cursor.close()
                }
            } else if ("file" == uri.scheme) {
                fileName = File(uri.path!!).name
            }

            return fileName
        } catch (error: Exception) {
            error.printStackTrace()
            return null
        }
    }

    /**
     * Converts a URI to a File object, typically useful for "file://" URIs.
     *
     * This method does not verify file existence or permissions — it only converts the path.
     * For "content://" URIs, the path may not point to a real file on disk.
     *
     * @param uri The URI representing a file.
     * @return A File object if the path is valid, otherwise null.
     */
    @JvmStatic
    fun getFileFromUri(uri: Uri): File? {
        return try {
            val filePath = uri.path
            val file = if (filePath != null) {
                File(filePath)
            } else null

            file
        } catch (error: Exception) {
            error.printStackTrace()
            null
        }
    }

    /**
     * Writes a string into the internal storage as a private file.
     *
     * The file is created (or overwritten) in the app's internal directory. Only the app can access it.
     *
     * @param fileName Name of the file to save (no path).
     * @param data The content to write into the file.
     *
     * Usage:
     *     saveStringToInternalStorage("settings.json", jsonString)
     */
    @JvmStatic
    fun saveStringToInternalStorage(fileName: String, data: String) {
        val applicationContext = APP_INSTANCE
        try {
            val fileOutputStream = applicationContext
                .openFileOutput(fileName, MODE_PRIVATE)
            fileOutputStream.write(data.toByteArray())
            fileOutputStream.close()
        } catch (error: Exception) {
            error.printStackTrace()
            error.printStackTrace()
        }
    }

    /**
     * Reads the contents of a file from internal storage as a UTF-8 string.
     *
     * This should only be used for files previously saved by this app.
     * If the file does not exist or cannot be read, an exception is thrown.
     *
     * @param fileName The name of the file to read (no path).
     * @return The full string content of the file.
     * @throws Exception if file reading fails.
     */
    @JvmStatic
    fun readStringFromInternalStorage(fileName: String): String {
        val applicationContext = APP_INSTANCE
        return try {
            val fileInputStream: FileInputStream = applicationContext.openFileInput(fileName)
            val content = fileInputStream.readBytes().toString(Charsets.UTF_8)
            fileInputStream.close()
            content
        } catch (error: Exception) {
            error.printStackTrace()
            throw error
        }
    }

    /**
     * Aggressively sanitizes a filename by allowing only alphanumeric characters and a minimal
     * set of symbols `()@[]_.-`. All other characters are replaced with underscores.
     *
     * Use this when targeting cross-platform or server-side storage where strict file naming is required.
     *
     * @param fileName The original, potentially unsafe filename.
     * @return A sanitized version of the filename.
     */
    @JvmStatic
    fun sanitizeFileNameExtreme(fileName: String): String {
        val sanitizedFileName = fileName.replace(Regex("[^a-zA-Z0-9()@\\[\\]_.-]"), "_")
            .replace(" ", "_")
            .replace("___", "_")
            .replace("__", "_")
        return sanitizedFileName
    }

    /**
     * Sanitizes a filename for general Android file system compatibility.
     *
     * Replaces characters not allowed in Android/Linux filenames (like `\ / : * ? " < > |`)
     * and trims trailing periods and excess underscores.
     *
     * @param fileName The user-defined or external filename.
     * @return A clean filename safe for saving on internal or external storage.
     */
    @JvmStatic
    fun sanitizeFileNameNormal(fileName: String): String {
        val sanitizedFileName = fileName
            .replace(Regex("[\\\\/:*?\"<>|\\p{Cntrl}\u0000-\u001F\u007F]"), "_")
            .trimEnd('.')
            .trim()
            .replace(Regex("_+"), "_")
            .replace(" ", "_")
            .replace("___", "_")
            .replace("__", "_")
        return sanitizedFileName
    }

    /**
     * Validates whether a given filename can be safely used to create a file in internal storage.
     *
     * It attempts to create and immediately delete a temporary file using the given name.
     * Useful to pre-check invalid or restricted characters across different devices.
     *
     * @param fileName The file name to test.
     * @return True if valid and creatable, false if not.
     */
    @JvmStatic
    fun isFileNameValid(fileName: String): Boolean {
        return try {
            val internalApplicationDir = APP_INSTANCE.filesDir
            val tempFile = File(internalApplicationDir, fileName)
            tempFile.createNewFile()
            tempFile.delete()
            true
        } catch (error: IOException) {
            error.printStackTrace()
            false
        }
    }

    /**
     * Checks whether a given `DocumentFile` instance can be written to.
     *
     * This is especially useful for SAF (Storage Access Framework) files
     * where the app does not have direct file system access and must rely
     * on URI permissions granted by the user.
     *
     * @param file The `DocumentFile` to check.
     * @return True if writable, false otherwise.
     */
    @JvmStatic
    fun isWritableFile(file: DocumentFile?): Boolean {
        val isWritable = file?.canWrite() ?: false
        return isWritable
    }

    /**
     * Checks if the given folder has write access by trying to create and delete a temporary file.
     *
     * @param folder The [DocumentFile] folder to test for write permissions.
     * @return `true` if the app can write to the folder, `false` otherwise.
     */
    @JvmStatic
    fun hasWriteAccess(folder: DocumentFile?): Boolean {
        if (folder == null) return false
        return try {
            // Try creating a temporary file
            val tempFile = folder.createFile("text/plain", "temp_check_file.txt")
            if (tempFile != null) {
                val contentResolver = APP_INSTANCE.contentResolver
                val openOutputStream = contentResolver.openOutputStream(tempFile.uri)
                openOutputStream?.use { stream ->
                    stream.write("test".toByteArray())
                    stream.flush()
                }
                tempFile.delete() // Clean up the temporary file
                true
            } else false
        } catch (error: IOException) {
            error.printStackTrace()
            false
        }
    }

    /**
     * Writes an empty file of the specified size to the provided [DocumentFile].
     *
     * @param context The Android [Context] used to access the [ContentResolver].
     * @param file The [DocumentFile] to write to.
     * @param fileSize The number of bytes to write into the file (creates a file of this size).
     * @return `true` if the operation was successful, `false` otherwise.
     */
    @JvmStatic
    fun writeEmptyFile(context: Context, file: DocumentFile, fileSize: Long): Boolean {
        return try {
            val contentResolver: ContentResolver = context.contentResolver
            val outputStream: OutputStream? = contentResolver.openOutputStream(file.uri)

            outputStream?.use { stream ->
                val placeholder = ByteArray(fileSize.toInt())
                stream.write(placeholder) // Write the placeholder bytes
                stream.flush()
            }
            true
        } catch (error: IOException) {
            error.printStackTrace()
            false
        }
    }

    /**
     * Generates a unique filename by appending a numeric prefix if a file with the original name already exists.
     * Ensures no name collisions in the given directory.
     *
     * @param fileDirectory The [DocumentFile] directory to check for existing files.
     * @param originalFileName The original file name (e.g., "example.txt").
     * @return A unique file name (e.g., "2_example.txt") that doesn't exist in the directory.
     */
    @JvmStatic
    fun generateUniqueFileName(fileDirectory: DocumentFile, originalFileName: String): String {
        var sanitizedFileName = sanitizeFileNameExtreme(originalFileName)
        var index = 1
        val regex = Regex("^(\\d+)_")

        // Keep modifying the file name until it doesn't exist in the directory
        while (fileDirectory.findFile(sanitizedFileName) != null) {
            val matchResult = regex.find(sanitizedFileName)
            if (matchResult != null) {
                val currentIndex = matchResult.groupValues[1].toInt()
                sanitizedFileName = sanitizedFileName.replaceFirst(regex, "")
                index = currentIndex + 1
            }
            sanitizedFileName = "${index}_$sanitizedFileName"
            index++
        }

        return sanitizedFileName
    }

    /**
     * Finds a file inside a given directory whose name starts with the specified prefix.
     *
     * @param internalDir The directory [File] to search in.
     * @param namePrefix The prefix the target file should start with.
     * @return The matching [File] if found, or `null` otherwise.
     */
    @JvmStatic
    fun findFileStartingWith(internalDir: File, namePrefix: String): File? {
        val result = internalDir.listFiles()?.find {
            it.isFile && it.name.startsWith(namePrefix)
        }
        return result
    }

    /**
     * Creates a new subdirectory with the specified name inside the given parent folder.
     *
     * @param parentFolder The [DocumentFile] representing the parent directory.
     * @param folderName The name of the subdirectory to be created.
     * @return The created [DocumentFile] if successful, or `null` if creation failed.
     */
    @JvmStatic
    fun makeDirectory(parentFolder: DocumentFile?, folderName: String): DocumentFile? {
        val newDirectory = parentFolder?.createDirectory(folderName)
        return newDirectory
    }

    /**
     * Determines the MIME type of a file based on its name/extension.
     *
     * @param fileName The name of the file (e.g., "example.pdf").
     * @return The MIME type string (e.g., "application/pdf") or `null` if undetermined.
     */
    @JvmStatic
    fun getMimeType(fileName: String): String? {
        val extension = getFileExtension(fileName)?.lowercase(Locale.getDefault())
        val mimeType = extension?.let {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(it)
        } ?: run {
            // Fallback to using URI if extension is not directly mapped
            val uri = "content://$extension".toUri()
            APP_INSTANCE.contentResolver.getType(uri)
        }
        return mimeType
    }
    /**
     * Extracts the file extension from a given filename.
     *
     * @param fileName The name of the file (e.g., "example.txt").
     * @return The file extension (e.g., "txt") or `null` if no extension is found.
     */
    @JvmStatic
    fun getFileExtension(fileName: String): String? {
        return fileName.substringAfterLast('.', "").takeIf { it.isNotEmpty() }
    }

    /**
     * Extension function to check if a [DocumentFile] has one of the specified file extensions.
     *
     * @param extensions Array of valid extensions (e.g., arrayOf("jpg", "png")).
     * @return `true` if the file name ends with one of the provided extensions, `false` otherwise.
     */
    @JvmStatic
    fun DocumentFile.isFileType(extensions: Array<String>): Boolean {
        return endsWithExtension(name, extensions)
    }

    /**
     * Checks if the given [DocumentFile] is an audio file.
     */
    @JvmStatic
    fun isAudio(file: DocumentFile): Boolean {
        return file.isFileType(MUSIC_EXTENSIONS)
    }

    /**
     * Checks if the given [DocumentFile] is an archive file (e.g., zip, rar).
     */
    @JvmStatic
    fun isArchive(file: DocumentFile): Boolean {
        return file.isFileType(ARCHIVE_EXTENSIONS)
    }

    /**
     * Checks if the given [DocumentFile] is a program or executable file.
     */
    @JvmStatic
    fun isProgram(file: DocumentFile): Boolean {
        return file.isFileType(PROGRAM_EXTENSIONS)
    }

    /**
     * Checks if the given [DocumentFile] is a video file.
     */
    @JvmStatic
    fun isVideo(file: DocumentFile): Boolean {
        return file.isFileType(VIDEO_EXTENSIONS)
    }

    /**
     * Checks if the given [DocumentFile] is a document (e.g., PDF, DOCX).
     */
    @JvmStatic
    fun isDocument(file: DocumentFile): Boolean {
        return file.isFileType(DOCUMENT_EXTENSIONS)
    }

    /**
     * Checks if the given [DocumentFile] is an image file.
     */
    @JvmStatic
    fun isImage(file: DocumentFile): Boolean {
        return file.isFileType(IMAGE_EXTENSIONS)
    }

    /**
     * Gets the type label for a [DocumentFile] based on its name/extension.
     *
     * @param file The [DocumentFile] to check.
     * @return A localized string representing the file type (e.g., "Videos", "Documents").
     */
    @JvmStatic
    fun getFileType(file: DocumentFile): String {
        return getFileType(file.name)
    }

    /**
     * Gets a file type label from a filename by analyzing its extension.
     *
     * @param fileName The file name to analyze.
     * @return A localized string representing the file category.
     */
    @JvmStatic
    fun getFileType(fileName: String?): String {
        return when {
            isAudioByName(fileName)    -> getText(R.string.sounds)
            isArchiveByName(fileName)  -> getText(R.string.archives)
            isProgramByName(fileName)  -> getText(R.string.programs)
            isVideoByName(fileName)    -> getText(R.string.videos)
            isDocumentByName(fileName) -> getText(R.string.documents)
            isImageByName(fileName)    -> getText(R.string.images)
            else                       -> getText(R.string.others)
        }
    }

    /**
     * Sends a broadcast to add a file to Android's MediaStore so it becomes visible in gallery/media apps.
     *
     * @param file The [File] to be scanned by the media scanner.
     */
    @JvmStatic
    fun addToMediaStore(file: File) {
        try {
            val fileUri = Uri.fromFile(file)
            val actionIntentKey = ACTION_MEDIA_SCANNER_SCAN_FILE
            val mediaScanIntent = Intent(actionIntentKey).apply {
                data = fileUri
            }
            APP_INSTANCE.sendBroadcast(mediaScanIntent)
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    /**
     * Checks if the given filename ends with any of the specified extensions (case-insensitive).
     *
     * @param fileName The file name to check.
     * @param extensions Array of valid extensions (e.g., "mp3", "png").
     * @return `true` if it ends with any extension, `false` otherwise.
     */
    @JvmStatic
    fun endsWithExtension(fileName: String?, extensions: Array<String>): Boolean {
        return extensions.any {
            fileName?.lowercase(Locale.getDefault())
                ?.endsWith(".$it") == true
        }
    }

    /**
     * Checks if a file name indicates an audio file by its extension.
     */
    @JvmStatic
    fun isAudioByName(name: String?): Boolean {
        return endsWithExtension(name, MUSIC_EXTENSIONS)
    }

    /**
     * Checks if a file name indicates an archive file by its extension.
     */
    @JvmStatic
    fun isArchiveByName(name: String?): Boolean {
        return endsWithExtension(name, ARCHIVE_EXTENSIONS)
    }

    /**
     * Checks if a file name indicates a program file by its extension.
     */
    @JvmStatic
    fun isProgramByName(name: String?): Boolean {
        return endsWithExtension(name, PROGRAM_EXTENSIONS)
    }

    /**
     * Checks if a file name indicates a video file by its extension.
     */
    @JvmStatic
    fun isVideoByName(name: String?): Boolean {
        return endsWithExtension(name, VIDEO_EXTENSIONS)
    }

    /**
     * Checks if a file name indicates a document file by its extension.
     */
    @JvmStatic
    fun isDocumentByName(name: String?): Boolean {
        return endsWithExtension(name, DOCUMENT_EXTENSIONS)
    }

    /**
     * Checks if a file name indicates an image file by its extension.
     */
    @JvmStatic
    fun isImageByName(name: String?): Boolean {
        return endsWithExtension(name, IMAGE_EXTENSIONS)
    }

    /**
     * Removes the extension from a filename.
     *
     * @param fileName The full name of the file (e.g., "photo.jpg").
     * @return The name without extension (e.g., "photo"). If no dot is found, returns original name.
     */
    @JvmStatic
    fun getFileNameWithoutExtension(fileName: String): String {
        return try {
            val dotIndex = fileName.lastIndexOf('.')
            if (dotIndex > 0) fileName.substring(0, dotIndex) else fileName
        } catch (error: Exception) {
            error.printStackTrace()
            fileName
        }
    }
}
