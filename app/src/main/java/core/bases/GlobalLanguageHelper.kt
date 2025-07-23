package core.bases

import core.bases.GlobalApplication.Companion.APP_INSTANCE
import core.bases.GlobalApplication.Companion.globalDatabaseHelper
import libs.process.CommonTimeUtility.OnTaskFinishListener
import libs.process.CommonTimeUtility.delay
import libs.process.LocalizationHelper
import java.util.Locale

/**
 * Provides functionality to manage and apply app-wide UI language preferences.
 * Supports a wide range of Indian regional languages.
 */
open class GlobalLanguageHelper {

    companion object {
        // Supported language codes following ISO 639-1 standard
        const val ENGLISH = "en"       // English
        const val HINDI = "hi"         // हिंदी
        const val BENGALI = "bn"       // বাংলা
        const val TELUGU = "te"        // తెలుగు
        const val MARATHI = "mr"       // मराठी
        const val TAMIL = "ta"         // தமிழ்
        const val GUJARATI = "gu"      // ગુજરાતી
        const val KANNADA = "kn"       // ಕನ್ನಡ
        const val MALAYALAM = "ml"     // മലയാളം
        const val ORIYA = "or"         // ଓଡ଼ିଆ
        const val PUNJABI = "pa"       // ਪੰਜਾਬੀ
        const val ASSAMESE = "as"      // অসমীয়া
        const val SANSKRIT = "sa"      // संस्कृतम्
        const val NEPALI = "ne"        // नेपाली
    }

    /** Flag to track if the current activity should be restarted after language change */
    open var finishActivityOnResume = false

    /** Flag to trigger app termination after language change is applied */
    open var quitApplicationCommand = false

    /** List of supported languages and their display names for UI purposes */
    open val languagesList: List<Pair<String, String>> = listOf(
        ENGLISH to "English (Default)",
        HINDI to "Hindi (हिंदी)",
        BENGALI to "Bengali (বাংলা)",
        MARATHI to "Marathi (मराठी)",
        TELUGU to "Telugu (తెలుగు)",
        TAMIL to "Tamil (தமிழ்)",
        GUJARATI to "Gujarati (ગુજરાતી)",
        KANNADA to "Kannada (ಕನ್ನಡ)",
        MALAYALAM to "Malayalam (മലയാളം)",
        ORIYA to "Odia (ଓଡ଼ିଆ)",
        PUNJABI to "Punjabi (ਪੰਜਾਬੀ)",
        ASSAMESE to "Assamese (অসমীয়া)",
        SANSKRIT to "Sanskrit (संस्कृतम्)",
        NEPALI to "Nepali (नेपाली)"
    )

    /**
     * Applies the user-selected language to the application context and activity.
     * Triggers the provided [onComplete] callback after applying the locale.
     */
    open fun applyUserSelectedLanguage(
        baseActivity: GlobalBaseActivity?,
        onComplete: () -> Unit = {}
    ) {
        baseActivity?.getActivity()?.let { safeActivityRef ->
            finishActivityOnResume = false

            val globalDatabase = globalDatabaseHelper.getGlobalDatabase()
            val globalAppSettings = globalDatabase?.appSettings?.target
            val languageCode = globalAppSettings?.userSelectedAppUILanguage ?: ENGLISH

            val locale = Locale.forLanguageTag(languageCode)
            LanguageAwareManager(safeActivityRef).setNewLocale(safeActivityRef, languageCode)
            LanguageAwareManager(APP_INSTANCE).setNewLocale(APP_INSTANCE, languageCode)
            LocalizationHelper.setAppLocale(APP_INSTANCE, locale)
            onComplete()
        }
    }

    /**
     * Finishes the current activity and quits the application if a language change has occurred
     * and restart is required.
     */
    open fun finishActivityIfLanguageChanged(baseActivity: GlobalBaseActivity?) {
        baseActivity?.getActivity()?.let { safeActivityRef ->
            if (finishActivityOnResume) {
                safeActivityRef.finishAffinity()
                quitApplicationCommand = true
                delay(300, object : OnTaskFinishListener {
                    override fun afterDelay() = quitApplication(safeActivityRef)
                })
            }
        }
    }

    /**
     * Quits the entire application cleanly if the [quitApplicationCommand] flag is set.
     */
    private fun quitApplication(baseActivity: GlobalBaseActivity?) {
        baseActivity?.getActivity()?.let { safeActivityRef ->
            if (quitApplicationCommand) {
                quitApplicationCommand = false
                safeActivityRef.finishAffinity()
            }
        }
    }
}
