@file:Suppress("DEPRECATION")

package libs.ui.builders

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import core.bases.GlobalApplication.Companion.APP_INSTANCE
import libs.networks.URLUtilityKT.isValidURL
import libs.texts.CommonTextUtility.getText
import net.base.R
import java.lang.ref.WeakReference

/**
 * A custom Toast class that allows setting a custom layout and icon.
 *
 * This class provides a reusable and centralized way to show toasts with
 * enhanced customization in your application. It prevents showing toasts for URLs
 * and uses a consistent layout across the app.
 *
 * @constructor Creates a ToastView instance with the given context.
 * @param context The context to use. Usually your Application or Activity context.
 */
class ToastViewBuilder(context: Context) : Toast(context) {
	
	/**
	 * Sets the icon for the toast if a valid view is available.
	 *
	 * @param iconResId The resource ID of the drawable to use as the icon.
	 */
	fun setIcon(iconResId: Int) {
		view?.findViewById<ImageView>(R.id.image_toast_app_icon)
			?.apply { setImageResource(iconResId) }
	}
	
	companion object {
		
		/**
		 * Shows a toast message. This method accepts either a string message or a resource ID.
		 * It automatically avoids displaying if the message is a valid URL.
		 *
		 * @param msg The string message to display. Optional.
		 * @param msgId The resource ID for the message string. Optional.
		 */
		@JvmStatic
		fun showToast(msg: String? = null, msgId: Int = -1) {
			when {
				msgId != -1 -> showResourceToast(msgId)
				msg != null -> showTextToast(msg)
			}
		}
		
		/**
		 * Displays a toast using a string resource ID.
		 * Skips displaying if the resolved message is a URL.
		 *
		 * @param msgId The resource ID of the message string.
		 */
		private fun showResourceToast(msgId: Int) {
			val message = getText(msgId)
			if (isValidURL(message)) return
			makeText(APP_INSTANCE, message).show()
		}
		
		/**
		 * Displays a toast using a string message.
		 * Skips displaying if the message is a URL.
		 *
		 * @param msg The message string to show.
		 */
		private fun showTextToast(msg: String) {
			if (isValidURL(msg)) return
			makeText(APP_INSTANCE, msg).show()
		}
		
		/**
		 * Creates and configures a [ToastViewBuilder] instance using a custom layout and duration.
		 *
		 * @param context The context used to create the toast.
		 * @param message The message to display in the toast.
		 * @param duration The duration of the toast. Defaults to [Toast.LENGTH_LONG].
		 * @return A configured [ToastViewBuilder] instance.
		 */
		private fun makeText(
			context: Context, message: CharSequence?, duration: Int = LENGTH_SHORT
		): ToastViewBuilder {
			return WeakReference(context).get()?.let { safeContext ->
				configureToastView(safeContext, message, duration)
			} ?: run { ToastViewBuilder(context) }
		}
		
		/**
		 * Inflates the custom toast layout and sets its properties.
		 *
		 * @param context The context used to inflate the view.
		 * @param message The text to display.
		 * @param duration How long to display the toast.
		 * @return A [ToastViewBuilder] instance with the custom layout and message.
		 */
		@SuppressLint("InflateParams") private fun configureToastView(
			context: Context, message: CharSequence?, duration: Int
		): ToastViewBuilder {
			return ToastViewBuilder(context).apply {
				val systemService = context.getSystemService(LAYOUT_INFLATER_SERVICE)
				val inflater = systemService as LayoutInflater
				val toastView = inflater.inflate(R.layout.custom_toast_view, null)
				toastView.findViewById<TextView>(R.id.text_toast_message).text = message
				view = toastView
				setDuration(duration)
			}
		}
	}
}