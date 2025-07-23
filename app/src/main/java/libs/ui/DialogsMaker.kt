package libs.ui

import android.content.Context
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.widget.RelativeLayout
import android.widget.TextView
import core.bases.GlobalApplication.Companion.APP_INSTANCE
import core.bases.GlobalBaseActivity
import libs.texts.CommonTextUtility.getText
import libs.ui.builders.CustomDialogBuilder
import net.base.R
import java.lang.ref.WeakReference

/**
 * Utility object to create and display customizable dialog boxes across the application.
 * This helps in showing standard message dialogs with ease and consistent styling.
 */
object DialogsMaker {

	/**
	 * Global application context retrieved from the singleton instance of the application.
	 */
	private val applicationContext: Context
		get() = APP_INSTANCE

	/**
	 * Displays a customizable message dialog using default or user-defined parameters.
	 *
	 * @param baseActivityInf The calling activity instance to bind the dialog to.
	 * @param isCancelable Determines whether the dialog can be cancelled by the user.
	 * @param isTitleVisible Determines whether the title should be shown.
	 * @param titleText The title to be displayed in the dialog.
	 * @param messageTxt The message content to be displayed.
	 * @param positiveButtonText Label for the positive action button.
	 * @param negativeButtonText Label for the negative action button.
	 * @param isNegativeButtonVisible Whether to show the negative action button.
	 * @param onPositiveButtonClickListener Callback for the positive button click.
	 * @param onNegativeButtonClickListener Callback for the negative button click.
	 * @param messageTextViewCustomize Lambda to allow runtime customization of the message TextView.
	 * @param titleTextViewCustomize Lambda to allow runtime customization of the title TextView.
	 * @param dialogBuilderCustomize Lambda to allow customization of the CustomDialogBuilder instance.
	 * @param positiveButtonTextCustomize Lambda for customizing the positive button TextView.
	 * @param negativeButtonTextCustomize Lambda for customizing the negative button TextView.
	 * @param positiveButtonContainerCustomize Lambda for customizing the positive button container layout.
	 * @param negativeButtonContainerCustomize Lambda for customizing the negative button container layout.
	 *
	 * @return The CustomDialogBuilder instance used to show the dialog, or null if the activity is invalid.
	 */
	@JvmStatic
	fun showMessageDialog(
		baseActivityInf: GlobalBaseActivity?,
		isCancelable: Boolean = true,
		isTitleVisible: Boolean = false,
		titleText: CharSequence = getText(R.string.title_goes_here),
		messageTxt: CharSequence = applicationContext.getString(R.string.message_goes_here),
		positiveButtonText: CharSequence = applicationContext.getString(R.string.okay),
		negativeButtonText: CharSequence = applicationContext.getString(R.string.cancel),
		isNegativeButtonVisible: Boolean = true,
		onPositiveButtonClickListener: OnClickListener? = null,
		onNegativeButtonClickListener: OnClickListener? = null,
		messageTextViewCustomize: ((TextView) -> Unit)? = {},
		titleTextViewCustomize: ((TextView) -> Unit)? = {},
		dialogBuilderCustomize: ((CustomDialogBuilder) -> Unit)? = {},
		positiveButtonTextCustomize: ((TextView) -> Unit)? = {},
		negativeButtonTextCustomize: ((TextView) -> Unit)? = {},
		positiveButtonContainerCustomize: ((RelativeLayout) -> Unit)? = {},
		negativeButtonContainerCustomize: ((RelativeLayout) -> Unit)? = {}
	): CustomDialogBuilder? {
		val dialogBuilder = getMessageDialog(
			baseActivity = baseActivityInf,
			isCancelable = isCancelable,
			isTitleVisible = isTitleVisible,
			titleText = titleText,
			messageTxt = messageTxt,
			positiveButtonText = positiveButtonText,
			negativeButtonText = negativeButtonText,
			isNegativeButtonVisible = isNegativeButtonVisible,
			onPositiveButtonClickListener = onPositiveButtonClickListener,
			onNegativeButtonClickListener = onNegativeButtonClickListener,
			messageTextViewCustomize = messageTextViewCustomize,
			titleTextViewCustomize = titleTextViewCustomize,
			dialogBuilderCustomize = dialogBuilderCustomize,
			positiveButtonTextCustomize = positiveButtonTextCustomize,
			positiveButtonContainerCustomize = positiveButtonContainerCustomize,
			negativeButtonTextCustomize = negativeButtonTextCustomize,
			negativeButtonContainerCustomize = negativeButtonContainerCustomize
		)
		dialogBuilder?.show()
		return dialogBuilder
	}

	/**
	 * Constructs a dialog but does not show it. Provides full control over customization.
	 *
	 * @param baseActivity Activity from which the dialog is called.
	 * @param isCancelable Whether the dialog can be dismissed by tapping outside.
	 * @param isTitleVisible Whether to display the title view.
	 * @param titleText The title string to be shown.
	 * @param messageTxt The message body text.
	 * @param positiveButtonText Text for the confirmation button.
	 * @param negativeButtonText Text for the cancellation button.
	 * @param isNegativeButtonVisible Whether to display the cancel button.
	 * @param onPositiveButtonClickListener Click listener for the positive button.
	 * @param onNegativeButtonClickListener Click listener for the negative button.
	 * @param messageTextViewCustomize Lambda for customizing message view.
	 * @param titleTextViewCustomize Lambda for customizing title view.
	 * @param dialogBuilderCustomize Lambda to modify the CustomDialogBuilder instance itself.
	 * @param positiveButtonTextCustomize Lambda for customizing the positive button's TextView.
	 * @param negativeButtonTextCustomize Lambda for customizing the negative button's TextView.
	 * @param positiveButtonContainerCustomize Lambda for customizing the container of the positive button.
	 * @param negativeButtonContainerCustomize Lambda for customizing the container of the negative button.
	 *
	 * @return A CustomDialogBuilder ready to be shown or null if the context is invalid.
	 */
	@JvmStatic
	fun getMessageDialog(
		baseActivity: GlobalBaseActivity?,
		isCancelable: Boolean = true,
		isTitleVisible: Boolean = false,
		titleText: CharSequence = getText(R.string.title_goes_here),
		messageTxt: CharSequence = getText(R.string.message_goes_here),
		positiveButtonText: CharSequence = applicationContext.getString(R.string.okay),
		negativeButtonText: CharSequence = applicationContext.getString(R.string.cancel),
		isNegativeButtonVisible: Boolean = true,
		onPositiveButtonClickListener: OnClickListener? = null,
		onNegativeButtonClickListener: OnClickListener? = null,
		messageTextViewCustomize: ((TextView) -> Unit)? = {},
		titleTextViewCustomize: ((TextView) -> Unit)? = {},
		dialogBuilderCustomize: ((CustomDialogBuilder) -> Unit)? = {},
		positiveButtonTextCustomize: ((TextView) -> Unit)? = {},
		negativeButtonTextCustomize: ((TextView) -> Unit)? = {},
		positiveButtonContainerCustomize: ((RelativeLayout) -> Unit)? = {},
		negativeButtonContainerCustomize: ((RelativeLayout) -> Unit)? = {},
	): CustomDialogBuilder? {
		return WeakReference(baseActivity).get()?.getActivity()?.let { safeContextRef ->
			CustomDialogBuilder(safeContextRef).apply {
				// Setup dialog layout
				setView(R.layout.dialog_basic_message)
				setCancelable(isCancelable)

				// Fetch dialog components
				val titleTextView = view.findViewById<TextView>(R.id.txt_dialog_title)
				val messageTextView = view.findViewById<TextView>(R.id.txt_dialog_message)
				val btnNegativeTextView = view.findViewById<TextView>(R.id.btn_dialog_negative)
				val btnNegativeContainer = view.findViewById<RelativeLayout>(R.id.btn_dialog_negative_container)
				val btnPositiveTextView = view.findViewById<TextView>(R.id.btn_dialog_positive)
				val btnPositiveContainer = view.findViewById<RelativeLayout>(R.id.btn_dialog_positive_container)

				// Apply default or custom content
				titleTextView.text = titleText
				messageTextView.text = messageTxt
				btnPositiveTextView.text = positiveButtonText
				btnNegativeTextView.text = negativeButtonText

				// Customization hooks
				messageTextViewCustomize?.invoke(messageTextView)
				titleTextViewCustomize?.invoke(titleTextView)
				dialogBuilderCustomize?.invoke(this)
				positiveButtonTextCustomize?.invoke(btnPositiveTextView)
				positiveButtonContainerCustomize?.invoke(btnPositiveContainer)
				negativeButtonTextCustomize?.invoke(btnNegativeTextView)
				negativeButtonContainerCustomize?.invoke(btnNegativeContainer)

				// Visibility rules
				btnNegativeTextView.visibility = if (isNegativeButtonVisible) View.VISIBLE else GONE
				btnNegativeContainer.visibility = if (isNegativeButtonVisible) View.VISIBLE else GONE

				titleTextView.visibility = when {
					!isTitleVisible -> GONE
					titleTextView.text.toString() == getText(R.string.title_goes_here) -> GONE
					else -> View.VISIBLE
				}

				// Default button click behavior if none provided
				btnNegativeContainer.setOnClickListener(onNegativeButtonClickListener ?: OnClickListener { close() })
				btnPositiveContainer.setOnClickListener(onPositiveButtonClickListener ?: OnClickListener { close() })
			}
		}
	}
}