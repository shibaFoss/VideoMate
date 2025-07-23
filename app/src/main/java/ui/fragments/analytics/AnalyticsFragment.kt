package ui.fragments.analytics

import android.os.Bundle
import android.view.View
import core.bases.GlobalBaseFragment
import net.base.R

class AnalyticsFragment : GlobalBaseFragment() {
    override fun getLayoutResId(): Int {
        return R.layout.fragment_base
    }

    override fun onAfterLayoutLoad(layoutView: View, state: Bundle?) {}

    override fun onResumeFragment() {}

    override fun onPauseFragment() {}

    override fun onDestroyFragment() {}
}