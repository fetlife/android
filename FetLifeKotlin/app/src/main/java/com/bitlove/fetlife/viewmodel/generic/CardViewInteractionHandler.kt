package com.bitlove.fetlife.viewmodel.generic

import android.arch.persistence.room.Ignore
import android.databinding.ObservableField
import android.view.View
import android.widget.EditText
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.DataObject
import com.bitlove.fetlife.model.dataobject.base.Comment
import com.bitlove.fetlife.model.dataobject.base.Conversation
import com.bitlove.fetlife.model.network.job.getresource.GetCommentListJob
import java.util.*

class CardViewInteractionHandler {

    @Ignore
    open var commentsDisplayed = ObservableField<Boolean>(false)

    @Ignore
    open var commentsLoadInProgress = ObservableField<Boolean>(false)


    open fun onDisplayComments(v: View, cardData: CardViewDataHolder) {
        commentsDisplayed.set(!commentsDisplayed.get())

        //commentsLoadInProgress.set(true)
        //TODO verify this cast
        FetLifeApplication.instance.jobManager.addJobInBackground(GetCommentListJob(cardData as DataObject))
    }

    open fun onSendComment(editText: EditText, cardData: CardViewDataHolder) {
        //TODO add created at and current user
        val newComment = Comment(UUID.randomUUID().toString(), editText.text.toString(), cardData.getAppId(), false, null, null)
        FetLifeApplication.instance.fetlifeDataSource.sendComment(newComment)
    }

    open fun onDeleteCard(v: View, cardData: CardViewDataHolder) {}

//    open fun onLove(v: View, cardData: CardViewDataHolder) {
//        FetLifeApplication.instance.fetlifeDataSource.toggleLove(cardData)
//    }



}