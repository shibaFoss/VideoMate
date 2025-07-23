package libs.devices

import android.content.pm.PackageManager.GET_SIGNATURES
import android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES
import android.content.pm.PackageManager.NameNotFoundException
import android.content.pm.PackageManager.PackageInfoFlags.of
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import core.bases.GlobalApplication

/**
 * Utility object for retrieving app-specific metadata such as version name,
 * version code, and device SDK version.
 *
 * This class abstracts Android version compatibility differences and provides
 * simple access to common app version info without requiring repetitive boilerplate.
 */
object AppVersionUtility {

	/**
	 * Gets the app's version name as specified in the build.gradle or AndroidManifest.xml.
	 *
	 * This value is typically used to show the app version to the user, like "1.0.2".
	 *
	 * @return The version name string (e.g., "1.2.3") or `null` if the info could not be fetched.
	 *
	 * Internally handles Android version-specific logic:
	 * - Uses `GET_SIGNING_CERTIFICATES` for Android 13 (TIRAMISU) and above.
	 * - Uses deprecated `GET_SIGNATURES` for lower versions to maintain compatibility.
	 */
	@JvmStatic
	val versionName: String?
		get() {
			val applicationContext = GlobalApplication.APP_INSTANCE
			val packageName = applicationContext.packageName
			return try {
				val packageManager = applicationContext.packageManager

				// Fetch package info based on SDK version to avoid deprecated warnings.
				val packageInfo = if (SDK_INT >= TIRAMISU) {
					val flags = of(GET_SIGNING_CERTIFICATES.toLong())
					packageManager.getPackageInfo(packageName, flags)
				} else {
					@Suppress("DEPRECATION")
					packageManager.getPackageInfo(packageName, GET_SIGNATURES)
				}

				packageInfo.versionName
			} catch (error: NameNotFoundException) {
				// Fallback if the app's package is not found (highly unlikely in this context)
				error.printStackTrace()
				null
			}
		}

	/**
	 * Gets the app's version code (a numeric identifier used for internal versioning).
	 *
	 * This is useful for comparing versions programmatically during updates.
	 * Typically, this is incremented with each release.
	 *
	 * @return The long version code as defined in build.gradle (e.g., 10003 for v1.0.3),
	 * or `0` if an error occurs.
	 *
	 * Handles SDK version compatibility:
	 * - Uses `longVersionCode` for all versions to avoid int overflow.
	 */
	@JvmStatic
	val versionCode: Long
		get() {
			val applicationContext = GlobalApplication.APP_INSTANCE
			val packageName = applicationContext.packageName
			return try {
				val packageManager = applicationContext.packageManager

				val packageInfo = if (SDK_INT >= TIRAMISU) {
					val flags = of(GET_SIGNING_CERTIFICATES.toLong())
					packageManager.getPackageInfo(packageName, flags)
				} else {
					@Suppress("DEPRECATION")
					packageManager.getPackageInfo(packageName, GET_SIGNATURES)
				}

				packageInfo.longVersionCode
			} catch (error: NameNotFoundException) {
				// Fallback value in case package info is unavailable
				error.printStackTrace()
				0
			}
		}

	/**
	 * Retrieves the SDK version of the device on which the app is currently running.
	 *
	 * @return The device's Android SDK version as an integer (e.g., 33 for Android 13).
	 * Useful for conditional logic that depends on OS features.
	 */
	@JvmStatic
	val deviceSDKVersion: Int
		get() = SDK_INT
}