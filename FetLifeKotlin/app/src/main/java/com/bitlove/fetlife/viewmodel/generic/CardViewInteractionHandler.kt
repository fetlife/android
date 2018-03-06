package com.bitlove.fetlife.viewmodel.generic

import android.view.View
import android.widget.EditText
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.Comment
import java.util.*

class CardViewInteractionHandler {

    open fun onDisplayComments(v: View, cardData: CardViewDataHolder) {
        cardData.commentsDisplayed.set(!cardData.commentsDisplayed.get())
    }

    open fun onSendComment(editText: EditText, cardData: CardViewDataHolder) {
        //TODO add created at and current user
        val newComment = Comment(UUID.randomUUID().toString(), editText.text.toString(), cardData.getDataId(), false, null, null)
        FetLifeApplication.instance.fetlifeDataSource.sendComment(newComment)
    }

//    open fun onLove(v: View, cardData: CardViewDataHolder) {
//        FetLifeApplication.instance.fetlifeDataSource.toggleLove(cardData)
//    }



}