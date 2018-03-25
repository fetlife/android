package com.bitlove.fetlife.viewmodel.generic

import android.arch.persistence.room.Ignore
import android.databinding.ObservableField
import android.view.View
import android.widget.EditText
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.SyncObject

class CardViewInteractionHandler {

    @Ignore
    open var commentsDisplayed = ObservableField<Boolean>(false)

    @Ignore
    open var commentsLoadInProgress = ObservableField<Boolean>(false)


    open fun onDisplayComments(v: View, cardData: CardViewDataHolder) {
        if (cardData.getComments() == null || cardData.getComments()!!.isEmpty()) {
            commentsDisplayed.set(false)
        } else {
            commentsDisplayed.set(!commentsDisplayed.get())
        }

        //commentsLoadInProgress.set(true)
        //TODO verify this cast
        //FetLifeApplication.instance.jobManager.addJobInBackground(GetCommentListJob(cardData as SyncObject<ContentEntity>))
    }

    open fun onSendComment(view: View, cardData: CardViewDataHolder) {
        if (cardData is SyncObject<*> && view is EditText) {
            FetLifeApplication.instance.fetlifeDataSource.sendComment(view.text.toString(), cardData)
            view.setText("")
        }
    }

    open fun onLove(v: View, cardData: CardViewDataHolder) {
        //TODO implement
    }

    open fun onDeleteCard(v: View, cardData: CardViewDataHolder) {
        //TODO implement
    }

}