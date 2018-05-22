package com.bitlove.fetlife.view.generic

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bitlove.fetlife.view.dialog.InformationDialog

class EmptyFragment: Fragment() {

    companion object {
        private const val ARG_INFO_ID = "ARG_INFO_ID"

        fun newInstance(infoId: String) : EmptyFragment {
            val fragment = EmptyFragment()
            val arguments = Bundle()
            arguments.putString(ARG_INFO_ID,infoId)
            fragment.arguments = arguments
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return if (container != null) View(container?.context) else null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        InformationDialog.show(activity!!,InformationDialog.InfoType.NOT_IMPLEMENTED,arguments!!.getString(ARG_INFO_ID),true)
    }

}
