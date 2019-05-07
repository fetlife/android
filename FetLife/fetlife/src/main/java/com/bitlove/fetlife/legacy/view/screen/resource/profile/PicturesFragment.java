package com.bitlove.fetlife.legacy.view.screen.resource.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Picture;
import com.bitlove.fetlife.legacy.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.legacy.util.PictureUtil;
import com.bitlove.fetlife.legacy.util.UrlUtil;
import com.bitlove.fetlife.legacy.view.adapter.PicturesRecyclerAdapter;
import com.bitlove.fetlife.common.view.BaseActivity;
import com.bitlove.fetlife.legacy.view.screen.resource.LoadFragment;

public class PicturesFragment extends LoadFragment implements PictureUtil.OnPictureOverlayClickListener {

    private static final int PICTURE_GRID_COLUMN_COUNT = 3;
    public static int PAGE_COUNT = 24;

    public static PicturesFragment newInstance(String memberId) {
        PicturesFragment picturesFragment = new PicturesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REFERENCE_ID, memberId);
        picturesFragment.setArguments(args);
        return picturesFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        GridLayoutManager recyclerLayoutManager = new GridLayoutManager(getFetLifeApplication(), PICTURE_GRID_COLUMN_COUNT);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        PicturesRecyclerAdapter adapter = new PicturesRecyclerAdapter(getArguments().getString(ARG_REFERENCE_ID),this,getFetLifeApplication());
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_MEMBER_PICTURES;
    }

    @Override
    protected int getPageCount() {
        return PAGE_COUNT;
    }

    public void refreshUi() {
        if (recyclerView != null) {
            PicturesRecyclerAdapter recyclerViewAdapter = (PicturesRecyclerAdapter) recyclerView.getAdapter();
            recyclerViewAdapter.refresh();
        }
    }

    @Override
    public void onSharePicture(Picture picture, String url) {
        if (picture.isOnShareList()) {
            Picture.unsharePicture(picture);
        } else {
            Picture.sharePicture(picture);
        }
    }

    @Override
    public void onVisitPicture(Picture picture, String url) {
        UrlUtil.openUrl(getActivity(),url, true, false);
    }

    @Override
    public void onMemberClick(Member member) {
        ProfileActivity.startActivity((BaseActivity) getActivity(), member.getId());
    }
}
