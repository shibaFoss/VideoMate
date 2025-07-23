package libs.networks;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;
import static android.content.Context.WIFI_SERVICE;
import static android.net.NetworkCapabilities.TRANSPORT_CELLULAR;
import static android.net.NetworkCapabilities.TRANSPORT_WIFI;
import static android.webkit.MimeTypeMap.getSingleton;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import core.bases.GlobalApplication;

/**
 * Utility class for network-related operations including connectivity checks,
 * URL processing, and network information retrieval.
 * @noinspection ALL
 */
public class NetworkUtility {

    /**
     * Checks if network connectivity is available.
     * Uses modern NetworkCapabilities API for Android Q+ and falls back to
     * NetworkInfo for older versions.
     *
     * @return true if network is available, false otherwise
     */
    public static boolean isNetworkAvailable() {
        Context applicationContext = GlobalApplication.APP_INSTANCE;
        ConnectivityManager connectivityManager = (ConnectivityManager)
                applicationContext.getSystemService(CONNECTIVITY_SERVICE);

        if (connectivityManager == null) return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NetworkCapabilities capabilities = connectivityManager.
                    getNetworkCapabilities(connectivityManager.getActiveNetwork());

            return capabilities != null &&
                    (capabilities.hasTransport(TRANSPORT_WIFI) ||
                            capabilities.hasTransport(TRANSPORT_CELLULAR));
        } else {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }

    /**
     * Checks if WiFi is enabled on the device.
     *
     * @return true if WiFi is enabled, false otherwise
     */
    public static boolean isWifiEnabled() {
        Application applicationContext = GlobalApplication.APP_INSTANCE;
        Object wifiService = applicationContext.getSystemService(WIFI_SERVICE);
        WifiManager wifiManager = (WifiManager) wifiService;
        return wifiManager.isWifiEnabled();
    }

    /**
     * Extracts the MIME type from a URL based on its file extension.
     *
     * @param url The URL to analyze
     * @return The MIME type if determinable, null otherwise
     */
    @Nullable
    public static String getMimeTypeFromUrl(@Nullable String url) {
        if (url == null) return null;
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (fileExtension == null) return null;
        MimeTypeMap mimeTypeMap = getSingleton();
        String fileExtensionString = fileExtension.toLowerCase();
        return mimeTypeMap.getMimeTypeFromExtension(fileExtensionString);
    }

    /**
     * Follows URL redirects to get the original URL.
     *
     * @param fileURL The URL that might redirect
     * @return The final URL after following redirects
     * @throws IOException if there's an error during the connection
     */
    @NonNull
    public static String getOriginalUrlFromRedirectedUrl
    (@NonNull String fileURL) throws IOException {
        URL Url = new URL(fileURL);
        HttpURLConnection connection = (HttpURLConnection) Url.openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (responseCode >= 300 && responseCode < 400) {
            String locationHeaderField = "Location";
            String originalUrl = connection.getHeaderField(locationHeaderField);
            if (originalUrl != null) {
                return originalUrl;
            }
        }
        return fileURL;
    }

    /**
     * Gets the name of the current network service provider.
     *
     * @return The network operator name, or empty string if unavailable
     */
    @NonNull
    public static String getNetworkServiceProvider() {
        GlobalApplication applicationContext = GlobalApplication.APP_INSTANCE;
        Object telephonyService = applicationContext.getSystemService(TELEPHONY_SERVICE);
        TelephonyManager manager = (TelephonyManager) telephonyService;
        if (manager != null) return manager.getNetworkOperatorName();
        else return "";
    }

    /**
     * Checks if a URL is accessible by making a HEAD request.
     *
     * @param urlString The URL to check
     * @return true if the URL responds with HTTP OK (200), false otherwise
     */
    public static boolean isUrlAccessible(@NonNull String urlString) {
        try {
            URLConnection urlConnection = new URL(urlString).openConnection();
            HttpURLConnection connection = (HttpURLConnection) urlConnection;
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (Throwable error) {
            error.printStackTrace();
            return false;
        }
    }

    /**
     * Normalizes a URL by ensuring it ends with a forward slash.
     * @deprecated This method's behavior may not be suitable for all URL normalization cases
     *
     * @param url The URL to normalize
     * @return The normalized URL
     */
    @Deprecated
    @NonNull
    public static String normalizeUrl(@NonNull String url) {
        if (!url.endsWith("/") && URLUtil.isValidUrl(url)) {
            return url.replaceAll("/$", "") + "/";
        }
        return url;
    }

    /**
     * Extracts unique domains from an array of URLs.
     *
     * @param urls Array of URLs to process
     * @return Array of unique domain names
     */
    @NonNull
    public static String[] extractUniqueDomains(@NonNull String[] urls) {
        List<String> uniqueDomains = new ArrayList<>();
        for (String url : urls) {
            String domain = Uri.parse(url).getHost();
            if (domain != null && !uniqueDomains.contains(domain)) {
                uniqueDomains.add(domain);
            }
        }
        return uniqueDomains.toArray(new String[0]);
    }
}