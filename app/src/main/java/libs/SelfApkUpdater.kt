package libs

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import libs.devices.AppVersionUtility
import libs.process.LogHelperUtils
import java.net.HttpURLConnection
import java.net.URL

class SelfApkUpdater {
    private val logger = LogHelperUtils.from(javaClass)

    companion object {
        const val GITHUB_VERSION_INFO_FILE_URL =
            "https://github.com/shibaFoss/VideoMate/raw/refs" +
                    "/heads/master/others/version_info.txt"
    }

    /**
     * Checks if a newer version of the app is available online by comparing
     * the current local version code with the one fetched from a remote file.
     *
     * @return true if an update is available, false otherwise
     */
    suspend fun isLatestVersionAvailable(): Boolean {
        val currentLocalVersionCode = AppVersionUtility.versionCode

        val remoteVersionCode = fetchOnlineVersionCode()
        return remoteVersionCode?.let { it > currentLocalVersionCode } ?: false
    }

    /**
     * Fetches the version code from the remote `version_info.txt` file.
     * Assumes the file contains only a plain integer version code.
     *
     * @return the remote version code as an Int, or null if an error occurs
     */
    private suspend fun fetchOnlineVersionCode(): Int? = withContext(Dispatchers.IO) {
        return@withContext try {
            val url = URL(GITHUB_VERSION_INFO_FILE_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            connection.inputStream.bufferedReader().useLines { lines ->
                lines.firstOrNull { it.startsWith("latest_version=") }
                    ?.substringAfter("=")
                    ?.trim()
                    ?.toIntOrNull()
            }
        } catch (error: Exception) {
            logger.e(error)
            null
        }
    }

}