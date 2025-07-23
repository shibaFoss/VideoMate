package core.bases

import android.app.Application
import android.content.Context
import android.content.res.Configuration

open class LanguageAwareApplication : Application() {
	
	override fun attachBaseContext(base: Context) {
		languageAwareManager = LanguageAwareManager(base)
		super.attachBaseContext(languageAwareManager?.setLocale(base) ?: base)
	}
	
	override fun onConfigurationChanged(newConfig: Configuration) {
		super.onConfigurationChanged(newConfig)
		languageAwareManager?.setLocale(this)
	}
	
	companion object {
		var languageAwareManager: LanguageAwareManager? = null
			private set
	}
}