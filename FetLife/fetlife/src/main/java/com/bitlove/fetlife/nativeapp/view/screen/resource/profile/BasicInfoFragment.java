package com.bitlove.fetlife.nativeapp.view.screen.resource.profile;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Relationship;
import com.bitlove.fetlife.nativeapp.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.nativeapp.view.screen.BaseActivity;
import com.bitlove.fetlife.nativeapp.view.screen.resource.LoadFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicInfoFragment extends LoadFragment {

    private static final String ARG_MEMBER_ID = "ARG_REFERENCE_ID";
    private static final String LOCATION_SEPARATOR = ", ";

    private View locationRowView, relationshipRowView, orientationRowView, lookingForRowView;
    private TextView locationTextView, relationshipTextView, orientationTextView, lookingForTextView;

    public static BasicInfoFragment newInstance(String memberId) {
        BasicInfoFragment aboutFragment = new BasicInfoFragment();
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

        Relationship.loadForMember(member);

        String country = member.getCountry();
        String city = member.getCity();
        String administrativeArea = member.getAdministrativeArea();

        String location = "";
        if (country != null) {
            location = country;
        }
        if (administrativeArea != null) {
            location = administrativeArea + LOCATION_SEPARATOR + location;
        }
        if (city != null) {
            location = city + LOCATION_SEPARATOR + location;
        }
        if ("".equals(location)) {
            location = null;
        }

        locationRowView.setVisibility(location != null ? View.VISIBLE : View.GONE);
        locationTextView.setText(location);

        String orientation = member.getSexualOrientation();

        orientationRowView.setVisibility(orientation != null ? View.VISIBLE : View.GONE);
        orientationTextView.setText(getOrientationText(orientation));

        List<String> lookingFors = member.getLookingFor();
        if (lookingFors != null && !lookingFors.isEmpty()) {
            lookingForRowView.setVisibility(View.VISIBLE);
            String lookingForText = "";
            for (String lookingFor : lookingFors) {
                if (lookingFor == null || lookingFor.isEmpty()) {
                    continue;
                }
                lookingForText += getLookingForDisplayString(lookingFor) + "\n";
            }
            lookingForTextView.setText(lookingForText);
        } else {
            lookingForRowView.setVisibility(View.GONE);
            lookingForTextView.setText("");
        }

        List<Relationship> relationships = member.getRelationships();
        if (relationships != null && !relationships.isEmpty()) {
            relationshipRowView.setVisibility(View.VISIBLE);
            String relationshipsText = "";
            Map<String,String> nicknamesToSpan = new HashMap<>();
            for (Relationship relationship : relationships) {
                String targetMemberNickname = relationship.getTargetMemberNickname();
                String targetMemberId = relationship.getTargetMemberId();
                if (targetMemberNickname != null && targetMemberId != null) {
                    nicknamesToSpan.put(targetMemberNickname,targetMemberId);
                }
                relationshipsText += getRelationshipDisplayString(relationship) + "\n";
            }
            SpannableString spannableString = new SpannableString(relationshipsText);
            for (Map.Entry<String,String> nicknameToSpan : nicknamesToSpan.entrySet()) {
                String nickname = nicknameToSpan.getKey();
                final String id = nicknameToSpan.getValue();
                int index = -1;
                while (index < relationshipsText.length()) {
                    index = relationshipsText.indexOf(nickname,++index);
                    if (index == -1) {
                        break;
                    }
                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View textView) {
                            ProfileActivity.startActivity((BaseActivity) getActivity(),id);
                        }
                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setUnderlineText(false);
                        }
                    };
                    spannableString.setSpan(clickableSpan, index, index+nickname.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            relationshipTextView.setText(spannableString);
            relationshipTextView.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            relationshipRowView.setVisibility(View.GONE);
            relationshipTextView.setText("");
        }
    }

    private String getOrientationText(String orientation) {
        if (orientation == null) {
            return null;
        }
        switch (orientation) {
            case "Straight":
                return getString(R.string.text_profile_orientation_straight);
            case "Heteroflexible":
                return getString(R.string.text_profile_orientation_heteroflexible);
            case "Bisexual":
                return getString(R.string.text_profile_orientation_bisexual);
            case "Homoflexible":
                return getString(R.string.text_profile_orientation_homoflexible);
            case "Gay":
                return getString(R.string.text_profile_orientation_gay);
            case "Lesbian":
                return getString(R.string.text_profile_orientation_lesbian);
            case "Queer":
                return getString(R.string.text_profile_orientation_queer);
            case "Pansexual":
                return getString(R.string.text_profile_orientation_pansexual);
            case "Fluctuating/Evolving":
                return getString(R.string.text_profile_orientation_evolving);
            case "Asexual":
                return getString(R.string.text_profile_orientation_asexual);
            case "Unsure":
                return getString(R.string.text_profile_orientation_unsure);
            case "Not Applicable":
                return getString(R.string.text_profile_orientation_na);
            default:
                return orientation;
        }
    }

    private String getLookingForDisplayString(String lookingForText) {
        if (lookingForText == null) {
            return null;
        }
        switch (lookingForText) {
            case "lifetime_relationship":
                return getString(R.string.text_profile_lookingfor_ltr);
            case "relationship":
                return getString(R.string.text_profile_lookingfor_relationship);
            case "teacher":
                return getString(R.string.text_profile_lookingfor_teacher);
            case "someone_to_play_with":
                return getString(R.string.text_profile_lookingfor_playpartner);
            case "princess_by_day_slut_by_night":
                return getString(R.string.text_profile_lookingfor_princessslut);
            case "friendship":
                return getString(R.string.text_profile_lookingfor_friendship);
            case "slave":
                return getString(R.string.text_profile_lookingfor_slave);
            case "sub":
                return getString(R.string.text_profile_lookingfor_sub);
            case "master":
                return getString(R.string.text_profile_lookingfor_master);
            case "mistress":
                return getString(R.string.text_profile_lookingfor_mistress);
            case "fetnights":
                return getString(R.string.text_profile_lookingfor_events);
            default:
                return lookingForText;
        }
    }

    private String getRelationshipDisplayString(Relationship relationship) {
        if (relationship == null) {
            return null;
        }
        String targetMemberNickname = relationship.getTargetMemberNickname();
        String status = relationship.getStatus();
        if (status == null) {
            return null;
        }
        switch (status) {
            case "Single":
                return getString(R.string.text_profile_relationship_single);
            case "Dating":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_dating, targetMemberNickname) : getString(R.string.text_profile_relationship_dating);
            case "Friend With Benefits":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_friend_with_benefits, targetMemberNickname) : getString(R.string.text_profile_relationship_friend_with_benefits);
            case "Play Partners":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_play_partners, targetMemberNickname) : getString(R.string.text_profile_relationship_play_partners);
            case "In A Relationship":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_friend_with_in_a_relationship, targetMemberNickname) : getString(R.string.text_profile_relationship_in_a_relationship);
            case "In An Open Relationship":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_friend_with_in_an_open_relationship, targetMemberNickname) : getString(R.string.text_profile_relationship_in_an_open_relationship);
            case "Lover":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_friend_with_lover, targetMemberNickname) : getString(R.string.text_profile_relationship_lover);
            case "Engaged":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_friend_with_engaged, targetMemberNickname) : getString(R.string.text_profile_relationship_engaged);
            case "Married":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_friend_with_married, targetMemberNickname) : getString(R.string.text_profile_relationship_married);
            case "Widow":
                return getString(R.string.text_profile_relationship_widow);
            case "Widower":
                return getString(R.string.text_profile_relationship_widower);
            case "Monogamous":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_friend_with_monogamous, targetMemberNickname) : getString(R.string.text_profile_relationship_monogamous);
            case "Polyamorous":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_friend_with_polyamorous, targetMemberNickname) : getString(R.string.text_profile_relationship_polyamorous);
            case "In A Poly Group":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_friend_with_in_a_poly_group, targetMemberNickname) : getString(R.string.text_profile_relationship_in_a_poly_group);
            case "In A Leather Family":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_friend_with_in_a_leather_family, targetMemberNickname) : getString(R.string.text_profile_relationship_in_a_leather_family);
            case "In a Pack":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_friend_with_in_a_pack, targetMemberNickname) : getString(R.string.text_profile_relationship_in_a_pack);
            case "In a Rope Family":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_friend_with_in_a_rope_family, targetMemberNickname) : getString(R.string.text_profile_relationship_in_a_rope_family);
            case "Member Of A House":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_friend_with_member_of_a_house, targetMemberNickname) : getString(R.string.text_profile_relationship_member_of_a_house);
            case "Dominant":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_dominant, targetMemberNickname) : getString(R.string.text_profile_relationship_dominant);
            case "Sadist":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_sadist, targetMemberNickname) : getString(R.string.text_profile_relationship_sadist);
            case "Sadomasochist":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_sadomasochist, targetMemberNickname) : getString(R.string.text_profile_relationship_sadomasochist);
            case "Master":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_master, targetMemberNickname) : getString(R.string.text_profile_relationship_master);
            case "Mistress":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_mistress, targetMemberNickname) : getString(R.string.text_profile_relationship_mistress);
            case "Owner":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_owner, targetMemberNickname) : getString(R.string.text_profile_relationship_owner);
            case "Mistress and Owner":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_mistress_and_owner, targetMemberNickname) : getString(R.string.text_profile_relationship_mistress_and_owner);
            case "Master and Owner":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_master_owner, targetMemberNickname) : getString(R.string.text_profile_relationship_master_owner);
            case "Top":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_top, targetMemberNickname) : getString(R.string.text_profile_relationship_top);
            case "Daddy":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_daddy, targetMemberNickname) : getString(R.string.text_profile_relationship_daddy);
            case "Mommy":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_mommy, targetMemberNickname) : getString(R.string.text_profile_relationship_mommy);
            case "Brother":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_brother, targetMemberNickname) : getString(R.string.text_profile_relationship_brother);
            case "Sister":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_sister, targetMemberNickname) : getString(R.string.text_profile_relationship_sister);
            case "Being Served":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_being_served, targetMemberNickname) : getString(R.string.text_profile_relationship_being_served);
            case "Considering":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_considering, targetMemberNickname) : getString(R.string.text_profile_relationship_considering);
            case "Protecting":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_protecting, targetMemberNickname) : getString(R.string.text_profile_relationship_protecting);
            case "Mentoring":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_mentoring, targetMemberNickname) : getString(R.string.text_profile_relationship_mentoring);
            case "Teaching":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_teaching, targetMemberNickname) : getString(R.string.text_profile_relationship_teaching);
            case "Training":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_training, targetMemberNickname) : getString(R.string.text_profile_relationship_training);
            case "Switches":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_switches, targetMemberNickname) : getString(R.string.text_profile_relationship_switches);
            case "submissive":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_submissive, targetMemberNickname) : getString(R.string.text_profile_relationship_submissive);
            case "masochist":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_masochist, targetMemberNickname) : getString(R.string.text_profile_relationship_masochist);
            case "bottom":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_bottom, targetMemberNickname) : getString(R.string.text_profile_relationship_bottom);
            case "owned":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_owned, targetMemberNickname) : getString(R.string.text_profile_relationship_owned);
            case "owned and collared":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_owned_and_collared, targetMemberNickname) : getString(R.string.text_profile_relationship_owned_and_collared);
            case "collared":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_collared, targetMemberNickname) : getString(R.string.text_profile_relationship_collared);
            case "property":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_property, targetMemberNickname) : getString(R.string.text_profile_relationship_property);
            case "kajira":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_kajira, targetMemberNickname) : getString(R.string.text_profile_relationship_kajira);
            case "kajirus":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_kajirus, targetMemberNickname) : getString(R.string.text_profile_relationship_kajirus);
            case "in service":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_inservice, targetMemberNickname) : getString(R.string.text_profile_relationship_inservice);
            case "under protection":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_under_protection, targetMemberNickname) : getString(R.string.text_profile_relationship_under_protection);
            case "under consideration":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_under_consideration, targetMemberNickname) : getString(R.string.text_profile_relationship_under_consideration);
            case "pet":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_pet, targetMemberNickname) : getString(R.string.text_profile_relationship_pet);
            case "toy":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_toy, targetMemberNickname) : getString(R.string.text_profile_relationship_toy);
            case "boy":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_boy, targetMemberNickname) : getString(R.string.text_profile_relationship_boy);
            case "girl":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_girl, targetMemberNickname) : getString(R.string.text_profile_relationship_girl);
            case "babygirl":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_babygirl, targetMemberNickname) : getString(R.string.text_profile_relationship_babygirl);
            case "babyboy":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_babyboy, targetMemberNickname) : getString(R.string.text_profile_relationship_babyboy);
            case "brat":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_brat, targetMemberNickname) : getString(R.string.text_profile_relationship_brat);
            case "Keyholder":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_keyholder, targetMemberNickname) : getString(R.string.text_profile_relationship_keyholder);
            case "in chastity":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_inchastity, targetMemberNickname) : getString(R.string.text_profile_relationship_inchastity);
            case "being mentored":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_being_mentored, targetMemberNickname) : getString(R.string.text_profile_relationship_being_mentored);
            case "student":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_student, targetMemberNickname) : getString(R.string.text_profile_relationship_student);
            case "trainee":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_trainee, targetMemberNickname) : getString(R.string.text_profile_relationship_trainee);
            case "unowned":
                return getString(R.string.text_profile_relationship_unowned);
            case "unpartnered":
                return getString(R.string.text_profile_relationship_unpartnered);
            case "It's Complicated":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_its_complicated, targetMemberNickname) : getString(R.string.text_profile_relationship_its_complicated);
            case "Presently Inactive":
                return getString(R.string.text_profile_relationship_presently_inactive);
            case "Not Applicable":
                return getString(R.string.text_profile_relationship_not_applicable);
            case "slave":
                return targetMemberNickname != null ? getString(R.string.text_profile_relationship_with_member_slave, targetMemberNickname) : getString(R.string.text_profile_relationship_slave);
            default:
                return targetMemberNickname != null ? status + " of " + targetMemberNickname : status;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_basicinfo, container, false);
        locationRowView = view.findViewById(R.id.text_profile_row_location);
        relationshipRowView = view.findViewById(R.id.text_profile_row_relationship);
        orientationRowView = view.findViewById(R.id.text_profile_row_orientation);
        lookingForRowView = view.findViewById(R.id.text_profile_row_lookingfor);
        locationTextView = (TextView) view.findViewById(R.id.text_profile_value_location);
        relationshipTextView = (TextView) view.findViewById(R.id.text_profile_value_relationship);
        orientationTextView = (TextView) view.findViewById(R.id.text_profile_value_orientation);
        lookingForTextView = (TextView) view.findViewById(R.id.text_profile_value_lookingfor);
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
