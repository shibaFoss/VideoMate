package libs.ui.builders

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import core.bases.GlobalBaseActivity
import libs.process.AsyncJobsUtility
import libs.ui.ViewUtility.animateFadInOutAnim
import libs.ui.ViewUtility.showView
import net.base.R

/**
 * A utility class for displaying a custom waiting/progress dialog using [CustomDialogBuilder].
 *
 * This dialog displays:
 * - A loading message.
 * - A Lottie animation.
 * - An optional OK button.
 *
 * @param baseActivity The [GlobalBaseActivity] context in which the dialog will be shown.
 * @param loadingMessage The message to be shown in the dialog.
 * @param shouldHideOkayButton If true, hides the OK button.
 * @param isCancelable Determines whether the dialog can be canceled by user interaction.
 * @param dialogCancelListener Optional listener to be notified when the dialog is canceled.
 */
class ProgressDialogBuilder(
    private val baseActivity: GlobalBaseActivity?,
    private val loadingMessage: String,
    private val cancelButtonText: String = "",
    private val shouldHideOkayButton: Boolean = false,
    private val isCancelable: Boolean = true,
    private val customLottieRawResId: Int? = null,
    private val dialogCancelListener: CustomDialogBuilder.OnCancelListener? = null
) {

    /**
     * Lazily initialized instance of [CustomDialogBuilder] that builds the dialog.
     */
    private val dialogBuilder: CustomDialogBuilder? by lazy {
        CustomDialogBuilder(baseActivity).apply { initializeDialogComponents() }
    }

    /**
     * Initializes the dialog components, sets layout, and applies customization.
     */
    private fun CustomDialogBuilder.initializeDialogComponents() {
        setView(R.layout.dialog_simple_progress)
        setCancelable(isCancelable)
        configureCancelListener()
        configureDialogContent()
    }

    /**
     * Sets the cancel listener for the dialog.
     * If a custom listener is provided, it is called on cancellation.
     * Otherwise, the dialog is simply dismissed.
     */
    private fun CustomDialogBuilder.configureCancelListener() {
        dialog.setOnCancelListener { dialog ->
            dialogCancelListener?.onCancel(dialog) ?: dialog?.cancel()
        }
    }

    /**
     * Configures the content of the dialog such as message text, animation, and OK button visibility.
     */
    private fun CustomDialogBuilder.configureDialogContent() {
        view.apply {
            // Set loading message with fade animation
            findViewById<TextView>(R.id.txt_progress_info).let {
                it.text = loadingMessage
                animateFadInOutAnim(it)
            }

            // Set cancel button text with custom text given
            findViewById<TextView>(R.id.btn_dialog_positive).let {
                if (cancelButtonText.isNotEmpty()) {
                    it.text = cancelButtonText
                }
            }

            // Configure OK button behavior
            findViewById<View>(R.id.btn_dialog_positive_container).apply {
                setOnClickListener { close() }
                visibility = if (shouldHideOkayButton) GONE else VISIBLE
            }

            // Show loading animation
            if (customLottieRawResId != null) {
                findViewById<LottieAnimationView>(R.id.img_progress_circle).apply {
                    setAnimation(customLottieRawResId)
                    showView(targetView = this, shouldAnimate = true, animTimeout = 400)
                }
            }
        }
    }

    /**
     * Return the [CustomDialogBuilder] of this popup dialog builder.
     */
    fun getCustomDialogBuilder(): CustomDialogBuilder? {
        return this.dialogBuilder
    }

    /**
     * Dynamically updates the message shown in the dialog.
     * This is executed on the main thread using [AsyncJobsUtility].
     *
     * @param newLoadingMessage The new message to be shown. No-op if null or empty.
     */
    fun changeLoadingMessage(newLoadingMessage: String?) {
        if (newLoadingMessage.isNullOrEmpty()) return
        AsyncJobsUtility.executeOnMainThread {
            dialogBuilder?.let { dialogBuilder ->
                dialogBuilder.view.apply {
                    val txtProgressInfo = findViewById<TextView>(R.id.txt_progress_info)
                    txtProgressInfo.text = newLoadingMessage
                }
            }
        }
    }

    /**
     * Displays the dialog if it is not already visible.
     *
     * @throws IllegalStateException If the dialog context is not available.
     */
    fun show() {
        dialogBuilder?.let { dialogBuilder ->
            if (!dialogBuilder.isShowing) {
                dialogBuilder.show()
            }
        } ?: run {
            throw IllegalStateException("Dialog context unavailable")
        }
    }

    /**
     * Closes the dialog if it is currently visible.
     *
     * @return true if the dialog was closed, false otherwise.
     */
    fun close(): Boolean {
        return dialogBuilder?.let {
            if (it.isShowing) {
                it.close()
                true
            } else false
        } ?: false
    }
}