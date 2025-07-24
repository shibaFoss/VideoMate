package ui.activities.mother

import android.graphics.PorterDuff.Mode.SRC_IN
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.core.content.res.ResourcesCompat.getFont
import net.base.R

class MotherActivityNav(private val motherActivity: MotherActivity?) {

    private val buttons by lazy {
        motherActivity?.let { safeActivityRef ->
            mapOf(
                R.id.btn_home_tab to { safeActivityRef.openHomeFragment() },
                R.id.btn_music_tab to { safeActivityRef.openMusicFragment() },
                R.id.btn_browser_tab to { safeActivityRef.openBrowserFragment() },
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
                    view.setOnClickListener { buttons[id]?.invoke() }
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
                Tab.BROWSER_TAB to listOf(
                    R.id.btn_browser_tab,
                    R.id.img_browser_tab,
                    R.id.txt_browser_tab
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

            buttonTabs[tab]?.let { ids ->
                safeActivityRef.findViewById<View>(ids[0])?.let { container ->
                    val bgDrawableResId = R.drawable.rd_transparent
                    val resources = safeActivityRef.resources
                    val activityTheme = safeActivityRef.theme
                    val buttonBg = getDrawable(resources, bgDrawableResId, activityTheme)
                    container.background = buttonBg
                }

                safeActivityRef.findViewById<View>(ids[1])?.let { logoImage ->
                    (logoImage as ImageView).apply {
                        setColorFilter(getColor(context, R.color.color_text_primary), SRC_IN)
                    }
                }

                safeActivityRef.findViewById<TextView>(ids[2])?.let { textTab ->
                    textTab.apply {
                        setTextColor(getColor(context, R.color.color_text_primary))
                        typeface = getFont(context, R.font.sans_font_bold)
                    }
                }
            }
        }
    }

    enum class Tab {
        HOME_TAB,
        MUSIC_TAB,
        BROWSER_TAB,
        DOWNLOADS_TAB,
        SETTINGS_TAB
    }
}