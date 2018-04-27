package com.bitlove.fetlife.logic.interactionhandler

import android.databinding.ObservableField
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.view.navigation.NavigationCallback
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreEvent
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
import org.jetbrains.anko.coroutines.experimental.bg

class CardViewInteractionHandler {

    companion object {
        private const val TRESHOLD_USER_INTERACTION = 100L
        private const val COLLAPSED_CHILDREN_COUNT = 6
        private const val DETAIL_COMMENT_PAGE_COUNT = 9
        private const val LIST_COMMENT_PAGE_COUNT = 3
    }

    private var lastUserInteraction: Long = 0L
    private var pageRequested = 1

    private var cardData : CardViewDataHolder
    private var expandedByDefault : Boolean = false
    private var commentPageCount: Int
    private var navigationCallback: NavigationCallback? = null
    private var cardListTitle: String? = null
    var cardList: List<CardViewDataHolder>? = null
    private var position: Int = -1

    open var expandable: ObservableField<Boolean?> = ObservableField(false)
    open var commentLoadInProgress: ObservableField<Boolean?> = ObservableField(false)
    open var childrenCardCount: ObservableField<Int?> = ObservableField(COLLAPSED_CHILDREN_COUNT)
    open val hasActions: Boolean = true
    open var expanded: ObservableField<Boolean?>
    open var commentsDisplayed: ObservableField<Boolean?>
    open var commentCount: ObservableField<Int?>
    open var pagingComments: ObservableField<Boolean>

    constructor(cardData: CardViewDataHolder, expandedByDefault: Boolean = false, displayComments: Boolean = true, navigationCallback: NavigationCallback? = null, cardListTitle: String? = null, commentPageCount: Int = DETAIL_COMMENT_PAGE_COUNT, commentsPaging: Boolean = true) {
        this.cardData = cardData
        this.expandedByDefault = expandedByDefault
        this.navigationCallback = navigationCallback
        this.cardListTitle = cardListTitle
        this.commentPageCount = commentPageCount
        commentCount = ObservableField(commentPageCount)
        commentsDisplayed = ObservableField(displayComments)
        expanded = ObservableField(expandedByDefault)
        pagingComments = ObservableField(commentsPaging)
        if (displayComments && commentsPaging && cardData is SyncObject<*>) startCommentCall(cardData as SyncObject<*>)
    }

    constructor(cardList: List<CardViewDataHolder>, position: Int, expandedByDefault: Boolean = false, displayComments: Boolean = false, navigationCallback: NavigationCallback? = null, cardListTitle: String? = null, commentPageCount: Int = LIST_COMMENT_PAGE_COUNT, commentsPaging: Boolean = true) {
        this.cardData = cardList[position]
        this.expandedByDefault = expandedByDefault
        this.navigationCallback = navigationCallback
        this.cardListTitle = cardListTitle
        this.cardList = cardList
        this.position = position
        this.commentPageCount = commentPageCount
        commentCount = ObservableField(commentPageCount)
        commentsDisplayed = ObservableField(cardData?.displayComments()?:false)
        expanded = ObservableField(expandedByDefault)
        pagingComments = ObservableField(commentsPaging)
        if (displayComments && commentsPaging && cardData is SyncObject<*>) startCommentCall(cardData as SyncObject<*>)
    }

    open fun onOpenCard() {
        if (cardList != null) {
            navigationCallback?.onCardNavigate(cardList!!, position, cardListTitle)
        }
    }

    open fun onOpenChildrenCard(position: Int, cardData: CardViewDataHolder?) {
        if (cardData?.getChildren() != null) {
            navigationCallback?.onCardNavigate(cardData.getChildren()!!, position, cardData!!.getChildrenScreenTitle())
        }
    }

    open fun onExpand() {
        expanded.set(expanded.get() != true)
        childrenCardCount.set(if (expanded.get() == true) Int.MAX_VALUE else COLLAPSED_CHILDREN_COUNT )
    }

    open fun onDisplayComments(v: View?, cardData: CardViewDataHolder) {
        if (commentsDisplayed.get() != true && cardData is SyncObject<*>) {
            //TODO(job_priority) reduce previous call priority if comments are closed
            startCommentCall(cardData)
        }

        commentsDisplayed.set(commentsDisplayed.get() != true)
    }

    private fun startCommentCall(cardData: SyncObject<*>) {
        //TODO use lifecycle observe
        val resourceResult = FetLifeApplication.instance.fetlifeDataSource.getCommentsLoader(cardData, pageRequested, commentPageCount)
        resourceResult.liveData.observeForever{
            bg{/*cardData.save()*/}
        }
        resourceResult.progressTracker.observeForever(
                {progressTracker -> commentLoadInProgress.set(progressTracker != null && progressTracker.inProgress()) }
        )
        resourceResult.execute()
    }

    open fun onGetMoreComments(view: View, cardData: CardViewDataHolder) {
        if (cardData is SyncObject<*>) {
            pageRequested++
            commentCount.set(pageRequested*commentPageCount)
            startCommentCall(cardData)
        }
    }

    open fun onSendComment(view: View, cardData: CardViewDataHolder) {
        if (view is EditText) {
            val currentTime = System.currentTimeMillis()
            if ((currentTime-lastUserInteraction) < TRESHOLD_USER_INTERACTION) {
                return
            }
            lastUserInteraction = currentTime
            val comment = view.text.toString()
            if (TextUtils.isEmpty(comment)) {
                return
            }

            val content = when (cardData) {
                is Content -> cardData
                is ExploreStory -> cardData.getChild() as? Content
                is ExploreEvent -> cardData.getChild() as? Content
                else -> null
            } ?: return

            FetLifeApplication.instance.fetlifeDataSource.sendComment(comment, content).execute()
            view.setText("")
        }
    }

    open fun onLove(v: View, cardData: CardViewDataHolder) {
        val currentTime = System.currentTimeMillis()
        if ((currentTime-lastUserInteraction) < TRESHOLD_USER_INTERACTION) {
            return
        }
        val content = when (cardData) {
            is Content -> cardData
            is ExploreStory -> cardData.getChild() as? Content
            is ExploreEvent -> cardData.getChild() as? Content
            else -> null
        } ?: return
        lastUserInteraction = currentTime
        FetLifeApplication.instance.fetlifeDataSource.sendLove(content).execute()
    }

    open fun onDeleteCard(v: View, cardData: CardViewDataHolder) {
        //TODO implement`
    }

}