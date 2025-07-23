package libs.process

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * A utility object that provides functionality for dynamically updating
 * the application's locale (language and region) at runtime.
 *
 * This class enables changing the language of the app without restarting the entire app,
 * and helps in retrieving localized strings according to the selected locale.
 *
 * Example usage:
 * ```
 * // Set application language to French
 * LocalizationHelper.setAppLocale(context, Locale.FRENCH)
 *
 * // Get a localized string
 * val welcomeText = LocalizationHelper.getLocalizedString(context, R.string.welcome)
 * ```
 */
object LocalizationHelper {

    /**
     * Holds the currently set [Locale] for the application.
     * If this is `null`, the application will use the system's default locale.
     *
     * This is used internally to ensure that string retrieval and configuration
     * updates are consistent with the desired language setting.
     */
    private var currentLocale: Locale? = null

    /**
     * Sets the application's locale to the specified [locale].
     *
     * This method updates the internal [currentLocale] and reconfigures the provided [context]
     * to reflect the new language and layout direction settings.
     * It should be called before initializing UI components to apply the language correctly.
     *
     * @param context The [Context] to which the locale should be applied.
     * @param locale The [Locale] representing the language and region to use.
     */
    @JvmStatic
    fun setAppLocale(context: Context, locale: Locale) {
        currentLocale = locale
        updateResources(context, locale)
    }

    /**
     * Retrieves a localized string resource based on the currently set locale.
     *
     * If a custom locale has been set using [setAppLocale], this method will return
     * the string in that locale. Otherwise, it falls back to the system's default locale.
     *
     * This is particularly useful for retrieving strings for display in UI components
     * after a locale change, without needing to recreate the whole activity or context.
     *
     * @param context The context used to access the localized resources.
     * @param resId The resource ID of the string to retrieve.
     * @return The localized string based on the current locale configuration.
     */
    @JvmStatic
    fun getLocalizedString(context: Context, resId: Int): String {
        return if (currentLocale != null) {
            val config = Configuration(context.resources.configuration)
            config.setLocale(currentLocale)
            context.createConfigurationContext(config).resources.getString(resId)
        } else {
            context.resources.getString(resId)
        }
    }

    /**
     * Updates the configuration and resources of the provided [context] to use the specified [locale].
     *
     * This method creates a new [Configuration] object, sets the desired locale and layout direction,
     * and returns a new [Context] that reflects these changes.
     *
     * Use this method when you need a localized context (e.g., for inflating views, loading layouts,
     * or retrieving strings in a specific language).
     *
     * @param context The base context whose configuration is to be modified.
     * @param locale The new [Locale] to apply for language and layout direction.
     * @return A new [Context] with the updated locale configuration applied.
     */
    @JvmStatic
    fun updateResources(context: Context, locale: Locale): Context {
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }
}
