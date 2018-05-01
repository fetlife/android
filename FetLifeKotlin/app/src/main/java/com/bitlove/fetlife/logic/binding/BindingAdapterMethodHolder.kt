package com.bitlove.fetlife.logic.binding

import android.databinding.BindingAdapter
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.text.Html
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.view.get
import androidx.view.size
import com.android.databinding.library.baseAdapters.BR
import com.bitlove.fetlife.R
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.logic.interactionhandler.CardViewInteractionHandler
import com.bitlove.fetlife.logic.dataholder.ReactionViewDataHolder
import com.bitlove.fetlife.view.generic.MediaCardAdapter
import com.bitlove.fetlife.view.widget.ImageActivity
import com.facebook.drawee.view.SimpleDraweeView
import com.mikepenz.iconics.IconicsDrawable

@BindingAdapter("comments", "commentsDisplayed", "maxCommentCount")
fun setComments(viewGroup: ViewGroup,
                comments: List<ReactionViewDataHolder>?, commentsDisplayed: Boolean?, maxCommentCount: Int?) {

    if (viewGroup.visibility == View.GONE) return
    if (comments == null || comments.isEmpty()) {viewGroup.removeAllViews();return}

    //TODO comments : solve max comment count
    val maxComments = Math.min(comments.size,maxCommentCount?:0)
    if(viewGroup.childCount > maxComments) {
        viewGroup.removeViews(maxComments,viewGroup.childCount-maxComments)
    }

    val inflater = viewGroup.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    for (i in 0 until maxComments) {
        val binding = if (viewGroup.size > i) viewGroup[i].tag as ViewDataBinding
                else DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.item_data_card_comment, viewGroup, true)
        viewGroup[i].tag = binding
        binding.setVariable(BR.commentData, comments[i+(comments.size-maxComments)])
    }

//        //Truncate Code
//        if (commentsDisplayed != true && comment.body != null) {
//            if (comment.body!!.length > Reaction.TRUNCATED_LENGTH) {
//                comment.body = comment.body!!.substring(0, Comment.TRUNCATED_LENGTH)
//                comment.body = comment.body!!.substring(0,comment.body!!.lastIndexOf(' ')) + Comment.TRUNCATED_SUFFIX
//            }
//        }

}

@BindingAdapter( "parentCard", "childrenInteractionHandler", "limitCardCount","childrenCardCount")
fun setChildrenCards(mediaGridView: AdapterView<ListAdapter>, parentCard: CardViewDataHolder?, childrenInteractionHandler: CardViewInteractionHandler?, limitCardCount : Boolean = false, childrenCardCount: Int = 6) {

    if (mediaGridView.visibility == View.GONE) return

    var mediaCardAdapter = MediaCardAdapter()
    mediaGridView.adapter = mediaCardAdapter

    mediaCardAdapter.maxCount = if (limitCardCount) childrenCardCount else Int.MAX_VALUE
    mediaCardAdapter.mediaCards = parentCard?.getChildren()
    if (childrenInteractionHandler?.expandable?.get() == true || parentCard?.getChildren() != null && (parentCard!!.getChildren()!!.size > childrenCardCount)) {
        childrenInteractionHandler!!.expandable.set(true)
    }
    mediaCardAdapter.clickListener = {position -> childrenInteractionHandler?.onOpenChildrenCard(position, parentCard)}

}

@BindingAdapter("textInteractionHandler", "formattedText", "truncateText", "truncatedLength", "removeLineBreaks"/*, "textEntities", "mediaEntityHolder"*/)
fun setFormattedText(textView: TextView?, textInteractionHandler: CardViewInteractionHandler?, formattedText: String?, truncateText : Boolean? = false, truncatedLength : Int = 200, removeLineBreaks : Boolean? = false/*, textEntities : String? = null, mediaEntityHolder : Int? = 0*/) {
    var minCharacterTreshold = 10
    var ellipsizeChr = "…"

    if (formattedText == null) {
        textView?.text = null
        return
    }

    var formattedString = if (removeLineBreaks == true) {
        formattedText?.replace("\\s".toRegex()," ")
    } else {
        formattedText
    }

    val maxLength = Math.min(formattedString.length,truncatedLength)

    formattedString = if (truncateText == true && formattedString.length > (maxLength + minCharacterTreshold)) {
        textInteractionHandler?.expandable?.set(true)
        formattedText?.substring(0,maxLength) + ellipsizeChr
    } else {
        formattedText
    }

    //TODO Add Text entities to the appropriate view
    textView?.text = Html.fromHtml(formattedString)
}

@BindingAdapter("visible")
fun setVisibility(view: View?, visible: Boolean) {
    view?.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("ico_icon_bind","ico_size_bind")
fun setIcon(view: ImageView, refText: String, sizeDp: Int) {
    val iconicsDrawable = IconicsDrawable(view.context,refText).sizeDp(sizeDp)
    view.setImageDrawable(iconicsDrawable)
}

//TODO: consider using screen height as max height to make sure pictures fit
@BindingAdapter("srcFresco")
fun setFrescoSrc(imageView: SimpleDraweeView, srcFresco: String?) {
    imageView.setImageURI(srcFresco)
}

@BindingAdapter("arFresco")
fun setFrescoAr(simpleDraweeView: SimpleDraweeView, arFresco: Float?) {
    simpleDraweeView.aspectRatio = arFresco?:16f/9f
}

@BindingAdapter("onSubmitHandler","onSubmitData")
fun bindSubmitComment(editText: EditText, onSubmitHandler: CardViewInteractionHandler?, onSubmitData: CardViewDataHolder?) {
    if (onSubmitData == null || onSubmitHandler == null) {
        return
    }
    editText.setOnEditorActionListener { v, actionId, event ->
        if (event != null && event.isShiftPressed) {
            false
        }
        onSubmitHandler.onSendComment(editText,onSubmitData)
        val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
        true
    }
}

//TODO: check out other list bindings:
// https://medium.com/google-developers/android-data-binding-list-tricks-ef3d5630555e
// https://github.com/evant/binding-collection-adapter

//Generic List Binding
//@BindingAdapter("entries", "layout")
//fun <T> setComments(viewGroup: ViewGroup,
//                   entries: List<T>?, layoutId: Int) {
//    viewGroup.removeAllViews()
//    if (entries == null) {
//        return
//    }
//    val inflater = viewGroup.context
//            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//    for (i in entries.indices) {
//        val entry = entries[i]
//        val binding = DataBindingUtil
//                .inflate<ViewDataBinding>(inflater, layoutId, viewGroup, true)
//        binding.setVariable(BR.data, entry)
//    }
//}