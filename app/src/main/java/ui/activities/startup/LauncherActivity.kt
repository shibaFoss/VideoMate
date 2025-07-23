package ui.activities.startup

import android.content.Intent
import core.bases.GlobalApplication
import core.bases.GlobalApplicationKeys.FROM_CRASH_HANDLER
import core.bases.GlobalApplicationKeys.WHERE_DID_YOU_COME_FROM
import core.bases.GlobalBaseActivity
import libs.ui.AnimatorUtility.animActivityFade
import libs.ui.builders.ToastViewBuilder
import ui.activities.mother.MotherActivity
import ui.activities.others.UserFeedbackActivity
import java.lang.ref.WeakReference

/**
 * The initial activity launched when the application starts.
 *
 * This activity serves as the entry point and decides which screen to show first:
 * - User feedback screen if the app crashed previously
 * - Normal opening screen otherwise
 *
 * Manages weak references to prevent memory leaks and handles proper activity transitions.
 */
class LauncherActivity : GlobalBaseActivity() {

    // Weak reference to self to prevent memory leaks
    private var weakSelfReference: WeakReference<LauncherActivity>? = null

    // Strong reference that's cleared in onDestroy
    private var safeLauncherActivityReference: LauncherActivity? = null

    /**
     * Specifies the layout resource for this activity.
     * @return -1 indicates no layout as this is a transitional activity
     */
    override fun onRenderingLayout(): Int {
        return -1
    }

    /**
     * Called after the layout has been rendered (though none exists in this case).
     * Initializes references and determines which activity to launch next.
     */
    override fun onAfterLayoutRendered() {
        // Set up weak reference management
        weakSelfReference = WeakReference(this)
        safeLauncherActivityReference = weakSelfReference?.get()

        // Check if we need to show crash feedback
        val databaseHelper = GlobalApplication.globalDatabaseHelper
        val settingDatabase = databaseHelper.getGlobalAppSettings()

        if (settingDatabase != null && settingDatabase.hasAppCrashedRecently) {
            launchFeedbackActivity()
        } else {
            if (settingDatabase?.isPremiumUser == true) {
                launchMotherActivity()
            } else {
                launchStartupActivity()
            }
        }
    }

    /**
     * Empty implementation as this transitional activity doesn't need resume handling.
     */
    override fun onResumeActivity() {}

    /**
     * Empty implementation as this transitional activity doesn't need pause handling.
     */
    override fun onPauseActivity() {}

    /**
     * Handles back press with double-tap-to-exit behavior.
     */
    override fun onBackPressActivity() {
        exitActivityOnDoubleBackPress()
    }

    /**
     * Cleans up references when activity is destroyed to prevent memory leaks.
     */
    override fun onDestroyActivity() {
        weakSelfReference?.clear()
        safeLauncherActivityReference = null
        clearWeakActivityReference()
    }

    /**
     * Launches the user feedback activity when app crashed previously.
     * Resets the crash flag and passes appropriate intent extras.
     */
    private fun launchFeedbackActivity() {
        safeLauncherActivityReference?.let { safeActivityContextRef ->
            // Reset crash flag
            val databaseHelper = GlobalApplication.globalDatabaseHelper
            val settingDatabase = databaseHelper.getGlobalAppSettings()
            settingDatabase?.apply { hasAppCrashedRecently = false }
            databaseHelper.saveGlobalData(settings = settingDatabase)

            // Launch feedback activity
            Intent(safeActivityContextRef, UserFeedbackActivity::class.java).apply {
                flags = getSingleTopIntentFlags()
                putExtra(WHERE_DID_YOU_COME_FROM, FROM_CRASH_HANDLER)
                startActivity(this)
                finish()
                animActivityFade(safeActivityContextRef)
            }
        }
    }

    /**
     * Launches the normal startup activity (main entry point).
     */
    private fun launchStartupActivity() {
        safeLauncherActivityReference?.let { safeActivityContextRef ->
            Intent(safeActivityContextRef, StartupActivity::class.java).apply {
                flags = getSingleTopIntentFlags()
                startActivity(this)
                finish()
                animActivityFade(getActivity())
            }
        }
    }

    /**
     * Launches the mother activity straight-up, no-more startup activity.
     */
    private fun launchMotherActivity() {
        safeLauncherActivityReference?.let { _ ->
            openActivity(MotherActivity::class.java, shouldAnimate = false)
            finish()
        }
    }
}