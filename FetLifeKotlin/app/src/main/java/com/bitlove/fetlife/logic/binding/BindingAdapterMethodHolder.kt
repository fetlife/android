package com.bitlove.fetlife.logic.binding

import android.app.Activity
import android.arch.persistence.room.util.StringUtil
import android.databinding.BindingAdapter
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.graphics.Rect
import android.text.Html
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.view.get
import androidx.view.size
import com.android.databinding.library.baseAdapters.BR
import com.bitlove.fetlife.R
import com.bitlove.fetlife.countOccurance
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.logic.interactionhandler.CardViewInteractionHandler
import com.bitlove.fetlife.logic.dataholder.ReactionViewDataHolder
import com.bitlove.fetlife.view.generic.MediaCardAdapter
import com.bitlove.fetlife.view.widget.ImageActivity
import com.facebook.drawee.view.SimpleDraweeView
import com.mikepenz.iconics.IconicsDrawable
import org.apache.commons.lang3.StringUtils
import android.opengl.ETC1.getHeight
import android.text.Layout
import android.text.StaticLayout
import android.util.Log
import android.util.TypedValue
import com.bitlove.fetlife.getSafeColor
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ScalingUtils
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import org.jetbrains.anko.db.INTEGER


//TODO: clean this shit up
@BindingAdapter("comments", "commentsDisplayed", "maxCommentCount", "commentInteractionHandler", "reuseBindings")
fun setComments(viewGroup: ViewGroup,
                comments: List<ReactionViewDataHolder>?, commentsDisplayed: Boolean?, maxCommentCount: Int?, commentInteractionHandler: CardViewInteractionHandler?, reuseBindings: Boolean) {

//    val start = System.currentTimeMillis()

//    if (viewGroup.visibility == View.GONE) return
//    viewGroup.removeAllViews()
//    if (comments == null || comments.isEmpty()) {return}
//
//    val maxComments = Math.min(comments.size,maxCommentCount?:0)
//
//    val inflater = viewGroup.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//
//    for (i in 0 until maxComments) {
//        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.item_data_card_comment, viewGroup, true)
//        binding.setVariable(BR.commentData, comments[i+(comments.size-maxComments)])
//        binding.setVariable(BR.commentInteractionHandler, commentInteractionHandler)
//    }

    if (viewGroup.visibility == View.GONE) return
    if (comments == null || comments.isEmpty()) {viewGroup.removeAllViews();return}


    //TODO comments : solve max comment count
    val maxComments = Math.min(comments.size,maxCommentCount?:0)
    if (reuseBindings) {
        if(viewGroup.childCount > maxComments) {
            viewGroup.removeViews(maxComments,viewGroup.childCount-maxComments)
        }
    } else {
        viewGroup.removeAllViews()
    }

    val inflater = viewGroup.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var height = 0

    var width = viewGroup.resources.displayMetrics.widthPixels
    width -= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56F, viewGroup.resources.displayMetrics).toInt()
//    val maxRowHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48F, viewGroup.resources.displayMetrics).toInt()
    val maxRowHeight = Int.MAX_VALUE

    val minRowHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48F, viewGroup.resources.displayMetrics).toInt()

    for (i in 0 until maxComments) {
        val binding = if (reuseBindings && viewGroup.size > i) viewGroup[i].tag as ViewDataBinding
        else DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.item_data_card_comment, viewGroup, true)
        if (reuseBindings) viewGroup[i].tag = binding
        var rowHeight = 0
        val comment = comments[i+(comments.size-maxComments)]

        val avatarName = comment.getAvatar()?.getAvatarName()
        if (avatarName != null) {
            val bodyTextView = binding.root.findViewById<TextView>(R.id.avatar_name)
            val bounds = Rect()
            bodyTextView.paint.getTextBounds(avatarName,0,avatarName!!.length,bounds)
            rowHeight += bounds.height()
        }
        var body = comment.getText()
        if (body != null) {
            if (commentInteractionHandler?.truncateComment?.get() == true && maxComments == 1) {
                //TODO remove hardCoding
                body = body.substring(0,Math.min(body.length,250))
            }
            val bodyTextView = binding.root.findViewById<TextView>(R.id.comment_body)
            //val bounds = Rect()
            //bodyTextView.paint.getTextBounds(body,0,body!!.length,bounds)
            val layout = StaticLayout(body, bodyTextView.paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true)
            rowHeight += layout.height
        }
        rowHeight = Math.min(maxRowHeight,Math.max(rowHeight,minRowHeight))
        height += rowHeight
        binding.setVariable(BR.commentData, comment)
        binding.setVariable(BR.commentInteractionHandler, commentInteractionHandler)
    }

    viewGroup.minimumHeight = height

//        //Truncate Code
//        if (commentsDisplayed != true && comment.body != null) {
//            if (comment.body!!.length > Reaction.TRUNCATED_LENGTH) {
//                comment.body = comment.body!!.substring(0, Comment.TRUNCATED_LENGTH)
//                comment.body = comment.body!!.substring(0,comment.body!!.lastIndexOf(' ')) + Comment.TRUNCATED_SUFFIX
//            }
//        }

//    Log.w("PERF", "time: " + (System.currentTimeMillis()-start))

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
    var newLineHtml = "<br/>"

    if (formattedText == null) {
        textView?.text = null
        return
    }

    //TODO: cleanup, add ellipsize when needed

    var formattedString = formattedText.replace("\n", newLineHtml)
    var newLineFound = formattedString != formattedText
    var newLineCount = formattedString.countOccurance(newLineHtml)
    if (newLineFound && textView!!.maxLines < newLineCount+1) {
        formattedString = formattedString.substring(0,StringUtils.ordinalIndexOf(formattedString,newLineHtml,textView!!.maxLines-1)+newLineHtml.length) + ellipsizeChr
    }

    formattedString = Html.fromHtml(formattedString).toString().trim()

    formattedString = if (removeLineBreaks == true) {
        formattedString?.replace("\n"," ").replace("<br>".toRegex()," ")
    } else {
        formattedString
    }

    val maxLength = Math.min(formattedString.length,truncatedLength)

    formattedString = if (truncateText == true && formattedString.length > (maxLength + minCharacterTreshold)) {
        textInteractionHandler?.expandable?.set(true)
        formattedString?.substring(0,maxLength).trim() + ellipsizeChr
    } else if (formattedString.length > maxLength){
        formattedString + ellipsizeChr
    } else {
        formattedString
    }

    //TODO Add Text entities to the appropriate view
    textView?.text = formattedString
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
    val context = imageView.context
    val retryDrawable = IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_refresh).color(context.getSafeColor(R.color.silver_50)).sizeDp(42)
    val placeHolderDrawable = IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_timer).color(context.getSafeColor(R.color.nearBlack)).sizeDp(42)
    imageView.hierarchy.setRetryImage(retryDrawable, ScalingUtils.ScaleType.CENTER_INSIDE)
    imageView.hierarchy.setPlaceholderImage(placeHolderDrawable, ScalingUtils.ScaleType.CENTER_INSIDE)
    imageView.controller = Fresco.newDraweeControllerBuilder()
            .setTapToRetryEnabled(true)
            .setUri(srcFresco)
            .build()
}

@BindingAdapter("arFresco")
fun setFrescoAr(simpleDraweeView: SimpleDraweeView, arFresco: Float?) {
    simpleDraweeView.aspectRatio = arFresco?:16f/9f
}

@BindingAdapter("hitrectExtension")
fun setHitRect(view: View, hitrectExtension: Int) {
    val parent = view.parent as View
    parent.post({
        val rect = Rect()
        view.getHitRect(rect)
        rect.top -= hitrectExtension
        rect.left -= hitrectExtension
        rect.bottom += hitrectExtension
        rect.right += hitrectExtension

        parent.touchDelegate = TouchDelegate(rect,view)
    })
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
        onSubmitHandler.onSendComment(editText,onSubmitData,null)
        val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
        true
    }
}
@BindingAdapter("onSubmitFor","onSubmitHandler","onSubmitData","scrollWithData")
fun bindSubmitComment(view: View, onSubmitFor: Int, onSubmitHandler: CardViewInteractionHandler?, onSubmitData: CardViewDataHolder?, scrollWithData: Int?) {
    if (onSubmitData == null || onSubmitHandler == null) {
        return
    }
    view.setOnClickListener({
        v ->
        val editText = (v.context as Activity).findViewById<EditText>(onSubmitFor)
        onSubmitHandler.onSendComment(editText,onSubmitData,scrollWithData)
        val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    })
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