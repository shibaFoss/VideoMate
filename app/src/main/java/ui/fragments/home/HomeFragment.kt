package ui.fragments.home

import android.os.Bundle
import android.view.View
import core.bases.GlobalBaseFragment
import net.base.R

class HomeFragment : GlobalBaseFragment() {

    override fun getLayoutResId(): Int {
        return R.layout.fragment_base
    }

    override fun onAfterLayoutLoad(layoutView: View, state: Bundle?) {
        safeFragmentLayoutRef?.apply {

        }
    }

    override fun onResumeFragment() {}

    override fun onPauseFragment() {}

    override fun onDestroyFragment() {}

}