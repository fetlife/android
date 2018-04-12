package com.bitlove.fetlife.logic.interactionhandler

import android.databinding.ObservableField
import android.view.View
import android.widget.EditText
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.resource.get.GetReactionListResource
import com.bitlove.fetlife.view.navigation.NavigationCallback
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction
import org.jetbrains.anko.coroutines.experimental.bg

class CardViewInteractionHandler(cardData: CardViewDataHolder?, var cardList: List<CardViewDataHolder>? = null, private val position: Int = -1, private val navigationCallback: NavigationCallback? = null) {

    companion object {
        private const val TRESHOLD_SEND_COMMENT_TIME = 100L
        private const val COMMENT_PAGE_COUNT = 5
    }

    private var lastCommentSentAt: Long = 0L

    private var pageRequested = 1

    open var commentsDisplayed: ObservableField<Boolean?> = ObservableField(cardData?.displayComments()?:false)
    open var commentLoadInProgress: ObservableField<Boolean?> = ObservableField(false)
    open var commentCount: ObservableField<Int?> = ObservableField(COMMENT_PAGE_COUNT)

    open fun onOpenCard() {
        if (cardList != null) {
            navigationCallback?.onCardNavigate(cardList!!, position)
        }
    }

    open fun onDisplayComments(v: View, cardData: CardViewDataHolder) {
        if (commentsDisplayed.get() != true && cardData is SyncObject<*>) {
            //TODO(job_priority) reduce previous call priority if comments are closed
            startCommentCall(cardData)
        }

        commentsDisplayed.set(commentsDisplayed.get() != true)
    }

    private fun startCommentCall(cardData: SyncObject<*>) {
        //TODO use lifecycle observe
        val resourceResult = FetLifeApplication.instance.fetlifeDataSource.loadComments(cardData, pageRequested, COMMENT_PAGE_COUNT)
        resourceResult.liveData.observeForever{
            bg{cardData.save()}
        }
        resourceResult.progressTracker.observeForever(
                {progressTracker -> commentLoadInProgress.set(progressTracker != null && progressTracker.inProgress()) }
        )
    }

    open fun onGetMoreComments(view: View, cardData: CardViewDataHolder) {
        if (cardData is SyncObject<*>) {
            pageRequested++
            commentCount.set(pageRequested*COMMENT_PAGE_COUNT)
            startCommentCall(cardData)
        }
    }

    open fun onSendComment(view: View, cardData: CardViewDataHolder) {
        if (view is EditText) {

            val currentTime = System.currentTimeMillis()
            if ((currentTime-lastCommentSentAt) < TRESHOLD_SEND_COMMENT_TIME) {
                return
            }
            lastCommentSentAt = currentTime

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