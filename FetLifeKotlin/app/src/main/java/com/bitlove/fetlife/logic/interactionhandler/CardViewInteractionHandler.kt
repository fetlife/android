package com.bitlove.fetlife.logic.interactionhandler

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.databinding.ObservableField
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.view.navigation.NavigationCallback
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.model.dataobject.SyncObject
import org.jetbrains.anko.coroutines.experimental.bg
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent.ACTION_SEND
import android.widget.ImageView
import android.widget.ScrollView
import com.bitlove.fetlife.model.dataobject.wrapper.*
import com.bitlove.fetlife.shareExternal
import com.bitlove.fetlife.view.widget.ImageActivity


class CardViewInteractionHandler {

    companion object {
        private const val TRESHOLD_USER_INTERACTION = 100L
        private const val COLLAPSED_CHILDREN_COUNT = 6
        private const val DETAIL_COMMENT_PAGE_COUNT = 20
        private const val LIST_COMMENT_PAGE_COUNT = 3
    }

    private lateinit var owner: LifecycleOwner

    private var lastUserInteraction: Long = 0L
    private var pageRequested = 1

    private var expandedByDefault : Boolean = false
    private var commentPageCount: Int
    private var navigationCallback: NavigationCallback? = null
    private var cardListTitle: String? = null
    var cardData : CardViewDataHolder?
    var cardList: List<CardViewDataHolder>? = null
    var position: Int = -1

    //TODO cleanup with external list/card refresh
    open var expandable: ObservableField<Boolean?> = ObservableField(false)
    open var commentLoadInProgress: ObservableField<Boolean?> = ObservableField(false)
    open var childrenCardCount: ObservableField<Int?> = ObservableField(COLLAPSED_CHILDREN_COUNT)
    open val hasActions: Boolean = true
    open var expanded: ObservableField<Boolean?>
    open var commentsDisplayed: ObservableField<Boolean?>
    open var commentCount: ObservableField<Int?>
    open var pagingComments: ObservableField<Boolean>
    open var displayTextEdit: ObservableField<Boolean>
    open var truncateComment: ObservableField<Boolean>

    constructor(owner: LifecycleOwner, cardData: CardViewDataHolder, expandedByDefault: Boolean = false, displayComments: Boolean = true, navigationCallback: NavigationCallback? = null, cardListTitle: String? = null, commentPageCount: Int = DETAIL_COMMENT_PAGE_COUNT, commentsPaging: Boolean = true, textEditDisplay : Boolean = false, truncatedComments: Boolean = false) {
        this.owner = owner
        this.cardData = cardData
        this.expandedByDefault = expandedByDefault
        this.navigationCallback = navigationCallback
        this.cardListTitle = cardListTitle
        this.commentPageCount = commentPageCount
        commentCount = ObservableField(commentPageCount)
        commentsDisplayed = ObservableField(displayComments)
        expanded = ObservableField(expandedByDefault)
        pagingComments = ObservableField(commentsPaging)
        displayTextEdit = ObservableField(textEditDisplay)
        truncateComment = ObservableField(truncatedComments)
        if (cardData?.getCommentCountText() != null && displayComments && commentsPaging && cardData is SyncObject<*>) startCommentCall(cardData as SyncObject<*>)
    }

    constructor(owner: LifecycleOwner, cardList: List<CardViewDataHolder>, position: Int, expandedByDefault: Boolean = false, displayComments: Boolean = false, navigationCallback: NavigationCallback? = null, cardListTitle: String? = null, commentPageCount: Int = LIST_COMMENT_PAGE_COUNT, commentsPaging: Boolean = true, textEditDisplay : Boolean = true, truncatedComments: Boolean = true) {
        this.owner = owner
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
        displayTextEdit = ObservableField(textEditDisplay)
        truncateComment = ObservableField(truncatedComments)
        if (cardData?.getCommentCountText() != null && displayComments && commentsPaging && cardData is SyncObject<*>) startCommentCall(cardData as SyncObject<*>)
    }

    open fun onOpenCard(scrollToComments: Boolean = false) {
        if (!checkInteractionTime()) return
        if (cardList != null) {
            navigationCallback?.onCardNavigate(cardList!!, position, cardListTitle, scrollToComments || pagingComments.get() == false)
        }
    }

    open fun onOpenChildrenCard(position: Int, cardData: CardViewDataHolder?) {
        if (!checkInteractionTime()) return
        if (cardData?.getChildren() != null) {
            navigationCallback?.onCardNavigate(cardData.getChildren()!!, position, cardData!!.getChildrenScreenTitle(), false)
        }
    }

    open fun onExpand() {
        if (!checkInteractionTime()) return
        expanded.set(expanded.get() != true)
        childrenCardCount.set(if (expanded.get() == true) Int.MAX_VALUE else COLLAPSED_CHILDREN_COUNT )
    }

    open fun onDisplayComments(v: View?, cardData: CardViewDataHolder) {
        if (!checkInteractionTime()) return
        if (commentsDisplayed.get() != true && cardData is SyncObject<*>) {
            //TODO(job_priority) reduce previous call priority if comments are closed
            startCommentCall(cardData)
        }

        commentsDisplayed.set(commentsDisplayed.get() != true)
    }

    open fun onShareExternal(v: View?, cardData: CardViewDataHolder) {
        if (!checkInteractionTime() || v == null) return
        cardData.getUrl()?.shareExternal(v!!.context)
    }

    private fun startCommentCall(cardData: SyncObject<*>) {
        //TODO use lifecycle observe
        val resourceResult = FetLifeApplication.instance.fetlifeDataSource.getCommentsLoader(cardData, pageRequested, commentPageCount)
        resourceResult.liveData.observe(owner, Observer{
            bg{/*cardData.save()*/}
        })
        resourceResult.progressTracker.observe(owner, Observer
                {progressTracker ->
                    if (progressTracker == null) return@Observer
                    commentLoadInProgress.set(progressTracker.inProgress())
                    if (progressTracker.isExecuted()) {
                        resourceResult.liveData.removeObservers(owner)
                        resourceResult.progressTracker.removeObservers(owner)
                    }
                }
        )
        resourceResult.execute()
    }

    open fun onGetMoreComments(view: View, cardData: CardViewDataHolder) {
        if (!checkInteractionTime()) return
        if (view.visibility != View.VISIBLE) return
        if (cardData is SyncObject<*>) {
            pageRequested++
            commentCount.set(pageRequested*commentPageCount)
            startCommentCall(cardData)
        }
    }

    open fun onMediaImageClick(view: View, cardData: CardViewDataHolder) {
        if (!checkInteractionTime()) return
        if (cardData.getMediaUrl() == null) return
        ImageActivity.start(cardData.getMediaUrl()!!,view.context)
    }

    open fun onSendComment(view: View, cardData: CardViewDataHolder, scrollToBottomViewId: Int?) {
        if (!checkInteractionTime()) return
        if (view is EditText) {
            val comment = view.text.toString()
            if (TextUtils.isEmpty(comment)) {
                return
            }

            val content = when (cardData) {
                is Content -> cardData
                is ExploreStory -> cardData.getChild() as? Content
                is ExploreEvent -> cardData.getChild() as? Content
                is Favorite -> cardData.getChild() as? Content
                else -> null
            } ?: return

            val resourceResult = FetLifeApplication.instance.fetlifeDataSource.sendComment(comment, content)
            resourceResult.progressTracker.observe(owner, Observer{progressTracker ->
                if (progressTracker?.getState() == ProgressTracker.STATE.QUEUED) {
                    if (view.isAttachedToWindow && scrollToBottomViewId != null) {
                        var scrollView = view.rootView.findViewById<ScrollView>(scrollToBottomViewId)
                        scrollView?.fullScroll(View.FOCUS_DOWN)
                    }
                    resourceResult.progressTracker.removeObservers(owner)
                }
            })
            resourceResult.execute()
            view.setText("")
        }
    }

    open fun onLove(v: View, cardData: CardViewDataHolder) {
        if (!checkInteractionTime()) return
        val content = when (cardData) {
            is Content -> cardData
            is ExploreStory -> cardData.getChild() as? Content
            is ExploreEvent -> cardData.getChild() as? Content
            is Favorite -> cardData.getChild() as? Content
            else -> null
        } ?: return
        if (content?.isLoved() == true) {
            FetLifeApplication.instance.fetlifeDataSource.removeLove(content).execute()
        } else {
            FetLifeApplication.instance.fetlifeDataSource.sendLove(content).execute()
        }
    }

    open fun onSetFavorite(v: View, cardData: CardViewDataHolder) {
        if (!checkInteractionTime()) return
        if (cardData is Favoritable) {
            val favoriteLoadResult = FetLifeApplication.instance.fetlifeDataSource.setFavorite(cardData)
            favoriteLoadResult.execute()
        }
    }

    open fun onMembership(v: View, cardData: CardViewDataHolder) {

    }

    open fun onDeleteCard(v: View, cardData: CardViewDataHolder) {
        if (!checkInteractionTime()) return
        //TODO implement`
    }

    private fun checkInteractionTime(): Boolean {
        val currentTime = System.currentTimeMillis()
        if ((currentTime-lastUserInteraction) < TRESHOLD_USER_INTERACTION) {
            return false
        }
        lastUserInteraction = currentTime
        return true
    }

}