package libs.texts

import android.content.ClipData.newHtmlText
import android.content.ClipData.newPlainText
import android.content.ClipboardManager
import android.content.ClipboardManager.OnPrimaryClipChangedListener
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import java.lang.ref.WeakReference

/**
 * A utility object for managing system clipboard operations in Android.
 *
 * Provides static helper functions to:
 * - Copy and retrieve plain text or HTML from the clipboard
 * - Append new text to existing clipboard content
 * - Clear clipboard content
 * - Monitor clipboard changes via listeners
 *
 * Uses [WeakReference] to wrap the `Context`, preventing memory leaks when used
 * in long-lived static objects.
 */
object ClipboardUtility {

	/**
	 * Clears the current content of the clipboard by setting it to an empty plain text clip.
	 *
	 * @param context The context used to access the system clipboard service.
	 *
	 * **Note:** If the context is null or invalid, this method does nothing.
	 */
	@JvmStatic
	fun clearClipboard(context: Context?) {
		WeakReference(context).get()?.let {
			(it.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).apply {
				setPrimaryClip(newPlainText("", ""))
			}
		}
	}

	/**
	 * Checks whether the clipboard currently contains a non-empty plain text item.
	 *
	 * @param context The context used to access the clipboard.
	 * @return `true` if non-empty plain text is present, otherwise `false`.
	 */
	@JvmStatic
	fun hasTextInClipboard(context: Context?): Boolean {
		return WeakReference(context).get()?.let {
			val clipboard = it.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
			clipboard.primaryClip?.takeIf { clip ->
				clip.itemCount > 0 && clip.getItemAt(0).text?.isNotEmpty() == true
			} != null
		} ?: false
	}

	/**
	 * Retrieves the HTML content from the clipboard, if available.
	 *
	 * @param context The context used to access the clipboard service.
	 * @return The HTML text from the clipboard if available, otherwise an empty string.
	 *
	 * **Note:** Only returns the HTML portion of the clipboard item if supported.
	 */
	@JvmStatic
	fun getHtmlFromClipboard(context: Context?): String {
		return WeakReference(context).get()?.let {
			val clipboard = it.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
			clipboard.primaryClip?.takeIf { clip -> clip.itemCount > 0 }
				?.getItemAt(0)?.htmlText ?: ""
		} ?: ""
	}

	/**
	 * Copies the given HTML content to the clipboard.
	 *
	 * @param context The context used to access the clipboard service.
	 * @param html The HTML content to copy. If null or empty, nothing is copied.
	 */
	@JvmStatic
	fun copyHtmlToClipboard(context: Context?, html: String?) {
		html?.takeIf { it.isNotEmpty() }?.let { validHtml ->
			WeakReference(context).get()?.let {
				(it.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).apply {
					setPrimaryClip(newHtmlText("html_clip", validHtml, validHtml))
				}
			}
		}
	}

	/**
	 * Appends the given text to the existing clipboard plain text content.
	 * If no previous content exists, it will act like a normal copy.
	 *
	 * @param context The context used to access the clipboard.
	 * @param text The new text to append. Ignored if null or empty.
	 */
	@JvmStatic
	fun appendTextToClipboard(context: Context?, text: String?) {
		text?.takeIf { it.isNotEmpty() }?.let { validText ->
			WeakReference(context).get()?.let { ctx ->
				val current = getTextFromClipboard(ctx)
				copyTextToClipboard(ctx, current + validText)
			}
		}
	}

	/**
	 * Retrieves the plain text content currently stored in the clipboard.
	 *
	 * @param context The context used to access the clipboard.
	 * @return The plain text from the clipboard, or an empty string if not available.
	 */
	@JvmStatic
	fun getTextFromClipboard(context: Context?): String {
		return WeakReference(context).get()?.let {
			val clipboard = it.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
			clipboard.primaryClip?.takeIf { clip -> clip.itemCount > 0 }
				?.getItemAt(0)?.text?.toString() ?: ""
		} ?: ""
	}

	/**
	 * Copies plain text to the system clipboard.
	 *
	 * @param context The context used to access the clipboard service.
	 * @param text The plain text to copy. If null or empty, no operation is performed.
	 */
	@JvmStatic
	fun copyTextToClipboard(context: Context?, text: String?) {
		text?.takeIf { it.isNotEmpty() }?.let { validText ->
			WeakReference(context).get()?.let {
				(it.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).apply {
					setPrimaryClip(newPlainText("text_clip", validText))
				}
			}
		}
	}

	/**
	 * Registers a listener to be notified when clipboard content changes.
	 *
	 * @param context The context used to access the clipboard service.
	 * @param listener The [OnPrimaryClipChangedListener] to register.
	 *
	 * **Note:** If the listener is null, it is ignored.
	 */
	@JvmStatic
	fun setClipboardListener(
		context: Context?,
		listener: OnPrimaryClipChangedListener? = null
	) {
		listener?.let { validListener ->
			WeakReference(context).get()?.let {
				(it.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).apply {
					addPrimaryClipChangedListener(validListener)
				}
			}
		}
	}

	/**
	 * Unregisters a previously added clipboard change listener.
	 *
	 * @param context The context used to access the clipboard service.
	 * @param listener The listener to be removed. If null, no operation is performed.
	 */
	@JvmStatic
	fun removeClipboardListener(
		context: Context?,
		listener: OnPrimaryClipChangedListener? = null
	) {
		listener?.let { validListener ->
			WeakReference(context).get()?.let {
				(it.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).apply {
					removePrimaryClipChangedListener(validListener)
				}
			}
		}
	}
}