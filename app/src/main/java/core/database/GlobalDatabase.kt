/**
 * Package containing all core database entities and helper classes for the application.
 * Uses ObjectBox as the underlying database solution.
 */
package core.database

import core.bases.GlobalApplication.Companion.APP_INSTANCE
import core.database.AccountType.BANK
import core.database.TransactionType.EXPENSE
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne

/**
 * Enum representing different types of financial accounts.
 *
 * @property BANK Traditional savings/checking accounts
 * @property WALLET Digital wallets (Paytm, PhonePe, GPay)
 * @property UPI Unified Payments Interface IDs
 * @property CREDIT_CARD Credit card accounts
 * @property DEBIT_CARD Debit card accounts
 * @property CASH Physical cash tracking
 * @property CRYPTO Cryptocurrency wallets (BTC, ETH, etc.)
 * @property LOAN Loan or EMI accounts
 * @property INVESTMENT Mutual funds, stocks, SIPs
 * @property SUBSCRIPTION Recurring billing accounts (Netflix, Spotify)
 * @property REWARD_POINTS Loyalty or reward points
 * @property FOREIGN_CURRENCY Non-INR accounts
 * @property GIFT_CARD Stored-value gift cards
 * @property BUSINESS Business or corporate accounts
 * @property PERSONAL Personal account grouping
 * @property JOINT Joint or shared accounts
 * @property TEMPORARY Temp accounts for short-term usage
 * @property OTHER Catch-all for anything else
 */
enum class AccountType {
    BANK, WALLET, UPI, CREDIT_CARD, DEBIT_CARD, CASH, CRYPTO,
    LOAN, INVESTMENT, SUBSCRIPTION, REWARD_POINTS, FOREIGN_CURRENCY,
    GIFT_CARD, BUSINESS, PERSONAL, JOINT, TEMPORARY, OTHER
}

/**
 * Enum representing different types of financial transactions.
 *
 * @property EXPENSE Money going out (purchases, bills)
 * @property INCOME Money coming in (salary, interest)
 * @property TRANSFER Moving funds between accounts
 * @property REFUND Returned expense (money back)
 * @property ADJUSTMENT Manual correction or balance fix
 * @property WITHDRAWAL Cash withdrawn from bank or ATM
 * @property DEPOSIT Manual or cash deposit
 * @property FEE Bank charges, late fees, etc.
 * @property REWARD Cashback, loyalty points, rewards
 * @property LOAN Money given as a loan
 * @property LOAN_REPAYMENT Money received or paid for loan repayment
 * @property SUBSCRIPTION Recurring payment like Netflix
 * @property INVESTMENT Stocks, crypto, mutual funds, SIPs
 * @property OTHER Anything not categorized above
 */
enum class TransactionType {
    EXPENSE, INCOME, TRANSFER, REFUND, ADJUSTMENT, WITHDRAWAL, DEPOSIT,
    FEE, REWARD, LOAN, LOAN_REPAYMENT, SUBSCRIPTION, INVESTMENT, OTHER
}

/**
 * Root database entity containing all application data relationships.
 *
 * @property id Primary key
 * @property appSettings 1:1 relationship with application settings
 * @property appUser 1:1 relationship with user profile
 * @property accounts 1:N relationship with financial accounts
 * @property categories 1:N relationship with transaction categories
 * @property transactions 1:N relationship with financial transactions
 */
@Entity
data class GlobalDatabase(@Id var id: Long = 0) {
    var appSettings: ToOne<GlobalAppSettings> = ToOne(this, GlobalDatabase_.appSettings)
    var appUser: ToOne<GlobalAppUser> = ToOne(this, GlobalDatabase_.appUser)

    @Backlink(to = "database")
    var accounts: ToMany<GlobalAccount> = ToMany(this, GlobalDatabase_.accounts)

    @Backlink(to = "database")
    var categories: ToMany<GlobalCategory> = ToMany(this, GlobalDatabase_.categories)

    @Backlink(to = "database")
    var transactions: ToMany<GlobalTransaction> = ToMany(this, GlobalDatabase_.transactions)
}

/**
 * Entity representing application user profile.
 *
 * @property id Primary key
 * @property userName User's display name
 * @property emailId User's email address
 * @property phoneNumber Contact number (optional)
 * @property profileImageUrl URL for profile photo
 * @property gender User's gender (optional)
 * @property birthDate Date of birth (optional)
 * @property country Country of residence
 * @property countryCode Country dial code
 * @property preferredCurrency ISO currency code
 * @property preferredLanguage UI language (ISO code)
 * @property hasLoggedIn Flag indicating if user has logged in
 * @property loginCount Number of times user has logged in
 * @property lastLoginAt Timestamp of last login
 * @property isGuestUser Flag for guest users
 * @property isSynced Sync status with remote
 * @property syncId Remote sync identifier
 * @property createdAt Creation timestamp
 * @property updatedAt Last update timestamp
 */
@Entity
data class GlobalAppUser(
    @Id var id: Long = 0,
    var userName: String = "",
    var emailId: String = "",
    var phoneNumber: String = "",
    var profileImageUrl: String = "",
    var gender: String = "",
    var birthDate: String = "",
    var country: String = "",
    var countryCode: Int = 91,
    var preferredCurrency: String = "USD",
    var preferredLanguage: String = "en",
    var hasLoggedIn: Boolean = false,
    var loginCount: Int = 0,
    var lastLoginAt: Long = 0L,
    var isGuestUser: Boolean = false,
    var isSynced: Boolean = false,
    var syncId: String = "",
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
)

/**
 * Entity representing application settings and preferences.
 *
 * @property id Primary key
 * @property appThemeCode Theme identifier (light/dark/custom)
 * @property appFontScale Font scaling factor
 * @property defaultHomeTab Default tab to show on launch
 * @property hasAppCrashedRecently Crash detection flag
 * @property crashLog Recent crash log (optional)
 * @property userSelectedAppUILanguage User's language preference
 * @property notificationsEnabled Notifications toggle
 * @property autoSyncEnabled Background sync toggle
 * @property useBiometricAuth Biometric auth toggle
 * @property backupFrequencyDays Backup frequency in days
 * @property lastBackupAt Timestamp of last backup
 * @property isPremiumUser Premium status flag
 * @property appLaunchCount Usage counter
 * @property lastUpdatedAt Last update timestamp
 */
@Entity
data class GlobalAppSettings(
    @Id var id: Long = 0,
    var appThemeCode: Int = 0,
    var appFontScale: Float = 1.0f,
    var defaultHomeTab: String = "",
    var hasAppCrashedRecently: Boolean = false,
    var crashLog: String = "",
    var userSelectedAppUILanguage: String = "en",
    var notificationsEnabled: Boolean = true,
    var autoSyncEnabled: Boolean = true,
    var useBiometricAuth: Boolean = false,
    var backupFrequencyDays: Int = 7,
    var lastBackupAt: Long = 0L,
    var isPremiumUser: Boolean = true,
    var appLaunchCount: Int = 0,
    var lastUpdatedAt: Long = System.currentTimeMillis()
)

/**
 * Entity representing a financial account.
 *
 * @property id Primary key
 * @property uniqueId Unique timestamp-based identifier
 * @property accountName Display name of account
 * @property accountType Type of account (from AccountType enum)
 * @property institutionName Financial institution name
 * @property branchName Branch/location (optional)
 * @property institutionLogoUrl Institution logo URL (optional)
 * @property accountHolderName Name on account
 * @property accountNumberMasked Masked account number
 * @property accountNumberFull Full account number (encrypted)
 * @property ifscCode Indian bank IFSC code
 * @property upiId UPI ID if applicable
 * @property countryCode Country dial code
 * @property currencyCode ISO currency code
 * @property currencySymbol Currency symbol
 * @property isPrimary Primary account flag
 * @property isActive Active status flag
 * @property isFavorite Favorite flag for UI
 * @property accountColor Theming color
 * @property balanceAmount Current balance
 * @property creditLimit Credit limit if applicable
 * @property overdraftLimit Overdraft limit if applicable
 * @property tags Categorization tags
 * @property iconResId Icon resource ID
 * @property notes User notes
 * @property syncId Remote sync identifier
 * @property isSynced Sync status flag
 * @property isDeleted Soft delete flag
 * @property createdAt Creation timestamp
 * @property lastUpdated Last update timestamp
 * @property database Backlink to parent GlobalDatabase
 */
@Entity
data class GlobalAccount(
    @Id var id: Long = 0,
    var uniqueId: Long = System.currentTimeMillis(),
    var accountName: String = "Unknown",
    var accountType: String = BANK.toString(),
    var institutionName: String = "",
    var branchName: String = "",
    var institutionLogoUrl: String = "",
    var accountHolderName: String = "",
    var accountNumberMasked: String = "",
    var accountNumberFull: String = "",
    var ifscCode: String = "",
    var upiId: String = "",
    var countryCode: Int = 91,
    var currencyCode: String = "USD",
    var currencySymbol: String = "$",
    var isPrimary: Boolean = false,
    var isActive: Boolean = true,
    var isFavorite: Boolean = false,
    var accountColor: String = "#4CAF50",
    var balanceAmount: Double = 0.0,
    var creditLimit: Double = 0.0,
    var overdraftLimit: Double = 0.0,
    var tags: String = "",
    var iconResId: Int = 0,
    var notes: String = "",
    var syncId: String = "",
    var isSynced: Boolean = false,
    var isDeleted: Boolean = false,
    var createdAt: Long = System.currentTimeMillis(),
    var lastUpdated: Long = System.currentTimeMillis()
) {
    var database: ToOne<GlobalDatabase> = ToOne(this, GlobalAccount_.database)
}

/**
 * Entity representing a transaction category.
 *
 * @property id Primary key
 * @property categoryName Display name
 * @property categoryType EXPENSE or INCOME
 * @property iconName Icon identifier
 * @property colorHex Theming color
 * @property isDefault System category flag
 * @property isArchived Archived status
 * @property displayOrder UI sort order
 * @property usageCount Usage frequency counter
 * @property createdAt Creation timestamp
 * @property updatedAt Last update timestamp
 * @property isSynced Sync status flag
 * @property syncId Remote sync identifier
 * @property database Backlink to parent GlobalDatabase
 */
@Entity
data class GlobalCategory(
    @Id var id: Long = 0,
    var categoryName: String = "",
    var categoryType: String = EXPENSE.toString(),
    var iconName: String = "",
    var colorHex: String = "#FF5722",
    var isDefault: Boolean = false,
    var isArchived: Boolean = false,
    var displayOrder: Int = 0,
    var usageCount: Int = 0,
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis(),
    var isSynced: Boolean = false,
    var syncId: String = ""
) {
    var database: ToOne<GlobalDatabase> = ToOne(this, GlobalCategory_.database)
}

/**
 * Entity representing a financial transaction.
 *
 * @property id Primary key
 * @property uniqueId Unique timestamp-based identifier
 * @property associatedAccountUniqueId Related account ID
 * @property transactionNote User notes
 * @property transactionType Transaction type
 * @property categoryIcon Category icon
 * @property isExpense Expense/income flag
 * @property transactionAmount Transaction amount
 * @property transactionDate ISO date string
 * @property timestamp Unix timestamp
 * @property dayCode Day component
 * @property monthCode Month component
 * @property yearCode Year component
 * @property paymentMethod Payment method
 * @property location Transaction location
 * @property tag Categorization tag
 * @property isRecurring Recurring transaction flag
 * @property isRefund Refund transaction flag
 * @property isSynced Sync status flag
 * @property syncId Remote sync identifier
 * @property createdAt Creation timestamp
 * @property updatedAt Last update timestamp
 * @property database Backlink to parent GlobalDatabase
 */
@Entity
data class GlobalTransaction(
    @Id var id: Long = 0,
    var uniqueId: Long = System.currentTimeMillis(),
    var associatedAccountUniqueId: Long = 0,
    var transactionNote: String = "",
    var transactionType: String = EXPENSE.toString(),
    var categoryIcon: String = "",
    var isExpense: Boolean = true,
    var transactionAmount: Double = 0.0,
    var transactionDate: String = "",
    var timestamp: Long = System.currentTimeMillis(),
    var dayCode: Int = 0,
    var monthCode: Int = 0,
    var yearCode: Int = 0,
    var paymentMethod: String = "",
    var location: String = "",
    var tag: String = "",
    var isRecurring: Boolean = false,
    var isRefund: Boolean = false,
    var isSynced: Boolean = false,
    var syncId: String = "",
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
) {
    var database: ToOne<GlobalDatabase> = ToOne(this, GlobalTransaction_.database)
}

/**
 * Helper class for managing the global database instance.
 * Implements singleton pattern to ensure single database instance.
 *
 * @property boxStore Lazy-initialized ObjectBox store
 * @property databaseBox Lazy-initialized GlobalDatabase box
 * @property userBox Lazy-initialized GlobalAppUser box
 * @property settingsBox Lazy-initialized GlobalAppSettings box
 */
class GlobalDatabaseHelper private constructor() {
    private val boxStore: BoxStore by lazy {
        MyObjectBox.builder()
            .androidContext(APP_INSTANCE)
            .build()
    }

    private val databaseBox: Box<GlobalDatabase> by lazy {
        boxStore.boxFor(GlobalDatabase::class.java)
    }

    private val userBox: Box<GlobalAppUser> by lazy {
        boxStore.boxFor(GlobalAppUser::class.java)
    }

    private val settingsBox: Box<GlobalAppSettings> by lazy {
        boxStore.boxFor(GlobalAppSettings::class.java)
    }

    companion object {
        @Volatile
        private var instance: GlobalDatabaseHelper? = null

        /**
         * Gets the singleton instance of GlobalDatabaseHelper.
         * @return The singleton instance
         */
        fun getInstance(): GlobalDatabaseHelper {
            return instance ?: synchronized(this) {
                instance ?: GlobalDatabaseHelper().also { instance = it }
            }
        }
    }

    /**
     * Ensures default data exists in the database.
     * Creates default settings, user and database if they don't exist.
     */
    private fun ensureDefaultDataExists() {
        if (databaseBox.get(1) == null) {
            val defaultSettings = GlobalAppSettings()
            val settingsId = settingsBox.put(defaultSettings)

            val defaultUser = GlobalAppUser()
            val userId = userBox.put(defaultUser)

            val defaultDb = GlobalDatabase()
            defaultDb.appSettings.target = settingsBox.get(settingsId)
            defaultDb.appUser.target = userBox.get(userId)
            databaseBox.put(defaultDb)
        }
    }

    /**
     * Saves global data (settings and/or user) to the database.
     * @param settings Optional GlobalAppSettings to save
     * @param user Optional GlobalAppUser to save
     */
    @Synchronized
    fun saveGlobalData(settings: GlobalAppSettings? = null, user: GlobalAppUser? = null) {
        try {
            val global = databaseBox.get(1) ?: GlobalDatabase(id = 1).also { databaseBox.put(it) }

            settings?.let {
                val settingsId = settingsBox.put(it)
                global.appSettings.target = settingsBox.get(settingsId)
            }

            user?.let {
                val userId = userBox.put(it)
                global.appUser.target = userBox.get(userId)
            }

            databaseBox.put(global)
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    /**
     * Retrieves the GlobalDatabase instance.
     * @return GlobalDatabase instance or null if error occurs
     */
    fun getGlobalDatabase(): GlobalDatabase? {
        ensureDefaultDataExists()
        return try {
            databaseBox.get(1)
        } catch (error: Exception) {
            error.printStackTrace()
            null
        }
    }

    /**
     * Retrieves the GlobalAppSettings instance.
     * @return GlobalAppSettings instance or null if error occurs
     */
    fun getGlobalAppSettings(): GlobalAppSettings? {
        return try {
            getGlobalDatabase()?.appSettings?.target
        } catch (error: Exception) {
            error.printStackTrace()
            null
        }
    }

    /**
     * Retrieves the GlobalAppUser instance.
     * @return GlobalAppUser instance or null if error occurs
     */
    fun getGlobalAppUser(): GlobalAppUser? {
        return try {
            getGlobalDatabase()?.appUser?.target
        } catch (error: Exception) {
            error.printStackTrace()
            null
        }
    }

    /**
     * Clears all global data from the database.
     * WARNING: This will delete all user data!
     */
    @Synchronized
    fun clearGlobalData() {
        try {
            databaseBox.removeAll()
            userBox.removeAll()
            settingsBox.removeAll()
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }
}