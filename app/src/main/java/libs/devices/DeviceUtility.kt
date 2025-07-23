package libs.devices

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Context.TELEPHONY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.os.Build
import android.telephony.TelephonyManager
import core.bases.GlobalApplication
import libs.texts.CommonTextUtility.capitalizeFirstLetter
import libs.texts.CommonTextUtility.getText
import net.base.R
import java.lang.ref.WeakReference
import java.util.Locale.getDefault

/**
 * DeviceUtility is a helper object providing device-specific information related to:
 * - Screen density and display characteristics
 * - Manufacturer and model information
 * - Network connectivity status
 * - Device locale or country code
 *
 * This class is useful for analytics, UI customization based on screen types,
 * network condition checks, or logging environment info for debugging or telemetry.
 */
object DeviceUtility {

	/**
	 * Retrieves the raw screen density of the device as a float value.
	 *
	 * This can be used when you need the actual scale factor of the screen (e.g., 2.0 for xhdpi),
	 * helpful for performing scaling or layout adjustments manually.
	 *
	 * @param context Context required to access display metrics.
	 *                Uses WeakReference to avoid memory leaks.
	 * @return Density as a float (e.g., 1.0, 2.0), or 0.0f if context is null.
	 */
	@JvmStatic
	fun getDeviceScreenDensity(context: Context?): Float {
		WeakReference(context).get()?.let { safeRef ->
			val displayMetrics = safeRef.resources.displayMetrics
			return displayMetrics.density
		} ?: run { return 0.0f }
	}

	/**
	 * Returns the screen density in a human-readable format like "hdpi", "xhdpi", etc.
	 *
	 * Android categorizes screen densities into buckets. This method converts the raw density
	 * value into a string identifier, which can be used for logging, analytics, or UI decisions.
	 *
	 * @param context Context to access display metrics (wrapped in WeakReference).
	 * @return One of: "ldpi", "mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi", or an empty string if context is null.
	 */
	@JvmStatic
	fun getDeviceScreenDensityInFormat(context: Context?): String {
		WeakReference(context).get()?.let { safeRef ->
			val density = safeRef.resources.displayMetrics.density
			return when {
				density >= 4.0 -> "xxxhdpi"
				density >= 3.0 -> "xxhdpi"
				density >= 2.0 -> "xhdpi"
				density >= 1.5 -> "hdpi"
				density >= 1.0 -> "mdpi"
				else -> "ldpi"
			}
		} ?: run { return "" }
	}

	/**
	 * Returns the formatted manufacturer and model name of the device.
	 *
	 * For example: "Samsung Galaxy S23" or "OnePlus Nord 2".
	 * Ensures the output is properly capitalized and avoids duplication when the model already includes the manufacturer.
	 *
	 * @return A readable device name string, or null if not available.
	 */
	@JvmStatic
	fun getDeviceManufactureModelName(): String? {
		val manufacturer = Build.MANUFACTURER
		val model = Build.MODEL
		return if (model.startsWith(manufacturer, ignoreCase = true)) {
			capitalizeFirstLetter(model)
		} else {
			capitalizeFirstLetter("$manufacturer $model")
		}
	}

	/**
	 * Checks if the device has an active internet connection.
	 *
	 * This checks across multiple transport types (Wi-Fi, Cellular, Ethernet, Bluetooth)
	 * using Android's modern `NetworkCapabilities` API.
	 *
	 * @return `true` if connected to any internet-capable network, `false` otherwise.
	 */
	@JvmStatic
	fun isDeviceConnectedToInternet(): Boolean {
		val applicationContext = GlobalApplication.APP_INSTANCE
		val connectivityManager =
			applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

		val network = connectivityManager.activeNetwork ?: return false
		val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

		return capabilities.hasTransport(TRANSPORT_WIFI) ||
				capabilities.hasTransport(TRANSPORT_CELLULAR) ||
				capabilities.hasTransport(TRANSPORT_ETHERNET) ||
				capabilities.hasTransport(TRANSPORT_BLUETOOTH)
	}

	/**
	 * Attempts to determine the user's current country.
	 *
	 * Primary source: Device locale (system language/country).
	 * Fallback: SIM country information via TelephonyManager if locale is unavailable.
	 * Final fallback: Returns "Unknown".
	 *
	 * This is useful for region-specific features, content filtering, or analytics.
	 *
	 * @return ISO country code in uppercase (e.g., "IN", "US"), or "Unknown".
	 */
	@JvmStatic
	fun getDeviceUserCountry(): String {
		val localeCountry = getDefault().country
		if (localeCountry.isNotEmpty()) return localeCountry

		val applicationContext = GlobalApplication.APP_INSTANCE
		val telephonyManager =
			applicationContext.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
		val simCountry = telephonyManager.simCountryIso

		return if (simCountry.isNotEmpty()) simCountry.uppercase(getDefault())
		else getText(R.string.unknown)
	}
}