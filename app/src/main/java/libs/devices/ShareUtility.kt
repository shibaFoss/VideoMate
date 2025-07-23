package libs.devices

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.webkit.MimeTypeMap.getSingleton
import androidx.core.content.FileProvider.getUriForFile
import androidx.documentfile.provider.DocumentFile
import libs.texts.CommonTextUtility.getText
import libs.ui.builders.ToastViewBuilder.Companion.showToast
import net.base.R
import java.io.File
import java.lang.ref.WeakReference

/**
 * A utility object for sharing and opening different types of files or content through intents.
 * Handles sharing URLs, text, media files, videos, documents, and APKs, and supports safe context references.
 */
object ShareUtility {

	/**
	 * Shares a URL using a text/plain intent through the system share sheet.
	 *
	 * @param context Context used to start the sharing intent.
	 * @param fileURL The URL string to be shared.
	 * @param titleText Optional title text for the chooser dialog.
	 * @param onDone Callback invoked after the share intent is triggered.
	 */
	@JvmStatic
	fun shareUrlText(
		context: Context?,
		fileURL: String,
		titleText: String = getText(R.string.share),
		onDone: () -> Unit = {}
	) {
		WeakReference(context).get()?.let { safeContextRef ->
			Intent(ACTION_SEND).apply {
				type = "text/plain"
				putExtra(EXTRA_TEXT, fileURL)
				safeContextRef.startActivity(createChooser(this, titleText))
				onDone()
			}
		}
	}

	/**
	 * Shares a document file using ACTION_SEND and grants URI read permission.
	 *
	 * @param context Context to use for intent.
	 * @param documentFile DocumentFile object to share.
	 * @param titleText Optional chooser dialog title.
	 * @param onDone Callback after sharing is triggered.
	 */
	@JvmStatic
	fun shareDocumentFile(
		context: Context?,
		documentFile: DocumentFile,
		titleText: String = getText(R.string.share),
		onDone: () -> Unit = {}
	) {
		WeakReference(context).get()?.let { safeContextRef ->
			val file = File(documentFile.uri.path ?: return)
			val fileUri: Uri = getUriForFile(
				safeContextRef,
				"${safeContextRef.packageName}.provider", file
			)

			Intent(ACTION_SEND).apply {
				type = safeContextRef.contentResolver.getType(documentFile.uri)
				putExtra(EXTRA_STREAM, fileUri)
				addFlags(FLAG_GRANT_READ_URI_PERMISSION)
				safeContextRef.startActivity(createChooser(this, titleText))
				onDone()
			}
		}
	}

	/**
	 * Attempts to open a given file using the default app for its MIME type.
	 * Falls back with a toast if no app can handle the file.
	 *
	 * @param file The file to open.
	 * @param context Context used to launch the intent.
	 */
	@JvmStatic
	fun openFile(file: File, context: Context?) {
		WeakReference(context).get()?.let { safeContextRef ->
			val fileUri: Uri = getUriForFile(
				safeContextRef,
				"${safeContextRef.packageName}.provider", file
			)
			val mimeType = getSingleton().getMimeTypeFromExtension(file.extension) ?: "*/*"

			val openFileIntent = Intent(ACTION_VIEW).apply {
				setDataAndType(fileUri, mimeType)
				addFlags(FLAG_GRANT_READ_URI_PERMISSION)
			}

			if (openFileIntent.resolveActivity(safeContextRef.packageManager) != null) {
				safeContextRef.startActivity(openFileIntent)
			} else {
				showToast(getText(R.string.no_app_found_to_open_this_file))
			}
		}
	}

	/**
	 * Shares a media file such as audio or video using an ACTION_SEND intent.
	 *
	 * @param context Context used for creating the share intent.
	 * @param file Media file to share.
	 */
	@JvmStatic
	fun shareMediaFile(context: Context?, file: File) {
		WeakReference(context).get()?.let { safeContextRef ->
			try {
				val fileUri: Uri = getUriForFile(
					safeContextRef,
					"${safeContextRef.packageName}.provider", file
				)
				val shareIntent = Intent(ACTION_SEND).apply {
					type = safeContextRef.contentResolver.getType(fileUri) ?: "audio/*"
					putExtra(EXTRA_STREAM, fileUri)
					addFlags(FLAG_GRANT_READ_URI_PERMISSION)
				}
				val shareMediaString = getText(R.string.sharing_media_file_)
				val intentChooser = createChooser(shareIntent, shareMediaString)
				safeContextRef.startActivity(intentChooser)
			} catch (error: Exception) {
				error.printStackTrace()
			}
		}
	}

	/**
	 * Shares a video file from a DocumentFile URI using an ACTION_SEND intent.
	 *
	 * @param context Context to use for launching the share intent.
	 * @param videoFile Video file to share.
	 * @param title Title for the share chooser dialog.
	 * @param onDone Callback invoked after share is triggered.
	 */
	@JvmStatic
	fun shareVideo(
		context: Context?,
		videoFile: DocumentFile,
		title: String = getText(R.string.share),
		onDone: () -> Unit = {}
	) {
		WeakReference(context).get()?.let { safeContextRef ->
			Intent(ACTION_SEND).apply {
				val videoUri = videoFile.uri
				type = "video/*"
				putExtra(EXTRA_STREAM, videoUri)
				addFlags(FLAG_GRANT_READ_URI_PERMISSION)
				safeContextRef.startActivity(createChooser(this, title))
				onDone()
			}
		}
	}

	/**
	 * Shares plain text using the system share dialog.
	 *
	 * @param context Context to use for the share intent.
	 * @param text The text content to share.
	 * @param title Optional title for the chooser.
	 * @param onDone Callback after the share is launched.
	 */
	@JvmStatic
	fun shareText(
		context: Context?,
		text: String,
		title: String = getText(R.string.share),
		onDone: () -> Unit = {}
	) {
		WeakReference(context).get()?.let { safeContextRef ->
			Intent(ACTION_SEND).apply {
				type = "text/plain"
				putExtra(EXTRA_TEXT, text)
				safeContextRef.startActivity(createChooser(this, title))
				onDone()
			}
		}
	}

	/**
	 * Opens an APK file for installation. Grants URI read permission and sets appropriate MIME type.
	 *
	 * @param activity Activity context used for the intent.
	 * @param apkFile The APK file to be installed.
	 * @param authority File provider authority declared in manifest.
	 */
	@JvmStatic
	fun openApkFile(
		activity: Activity?,
		apkFile: File,
		authority: String
	) {
		WeakReference(activity).get()?.let { safeContextRef ->
			val intent = Intent(ACTION_VIEW).apply {
				flags = FLAG_ACTIVITY_NEW_TASK or FLAG_GRANT_READ_URI_PERMISSION
				val apkUri: Uri = getUriForFile(safeContextRef, authority, apkFile)
				setDataAndType(apkUri, "application/vnd.android.package-archive")
			}
			safeContextRef.startActivity(intent)
		}
	}
}