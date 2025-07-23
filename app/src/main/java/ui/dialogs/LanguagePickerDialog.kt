package ui.dialogs

import android.view.View
import android.view.View.inflate
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.RadioButton
import android.widget.RadioGroup
import core.bases.GlobalApplication.Companion.globalDatabaseHelper
import core.bases.GlobalApplication.Companion.globalLanguageHelper
import core.bases.GlobalBaseActivity
import libs.ui.builders.CustomDialogBuilder
import net.base.R
import java.lang.ref.WeakReference

class LanguagePickerDialog(private val baseActivity: GlobalBaseActivity) {
    private val safeGlobalBaseActivityRef by lazy { WeakReference(baseActivity).get() }

    private val languageSelectionDialog by lazy {
        CustomDialogBuilder(safeGlobalBaseActivityRef).apply {
            setView(R.layout.dialog_language_change)
        }
    }

    private var onApplyLanguage: () -> Unit? = {}

    init {
        languageSelectionDialog.setCancelable(true)
        languageSelectionDialog.view.apply {
            setAvailableLanguages(this)
            setButtonOnClickListeners(this)
        }
    }

    fun setOnApplyLanguageListener(listener: () -> Unit) {
        onApplyLanguage = listener
    }

    fun getDialogBuilder(): CustomDialogBuilder {
        return languageSelectionDialog
    }

    fun close() {
        if (languageSelectionDialog.isShowing) {
            languageSelectionDialog.close()
        }
    }

    fun show() {
        if (!languageSelectionDialog.isShowing) {
            languageSelectionDialog.show()
        }
    }

    private fun setAvailableLanguages(dialogLayoutView: View) {
        safeGlobalBaseActivityRef?.let { safeActivityRef ->
            removeAllRadioSelectionViews(dialogLayoutView)

            val languageList = globalLanguageHelper.languagesList
            languageList.forEachIndexed { index, (_, name) ->
                inflate(safeActivityRef, R.layout.dialog_language_change_item, null).apply {
                    (this as RadioButton).apply {
                        id = index
                        text = name

                        val radioButtonHeight = resources.getDimensionPixelSize(R.dimen._40)
                        layoutParams = LayoutParams(MATCH_PARENT, radioButtonHeight)

                        val horizontalPadding = resources.getDimensionPixelSize(R.dimen._5)
                        val verticalPadding = resources.getDimensionPixelSize(R.dimen._5)
                        setPadding(
                            horizontalPadding,
                            verticalPadding,
                            horizontalPadding,
                            verticalPadding
                        )
                    }

                    getLanguageRadioGroupView(dialogLayoutView).addView(this)
                }
            }

            val globalAppSettings = globalDatabaseHelper.getGlobalAppSettings()
            val currentLanguageCode = globalAppSettings?.userSelectedAppUILanguage
            val selectedIndex = languageList.indexOfFirst { it.first == currentLanguageCode }
            if (selectedIndex >= 0) {
                getLanguageRadioGroupView(dialogLayoutView)
                    .findViewById<RadioButton>(selectedIndex)?.isChecked = true
            }
        }
    }

    private fun removeAllRadioSelectionViews(dialogLayoutView: View) {
        getLanguageRadioGroupView(dialogLayoutView).removeAllViews()
    }

    private fun getLanguageRadioGroupView(view: View): RadioGroup {
        return view.findViewById(R.id.language_options_container)
    }

    private fun setButtonOnClickListeners(dialogLayoutView: View) {
        dialogLayoutView.findViewById<View>(R.id.btn_positive_container).apply {
            setOnClickListener { applySelectedApplicationLanguage(dialogLayoutView) }
        }
    }

    private fun applySelectedApplicationLanguage(dialogLayoutView: View) {
        val languageRadioGroup = getLanguageRadioGroupView(dialogLayoutView)
        val selectedLanguageId = languageRadioGroup.checkedRadioButtonId

        if (selectedLanguageId == -1) return
        val languageList = globalLanguageHelper.languagesList
        val (selectedLanguageCode, _) = languageList[selectedLanguageId]

        val globalAppSettings = globalDatabaseHelper.getGlobalAppSettings()
        globalAppSettings?.userSelectedAppUILanguage = selectedLanguageCode
        globalDatabaseHelper.saveGlobalData(settings = globalAppSettings)

        close()
        onApplyLanguage.invoke()
    }
}