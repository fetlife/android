package com.bitlove.fetlife.view.generic

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bitlove.fetlife.R
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.toUniqueLong
import com.bitlove.fetlife.view.widget.SquareDraweeView
import org.jetbrains.anko.layoutInflater

class MediaCardAdapter : BaseAdapter() {

    var mediaCards : List<CardViewDataHolder>? = null
    var maxCount: Int = 0
    var clickListener: ((Int)->Unit)? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var mediaView : SquareDraweeView = if (convertView as? SquareDraweeView != null) {
            convertView
        } else {
            parent!!.context.layoutInflater.inflate(R.layout.item_media_grid,parent,false) as SquareDraweeView
        }
        mediaView.setImageURI(getItem(position).getThumbUrl())
        mediaView.setOnClickListener({clickListener?.invoke(position)})
        return mediaView
    }

    override fun getItem(position: Int): CardViewDataHolder {
        return mediaCards!![position]
    }

    override fun getItemId(position: Int): Long {
        return mediaCards?.get(position)?.getLocalId()?.toUniqueLong()?:0
    }

    override fun getCount(): Int {
        return Math.min(maxCount,mediaCards?.size?:0)
    }

}