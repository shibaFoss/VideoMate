package libs.devices

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.ACTION_VIEW
import android.content.Intent.EXTRA_TEXT
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.ResolveInfo
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.core.net.toUri
import libs.networks.URLUtilityKT.isValidURL
import libs.texts.CommonTextUtility.getText
import net.base.R
import java.lang.ref.WeakReference

/**
 * Utility class for handling various Intent-related operations like opening URLs,
 * launching other apps, sharing content, or accessing system settings etc.
 */
object IntentUtility {

	/**
	 * Opens the specified URL in the default browser if any browser app is available.
	 *
	 * This method attempts to open a web page using an implicit intent.
	 * If no browser app is installed to handle the request, an [IllegalStateException] is thrown.
	 *
	 * @param context The context used to start the browser activity.
	 * @param url The URL string to open.
	 * @throws IllegalStateException If no application is available to handle the intent.
	 */
	@JvmStatic
	fun openUrlInBrowser(context: Context?, url: String) {
		if (url.isEmpty()) return
		WeakReference(context).get()?.let { safeContextRef ->
			val webpage = url.toUri()
			val intent = Intent(ACTION_VIEW, webpage)
			if (intent.resolveActivity(safeContextRef.packageManager) != null) {
				safeContextRef.startActivity(intent)
			} else {
				val logString =
					"No application can handle this request. Please install a web browser."
				throw IllegalStateException(logString)
			}
		}
	}

	/**
	 * Retrieves the data URI (e.g., shared text or link) from the given [Activity]'s intent.
	 *
	 * Supports both `ACTION_SEND` and `ACTION_VIEW`.
	 *
	 * @param activity The activity whose intent data is to be extracted.
	 * @return A string representing the data URI, or `null` if not available.
	 */
	@JvmStatic
	fun getIntentDataURI(activity: Activity?): String? {
		WeakReference(activity).get()?.let { safeContextRef ->
			val intent = safeContextRef.intent
			return when (intent.action) {
				ACTION_SEND -> intent.getStringExtra(EXTRA_TEXT)
				ACTION_VIEW -> intent.dataString
				else -> null
			}
		} ?: return null
	}

	/**
	 * Opens a valid URL in the system browser. If the URL is invalid or the operation fails,
	 * the provided [onFailed] callback is invoked.
	 *
	 * @param fileUrl The URL string to open.
	 * @param activity The current activity context.
	 * @param onFailed Callback invoked if the URL is invalid or cannot be opened.
	 */
	@JvmStatic
	fun openLinkInSystemBrowser(fileUrl: String, activity: Activity?, onFailed: () -> Unit) {
		WeakReference(activity).get()?.let { safeContextRef ->
			try {
				if (fileUrl.isNotEmpty() && isValidURL(fileUrl)) {
					val intent = Intent(ACTION_VIEW, fileUrl.toUri()).apply {
						addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK)
					}
					safeContextRef.startActivity(intent)
				} else onFailed()
			} catch (error: Exception) {
				error.printStackTrace()
				onFailed()
			}
		}
	}

	/**
	 * Shares plain text content using the system's share sheet.
	 *
	 * This method launches a chooser dialog that allows the user to pick
	 * an app to share the provided text content.
	 *
	 * @param context The context used to launch the share dialog.
	 * @param text The text content to share.
	 * @param chooserTitle Optional title shown on the chooser dialog.
	 */
	@JvmStatic
	fun shareText(
		context: Context?, text: String,
		chooserTitle: String = getText(R.string.share_via)
	) {
		if (text.isEmpty()) return
		WeakReference(context).get()?.let { safeContextRef ->
			val shareIntent = Intent(ACTION_SEND).apply {
				type = "text/plain"
				putExtra(EXTRA_TEXT, text)
			}
			val intentChooser = Intent.createChooser(shareIntent, chooserTitle)
			safeContextRef.startActivity(intentChooser)
		}
	}

	/**
	 * Attempts to launch an installed app using its package name.
	 *
	 * If the app is found, its main activity is launched. Otherwise, this function returns `false`.
	 *
	 * @param context The context used to launch the app.
	 * @param packageName The package name of the app to be launched.
	 * @return `true` if the app was successfully launched, `false` otherwise.
	 */
	@JvmStatic
	fun launchAppByPackageName(context: Context?, packageName: String): Boolean {
		WeakReference(context).get()?.let { safeContextRef ->
			val packageManager = safeContextRef.packageManager
			val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
			return if (launchIntent != null) {
				safeContextRef.startActivity(launchIntent)
				true
			} else false
		} ?: return false
	}

	/**
	 * Opens the user's email client with recipient, subject, and message body prefilled.
	 *
	 * This method uses `mailto:` URI scheme and allows users to pick their preferred email app.
	 *
	 * @param context The context used to launch the email intent.
	 * @param email Recipient's email address.
	 * @param subject Email subject.
	 * @param body Email body content.
	 */
	@JvmStatic
	fun sendEmailIntent(context: Context?, email: String, subject: String, body: String) {
		WeakReference(context).get()?.let { safeContextRef ->
			val intent = Intent(Intent.ACTION_SENDTO).apply {
				data = "mailto:$email".toUri()
				putExtra(Intent.EXTRA_SUBJECT, subject)
				putExtra(EXTRA_TEXT, body)
			}
			try {
				safeContextRef.startActivity(Intent.createChooser(intent,
					getText(R.string.send_email)
				))
			} catch (e: ActivityNotFoundException) {
				e.printStackTrace()
			}
		}
	}

	/**
	 * Opens the application details settings screen for the current app.
	 *
	 * This is useful when prompting users to grant permissions or manage app-level settings.
	 *
	 * @param context The context used to launch the settings intent.
	 */
	@JvmStatic
	fun openAppSettings(context: Context?) {
		WeakReference(context).get()?.let { safeContextRef ->
			val intent =
				Intent(ACTION_APPLICATION_DETAILS_SETTINGS).apply {
				data = "package:${safeContextRef.packageName}".toUri()
				addFlags(FLAG_ACTIVITY_NEW_TASK)
			}
			safeContextRef.startActivity(intent)
		}
	}

	/**
	 * Safely starts an activity for a given intent, falling back to a toast or log.
	 *
	 * @param context The context to start the intent.
	 * @param intent The intent to launch.
	 * @param onFailure Optional lambda triggered if the intent cannot be resolved.
	 */
	@JvmStatic
	fun startIntentSafely(
		context: Context?, intent: Intent,
		onFailure: (() -> Unit)? = null
	) {
		WeakReference(context).get()?.let { safeContextRef ->
			try {
				if (intent.resolveActivity(safeContextRef.packageManager) != null) {
					safeContextRef.startActivity(intent)
				} else {
					onFailure?.invoke()
				}
			} catch (error: Exception) {
				error.printStackTrace()
				onFailure?.invoke()
			}
		}
	}

	/**
	 * Opens the phone dialer app with the given phone number prefilled.
	 *
	 * This does not initiate a call directly; it only opens the dialer screen,
	 * allowing the user to confirm and initiate the call manually.
	 *
	 * @param context The context used to start the dialer intent.
	 * @param phoneNumber The phone number to be displayed in the dialer.
	 */
	@JvmStatic
	fun dialPhoneNumber(context: Context?, phoneNumber: String) {
		WeakReference(context).get()?.let { ctx ->
			val intent = Intent(Intent.ACTION_DIAL).apply {
				data = "tel:$phoneNumber".toUri()
			}
			ctx.startActivity(intent)
		}
	}

	/**
	 * Sends plain text content to a specific application identified by its package name.
	 *
	 * This method is useful when you want to share a message directly to a known app,
	 * like sending a message to WhatsApp, Telegram, or Gmail, without showing the chooser dialog.
	 *
	 * @param context The context used to start the intent.
	 * @param text The text message to send.
	 * @param packageName The package name of the target app (e.g., "com.whatsapp").
	 */
	@JvmStatic
	fun sendTextToSpecificApp(context: Context?, text: String, packageName: String) {
		WeakReference(context).get()?.let { safeContextRef ->
			val intent = Intent(ACTION_SEND).apply {
				type = "text/plain"
				putExtra(EXTRA_TEXT, text)
				setPackage(packageName)
			}
			try {
				safeContextRef.startActivity(intent)
			} catch (error: Exception) {
				error.printStackTrace()
			}
		}
	}

	/**
	 * Checks whether a specific application is installed on the device.
	 *
	 * This is typically used to verify if a target app is available before attempting
	 * to interact with it using an intent or SDK.
	 *
	 * @param context The context used to access the package manager.
	 * @param packageName The package name of the application to check.
	 * @return `true` if the app is installed, `false` otherwise.
	 */
	@JvmStatic
	fun isAppInstalled(context: Context?, packageName: String): Boolean {
		WeakReference(context).get()?.let { safeContextRef ->
			return try {
				safeContextRef.packageManager.getPackageInfo(packageName, 0)
				true
			} catch (error: Exception) {
				error.printStackTrace()
				false
			}
		} ?: return false
	}


	/**
	 * Returns a list of activities that can handle the given [intent].
	 *
	 * @param activity The [Activity] context used to query the package manager.
	 * @param intent The [Intent] to be resolved.
	 * @return List of [ResolveInfo] for matching activities, or an empty list if none found.
	 */
	@JvmStatic
	fun getMatchingActivities(activity: Activity?, intent: Intent?): List<ResolveInfo> {
		if (intent == null || activity == null) return emptyList()
		WeakReference(activity).get()?.let { safeRef ->
			return safeRef.packageManager.queryIntentActivities(intent, 0)
		} ?: run { return emptyList() }
	}

	/**
	 * Retrieves the primary data shared via the [Activity]'s intent, supporting SEND and VIEW actions.
	 *
	 * @param activity The source [Activity].
	 * @return The shared URL or text if available, otherwise null.
	 */
	@JvmStatic
	fun getIntentData(activity: Activity?): String? {
		val intent = activity?.intent
		val action = intent?.action

		return when (action) {
			ACTION_SEND -> intent.getStringExtra(EXTRA_TEXT)
			ACTION_VIEW -> intent.dataString
			else -> null
		}
	}

	/**
	 * Checks whether the provided [intent] can be handled by any installed app.
	 *
	 * @param activity The [Activity] context.
	 * @param intent The [Intent] to verify.
	 * @return `true` if there's at least one activity that can handle it, `false` otherwise.
	 */
	@JvmStatic
	fun canHandleIntent(activity: Activity?, intent: Intent?): Boolean {
		if (intent == null || activity == null) return false
		WeakReference(activity).get()?.let { safeRef ->
			val activities = safeRef.packageManager.queryIntentActivities(intent, 0)
			return activities.isNotEmpty()
		} ?: run { return false }
	}

	/**
	 * Attempts to start an [Intent] if possible.
	 *
	 * @param activity The [Activity] initiating the intent.
	 * @param intent The [Intent] to launch.
	 * @return `true` if successfully started, `false` if not.
	 */
	@JvmStatic
	fun startActivityIfPossible(activity: Activity?, intent: Intent?): Boolean {
		if (intent == null || activity == null) return false
		WeakReference(activity).get()?.let { safeRef ->
			return if (canHandleIntent(safeRef, intent)) {
				activity.startActivity(intent); true
			} else false
		} ?: run { return false }
	}

	/**
	 * Retrieves the package name of the first app that can handle the given [intent].
	 *
	 * @param activity The [Activity] context.
	 * @param intent The [Intent] to resolve.
	 * @return The package name, or an empty string if none is found.
	 */
	@JvmStatic
	fun getPackageNameForIntent(activity: Activity?, intent: Intent?): String {
		if (intent == null || activity == null) return ""
		WeakReference(activity).get()?.let { safeRef ->
			val activities = safeRef.packageManager.queryIntentActivities(intent, 0)
			return if (activities.isNotEmpty()) activities[0].activityInfo.packageName else ""
		} ?: run { return "" }
	}
}
