package com.bitlove.fetlife.logic.interactionhandler

import android.arch.persistence.room.Ignore
import android.databinding.ObservableField
import android.view.View
import android.widget.EditText
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.ContentEntity
import com.bitlove.fetlife.model.resource.get.CommentListResource
import com.bitlove.fetlife.view.navigation.NavigationCallback
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder

class CardViewInteractionHandler(private val cardData: CardViewDataHolder, private val cardList: List<CardViewDataHolder>? = null, private val position: Int = -1, private val navigationCallback: NavigationCallback? = null) {

    @Ignore
    open var commentsDisplayed: ObservableField<Boolean?> = ObservableField(cardData.displayComments())

    @Ignore
    open var commentsLoadInProgress = ObservableField<Boolean>(false)

    open fun onOpenCard() {
        if (cardList != null) {
            navigationCallback?.onCardNavigate(cardList, position)
        }
    }

    open fun onDisplayComments(v: View, cardData: CardViewDataHolder) {
        if (cardData is SyncObject<*> && commentsDisplayed.get() != true) {
            //TODO make it nicer
            CommentListResource(cardData as SyncObject<ContentEntity>,true,1,5).load().observeForever({
                commentList -> {cardData.save()}})
        }
//        if (cardData.getComments() == null || cardData.getComments()!!.isEmpty()) {
//            commentsDisplayed.set(false)
//        } else {
//            commentsDisplayed.set(commentsDisplayed.get() == false)
//        }

        commentsDisplayed.set(commentsDisplayed.get() != true)

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
        //TODO implement`
    }

}