package com.bitlove.fetlife.view.dialog

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bitlove.fetlife.R
import com.bitlove.fetlife.putBoolean
import kotlinx.android.synthetic.main.dialogfragment_info.*

class InformationDialog : DialogFragment() {

    enum class InfoType {
        ALPHA_VERSION,
        NOT_IMPLEMENTED,
        EXPLORE,
        FAVORITES,
        WEB
    }

    companion object {
        private val FRAGMENT_TAG = InformationDialog::class.java.simpleName
        private const val ARG_INFO_ID = "ARG_INFO_ID"
        private const val ARG_INFO_TYPE = "ARG_INFO_TYPE"
        private const val ARG_PREF_BASED = "ARG_PREF_BASED"

        private val shownSet = HashSet<String>()

        private fun newInstance(infoType: InfoType, infoId: String, prefBased: Boolean): InformationDialog {
            val informationDialog = InformationDialog()
            val args = Bundle()
            args.putSerializable(ARG_INFO_ID, infoId)
            args.putSerializable(ARG_INFO_TYPE, infoType)
            args.putBoolean(ARG_PREF_BASED, prefBased)
            informationDialog.arguments = args
            return informationDialog
        }

        fun show(activity: FragmentActivity, infoType: InfoType, infoId: String, prefBased: Boolean) {
            if (activity.isFinishing) {
                return
            }

            if (shownSet.contains(infoId)) {
                return
            }
            shownSet.add(infoId)

            if (prefBased) {
                val prefs = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
                if (prefs.getBoolean(infoId,false)) {
                    return
                }
            }

            val ft: FragmentTransaction = activity.supportFragmentManager.beginTransaction()
            val prev = activity.supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)

            // Create and show the dialog.
            val newFragment = newInstance(infoType, infoId, prefBased)
            newFragment.show(ft, FRAGMENT_TAG)
        }
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialogfragment_info, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefBased = arguments!!.getBoolean(ARG_PREF_BASED)
        val infoId = arguments!!.getString(ARG_INFO_ID)
        val infoType = arguments!!.getSerializable(ARG_INFO_TYPE) as InfoType

        dialog_title.text = getTitle(infoType)
        dialog_message.text = getMessage(infoType)
        dialog_button.setText(android.R.string.ok)
        dialog_button.visibility = View.VISIBLE
        dialog_button.setOnClickListener {
            if (prefBased) {
                val prefs = PreferenceManager.getDefaultSharedPreferences(it.context.applicationContext)
                prefs.putBoolean(infoId,dialog_checkbox.isChecked)
            }
            dismissAllowingStateLoss()
        }
        dialog_checkbox.visibility = if (prefBased) View.VISIBLE else View.GONE
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDetach() {
        super.onDetach()
    }

    private fun getMessage(infoType: InfoType): String {
        return when(infoType) {
            InfoType.ALPHA_VERSION -> context!!.getString(R.string.info_dialog_alpha)
            InfoType.EXPLORE -> context!!.getString(R.string.info_dialog_explore)
            InfoType.FAVORITES -> context!!.getString(R.string.info_dialog_favorites)
            InfoType.NOT_IMPLEMENTED -> context!!.getString(R.string.info_dialog_not_implemented)
            InfoType.WEB -> context!!.getString(R.string.info_dialog_web)
        }
    }

    private fun getTitle(infoType: InfoType): String {
        return "Information"
    }

}