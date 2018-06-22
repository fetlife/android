package com.bitlove.fetlife.view.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.MenuItem
import com.bitlove.fetlife.R
import com.bitlove.fetlife.getSafeColor
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.logic.viewmodel.CardListViewModel
import com.bitlove.fetlife.model.dataobject.wrapper.*
import com.bitlove.fetlife.view.generic.CardListFragment
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import kotlinx.android.synthetic.main.include_appbar.*

class PhoneCardListActivity : PhoneNavigationActivity() {

    companion object {
        private const val EXTRA_CARD_LIST_TYPE = "EXTRA_CARD_LIST_TYPE"
        private const val EXTRA_CARD_ID = "EXTRA_CARD_IDS"
        private const val EXTRA_SCREEN_TITLE = "EXTRA_SCREEN_TITLE"

        fun start(context: Context, cardData: CardViewDataHolder) {
            val intent = Intent(context, PhoneCardListActivity::class.java)
            val cardType = when (cardData) {
                is Group -> CardListViewModel.CardListType.GROUPS_DISCUSSIONS
                else -> throw IllegalArgumentException()
            }
            intent.putExtra(EXTRA_CARD_LIST_TYPE, cardType)
            intent.putExtra(EXTRA_CARD_ID,cardData.getLocalId())
            intent.putExtra(EXTRA_SCREEN_TITLE,cardData.getTitle())

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun onResourceCreate(savedInstanceState: Bundle?) {
        this.savedInstanceState = savedInstanceState
        setLayoutResource()
        setStateContentFragment(savedInstanceState)
        setTitle()
        setActionBar()
    }

    override fun setTitle() {
        super.setTitle(intent.extras.getString(EXTRA_SCREEN_TITLE))
    }

    override fun createFragment(): Fragment {
        return CardListFragment.newInstance(CardListViewModel.CardListType.GROUPS_DISCUSSIONS,intent.extras?.getString(EXTRA_CARD_ID),intent.extras?.getString(EXTRA_SCREEN_TITLE))
    }

    override fun setActionBar() {
        super.setActionBar()
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_arrow_back).color(getSafeColor(R.color.toolbar_icon_color)).sizeDp(18))
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}