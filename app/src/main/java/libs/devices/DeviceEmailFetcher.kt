package libs.devices

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import android.util.Patterns
import core.bases.GlobalApplication.Companion.APP_INSTANCE
import libs.texts.CommonTextUtility.capitalizeFirstLetter
import java.lang.ref.WeakReference

/**
 * A utility object that provides helper functions to retrieve user email-related data
 * from the Android system using [AccountManager].
 *
 * Functions include:
 * - Getting the primary Google account email
 * - Validating email formats
 * - Getting display names (if available)
 * - Listing all registered email addresses
 * - Extracting email domains
 */
object DeviceEmailFetcher {

	/**
	 * Returns the primary Google account email address from the system.
	 *
	 * Uses the application context [APP_INSTANCE] to access [AccountManager],
	 * and retrieves the first available "com.google" account.
	 *
	 * @return The primary email as a [String], or `null` if none found or an error occurred.
	 */
	@JvmStatic
	fun getPrimaryEmail(): String? {
		return try {
			val accountManager = AccountManager.get(APP_INSTANCE)
			val account = getAccount(accountManager)
			account?.name
		} catch (error: Exception) {
			error.printStackTrace()
			null
		}
	}

	/**
	 * Returns all valid email addresses registered on the device.
	 *
	 * Only accounts with valid email patterns (checked via [Patterns.EMAIL_ADDRESS]) are returned.
	 *
	 * @return An [Array] of email address [String]s. Empty array if none found or error occurred.
	 */
	@JvmStatic
	fun getAllRegisteredEmailAddresses(): Array<String> {
		val emailList = mutableListOf<String>()
		try {
			val accounts = AccountManager.get(APP_INSTANCE).accounts
			accounts.forEach { account ->
				if (isValidEmail(account.name)) {
					emailList.add(account.name)
				}
			}
		} catch (error: Exception) {
			error.printStackTrace()
		}
		return emailList.toTypedArray()
	}

	/**
	 * Checks if a given string is a valid email address.
	 *
	 * @param email The input email string to validate.
	 * @return `true` if the email matches the standard pattern, `false` otherwise.
	 */
	@JvmStatic
	fun isValidEmail(email: String): Boolean {
		return Patterns.EMAIL_ADDRESS.matcher(email).matches()
	}

	/**
	 * Retrieves the first Google account on the device using the provided [AccountManager].
	 *
	 * @param accountManager The [AccountManager] instance to use.
	 * @return The first [Account] of type `"com.google"`, or `null` if not available.
	 */
	@JvmStatic
	fun getAccount(accountManager: AccountManager): Account? {
		val accounts = accountManager.getAccountsByType("com.google")
		return if (accounts.isNotEmpty()) accounts[0] else null
	}

	/**
	 * Retrieves the display name for the user's primary Google account.
	 *
	 * - On Android Q and above, it attempts to get the user data field `"display_name"`.
	 * - If not available, falls back to guessing the name using [guessDisplayName].
	 *
	 * @return The display name as a [String], or `null` if unavailable.
	 */
	@JvmStatic
	fun getDisplayName(): String? {
		return try {
			val accountManager = AccountManager.get(APP_INSTANCE)
			val account = getAccount(accountManager)

			account?.let {
				val displayName = getDisplayNameUsingReflection(accountManager, it)
				displayName ?: guessDisplayName(it.name)
			}
		} catch (error: Exception) {
			error.printStackTrace()
			null
		}
	}

	/**
	 * Attempts to get the `"display_name"` field for the given account using [AccountManager].
	 * Only works on Android Q (API 29) and above.
	 *
	 * @param accountManager The [AccountManager] to retrieve user data.
	 * @param account The account for which the display name is needed.
	 * @return The display name, or `null` if unavailable or unsupported.
	 */
	@JvmStatic
	fun getDisplayNameUsingReflection(
		accountManager: AccountManager,
		account: Account
	): String? {
		return if (SDK_INT >= Q) {
			try {
				val result = accountManager.getUserData(account, "display_name")
				result?.toString()
			} catch (error: Exception) {
				error.printStackTrace()
				null
			}
		} else null
	}

	/**
	 * Attempts to generate a user-friendly display name by extracting the portion of the
	 * email before the `@` symbol and capitalizing the first letter.
	 *
	 * Example:
	 * `"john.doe@gmail.com"` → `"John.doe"`
	 *
	 * @param email The email address to derive a name from.
	 * @return The guessed display name, or `null` if the email is malformed.
	 */
	@JvmStatic
	fun guessDisplayName(email: String): String? {
		val atIndex = email.indexOf('@')
		return if (atIndex != -1) {
			capitalizeFirstLetter(email.substring(0, atIndex))
		} else null
	}

	/**
	 * Extracts the domain from the user's primary email address.
	 *
	 * For example, from `"john.doe@gmail.com"` it returns `"gmail.com"`.
	 *
	 * @return The domain as a [String], or `null` if not found or invalid.
	 */
	@JvmStatic
	fun getEmailDomain(): String? {
		return getPrimaryEmail()?.let { primaryEmail ->
			val atIndex = primaryEmail.indexOf('@')
			if (atIndex != -1 && atIndex < primaryEmail.length - 1) {
				primaryEmail.substring(atIndex + 1)
			} else null
		}
	}

	/**
	 * Retrieves a list of all accounts (email or otherwise) on the device,
	 * formatted as `"account_name (account_type)"`.
	 *
	 * A [WeakReference] is used to prevent memory leaks when using the context.
	 *
	 * @param context A [Context] used to access [AccountManager]. If null or GC’d, returns an empty list.
	 * @return A [List] of account descriptions, or empty list if unavailable.
	 */
	@JvmStatic
	fun getAllEmailAccounts(context: Context?): List<String> {
		return WeakReference(context).get()?.let { safeContextRef ->
			val accountsList = mutableListOf<String>()
			try {
				val accounts = AccountManager.get(safeContextRef).accounts
				accounts.forEach { account ->
					accountsList.add("${account.name} (${account.type})")
				}
			} catch (error: Exception) {
				error.printStackTrace()
			}
			accountsList
		} ?: run { emptyList() }
	}
}