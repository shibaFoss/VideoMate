package ui.activities.mother

import androidx.viewpager.widget.ViewPager
import core.bases.GlobalBaseActivity
import libs.ui.builders.CustomOnPageChangeListener
import net.base.R
import ui.fragments.records.RecordsFragment
import ui.fragments.category.CategoriesFragment
import ui.fragments.accounts.AccountsFragment
import ui.fragments.budgets.BudgetsFragment
import ui.fragments.analytics.AnalyticsFragment
import java.lang.ref.WeakReference

class MotherActivity : GlobalBaseActivity() {

    private var weakMotherActivityRef = WeakReference(this)
    private val safeMotherActivityRef = weakMotherActivityRef.get()

    private lateinit var fragmentViewPager: ViewPager
    private lateinit var motherActivityNav: MotherActivityNav

    var recordsFragment: RecordsFragment? = null
    var analyticsFragment: AnalyticsFragment? = null
    var budgetsFragment: BudgetsFragment? = null
    var accountsFragment: AccountsFragment? = null
    var categoriesFragment: CategoriesFragment? = null

    override fun onRenderingLayout(): Int {
        return R.layout.activity_mother
    }

    override fun onAfterLayoutRendered() {
        safeMotherActivityRef?.let { _ ->
            setLightSystemBarTheme()
            setupFragmentViewpager()
            setupBottomTabs()
        }
    }

    override fun onResumeActivity() {
        updateButtonTabSelectionUI()
    }

    override fun onPauseActivity() {}

    override fun onBackPressActivity() {
        handleBackPressEvent()
    }

    override fun onDestroyActivity() {}

    fun openHomeFragment() {
        if (fragmentViewPager.currentItem != 0) {
            fragmentViewPager.currentItem = 0
        }
    }

    fun openMusicFragment() {
        if (fragmentViewPager.currentItem != 1) {
            fragmentViewPager.currentItem = 1
        }
    }

    fun openBrowserFragment() {
        if (fragmentViewPager.currentItem != 2) {
            fragmentViewPager.currentItem = 2
        }
    }

    fun openDownloadsFragment() {
        if (fragmentViewPager.currentItem != 3) {
            fragmentViewPager.currentItem = 3
        }
    }

    fun openSettingsFragment() {
        if (fragmentViewPager.currentItem != 4) {
            fragmentViewPager.currentItem = 4
        }
    }

    private fun setupFragmentViewpager() {
        fragmentViewPager = findViewById(R.id.mother_activity_viewpager)
        fragmentViewPager.offscreenPageLimit = 5
        fragmentViewPager.adapter = FragmentsPageAdapter(supportFragmentManager)
        fragmentViewPager.addOnPageChangeListener(object : CustomOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                updateButtonTabSelectionUI(position)
            }
        })
    }

    private fun setupBottomTabs() {
        safeMotherActivityRef?.let { safeMotherActivityRef ->
            motherActivityNav = MotherActivityNav(safeMotherActivityRef)
            motherActivityNav.initialize()
        }
    }

    private fun updateButtonTabSelectionUI(currentItem: Int = fragmentViewPager.currentItem) {
        doSomeVibration(30)
        when (currentItem) {
            0 -> motherActivityNav.updateTabSelectionUI(MotherActivityNav.Tab.HOME_TAB)
            1 -> motherActivityNav.updateTabSelectionUI(MotherActivityNav.Tab.MUSIC_TAB)
            2 -> motherActivityNav.updateTabSelectionUI(MotherActivityNav.Tab.BROWSER_TAB)
            3 -> motherActivityNav.updateTabSelectionUI(MotherActivityNav.Tab.DOWNLOADS_TAB)
            else -> motherActivityNav.updateTabSelectionUI(MotherActivityNav.Tab.SETTINGS_TAB)
        }
    }

    private fun handleBackPressEvent() {
        safeMotherActivityRef?.let {
            when (fragmentViewPager.currentItem) {
                1 -> openHomeFragment()
                2 -> openMusicFragment()
                3 -> openBrowserFragment()
                4 -> openDownloadsFragment()
                else -> {
                    exitActivityOnDoubleBackPress()
                }
            }
        }
    }
}