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
                R.id.btn_records_tab to { safeActivityRef.openRecordsFragment() },
                R.id.btn_analytics_tab to { safeActivityRef.openAnalyticsFragment() },
                R.id.btn_budgets_tab to { safeActivityRef.openBudgetsFragment() },
                R.id.btn_accounts_tab to { safeActivityRef.openAccountsFragment() },
                R.id.btn_categories_tab to { safeActivityRef.openCategoriesFragment() }
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
                Tab.RECORDS_TAB to listOf(
                    R.id.btn_records_tab,
                    R.id.img_records_tab,
                    R.id.txt_records_tab
                ),
                Tab.ANALYTICS_TAB to listOf(
                    R.id.btn_analytics_tab,
                    R.id.img_analytics_tab,
                    R.id.txt_analytics_tab
                ),
                Tab.BUDGETS_TAB to listOf(
                    R.id.btn_budgets_tab,
                    R.id.img_budgets_tab,
                    R.id.txt_budgets_tab
                ),
                Tab.ACCOUNTS_TAB to listOf(
                    R.id.btn_accounts_tab,
                    R.id.img_accounts_tab,
                    R.id.txt_accounts_tab
                ),
                Tab.CATEGORIES_TAB to listOf(
                    R.id.btn_categories_tab,
                    R.id.img_categories_tab,
                    R.id.txt_categories_tab
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
                        typeface = getFont(context, R.font.sans_font_regular)
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
        RECORDS_TAB,
        ANALYTICS_TAB,
        BUDGETS_TAB,
        ACCOUNTS_TAB,
        CATEGORIES_TAB
    }
}