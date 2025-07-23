package libs.ui.builders

import androidx.viewpager.widget.ViewPager

/**
 * A custom implementation of [ViewPager.OnPageChangeListener] that provides
 * empty method bodies for all callbacks.
 *
 * This class is useful as a base when you want to override only one or two
 * methods instead of all three.
 *
 * Example usage:
 * ```kotlin
 * viewPager.addOnPageChangeListener(object : CustomOnPageChangeListener() {
 *     override fun onPageSelected(position: Int) {
 *         // Handle page selected
 *     }
 * })
 * ```
 */
open class CustomOnPageChangeListener : ViewPager.OnPageChangeListener {

    /**
     * Called when the current page is being scrolled, either as part of a programmatically
     * initiated smooth scroll or a user-initiated touch scroll.
     *
     * @param position Index of the first page currently being displayed.
     * @param positionOffset Value from [0, 1) indicating the offset from the page at `position`.
     * @param positionOffsetPixels Value in pixels indicating the offset from the page at `position`.
     */
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        // Default empty implementation
    }

    /**
     * Called when a new page becomes selected.
     *
     * @param position Index of the newly selected page.
     */
    override fun onPageSelected(position: Int) {
        // Default empty implementation
    }

    /**
     * Called when the scroll state changes.
     *
     * Possible states are:
     * - [ViewPager.SCROLL_STATE_IDLE] : No interaction, animation completed
     * - [ViewPager.SCROLL_STATE_DRAGGING] : Actively being dragged
     * - [ViewPager.SCROLL_STATE_SETTLING] : In the process of settling to a final position
     *
     * @param state The new scroll state.
     */
    override fun onPageScrollStateChanged(state: Int) {
        // Default empty implementation
    }
}
