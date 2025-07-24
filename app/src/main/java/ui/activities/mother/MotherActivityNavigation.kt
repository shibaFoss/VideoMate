package ui.activities.mother

import android.graphics.PorterDuff.Mode.SRC_IN
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.core.content.res.ResourcesCompat.getFont
import libs.ui.ViewUtility.onBounceBackOnClick
import net.base.R

class MotherActivityNavigation(private val motherActivity: MotherActivity?) {

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

    fun initialize() {
        motherActivity?.let { safeActivityRef ->
            buttons?.let { buttons ->
                buttons.keys.forEach { id ->
                    val view = safeActivityRef.findViewById<View>(id)
                    view.onBounceBackOnClick { buttons[id]?.invoke() }
                }
            }
        }
    }

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
                        setColorFilter(getColor(context, R.color.transparent_white), SRC_IN)
                    }
                }

                safeActivityRef.findViewById<TextView>(ids[2])?.let { textTab ->
                    textTab.apply {
                        setTextColor(getColor(context, R.color.transparent_white))
                        typeface = getFont(context, R.font.sans_font_medium)
                    }
                }
            }

            buttonTabs[tab]?.let { ids ->
                safeActivityRef.findViewById<View>(ids[0])?.let { container ->
                    val bgDrawableResId = R.drawable.rd_secondary
                    val resources = safeActivityRef.resources
                    val activityTheme = safeActivityRef.theme
                    val buttonBg = getDrawable(resources, bgDrawableResId, activityTheme)
                    container.background = buttonBg
                    container.elevation = resources.getDimension(R.dimen._2)
                }

                safeActivityRef.findViewById<View>(ids[1])?.let { logoImage ->
                    (logoImage as ImageView).apply {
                        setColorFilter(getColor(context, R.color.color_on_secondary), SRC_IN)
                    }
                }

                safeActivityRef.findViewById<TextView>(ids[2])?.let { textTab ->
                    textTab.apply {
                        setTextColor(getColor(context, R.color.color_on_secondary))
                        typeface = getFont(context, R.font.sans_font_bold)
                    }
                }
            }
        }
    }

    enum class Tab {
        HOME_TAB,
        MUSIC_TAB,
        VIDEOS_TAB,
        DOWNLOADS_TAB,
        SETTINGS_TAB
    }
}