package com.bitlove.fetlife.legacy.view.screen.resource.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Group;
import com.bitlove.fetlife.legacy.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.legacy.view.screen.resource.LoadFragment;

public class GroupInfoFragment extends LoadFragment {

    public enum GroupInfoEnum {
        RULES,
        DESCRIPTION
    }

    private static final String ARG_GROUP_ID = "ARG_GROUP_ID";
    private static final String ARG_GROUP_INFO_ENUM = "ARG_GROUP_INFO_ENUM";
    private Group group;

    private TextView descriptionTextView;

    public static GroupInfoFragment newInstance(String groupId, GroupInfoEnum groupInfoEnum) {
        GroupInfoFragment aboutFragment = new GroupInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_ID, groupId);
        args.putSerializable(ARG_GROUP_INFO_ENUM, groupInfoEnum);
        aboutFragment.setArguments(args);
        return aboutFragment;
    }

    private void loadAndSetDetails() {
        group = Group.loadGroup(getArguments().getString(ARG_GROUP_ID));
        if (group == null) {
            return;
        }
        GroupInfoEnum groupInfoEnum = (GroupInfoEnum) getArguments().getSerializable(ARG_GROUP_INFO_ENUM);
        CharSequence descText = groupInfoEnum == GroupInfoEnum.RULES ? group.getHtmlRules() : group.getHtmlDescription();
        descriptionTextView.setText(descText);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_info, container, false);
        descriptionTextView = (TextView) view.findViewById(R.id.text_group_description);
        loadAndSetDetails();
        return view;
    }

    @Override
    public String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_GROUP;
    }

    @Override
    public void startResourceCall(int pageCount, int requestedPage) {
        group = Group.loadGroup(getArguments().getString(ARG_GROUP_ID));
        if (group == null) {
            return;
        } else {
            FetLifeApiIntentService.startApiCall(getActivity(),getApiCallAction(),group.getId(),Integer.toString(pageCount),Integer.toString(requestedPage));
        }
    }

    @Override
    public void refreshUi() {
        loadAndSetDetails();
    }

}
