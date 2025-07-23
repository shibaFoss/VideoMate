package libs.ui.builders

import android.graphics.drawable.Drawable
import android.view.Gravity.NO_GRAVITY
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_OUTSIDE
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.View.MeasureSpec.UNSPECIFIED
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.core.content.res.ResourcesCompat
import core.bases.GlobalBaseActivity
import net.base.R
import java.lang.ref.WeakReference

/**
 * Builder class for creating and displaying a custom [PopupWindow] anchored to a view.
 *
 * Supports:
 * - Inflating layout from resource or using an existing [View]
 * - Touch handling for dismissal
 * - Optional immersive mode
 *
 * @param baseActivity The [GlobalBaseActivity] context used for layout inflation and resource access.
 * @param popupLayoutId Layout resource ID for the popup content. Ignored if [popupContentView] is provided.
 * @param popupContentView Optional pre-built [View] to use as popup content instead of inflating a layout.
 * @param popupAnchorView The view to anchor the popup window to when shown.
 */
class PopupMenuBuilder(
	private val baseActivity: GlobalBaseActivity?,
	private val popupLayoutId: Int = -1,
	private val popupContentView: View? = null,
	private val popupAnchorView: View
) {

	// Weak reference to avoid memory leaks
	private val safeActivityRef = WeakReference(baseActivity)

	// The core popup window object
	private val popupWindow = PopupWindow(safeActivityRef.get()?.getActivity())

	// Content view to be shown inside the popup
	private lateinit var popupLayout: View

	init {
		try {
			initializePopupContent() // Inflate or assign the content view
			validateContentView()    // Ensure valid content is provided
			setupPopupWindow()       // Setup dimensions, background, interaction handlers
		} catch (error: Exception) {
			error.printStackTrace()
			throw error
		}
	}

	/**
	 * Displays the popup window on screen, optionally hiding system UI for immersive experience.
	 *
	 * @param shouldHideStatusAndNavbar If true, sets immersive mode by hiding status and navigation bars.
	 */
	fun show(shouldHideStatusAndNavbar: Boolean = false) {
		try {
			if (popupWindow.isShowing) return
			if (shouldHideStatusAndNavbar) enableImmersiveMode()
			showPopupWindow()
		} catch (error: Exception) {
			error.printStackTrace()
		}
	}

	/**
	 * Dismisses the popup window if currently visible and the activity is in a valid state.
	 */
	fun close() {
		try {
			val activity = safeActivityRef.get()?.getActivity() ?: return
			if (activity.isValidForWindowManagement() && popupWindow.isShowing) {
				popupWindow.dismiss()
			}
		} catch (error: Exception) {
			error.printStackTrace()
		}
	}

	/**
	 * Returns the root content view of the popup window.
	 */
	fun getPopupView(): View = popupWindow.contentView

	/**
	 * Returns the internal [PopupWindow] instance for customization if needed.
	 */
	fun getPopupWindow(): PopupWindow = popupWindow

	/**
	 * Inflates the popup layout from resource or uses the provided [popupContentView].
	 *
	 * @throws IllegalArgumentException If neither layoutId nor view is provided.
	 */
	private fun initializePopupContent() {
		when {
			popupLayoutId != -1 -> {
				val inflater = LayoutInflater.from(safeActivityRef.get()?.getActivity())
				popupLayout = inflater.inflate(popupLayoutId, null, false)
			}
			popupContentView != null -> popupLayout = popupContentView
		}
	}

	/**
	 * Validates that a popup layout view has been initialized.
	 *
	 * @throws IllegalArgumentException if neither a layout ID nor a custom view is set.
	 */
	private fun validateContentView() {
		if (!::popupLayout.isInitialized) {
			throw IllegalArgumentException(
				"Must provide valid content via popupLayoutId or popupContentView"
			)
		}
	}

	/**
	 * Configures the [PopupWindow] properties like touch handling, size, and background.
	 */
	private fun setupPopupWindow() {
		popupWindow.apply {
			isTouchable = true
			isFocusable = true
			isOutsideTouchable = true

			setBackgroundDrawable(createTransparentBackground())
			configureTouchHandling()

			width = WindowManager.LayoutParams.WRAP_CONTENT
			height = WindowManager.LayoutParams.WRAP_CONTENT
			contentView = popupLayout
		}
	}

	/**
	 * Creates a transparent background drawable for the popup to allow outside touches.
	 */
	private fun createTransparentBackground(): Drawable? {
		return safeActivityRef.get()?.getActivity()?.let { ctx ->
			ResourcesCompat.getDrawable(
				ctx.resources,
				R.drawable.rd_transparent,
				ctx.theme
			)
		}
	}

	/**
	 * Configures how the popup window handles touch interactions:
	 * - Dismisses on outside touch.
	 * - Triggers click event on inside touch.
	 */
	private fun configureTouchHandling() {
		popupWindow.setTouchInterceptor { view, event ->
			when (event.action) {
				ACTION_UP -> view.performClick().let { false }
				ACTION_OUTSIDE -> popupWindow.dismiss().let { true }
				else -> false
			}
		}
	}

	/**
	 * Enables immersive full-screen mode on the popup content to hide navigation and status bars.
	 * Useful for modal UIs or overlay-style interactions.
	 */
	@Suppress("DEPRECATION")
	private fun enableImmersiveMode() {
		val s1 = SYSTEM_UI_FLAG_FULLSCREEN
		val s2 = SYSTEM_UI_FLAG_HIDE_NAVIGATION
		val s3 = SYSTEM_UI_FLAG_IMMERSIVE_STICKY
		popupWindow.contentView.systemUiVisibility = (s1 or s2 or s3)
	}

	/**
	 * Calculates popup position based on screen width and anchor view, then displays it.
	 */
	private fun showPopupWindow() {
		val anchorLocation = IntArray(2)
		popupAnchorView.getLocationOnScreen(anchorLocation)
		val anchorY = anchorLocation[1]

		val endMarginInPx = popupLayout.resources.getDimensionPixelSize(R.dimen._10)
		val displayMetrics = popupLayout.resources.displayMetrics
		val screenWidth = displayMetrics.widthPixels

		popupLayout.measure(UNSPECIFIED, UNSPECIFIED)
		val popupWidth = popupLayout.measuredWidth

		val xOffset = screenWidth - popupWidth - endMarginInPx

		popupWindow.showAtLocation(popupAnchorView, NO_GRAVITY, xOffset, anchorY)
	}

	/**
	 * Extension function to check if the [GlobalBaseActivity]'s window is in a valid state
	 * to show or dismiss the popup (not finishing or destroyed).
	 */
	private fun GlobalBaseActivity?.isValidForWindowManagement(): Boolean {
		this?.getActivity()?.let { activity ->
			return !activity.isFinishing && !activity.isDestroyed
		} ?: run { return false }
	}
}