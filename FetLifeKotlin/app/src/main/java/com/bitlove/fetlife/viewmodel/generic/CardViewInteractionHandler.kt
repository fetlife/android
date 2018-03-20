package com.bitlove.fetlife.viewmodel.generic

import android.arch.persistence.room.Ignore
import android.databinding.ObservableField
import android.view.View
import android.widget.EditText
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.ContentEntity
import com.bitlove.fetlife.model.network.job.getresource.GetCommentListJob

class CardViewInteractionHandler {

    @Ignore
    open var commentsDisplayed = ObservableField<Boolean>(false)

    @Ignore
    open var commentsLoadInProgress = ObservableField<Boolean>(false)


    open fun onDisplayComments(v: View, cardData: CardViewDataHolder) {
        commentsDisplayed.set(!commentsDisplayed.get())

        //commentsLoadInProgress.set(true)
        //TODO verify this cast
        FetLifeApplication.instance.jobManager.addJobInBackground(GetCommentListJob(cardData as SyncObject<ContentEntity>))
    }

    open fun onSendComment(editText: EditText, cardData: CardViewDataHolder) {
        //TODO add created at and current user
    }

    open fun onLove(v: View, cardData: CardViewDataHolder) {
        //TODO implement
    }

    open fun onDeleteCard(v: View, cardData: CardViewDataHolder) {
        //TODO implement
    }

}