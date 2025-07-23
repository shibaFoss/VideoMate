@file:Suppress("DEPRECATION")

package core.bases

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.core.content.edit
import java.util.Locale

class LanguageAwareManager(context: Context?) {

    private val preferences: SharedPreferences = getDefaultSharedPreferences(context)

    fun setLocale(context: Context): Context {
        return updateResources(context, language)
    }

    fun setNewLocale(context: Context, language: String): Context {
        persistLanguage(language)
        return updateResources(context, language)
    }

    private val language: String?
        get() = preferences.getString(LANGUAGE_KEY, LANGUAGE_ENGLISH)

    @SuppressLint("ApplySharedPref")
    private fun persistLanguage(language: String) {
        preferences.edit(commit = true) { putString(LANGUAGE_KEY, language) }
    }

    private fun updateResources(context: Context, language: String?): Context {
        if (language == null) return context

        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    companion object {
        const val LANGUAGE_ENGLISH = "en"
        private const val LANGUAGE_KEY = "language_key"

        fun getLocale(res: Resources): Locale {
            return res.configuration.locales[0]
        }
    }
}