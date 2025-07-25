package ui.activities.mother

import android.graphics.PorterDuff.Mode.SRC_IN
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.core.content.res.ResourcesCompat.getFont
import net.base.R

/**
 * A utility class that manages the bottom navigation panel in [MotherActivity].
 * It handles initialization of navigation buttons and updates their UI based on selection.
 *
 * @param motherActivity Reference to the parent activity where the navigation is hosted.
 */
class MotherActivityNavigation(private val motherActivity: MotherActivity?) {

    /**
     * Lazily initialized map of button view IDs to their corresponding fragment opening logic.
     */
    private val buttons by lazy {
        motherActivity?.let { safeActivityRef ->
            mapOf(
                R.id.btn_home_tab to { safeActivityRef.openHomeFragment() },
                R.id.btn_music_tab to { safeActivityRef.openMusicFragment() },
                R.id.btn_videos_tab to { safeActivityRef.openVideosFragment() },
                R.id.btn_downloads_tab to { safeActivityRef.openDownloadsFragment() },
                R.id.btn_settings_tab to { safeActivityRef.openSettingsFragment() }
            )
        }
    }

    /**
     * Initializes the click listeners for the navigation buttons.
     * Applies a bounce-back animation on press and triggers associated navigation logic.
     */
    fun initialize() {
        motherActivity?.let { safeActivityRef ->
            buttons?.let { buttons ->
                buttons.keys.forEach { id ->
                    val view = safeActivityRef.findViewById<View>(id)
                    view.setOnClickListener{ buttons[id]?.invoke() }
                }
            }
        }
    }

    /**
     * Updates the visual appearance of the tab buttons based on the currently selected tab.
     * This includes changing background, elevation, icon color, and text style.
     *
     * @param tab The currently selected [Tab] enum value.
     */
    fun updateTabSelectionUI(tab: Tab) {
        motherActivity?.let { safeActivityRef ->
            val buttonTabs = mapOf(
                Tab.HOME_TAB to listOf(
                    R.id.btn_home_tab,
                    R.id.img_home_tab,
                    R.id.txt_home_tab
                ),
                Tab.MUSIC_TAB to listOf(
                    R.id.btn_music_tab,
                    R.id.img_music_tab,
                    R.id.txt_music_tab
                ),
                Tab.VIDEOS_TAB to listOf(
                    R.id.btn_videos_tab,
                    R.id.img_videos_tab,
                    R.id.txt_videos_tab
                ),
                Tab.DOWNLOADS_TAB to listOf(
                    R.id.btn_downloads_tab,
                    R.id.img_downloads_tab,
                    R.id.txt_downloads_tab
                ),
                Tab.SETTINGS_TAB to listOf(
                    R.id.btn_settings_tab,
                    R.id.img_settings_tab,
                    R.id.txt_settings_tab
                )
            )

            // Reset all tab buttons to unselected state
            buttonTabs.values.forEach { ids ->
                safeActivityRef.findViewById<View>(ids[0])?.let { container ->
                    val bgNegativeSelector = R.drawable.rd_transparent
                    val resources = safeActivityRef.resources
                    val activityTheme = safeActivityRef.theme
                    val inactiveButtonBg = getDrawable(resources, bgNegativeSelector, activityTheme)
                    container.background = inactiveButtonBg
                    container.elevation = resources.getDimension(R.dimen._0)
                }

                safeActivityRef.findViewById<View>(ids[1])?.let { logoImage ->
                    (logoImage as ImageView).apply {
                        setColorFilter(getColor(context, R.color.color_text_hint), SRC_IN)
                    }
                }

                safeActivityRef.findViewById<TextView>(ids[2])?.let { textTab ->
                    textTab.apply {
                        setTextColor(getColor(context, R.color.color_text_hint))
                        typeface = getFont(context, R.font.sans_font_medium)
                    }
                }
            }

            // Highlight the selected tab
            buttonTabs[tab]?.let { ids ->
                safeActivityRef.findViewById<View>(ids[0])?.let { container ->
                    val bgDrawableResId = R.drawable.rd_primary
                    val resources = safeActivityRef.resources
                    val activityTheme = safeActivityRef.theme
                    val buttonBg = getDrawable(resources, bgDrawableResId, activityTheme)
                    container.background = buttonBg
                    container.elevation = resources.getDimension(R.dimen._2)
                }

                safeActivityRef.findViewById<View>(ids[1])?.let { logoImage ->
                    (logoImage as ImageView).apply {
                        setColorFilter(getColor(context, R.color.color_on_primary), SRC_IN)
                    }
                }

                safeActivityRef.findViewById<TextView>(ids[2])?.let { textTab ->
                    textTab.apply {
                        setTextColor(getColor(context, R.color.color_on_primary))
                        typeface = getFont(context, R.font.sans_font_bold)
                    }
                }
            }
        }
    }

    /**
     * Enum class representing the available tabs in the bottom navigation bar.
     */
    enum class Tab {
        HOME_TAB,
        MUSIC_TAB,
        VIDEOS_TAB,
        DOWNLOADS_TAB,
        SETTINGS_TAB
    }
}