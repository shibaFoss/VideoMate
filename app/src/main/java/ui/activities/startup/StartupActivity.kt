package ui.activities.startup

import android.widget.TextView
import core.bases.GlobalBaseActivity
import kotlinx.coroutines.delay
import libs.devices.AppVersionUtility
import libs.process.ThreadsUtility
import net.base.R
import ui.activities.mother.MotherActivity

/**
 * The startup/splash screen activity that displays while the app initializes.
 *
 * This activity serves as the visual entry point of the application, showing a splash screen
 * for a fixed duration before transitioning to the main application flow. It handles:
 * - Displaying branding/loading visuals
 * - Performing brief initialization delays
 * - Transitioning to the main activity
 * - Preventing back navigation during splash
 */
class StartupActivity : GlobalBaseActivity() {

    /**
     * Provides the layout resource for the splash screen.
     * @return The layout resource ID (R.layout.activity_startup)
     */
    override fun onRenderingLayout(): Int {
        return R.layout.activity_startup
    }

    /**
     * Called after the splash screen layout has been rendered.
     * Initiates the timed transition to the main activity after a 2-second delay.
     */
    override fun onAfterLayoutRendered() {
        showApkVersionInfo()
        ThreadsUtility.executeInBackground(codeBlock = {
            // Display splash screen for minimum 2 seconds
            delay(2000)

            ThreadsUtility.executeOnMain {
                // Transition to main activity
                openActivity(MotherActivity::class.java, true)
                finish()
            }
        })
    }

    /**
     * Empty implementation - no special resume handling needed for splash screen.
     */
    override fun onResumeActivity() {}

    /**
     * Empty implementation - no special pause handling needed for splash screen.
     */
    override fun onPauseActivity() {}

    /**
     * Handles back button press during splash screen.
     * Implements double-tap-to-exit behavior to prevent accidental exits.
     */
    override fun onBackPressActivity() {
        exitActivityOnDoubleBackPress()
    }

    /**
     * Cleans up activity references when destroyed to prevent memory leaks.
     */
    override fun onDestroyActivity() {
        clearWeakActivityReference()
    }


    /**
     * Displays the current app version on the splash screen.
     *
     * This retrieves the version name from [AppVersionUtility] and sets it to
     * the [TextView] with ID [R.id.txt_version_info] using a formatted string
     * from resources.
     */
    private fun showApkVersionInfo() {
        val versionName = AppVersionUtility.versionName
        findViewById<TextView>(R.id.txt_version_info).text = getString(
            R.string.text_startup_version_name,
            versionName
        )
    }
}