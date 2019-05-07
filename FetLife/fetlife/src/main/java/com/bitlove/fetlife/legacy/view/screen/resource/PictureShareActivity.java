package com.bitlove.fetlife.legacy.view.screen.resource;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Picture;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Picture_Table;
import com.bitlove.fetlife.legacy.view.adapter.SharePictureGridAdapter;
import com.bitlove.fetlife.legacy.view.widget.AutoAlignGridView;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

public class PictureShareActivity extends ResourceActivity {

    public static String RESULT_STRINGS_URLS = "RESULT_STRINGS_URLS";

    private static final int MAX_SUGGEST_COUNT = 150;
    private SharePictureGridAdapter shareAdapter;
    private SharePictureGridAdapter suggestsAdapter;

    @Override
    protected void onCreateActivityComponents() {

    }

    @Override
    protected void onSetContentView() {
        setContentView(R.layout.activity_picture_share);
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Picture> pictureShares = new Select().from(Picture.class).where(Picture_Table.onShareList.eq(true)).orderBy(Picture_Table.lastViewedAt,false).orderBy(Picture_Table.createdAt,false).queryList();
        AutoAlignGridView sharesGrid = (AutoAlignGridView) findViewById(R.id.picture_grid_sharelist);
        View sharesHeader = findViewById(R.id.picture_sharelist_header);
        if (pictureShares.isEmpty()) {
            sharesGrid.setVisibility(View.GONE);
            sharesHeader.setVisibility(View.GONE);
        } else {
            sharesGrid.setVisibility(View.VISIBLE);
            sharesHeader.setVisibility(View.VISIBLE);
        }
        shareAdapter = setGridAdapter(sharesGrid,pictureShares,true);

        List<Picture> pictureSuggests = new Select().from(Picture.class).where(Picture_Table.onShareList.notEq(true)).or(Picture_Table.onShareList.isNull()).orderBy(Picture_Table.lastViewedAt,false).orderBy(Picture_Table.createdAt,false).limit(MAX_SUGGEST_COUNT).queryList();
        AutoAlignGridView suggestionGrid = (AutoAlignGridView) findViewById(R.id.pictrue_grid_suggests);
        View suggestionHeader = findViewById(R.id.picture_suggestlist_header);
        if (pictureSuggests.isEmpty()) {
            suggestionGrid.setVisibility(View.GONE);
            suggestionHeader.setVisibility(View.GONE);
        } else {
            suggestionGrid.setVisibility(View.VISIBLE);
            suggestionHeader.setVisibility(View.VISIBLE);
        }
        suggestsAdapter = setGridAdapter(suggestionGrid,pictureSuggests,false);
    }

    @Override
    protected void onResourceStart() {
    }

    private SharePictureGridAdapter setGridAdapter(AutoAlignGridView gridView, List<Picture> pictures, boolean removeWithLongClick) {
        gridView.setVisibility(View.VISIBLE);
        int columnCount = 3;
        gridView.setNumColumns(columnCount);
        SharePictureGridAdapter pictureGridAdapter = new SharePictureGridAdapter();
        pictureGridAdapter.setPictures(pictures);
        gridView.setAdapter(pictureGridAdapter);
        return pictureGridAdapter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                Intent result = new Intent();
                List<String> urls = new ArrayList<>();
                if (shareAdapter != null) {
                    urls.addAll(shareAdapter.getSelectedUrls());
                }
                if (suggestsAdapter != null) {
                    urls.addAll(suggestsAdapter.getSelectedUrls());
                }
                result.putExtra(RESULT_STRINGS_URLS,urls.toArray(new String[urls.size()]));
                setResult(RESULT_OK,result);
                finish();
                return true;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void startActivityForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, PictureShareActivity.class);
        activity.startActivityForResult(intent,requestCode);
    }
}
