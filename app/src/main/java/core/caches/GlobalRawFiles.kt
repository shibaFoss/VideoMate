package core.caches

import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory.fromRawRes
import core.bases.GlobalApplication.Companion.APP_INSTANCE
import libs.process.ThreadsUtility.executeInBackground
import net.base.R

/**
 * A utility class to preload and cache raw resource files (like Lottie animations)
 * into memory for quick access during runtime.
 */
class GlobalRawFiles {

    /**
     * Cached instance of the Lottie composition for the loading animation.
     */
    private var loadingLottieComposition: LottieComposition? = null

    /**
     * Loads the raw Lottie animation resource asynchronously into memory.
     * This improves performance by avoiding repeated parsing during UI use.
     */
    fun loadRawFilesIntoMemory() {
        executeInBackground(codeBlock = {
            fromRawRes(APP_INSTANCE, R.raw.lottie_anim_loading)
                .addListener { composition ->
                    loadingLottieComposition = composition
                }
        })
    }

    /**
     * Returns the preloaded Lottie composition for the loading animation, if available.
     *
     * @return A [LottieComposition] instance or null if not yet loaded.
     */
    fun getLottieCompositionLoading(): LottieComposition? {
        return loadingLottieComposition
    }
}
