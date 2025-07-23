package ui.fragments.records

import android.os.Bundle
import android.view.View
import com.dinuscxj.progressbar.CircleProgressBar
import core.bases.GlobalBaseFragment
import net.base.R

class RecordsFragment : GlobalBaseFragment() {

    override fun getLayoutResId(): Int {
        return R.layout.fragment_records
    }

    override fun onAfterLayoutLoad(layoutView: View, state: Bundle?) {
        safeFragmentLayoutRef?.apply {
            findViewById<CircleProgressBar>(R.id.progress_monthly_spent).progress = 70

        }
    }

    override fun onResumeFragment() {}

    override fun onPauseFragment() {}

    override fun onDestroyFragment() {}

}