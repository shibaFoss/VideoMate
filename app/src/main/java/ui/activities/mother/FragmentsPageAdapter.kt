@file:Suppress("DEPRECATION")

package ui.activities.mother

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ui.fragments.records.RecordsFragment
import ui.fragments.category.CategoriesFragment
import ui.fragments.accounts.AccountsFragment
import ui.fragments.budgets.BudgetsFragment
import ui.fragments.analytics.AnalyticsFragment

class FragmentsPageAdapter(fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = 5

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> RecordsFragment()
            1 -> AnalyticsFragment()
            2 -> BudgetsFragment()
            3 -> AccountsFragment()
            4 -> CategoriesFragment()
            else -> RecordsFragment()
        }
    }
}