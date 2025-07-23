package core.bases

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import java.lang.ref.WeakReference

abstract class GlobalBaseFragment : Fragment() {

    open val safeGlobalBaseActivityRef: GlobalBaseActivity? by lazy {
        WeakReference(activity).get() as GlobalBaseActivity
    }

    private var _fragmentLayout: View? = null
    open var isFragmentRunning: Boolean = false
    open val safeFragmentLayoutRef: View?
        get() = WeakReference(_fragmentLayout).get()

    protected abstract fun getLayoutResId(): Int
    protected abstract fun onAfterLayoutLoad(layoutView: View, state: Bundle?)
    protected abstract fun onResumeFragment()
    protected abstract fun onPauseFragment()
    protected abstract fun onDestroyFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflate = inflater.inflate(getLayoutResId(), container, false)
        return inflate.also { _fragmentLayout = it }
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        onAfterLayoutLoad(view, bundle)
    }

    override fun onResume() {
        super.onResume()
        isFragmentRunning = true
        onResumeFragment()
    }

    override fun onPause() {
        super.onPause()
        isFragmentRunning = false
        onPauseFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onDestroyFragment()
        isFragmentRunning = false
        _fragmentLayout = null
    }


}