package libs.texts

import android.text.Html.FROM_HTML_MODE_COMPACT
import android.text.Html.fromHtml
import android.text.Spanned
import core.bases.GlobalApplication.Companion.APP_INSTANCE
import libs.process.LocalizationHelper
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Locale

/**
 * A utility object providing a collection of static helper functions
 * for string manipulation, localization, HTML parsing, and text formatting.
 *
 * This object simplifies common text operations such as:
 * - Sanitizing input strings
 * - Capitalization logic
 * - Reading and parsing HTML content
 * - Localized text retrieval
 * - Removing blank lines or redundant characters
 * - Generating random strings
 */
object CommonTextUtility {

	/**
	 * Removes consecutive forward slashes in the given [input] string and replaces them with a single slash.
	 *
	 * Example: `"http://example.com//path///to///resource"` becomes `"http://example.com/path/to/resource"`
	 *
	 * @param input The input string to sanitize.
	 * @return A cleaned string with only single slashes or null if input is null.
	 */
	@JvmStatic
	fun removeDuplicateSlashes(input: String?): String? {
		if (input == null) return null
		return input.replace("/{2,}".toRegex(), "/")
	}

	/**
	 * Retrieves a localized string for the given resource ID from the application's context.
	 *
	 * @param resID The resource ID pointing to a string resource.
	 * @return The localized string as per current locale.
	 */
	@JvmStatic
	fun getText(resID: Int): String {
		return LocalizationHelper.getLocalizedString(APP_INSTANCE, resID)
	}

	/**
	 * Generates a random alphanumeric string of specified [length].
	 *
	 * Uses uppercase, lowercase characters and digits.
	 *
	 * @param length Length of the random string.
	 * @return A randomly generated alphanumeric string.
	 */
	@JvmStatic
	fun generateRandomString(length: Int): String {
		val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
				"abcdefghijklmnopqrstuvwxyz" +
				"0123456789"
		val sb = StringBuilder(length)
		for (index in 0 until length) {
			val randomIndex = (characters.indices).random()
			sb.append(characters[randomIndex])
		}
		return sb.toString()
	}

	/**
	 * Checks if a character is valid for common display scenarios.
	 * Accepts letters, digits, and common punctuation used in filenames or usernames.
	 *
	 * @receiver Character to validate.
	 * @return True if the character is valid, false otherwise.
	 */
	@JvmStatic
	fun Char.isValidCharacter(): Boolean {
		return this.isLetterOrDigit() ||
				this in setOf('_', '-', '.', '@', ' ', '[', ']', '(', ')')
	}

	/**
	 * Joins the provided [elements] using a [delimiter] to form a single string.
	 *
	 * @param delimiter String to separate each element.
	 * @param elements Variable number of string elements to join.
	 * @return Concatenated string with delimiter in between.
	 */
	@JvmStatic
	fun join(delimiter: String, vararg elements: String): String {
		if (elements.isEmpty()) return ""
		return elements.joinToString(separator = delimiter)
	}

	/**
	 * Reverses the characters in the given [input] string.
	 *
	 * @param input The string to reverse.
	 * @return A reversed version of the input string, or null if input is null.
	 */
	@JvmStatic
	fun reverse(input: String?): String? {
		if (input == null) return null
		return input.reversed()
	}

	/**
	 * Capitalizes the first character of the string while leaving the rest unchanged.
	 *
	 * @param string The input string to capitalize.
	 * @return A string with the first character capitalized, or null if the string is null or empty.
	 */
	@JvmStatic
	fun capitalizeFirstLetter(string: String?): String? {
		if (string.isNullOrEmpty()) return null
		val first = string[0]
		return if (Character.isUpperCase(first)) string
		else first.uppercaseChar() + string.substring(1)
	}

	/**
	 * Capitalizes the first letter of each word in the input string.
	 * Ignores multiple spaces and trims the result.
	 *
	 * @param input The string in which to capitalize each word.
	 * @return A capitalized version of the string, or the same input if it is blank or null.
	 */
	@JvmStatic
	fun capitalizeWords(input: String?): String? {
		if (input.isNullOrBlank()) return input
		return input.trim()
			.split("\\s+".toRegex())
			.joinToString(" ") { word ->
				word.replaceFirstChar {
					if (it.isLowerCase()) {
						it.titlecase(Locale.getDefault())
					} else {
						it.toString()
					}
				}
			}
	}

	/**
	 * Converts an HTML-formatted string into a [Spanned] text object suitable for display in TextViews.
	 *
	 * @param htmlString The raw HTML string.
	 * @return A Spanned object representing the HTML content.
	 */
	@JvmStatic
	fun fromHtmlStringToSpanned(htmlString: String): Spanned {
		return fromHtml(htmlString, FROM_HTML_MODE_COMPACT)
	}

	/**
	 * Loads an HTML file from the raw resources folder and returns its contents as a string.
	 *
	 * @param resId Resource ID of the HTML file.
	 * @return The raw HTML content as a plain string.
	 */
	@JvmStatic
	fun getHtmlString(resId: Int): String {
		return convertRawHtmlFileToString(resId)
	}

	/**
	 * Converts the contents of a raw HTML file from resources into a string.
	 *
	 * This method ensures the stream is closed properly and handles exceptions.
	 *
	 * @param resourceId The resource ID of the raw HTML file.
	 * @return The full string content of the file.
	 */
	@JvmStatic
	fun convertRawHtmlFileToString(resourceId: Int): String {
		val inputStream = APP_INSTANCE.resources.openRawResource(resourceId)
		val reader = BufferedReader(InputStreamReader(inputStream))
		val stringBuilder = StringBuilder()
		var line: String?
		try {
			while (reader.readLine().also { line = it } != null) {
				stringBuilder.append(line)
			}
		} catch (error: Throwable) {
			error.printStackTrace()
		} finally {
			try {
				inputStream.close()
				reader.close()
			} catch (err: Exception) {
				err.printStackTrace()
			}
		}
		return stringBuilder.toString()
	}

	/**
	 * Counts how many times a given [char] appears in the [input] string.
	 *
	 * @param input The string to search within.
	 * @param char The character to count.
	 * @return The number of times the character appears. Returns 0 if either is null.
	 */
	@JvmStatic
	fun countOccurrences(input: String?, char: Char?): Int {
		if (input == null || char == null) return 0
		return input.count { it == char }
	}

	/**
	 * Removes all blank or empty lines from a multiline [input] string.
	 *
	 * @param input The raw string with potential empty lines.
	 * @return A cleaned version of the string with only non-blank lines, or null if input is null or empty.
	 */
	@JvmStatic
	fun removeEmptyLines(input: String?): String? {
		if (input.isNullOrEmpty()) return null
		return input.split("\n")
			.filter { it.isNotBlank() }
			.joinToString("\n")
	}
}