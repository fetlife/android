package com.bitlove.fetlife.nativeapp.view.screen.resource.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.nativeapp.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.nativeapp.view.screen.resource.LoadFragment;

public class AboutFragment extends LoadFragment {

    private static final String ARG_MEMBER_ID = "ARG_REFERENCE_ID";
    private TextView aboutTextView;

    public static AboutFragment newInstance(String memberId) {
        AboutFragment aboutFragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MEMBER_ID, memberId);
        aboutFragment.setArguments(args);
        return aboutFragment;
    }

    private void loadAndSetAbout() {
        Member member = Member.loadMember(getArguments().getString(ARG_MEMBER_ID));
        if (member == null) {
            return;
        }
        aboutTextView.setText(member.getHtmlAbout());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_about, container, false);
        aboutTextView = (TextView) view.findViewById(R.id.text_profile_about);
        loadAndSetAbout();
        return view;
    }

    @Override
    public String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_MEMBER;
    }

    @Override
    public void refreshUi() {
        loadAndSetAbout();
    }

}
