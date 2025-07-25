@file:Suppress("DEPRECATION")

package ui.activities.mother

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ui.fragments.home.HomeFragment
import ui.fragments.settings.SettingsFragment
import ui.fragments.tasks.TasksFragment
import ui.fragments.videos.VideosFragment
import ui.fragments.music.MusicFragment

/**
 * A [FragmentPagerAdapter] used to supply fragments for each page/tab in a ViewPager.
 *
 * This adapter handles 5 fragments representing different sections of the app:
 * - Home
 * - Music
 * - Videos
 * - Tasks
 * - Settings
 *
 * Note: Uses the deprecated [FragmentPagerAdapter] with [BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT].
 * If possible, consider migrating to [androidx.viewpager2.adapter.FragmentStateAdapter].
 *
 * @param fragmentManager The FragmentManager that will interact with this adapter.
 */
class FragmentsPageAdapter(fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    /**
     * Returns the number of fragments/pages managed by the adapter.
     *
     * @return The total page count (5).
     */
    override fun getCount(): Int = 5

    /**
     * Returns the [Fragment] corresponding to the given position in the ViewPager.
     *
     * @param position The index of the fragment to retrieve (0 to 4).
     * @return The corresponding [Fragment] instance for the position.
     */
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> MusicFragment()
            2 -> VideosFragment()
            3 -> TasksFragment()
            4 -> SettingsFragment()
            else -> HomeFragment() // Fallback to HomeFragment if position is out of bounds
        }
    }
}