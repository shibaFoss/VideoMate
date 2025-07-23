package core.bases

import android.content.Context
import android.content.pm.PackageManager.GET_META_DATA
import android.content.pm.PackageManager.NameNotFoundException
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import core.bases.LanguageAwareApplication.Companion.languageAwareManager

abstract class LanguageAwareActivity : AppCompatActivity() {

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(languageAwareManager?.setLocale(context) ?: context)
    }

    private fun resetTitle() {
        try {
            val labelRes = packageManager.getActivityInfo(componentName, GET_META_DATA).labelRes
            if (labelRes != 0) setTitle(labelRes)
        } catch (error: NameNotFoundException) {
            error.printStackTrace()
        }
    }

    override fun applyOverrideConfiguration(configuration: Configuration?) {
        configuration?.let { safeConfig ->
            val uiMode = safeConfig.uiMode
            safeConfig.setTo(baseContext.resources.configuration)
            safeConfig.uiMode = uiMode
        }; super.applyOverrideConfiguration(configuration)
    }

    fun setNewLocale(language: String): Boolean {
        languageAwareManager?.setNewLocale(this, language) ?: return false
        recreate()
        return true
    }
}