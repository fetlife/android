package com.bitlove.fetlife.nativeapp.view.screen.resource;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Event;
import com.bitlove.fetlife.nativeapp.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.nativeapp.util.DateUtil;

import java.text.SimpleDateFormat;

public class EventInfoFragment extends LoadFragment {

    private static final String ARG_EVENT_ID = "ARG_EVENT_ID";
    private static final String DATE_INTERVAL_SEPARATOR = " - ";
    private Event event;

    private TextView locationTextView;
    private TextView addressTextView;
    private TextView dateTextView;
    private TextView dresscodeTextView;
    private TextView costTextView;
    private TextView descriptionTextView;

    public static EventInfoFragment newInstance(String eventId) {
        EventInfoFragment aboutFragment = new EventInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        aboutFragment.setArguments(args);
        return aboutFragment;
    }

    private void loadAndSetDetails() {
        event = Event.loadEvent(getArguments().getString(ARG_EVENT_ID));
        if (event == null) {
            return;
        }
        locationTextView.setText(event.getLocation());
        addressTextView.setText(event.getAddress());
        String startDateTime = event.getStartDateTime();
        String endDateTime = event.getEndDateTime();
        long startTimeLong = !TextUtils.isEmpty(startDateTime) ? DateUtil.parseDate(startDateTime,true) : -1;
        long endTimeLong = !TextUtils.isEmpty(endDateTime) ? DateUtil.parseDate(endDateTime,true) : -1;
        if (startTimeLong > -1) {
            startDateTime = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.DEFAULT,SimpleDateFormat.SHORT).format(startTimeLong);
            if (endTimeLong > -1) {
                endDateTime = ((endTimeLong - startTimeLong) > 24*60*60*1000) ? SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.DEFAULT,SimpleDateFormat.SHORT).format(endTimeLong) : SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(endTimeLong);
            } else {
                endDateTime = "";
            }
        } else {
            startDateTime = "";
            if (endTimeLong > -1) {
                endDateTime = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.DEFAULT,SimpleDateFormat.SHORT).format(endTimeLong);
            } else {
                endDateTime = "";
            }
        }
        dateTextView.setText(startDateTime + DATE_INTERVAL_SEPARATOR + endDateTime);
        dresscodeTextView.setText(event.getDressCode());
        costTextView.setText(event.getCost());
        descriptionTextView.setText(event.getHtmlDescription());
//        descriptionTextView.setBackgroundColor(0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        locationTextView = (TextView) view.findViewById(R.id.text_event_value_location);
        addressTextView = (TextView) view.findViewById(R.id.text_event_value_address);
        dateTextView = (TextView) view.findViewById(R.id.text_event_value_date);
        dresscodeTextView = (TextView) view.findViewById(R.id.text_event_value_dresscode);
        costTextView = (TextView) view.findViewById(R.id.text_event_value_cost);
        descriptionTextView = (TextView) view.findViewById(R.id.text_event_description);
        loadAndSetDetails();
        return view;
    }

    @Override
    public String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_EVENT;
    }

    @Override
    public void startResourceCall(int pageCount, int requestedPage) {
        event = Event.loadEvent(getArguments().getString(ARG_EVENT_ID));
        if (event == null) {
            return;
        } else {
            FetLifeApiIntentService.startApiCall(getActivity(),getApiCallAction(),event.getId(),Integer.toString(pageCount),Integer.toString(requestedPage));
        }
    }

    @Override
    public void refreshUi() {
        loadAndSetDetails();
    }

}
