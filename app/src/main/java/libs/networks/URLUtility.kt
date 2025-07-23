package libs.networks

import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_OK
import java.net.URI
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8

/**
 * Kotlin utility object for URL-related operations including parsing, validation,
 * content fetching, and metadata extraction.
 */
object URLUtilityKT {
	
	/**
	 * Extracts the host URL (scheme + host) from a given URL string.
	 * @param urlString The complete URL string
	 * @return Base host URL (e.g., "https://example.com")
	 */
	@JvmStatic
	fun extractHostUrl(urlString: String): String {
		val encodedUrl = URLEncoder.encode(urlString, UTF_8.toString())
		val uri = URI(encodedUrl)
		return "${uri.scheme}://${uri.host}"
	}
	
	/**
	 * Checks if a URL contains only the host without any path.
	 * @param url The URL to check
	 * @return true if URL has no path or only root path, false otherwise
	 */
	@JvmStatic
	fun isHostOnly(url: String): Boolean {
		return try {
			val parsedUrl = URL(url)
			val path = parsedUrl.path
			path.isNullOrEmpty() || path == "/"
		} catch (error: Exception) {
			error.printStackTrace()
			false
		}
	}
	
	/**
	 * Checks if a favicon URL is accessible.
	 * @param faviconUrl The favicon URL to check
	 * @return true if favicon is accessible, false otherwise
	 */
	@JvmStatic
	fun isFaviconAvailable(faviconUrl: String): Boolean {
		return try {
			val url = URL(faviconUrl)
			val connection = url.openConnection() as HttpURLConnection
			connection.requestMethod = "HEAD"
			val isAvailable = connection.responseCode == HTTP_OK
			isAvailable
		} catch (error: Exception) {
			error.printStackTrace()
			false
		}
	}
	
	/**
	 * Fetches the file size from a URL using OkHttp.
	 * @param httpClient Configured OkHttpClient instance
	 * @param url The file URL
	 * @return File size in bytes, or -1 if unavailable
	 */
	@JvmStatic
	fun fetchFileSize(httpClient: OkHttpClient, url: String): Long {
		return try {
			val request = Request.Builder().url(url).head().build()
			httpClient.newCall(request).execute().use { response ->
				val fileSize = response.header("Content-Length")?.toLong() ?: -1L
				fileSize
			}
		} catch (error: Exception) {
			error.printStackTrace()
			-1L
		}
	}
	/**
	 * Checks internet connectivity by attempting to reach google.com.
	 * @return true if connection succeeds, false otherwise
	 */
	@JvmStatic
	fun isInternetConnected(): Boolean {
		return try {
			val url = URL("https://www.google.com")
			with(url.openConnection() as HttpURLConnection) {
				requestMethod = "GET"
				connectTimeout = 1000
				readTimeout = 1000
				connect()
				val isConnected = responseCode == HTTP_OK
				isConnected
			}
		} catch (error: Exception) {
			error.printStackTrace()
			false
		}
	}

	/**
	 * Converts a string to URL-safe format by encoding spaces.
	 * @param input The input string
	 * @return URL-encoded string
	 */
	@JvmStatic
	fun getUrlSafeString(input: String): String {
		return input.replace(" ", "%20")
	}
	
	/**
	 * Extracts the base domain from a URL.
	 * @param url The complete URL
	 * @return Base domain (e.g., "google" from "www.google.com")
	 */
	@JvmStatic
	fun getBaseDomain(url: String): String? {
		return try {
			val domain = URL(url).host
			val parts = domain.split(".")
			val baseDomain = if (parts.size > 2) {
				parts[parts.size - 2]
			} else {
				parts[0]
			}
			
			baseDomain
		} catch (error: Exception) {
			error.printStackTrace()
			null
		}
	}
	
	/**
	 * Extracts the host from a URL string.
	 * @param urlString The URL string
	 * @return Host name or null if invalid URL
	 */
	@JvmStatic
	fun getHostFromUrl(urlString: String?): String? {
		return try {
			urlString?.let { URL(it).host }
		} catch (error: Exception) {
			error.printStackTrace()
			null
		}
	}
	
	/**
	 * Generates a Google favicon URL for a domain.
	 * @param domain The target domain
	 * @return Google favicon service URL
	 */
	@JvmStatic
	fun getGoogleFaviconUrl(domain: String): String {
		return "https://www.google.com/s2/favicons?domain=$domain&sz=128"
	}
	
	/**
	 * Checks if a URL is expired (returns 4xx/5xx status).
	 * @param urlString The URL to check
	 * @return true if URL returns error status, false otherwise
	 */
	@JvmStatic
	fun isUrlExpired(urlString: String): Boolean {
		return try {
			val url = URL(urlString)
			val connection = url.openConnection() as HttpURLConnection
			connection.requestMethod = "HEAD"
			connection.connectTimeout = 5000
			connection.readTimeout = 5000
			connection.connect()
			val responseCode = connection.responseCode
			val isExpired = responseCode >= 400
			isExpired
		} catch (error: Exception) {
			error.printStackTrace()
			true
		}
	}
	
	/**
	 * Removes 'www.' prefix from a URL if present.
	 * @param url The URL to process
	 * @return URL without 'www.' prefix
	 */
	@JvmStatic
	fun removeWwwFromUrl(url: String?): String {
		if (url == null) return ""
		return try {
			url.replaceFirst("www.", "")
		} catch (error: Exception) {
			error.printStackTrace()
			url
		}
	}

	/**
	 * Normalizes a URL by decoding and re-encoding components consistently.
	 * @param url The URL to normalize
	 * @return Normalized URL or original if error occurs
	 */
	@JvmStatic
	fun normalizeEncodedUrl(url: String): String {
		try {
			val unescapedUrl = url.replace("\\/", "/")
			val uri = URI(unescapedUrl)
			val baseUrl = "${uri.scheme}://${uri.host}${uri.path}"
			val query = uri.query ?: return baseUrl
			
			val queryParams = query.split("&").associate {
				it.split("=").let { pair ->
					val key = URLDecoder.decode(pair[0], "UTF-8")
					val value = if (pair.size > 1) URLDecoder.decode(pair[1], "UTF-8") else ""
					key to value
				}
			}.toSortedMap()
			
			val normalizedQuery = queryParams.map { (key, value) ->
				"${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value, "UTF-8")}"
			}.joinToString("&")
			
			return "$baseUrl?$normalizedQuery"
		} catch (error: Exception) {
			error.printStackTrace()
			return url
		}
	}

	/**
	 * Validates whether a string is a properly formatted URL.
	 *
	 * @param url The URL string to validate.
	 * @return `true` if the URL is valid, `false` otherwise.
	 */
	@JvmStatic
	fun isValidURL(url: String?): Boolean {
		if (url.isNullOrEmpty()) return false
		return try {
			URL(url)
			true
		} catch (error: Throwable) {
			error.printStackTrace()
			false
		}
	}

}