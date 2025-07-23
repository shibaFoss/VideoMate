@file:Suppress("DEPRECATION")

package core.bases

import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.MotionEvent
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat.getColor
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.anggrayudi.storage.SimpleStorageHelper
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.PermissionX.isGranted
import core.bases.GlobalApplication.Companion.globalLanguageHelper
import libs.process.CommonTimeUtility
import libs.process.CommonTimeUtility.delay
import libs.ui.AnimatorUtility.animActivityFade
import libs.ui.AnimatorUtility.animActivitySwipeRight
import libs.ui.builders.ToastViewBuilder.Companion.showToast
import net.base.R
import java.lang.Thread.setDefaultUncaughtExceptionHandler
import java.lang.ref.WeakReference
import java.util.TimeZone
import kotlin.system.exitProcess

abstract class GlobalBaseActivity : LanguageAwareActivity() {

    private var weakGlobalBaseActivityRef: WeakReference<GlobalBaseActivity>? = null
    private var safeGlobalBaseActivityRef: GlobalBaseActivity? = null

    private var isActivityRunning = false
    private var isUserPermissionCheckingActive = false
    private var isBackButtonEventFired = 0

    private var storageHelperLibrary: SimpleStorageHelper? = null
    private var permissionsResultListener: PermissionsResultListener? = null
    private val deviceVibrator: Vibrator? by lazy { getVibratorServices() }

    abstract fun onRenderingLayout(): Int
    abstract fun onAfterLayoutRendered()
    abstract fun onResumeActivity()
    abstract fun onBackPressActivity()
    abstract fun onPauseActivity()
    abstract fun onDestroyActivity()

    override fun onStart() {
        super.onStart()
        isActivityRunning = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        weakGlobalBaseActivityRef = WeakReference(this)
        safeGlobalBaseActivityRef = weakGlobalBaseActivityRef?.get()

        safeGlobalBaseActivityRef?.let { safeActivityRef ->
            val globalCrashHandler = GlobalCrashHandler(safeActivityRef.application)
            setDefaultUncaughtExceptionHandler(globalCrashHandler)

            setLightSystemBarTheme()

            storageHelperLibrary = SimpleStorageHelper(safeActivityRef)
            globalLanguageHelper.applyUserSelectedLanguage(safeActivityRef)

            WeakReference(object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() = onBackPressActivity()
            }).get()?.let { onBackPressedDispatcher.addCallback(safeActivityRef, it) }

            if (onRenderingLayout() > -1) setContentView(onRenderingLayout())
            onAfterLayoutRendered()
        }
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {
        if (motionEvent.action == MotionEvent.ACTION_DOWN) {
            val focusedView = currentFocus
            if (focusedView is EditText) {
                val outRect = Rect()
                focusedView.getGlobalVisibleRect(outRect)

                if (!outRect.contains(motionEvent.rawX.toInt(), motionEvent.rawY.toInt())) {
                    focusedView.clearFocus()
                    val service = getSystemService(INPUT_METHOD_SERVICE)
                    val imm = service as InputMethodManager
                    imm.hideSoftInputFromWindow(focusedView.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(motionEvent)
    }

    override fun onResume() {
        super.onResume()
        if (safeGlobalBaseActivityRef == null) {
            weakGlobalBaseActivityRef = WeakReference(this)
            safeGlobalBaseActivityRef = weakGlobalBaseActivityRef?.get()
        }

        safeGlobalBaseActivityRef?.let { safeActivityRef ->
            isActivityRunning = true
            onResumeActivity()
            globalLanguageHelper.finishActivityIfLanguageChanged(safeActivityRef)
        }
    }

    override fun onPause() {
        super.onPause()
        isActivityRunning = false
        onPauseActivity()
    }

    override fun onDestroy() {
        super.onDestroy()
        isActivityRunning = false
        deviceVibrator?.cancel()
        onDestroyActivity()
    }

    fun getWeakReference(): GlobalBaseActivity? {
        return weakGlobalBaseActivityRef?.get()
    }

    fun isActivityRunning(): Boolean {
        return isActivityRunning
    }

    fun getStorageHelper(): SimpleStorageHelper? {
        return storageHelperLibrary
    }

    fun setPermissionResultListener(listener: PermissionsResultListener) {
        permissionsResultListener = listener
    }

    fun getPermissionResultListener(): PermissionsResultListener? {
        return permissionsResultListener
    }

    fun getVibratorServices(): Vibrator? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val systemService = getSystemService(VIBRATOR_MANAGER_SERVICE)
                val vibratorManager = systemService as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                getSystemService(VIBRATOR_SERVICE) as Vibrator
            }
        } catch (error: Exception) {
            error.printStackTrace()
            null
        }
    }

    fun getRequiredPermissionsBySDKVersion(): ArrayList<String> {
        val permissions: ArrayList<String> = ArrayList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            permissions.add(POST_NOTIFICATIONS)
        else permissions.add(WRITE_EXTERNAL_STORAGE)
        return permissions
    }

    fun setSystemBarsColors(
        statusBarColorResId: Int,
        navigationBarColorResId: Int,
        isLightStatusBar: Boolean,
        isLightNavigationBar: Boolean,
    ) {
        val activityWindow = window
        activityWindow.statusBarColor = getColor(this, statusBarColorResId)
        activityWindow.navigationBarColor = getColor(this, navigationBarColorResId)
        val decorView = activityWindow.decorView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val insetsController = activityWindow.insetsController
            insetsController?.setSystemBarsAppearance(
                if (isLightStatusBar) APPEARANCE_LIGHT_STATUS_BARS else 0,
                APPEARANCE_LIGHT_STATUS_BARS
            )

            insetsController?.setSystemBarsAppearance(
                if (isLightNavigationBar) APPEARANCE_LIGHT_NAVIGATION_BARS else 0,
                APPEARANCE_LIGHT_NAVIGATION_BARS
            )
        } else {
            if (isLightStatusBar) decorView.systemUiVisibility =
                decorView.systemUiVisibility or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            else decorView.systemUiVisibility =
                decorView.systemUiVisibility and SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()

            if (isLightNavigationBar) decorView.systemUiVisibility =
                decorView.systemUiVisibility or SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            else decorView.systemUiVisibility =
                decorView.systemUiVisibility and SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
    }

    fun setLightSystemBarTheme() {
        setSystemBarsColors(
            statusBarColorResId = R.color.pure_white,
            navigationBarColorResId = R.color.pure_white,
            isLightStatusBar = true,
            isLightNavigationBar = true
        )
    }

    fun setDarkSystemBarTheme() {
        setSystemBarsColors(
            statusBarColorResId = R.color.color_primary,
            navigationBarColorResId = R.color.color_primary,
            isLightStatusBar = false,
            isLightNavigationBar = false
        )
    }

    fun setEdgeToEdgeFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.systemBars())
                it.systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or SYSTEM_UI_FLAG_FULLSCREEN
                    or SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }

        WindowCompat.getInsetsController(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            val barsBySwipe = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.systemBarsBehavior = barsBySwipe
        }
    }

    fun disableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.show(WindowInsets.Type.systemBars())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_DEFAULT
                }
            }
        } else {
            val flags = (SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            window.decorView.systemUiVisibility = flags
        }

        WindowCompat.getInsetsController(window, window.decorView).let { controller ->
            controller.show(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }
    }

    fun setEdgeToEdgeCustomCutoutColor(@ColorInt color: Int) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = color
        window.navigationBarColor = color
        val shortEdges = LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        window.attributes.layoutInDisplayCutoutMode = shortEdges
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setBackgroundDrawable(color.toDrawable())
        }
    }

    fun exitActivityOnDoubleBackPress() {
        if (isBackButtonEventFired == 0) {
            showToast(msgId = R.string.press_back_button_to_exit)
            isBackButtonEventFired = 1
            delay(2000, object : CommonTimeUtility.OnTaskFinishListener {
                override fun afterDelay() {
                    isBackButtonEventFired = 0
                }
            })
        } else if (isBackButtonEventFired == 1) {
            isBackButtonEventFired = 0
            closeActivityWithSwipeAnimation(shouldAnimate = true)
        }
    }

    fun openActivity(activity: Class<*>, shouldAnimate: Boolean) {
        getActivity()?.let { safeActivityRef ->
            val intent = Intent(safeActivityRef, activity)
            intent.flags = FLAG_ACTIVITY_CLEAR_TOP or FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            if (shouldAnimate) {
                animActivityFade(safeActivityRef)
            }
        }
    }

    fun closeActivityWithSwipeAnimation(shouldAnimate: Boolean) {
        getActivity()?.apply {
            finish()
            if (shouldAnimate) animActivitySwipeRight(this)
        }
    }

    fun closeActivityWithFadeAnimation(shouldAnimate: Boolean) {
        getActivity()?.apply {
            finish()
            if (shouldAnimate) animActivityFade(this)
        }
    }

    fun forceQuitApplication() {
        Process.killProcess(Process.myPid())
        exitProcess(0)
    }

    fun openAppInfoSetting() {
        val packageName = this.packageName
        val uri = "package:$packageName".toUri()
        val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS, uri)
        startActivity(intent)
    }

    fun openApplicationInPlayStore() {
        try {
            val uri = "market://details?id=$packageName"
            startActivity(Intent(Intent.ACTION_VIEW, uri.toUri()))
        } catch (error: Exception) {
            error.printStackTrace()
            showToast(msgId = R.string.google_play_store_inst_installed)
        }
    }

    fun getTimeZoneId(): String {
        return TimeZone.getDefault().id
    }

    fun getActivity(): GlobalBaseActivity? {
        return safeGlobalBaseActivityRef
    }

    open fun clearWeakActivityReference() {
        weakGlobalBaseActivityRef?.clear()
        safeGlobalBaseActivityRef = null
    }

    fun doSomeVibration(timeInMillis: Int) {
        if (deviceVibrator?.hasVibrator() == true) {
            deviceVibrator?.vibrate(
                VibrationEffect.createOneShot(
                    timeInMillis.toLong(),
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        }
    }

    fun getSingleTopIntentFlags(): Int {
        return FLAG_ACTIVITY_CLEAR_TOP or FLAG_ACTIVITY_SINGLE_TOP
    }

    fun launchPermissionRequest(permissions: ArrayList<String>) {
        safeGlobalBaseActivityRef?.let { safeActivityRef ->
            PermissionX.init(safeActivityRef).permissions(permissions)
                .onExplainRequestReason { callback, deniedList ->
                    callback.showRequestReasonDialog(
                        permissions = deniedList,
                        message = getString(R.string.allow_the_permissions),
                        positiveText = getString(R.string.allow_now)
                    )
                }.onForwardToSettings { scope, deniedList ->
                    scope.showForwardToSettingsDialog(
                        permissions = deniedList,
                        message = getString(R.string.allow_permission_in_setting),
                        positiveText = getString(R.string.allow_now)
                    )
                }.request { allGranted, grantedList, deniedList ->
                    isUserPermissionCheckingActive = false
                    permissionsResultListener?.onPermissionResultFound(
                        isGranted = allGranted,
                        grantedLs = grantedList,
                        deniedLs = deniedList
                    )
                }
            isUserPermissionCheckingActive = true
        }
    }

    fun requestForPermissionIfRequired() {
        safeGlobalBaseActivityRef?.let { safeActivityRef ->
            if (!isUserPermissionCheckingActive) {
                delay(1000, object : CommonTimeUtility.OnTaskFinishListener {
                    override fun afterDelay() {
                        val permissions = getRequiredPermissionsBySDKVersion()
                        if (permissions.isNotEmpty() && !isGranted(safeActivityRef, permissions[0]))
                            launchPermissionRequest(getRequiredPermissionsBySDKVersion()) else
                            permissionsResultListener?.onPermissionResultFound(
                                true,
                                permissions,
                                null
                            )
                    }
                })
            }
        }
    }

    interface PermissionsResultListener {
        fun onPermissionResultFound(
            isGranted: Boolean,
            grantedLs: List<String>?,
            deniedLs: List<String>?
        )
    }
}