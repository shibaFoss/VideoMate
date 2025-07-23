package core.database

/**
 * Enum representing currency symbols for supported currencies worldwide.
 * Follows ISO 4217 currency codes with additional cryptocurrency support.
 *
 * Provides utilities for currency code/symbol conversion and localization support.
 */
enum class CurrencySymbol(
    val currencySymbol: String,
    val currencyCode: String,
    val currencyName: String
) {
    // Major World Currencies
    INDIAN_RUPEE("₹", "INR", "Indian Rupee"),
    US_DOLLAR("$", "USD", "US Dollar"),
    EURO("€", "EUR", "Euro"),
    BRITISH_POUND("£", "GBP", "British Pound"),
    JAPANESE_YEN("¥", "JPY", "Japanese Yen"),

    // Asia-Pacific Currencies
    AUSTRALIAN_DOLLAR("$", "AUD", "Australian Dollar"),
    NEW_ZEALAND_DOLLAR("$", "NZD", "New Zealand Dollar"),
    SINGAPORE_DOLLAR("$", "SGD", "Singapore Dollar"),
    HONG_KONG_DOLLAR("$", "HKD", "Hong Kong Dollar"),
    SOUTH_KOREAN_WON("₩", "KRW", "South Korean Won"),
    CHINESE_YUAN("¥", "CNY", "Chinese Yuan"),
    MALAYSIAN_RINGGIT("RM", "MYR", "Malaysian Ringgit"),
    THAI_BAHT("฿", "THB", "Thai Baht"),
    INDONESIAN_RUPIAH("Rp", "IDR", "Indonesian Rupiah"),
    PHILIPPINE_PESO("₱", "PHP", "Philippine Peso"),
    VIETNAMESE_DONG("₫", "VND", "Vietnamese Dong"),

    // Middle East Currencies
    SAUDI_RIYAL("﷼", "SAR", "Saudi Riyal"),
    UAE_DIRHAM("د.إ", "AED", "UAE Dirham"),
    QATARI_RIYAL("ر.ق", "QAR", "Qatari Riyal"),
    KUWAITI_DINAR("د.ك", "KWD", "Kuwaiti Dinar"),

    // European Currencies
    SWISS_FRANC("CHF", "CHF", "Swiss Franc"),
    NORWEGIAN_KRONE("kr", "NOK", "Norwegian Krone"),
    SWEDISH_KRONA("kr", "SEK", "Swedish Krona"),
    DANISH_KRONE("kr", "DKK", "Danish Krone"),
    POLISH_ZLOTY("zł", "PLN", "Polish Zloty"),
    CZECH_KORUNA("Kč", "CZK", "Czech Koruna"),
    HUNGARIAN_FORINT("Ft", "HUF", "Hungarian Forint"),

    // Americas Currencies
    CANADIAN_DOLLAR("$", "CAD", "Canadian Dollar"),
    MEXICAN_PESO("$", "MXN", "Mexican Peso"),
    BRAZILIAN_REAL("$", "BRL", "Brazilian Real"),
    ARGENTINE_PESO("$", "ARS", "Argentine Peso"),

    // African Currencies
    SOUTH_AFRICAN_RAND("R", "ZAR", "South African Rand"),
    NIGERIAN_NAIRA("₦", "NGN", "Nigerian Naira"),
    EGYPTIAN_POUND("£", "EGP", "Egyptian Pound"),
    KENYAN_SHILLING("KSh", "KES", "Kenyan Shilling"),

    // Cryptocurrencies
    BITCOIN("₿", "BTC", "Bitcoin"),
    ETHEREUM("Ξ", "ETH", "Ethereum"),
    LITECOIN("Ł", "LTC", "Litecoin"),
    RIPPLE("XRP", "XRP", "Ripple"),

    // Special Cases
    SPECIAL_DRAWING_RIGHTS("XDR", "XDR", "SDR (IMF)"),
    GOLD("XAU", "XAU", "Gold Troy Ounce");

    companion object {
        /**
         * Gets the currency symbol for the given ISO currency code.
         * @param code ISO 4217 currency code (e.g., "USD", "INR")
         * @return Corresponding currency symbol or original code if not found
         */
        fun fromCode(code: String): String {
            return entries.firstOrNull {
                it.currencyCode.equals(
                    code,
                    ignoreCase = true
                )
            }?.currencySymbol ?: code
        }

        /**
         * Gets the complete CurrencySymbol enum for the given ISO code.
         * @param code ISO 4217 currency code
         * @return CurrencySymbol enum or null if not found
         */
        fun getByCode(code: String): CurrencySymbol? {
            return entries.firstOrNull { it.currencyCode.equals(code, ignoreCase = true) }
        }

        /**
         * Gets the ISO currency code for the given symbol.
         * @param symbol Currency symbol (e.g., "$", "₹")
         * @return Corresponding ISO currency code or null if not found
         */
        fun toCode(symbol: String): String? {
            return entries.firstOrNull { it.currencySymbol == symbol }?.currencyCode
        }

        /**
         * Gets all supported currency codes.
         * @return List of all supported ISO currency codes
         */
        fun supportedCodes(): List<String> {
            return entries.map { it.currencyCode }
        }

        /**
         * Gets all supported currency symbols.
         * @return List of all supported currency symbols
         */
        fun supportedSymbols(): List<String> {
            return entries.map { it.currencySymbol }
        }

        /**
         * Gets currencies grouped by region for UI display purposes.
         * @return Map of region names to currency lists
         */
        fun currenciesByRegion(): Map<String, List<CurrencySymbol>> {
            return mapOf(
                "Major Currencies" to listOf(
                    US_DOLLAR,
                    EURO,
                    BRITISH_POUND,
                    JAPANESE_YEN,
                    SWISS_FRANC
                ),

                "Asia-Pacific" to listOf(
                    INDIAN_RUPEE,
                    AUSTRALIAN_DOLLAR,
                    NEW_ZEALAND_DOLLAR,
                    SINGAPORE_DOLLAR,
                    HONG_KONG_DOLLAR,
                    SOUTH_KOREAN_WON,
                    CHINESE_YUAN,
                    MALAYSIAN_RINGGIT,
                    THAI_BAHT
                ),

                "Middle East" to listOf(
                    SAUDI_RIYAL,
                    UAE_DIRHAM,
                    QATARI_RIYAL,
                    KUWAITI_DINAR
                ),

                "Europe" to listOf(
                    NORWEGIAN_KRONE,
                    SWEDISH_KRONA,
                    DANISH_KRONE,
                    POLISH_ZLOTY,
                    CZECH_KORUNA,
                    HUNGARIAN_FORINT
                ),

                "Americas" to listOf(
                    CANADIAN_DOLLAR,
                    MEXICAN_PESO,
                    BRAZILIAN_REAL,
                    ARGENTINE_PESO
                ),

                "Africa" to listOf(
                    SOUTH_AFRICAN_RAND,
                    NIGERIAN_NAIRA,
                    EGYPTIAN_POUND,
                    KENYAN_SHILLING
                ),

                "Cryptocurrencies" to listOf(
                    BITCOIN,
                    ETHEREUM,
                    LITECOIN,
                    RIPPLE
                ),

                "Special" to listOf(
                    SPECIAL_DRAWING_RIGHTS,
                    GOLD
                )
            )
        }
    }

    /**
     * Gets the display string combining symbol and code (e.g., "₹ (INR)")
     */
    fun displayString(): String {
        return "$currencySymbol ($currencyCode)"
    }
}