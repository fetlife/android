package com.bitlove.fetlife.legacy.view.adapter.feed;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Event;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Group;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.GroupPost;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Picture;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Status;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Writing;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.FeedEvent;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.PeopleInto;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.Story;
import com.bitlove.fetlife.legacy.util.DateUtil;
import com.crashlytics.android.Crashlytics;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.text.SimpleDateFormat;
import java.util.List;

public class FeedItemResourceHelper {

    private final Story.FeedStoryType feedStoryType;
    private final Story feedStory;
    private FetLifeApplication fetLifeApplication;

    public FeedItemResourceHelper(FetLifeApplication fetLifeApplication, Story feedstory) {
        this.fetLifeApplication = fetLifeApplication;
        this.feedStoryType = feedstory.getType();
        this.feedStory = feedstory;
    }

    public Story.FeedStoryType getFeedStoryType() {
        return feedStoryType;
    }

    public String getHeader(List<FeedEvent> events) {
        int eventCount = events.size();
        FeedEvent feedEvent = events.get(0);
        switch (feedStoryType) {
            case STATUS_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_new_status) : fetLifeApplication.getString(R.string.feed_title_new_statuses, eventCount);
            case PEOPLE_INTO_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_people_into_fetish) : fetLifeApplication.getString(R.string.feed_title_people_into_fetishes, eventCount);
            case VIDEO_COMMENT_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_new_videocomment) : fetLifeApplication.getString(R.string.feed_title_new_videocomments, eventCount);
            case STATUS_COMMENT_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_new_statuscomment) : fetLifeApplication.getString(R.string.feed_title_new_statuscomments, eventCount);
            case GROUP_COMMENT_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_new_groupcomment) : fetLifeApplication.getString(R.string.feed_title_new_groupcomments, eventCount);
            case POST_COMMENT_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_new_postcomment) : fetLifeApplication.getString(R.string.feed_title_new_postcomments, eventCount);
            case GROUP_MEMBERSHIP_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_group_join) : fetLifeApplication.getString(R.string.feed_title_group_joins, eventCount);
            case POST_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_new_post) : fetLifeApplication.getString(R.string.feed_title_new_posts, eventCount);
            case GROUP_POST_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_new_grouppost) : fetLifeApplication.getString(R.string.feed_title_new_groupposts, eventCount);
            case WALL_POST_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_new_wallpost) : fetLifeApplication.getString(R.string.feed_title_new_wallposts, eventCount);
            case VIDEO_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_new_video) : fetLifeApplication.getString(R.string.feed_title_new_pictures, eventCount);
            case PICTURE_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_new_picture) : fetLifeApplication.getString(R.string.feed_title_new_pictures, eventCount);
            case LIKE_CREATED:
                FeedEvent firstEvent = feedStory.getEvents().get(0);
                if (firstEvent.getSecondaryTarget().getWriting() != null) {
                    return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_like_writing) : fetLifeApplication.getString(R.string.feed_title_like_writings, eventCount);
                } else if (firstEvent.getSecondaryTarget().getPicture() != null) {
                    return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_like_picture) : fetLifeApplication.getString(R.string.feed_title_like_pictures, eventCount);
                } else if (firstEvent.getSecondaryTarget().getVideo() != null) {
                    return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_like_video) : fetLifeApplication.getString(R.string.feed_title_like_videos, eventCount);
                } else if (firstEvent.getSecondaryTarget().getStatus() != null) {
                    return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_like_status) : fetLifeApplication.getString(R.string.feed_title_like_statuses, eventCount);
                } else {
                    throw new IllegalArgumentException("Like on not yet supported item");
                }
            case FRIEND_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_new_friend) : fetLifeApplication.getString(R.string.feed_title_new_friends, eventCount);
            case COMMENT_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_new_comment) : fetLifeApplication.getString(R.string.feed_title_new_comments, eventCount);
            case FOLLOW_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_new_follow) : fetLifeApplication.getString(R.string.feed_title_new_follows, eventCount);
            case RSVP_CREATED:
                switch (feedEvent.getTarget().getRsvp().getRsvpStatus()) {
                    case YES:
                        return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_rsvp_yes) : fetLifeApplication.getString(R.string.feed_title_rsvps_yes, eventCount);
                    case MAYBE:
                        return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_rsvp_maybe) : fetLifeApplication.getString(R.string.feed_title_rsvps_maybe, eventCount);
                    default:
                        return null;
                }
            default:
                return null;
        }
    }

    public boolean getExpandPreference() {
        int preferenceResource = -1;
        switch (feedStoryType) {
            case PICTURE_CREATED:
            case VIDEO_CREATED:
                preferenceResource = R.string.settings_key_feed_auto_expand_media;
                break;
            case POST_COMMENT_CREATED:
            case VIDEO_COMMENT_CREATED:
            case STATUS_COMMENT_CREATED:
            case COMMENT_CREATED:
            case GROUP_COMMENT_CREATED:
                preferenceResource = R.string.settings_key_feed_auto_expand_comment;
                break;
            case GROUP_POST_CREATED:
            case WALL_POST_CREATED:
            case POST_CREATED:
                preferenceResource = R.string.settings_key_feed_auto_expand_post;
                break;
            case STATUS_CREATED:
            case PEOPLE_INTO_CREATED:
                preferenceResource = R.string.settings_key_feed_auto_expand_profile;
                break;
            case RSVP_CREATED:
                preferenceResource = R.string.settings_key_feed_auto_expand_event;
                break;
            case GROUP_MEMBERSHIP_CREATED:
                preferenceResource = R.string.settings_key_feed_auto_expand_group;
                break;
            case LIKE_CREATED:
                preferenceResource = R.string.settings_key_feed_auto_expand_like;
                break;
            case FRIEND_CREATED:
            case FOLLOW_CREATED:
                preferenceResource = R.string.settings_key_feed_auto_expand_relation;
                break;
        }
        if (preferenceResource > 0) {
            return fetLifeApplication.getUserSessionManager().getActiveUserPreferences().getBoolean(fetLifeApplication.getString(preferenceResource),false);
        } else {
            return true;
        }
    }

    public Member getMember(List<FeedEvent> events) {
        if (events == null || events.isEmpty()) {
            return null;
        }
        return getMember(events.get(0));
    }

    public Member getMember(FeedEvent event) {
        try {
            switch (feedStoryType) {
                case PEOPLE_INTO_CREATED:
                    return event.getTarget().getPeopleInto().getMember();
                case GROUP_COMMENT_CREATED:
                case STATUS_COMMENT_CREATED:
                case VIDEO_COMMENT_CREATED:
                case POST_COMMENT_CREATED:
                    return event.getTarget().getComment().getMember();
                case GROUP_MEMBERSHIP_CREATED:
                    return event.getTarget().getGroupMembership().getMember();
                case STATUS_CREATED:
                    return event.getTarget().getStatus().getMember();
                case WALL_POST_CREATED:
                    return event.getTarget().getWallPost().getMember();
                case GROUP_POST_CREATED:
                    return event.getTarget().getGroupPost().getMember();
                case POST_CREATED:
                    return event.getTarget().getWriting().getMember();
                case VIDEO_CREATED:
                    return event.getTarget().getVideo().getMember();
                case PICTURE_CREATED:
                    return event.getTarget().getPicture().getMember();
                case LIKE_CREATED:
                    return event.getTarget().getLove().getMember();
                case FRIEND_CREATED:
                case FOLLOW_CREATED:
                    return event.getTarget().getRelation().getMember();
                case RSVP_CREATED:
                    return event.getTarget().getRsvp().getMember();
                case COMMENT_CREATED:
                    return event.getTarget().getComment().getMember();
                default:
                    return null;
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public Member getTargetMember(FeedEvent event) {
        try {
            switch (feedStoryType) {
                case FRIEND_CREATED:
                case FOLLOW_CREATED:
                    return event.getTarget().getRelation().getTargetMember();
                default:
                    return null;
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public Event getEvent(FeedEvent feedEvent) {
        try {
            switch (feedStoryType) {
                case RSVP_CREATED:
                    return feedEvent.getTarget().getRsvp().getEvent();
                default:
                    return null;
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public Writing getWriting(FeedEvent feedEvent) {
        try {
            switch (feedStoryType) {
                case POST_COMMENT_CREATED:
                case COMMENT_CREATED:
                case LIKE_CREATED:
                case POST_CREATED:
                    Writing writing = feedEvent.getTarget() != null ? feedEvent.getTarget().getWriting() : null;
                    if (writing == null) {
                        writing = feedEvent.getSecondaryTarget() != null ? feedEvent.getSecondaryTarget().getWriting() : null;
                    }
                    return writing;
                default:
                    return null;
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public Status getStatus(FeedEvent feedEvent) {
        try {
            switch (feedStoryType) {
                case STATUS_COMMENT_CREATED:
                case STATUS_CREATED:
                    Status status = feedEvent.getTarget() != null ? feedEvent.getTarget().getStatus() : null;
                    if (status == null) {
                        status = feedEvent.getSecondaryTarget() != null ? feedEvent.getSecondaryTarget().getStatus() : null;
                    }
                    return status;
                default:
                    return null;
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public Group getGroup(FeedEvent feedEvent) {
        try {
            switch (feedStoryType) {
                case GROUP_MEMBERSHIP_CREATED:
                    Group group = feedEvent.getSecondaryTarget().getGroup();
                    if (group == null) {
                        group = feedEvent.getTarget().getGroupMembership().getGroup();
                    }
                    return group;
                case GROUP_POST_CREATED:
                    group = feedEvent.getSecondaryTarget().getGroup();
                    if (group == null) {
                        group = feedEvent.getSecondaryTarget().getGroupPost().getGroup();
                    }
                    return group;
                case GROUP_COMMENT_CREATED:
                    group = feedEvent.getSecondaryTarget().getGroupPost().getGroup();
                    return group;
                default:
                    return null;
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public GroupPost getGroupPost(FeedEvent feedEvent) {
        try {
            switch (feedStoryType) {
                case GROUP_POST_CREATED:
                    GroupPost groupPost = feedEvent.getSecondaryTarget().getGroupPost();
                    return groupPost;
                case GROUP_COMMENT_CREATED:
                    groupPost = feedEvent.getSecondaryTarget().getGroupPost();
                    return groupPost;
                default:
                    return null;
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public Picture getPicture(FeedEvent feedEvent) {
        try {
            switch (feedStoryType) {
                case PICTURE_CREATED:
                    return feedEvent.getTarget().getPicture();
                case LIKE_CREATED:
                    if (feedEvent.getSecondaryTarget().getPicture() != null) {
                        return feedEvent.getSecondaryTarget().getPicture();
                    } else if (feedEvent.getSecondaryTarget().getVideo() != null) {
                        return feedEvent.getSecondaryTarget().getVideo().getThumbnail().getAsPicture(feedEvent.getSecondaryTarget().getVideo().getMember());
                    } else {
                        return null;
                    }
                case VIDEO_CREATED:
                    return feedEvent.getTarget().getVideo().getThumbnail().getAsPicture(feedEvent.getTarget().getVideo().getMember());
                case FRIEND_CREATED:
                case FOLLOW_CREATED:
                    return feedEvent.getSecondaryTarget().getMember().getAvatarPicture();
                case VIDEO_COMMENT_CREATED:
                    return feedEvent.getSecondaryTarget().getVideo().getThumbnail().getAsPicture(feedEvent.getSecondaryTarget().getVideo().getMember());
                case WALL_POST_CREATED:
                    return feedEvent.getTarget().getWallPost().getTargetMember().getAvatarPicture();
                case COMMENT_CREATED:
                    return feedEvent.getSecondaryTarget().getPicture();
                default:
                    return null;
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public String getServerTimeStamp(FeedEvent feedEvent) {
        try {
            String createdAt;
            switch (feedStoryType) {
                case PEOPLE_INTO_CREATED:
                    return createdAt = feedEvent.getTarget().getPeopleInto().getCreatedAt();
                case VIDEO_COMMENT_CREATED:
                case GROUP_COMMENT_CREATED:
                case STATUS_COMMENT_CREATED:
                case POST_COMMENT_CREATED:
                    return createdAt = feedEvent.getTarget().getComment().getCreatedAt();
                case GROUP_MEMBERSHIP_CREATED:
                    return createdAt = feedEvent.getTarget().getGroupMembership().getCreatedAt();
                case STATUS_CREATED:
                    return createdAt = feedEvent.getTarget().getStatus().getCreatedAt();
                case GROUP_POST_CREATED:
                    return createdAt = feedEvent.getTarget().getGroupPost().getCreatedAt();
                case WALL_POST_CREATED:
                    return createdAt = feedEvent.getTarget().getWallPost().getCreatedAt();
                case POST_CREATED:
                    return createdAt = feedEvent.getTarget().getWriting().getCreatedAt();
                case VIDEO_CREATED:
                    return createdAt = feedEvent.getTarget().getVideo().getCreatedAt();
                case PICTURE_CREATED:
                    return createdAt = feedEvent.getTarget().getPicture().getCreatedAt();
                case LIKE_CREATED:
                    return createdAt = feedEvent.getTarget().getLove().getCreatedAt();
                case COMMENT_CREATED:
                    return createdAt = feedEvent.getTarget().getComment().getCreatedAt();
                case FRIEND_CREATED:
                case FOLLOW_CREATED:
                    return createdAt = feedEvent.getTarget().getRelation().getCreatedAt();
                default:
                    return null;
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public String getCreatedAt(FeedEvent feedEvent) {
        String createdAt = getServerTimeStamp(feedEvent);
        if (createdAt !=  null) {
            return SimpleDateFormat.getDateTimeInstance().format(DateUtil.parseDate(createdAt));
        } else {
            return null;
        }
    }

    public String getUrl(FeedEvent feedEvent) {
        try {
            switch (feedStoryType) {
                case PEOPLE_INTO_CREATED:
                    return feedEvent.getTarget().getPeopleInto().getFetish().getUrl();
                case VIDEO_COMMENT_CREATED:
                    return feedEvent.getSecondaryTarget().getVideo().getUrl();
                case GROUP_COMMENT_CREATED:
                    return feedEvent.getSecondaryTarget().getGroupPost().getUrl();
                case POST_COMMENT_CREATED:
                    return feedEvent.getSecondaryTarget().getWriting().getUrl();
                case STATUS_COMMENT_CREATED:
                    return feedEvent.getSecondaryTarget().getStatus().getUrl();
                case GROUP_MEMBERSHIP_CREATED:
                    return getGroup(feedEvent).getUrl();
                case STATUS_CREATED:
                    return feedEvent.getTarget().getStatus().getUrl();
                case GROUP_POST_CREATED:
                    return feedEvent.getTarget().getGroupPost().getUrl();
                case WALL_POST_CREATED:
                    return feedEvent.getTarget().getWallPost().getUrl();
                case POST_CREATED:
                    return feedEvent.getTarget().getWriting().getUrl();
                case VIDEO_CREATED:
                    return feedEvent.getTarget().getVideo().getUrl();
                case PICTURE_CREATED:
                    return feedEvent.getTarget().getPicture().getUrl();
                case LIKE_CREATED:
                    if (feedEvent.getSecondaryTarget().getPicture() != null) {
                        return feedEvent.getSecondaryTarget().getPicture().getUrl();
                    } else if (feedEvent.getSecondaryTarget().getWriting() != null) {
                        return feedEvent.getSecondaryTarget().getWriting().getUrl();
                    } else if (feedEvent.getSecondaryTarget().getVideo() != null) {
                        return feedEvent.getSecondaryTarget().getVideo().getUrl();
                    } else if (feedEvent.getSecondaryTarget().getStatus() != null) {
                        return feedEvent.getSecondaryTarget().getStatus().getUrl();
                    } else {
                        return null;
                    }
                case COMMENT_CREATED:
                    return feedEvent.getSecondaryTarget().getPicture().getUrl();
                case RSVP_CREATED:
                    return feedEvent.getTarget().getRsvp().getEvent().getUrl();
                case FRIEND_CREATED:
                case FOLLOW_CREATED:
                    return feedEvent.getSecondaryTarget().getMember().getLink();
                default:
                    return null;
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public String getItemTitle(FeedEvent feedEvent) {
        try {
            switch (feedStoryType) {
                case LIKE_CREATED:
                    if (feedEvent.getSecondaryTarget().getWriting() != null) {
                        return feedEvent.getSecondaryTarget().getWriting().getTitle();
                    } else {
                        return null;
                    }
                case GROUP_COMMENT_CREATED:
                    return feedEvent.getSecondaryTarget().getGroupPost().getTitle();
                case POST_COMMENT_CREATED:
                    return feedEvent.getSecondaryTarget().getWriting().getTitle();
                case GROUP_MEMBERSHIP_CREATED:
                    return getGroup(feedEvent).getName();
                case GROUP_POST_CREATED:
                    return feedEvent.getTarget().getGroupPost().getTitle();
                case POST_CREATED:
                    return feedEvent.getTarget().getWriting().getTitle();
                case FOLLOW_CREATED:
                case FRIEND_CREATED:
                    return feedEvent.getSecondaryTarget().getMember().getNickname();
                case RSVP_CREATED:
                    return feedEvent.getTarget().getRsvp().getEvent().getName();
                case COMMENT_CREATED:
                    return null;
                default:
                    return null;
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public CharSequence getItemBody(FeedEvent feedEvent) {
        try {
            switch (feedStoryType) {
                case PEOPLE_INTO_CREATED:
                    String nickname = feedEvent.getTarget().getPeopleInto().getMember().getNickname();
                    String fetishName = feedEvent.getTarget().getPeopleInto().getFetish().getName();
                    PeopleInto peopleInto = feedEvent.getTarget().getPeopleInto();
                    try {
                        String body = peopleInto.getActivityEnum().toString(fetLifeApplication, nickname, fetishName, peopleInto.getStatusEnum().toString(fetLifeApplication));
                        if (body == null) {
                            throw new IllegalArgumentException();
                        }
                        return body;
                    } catch (Throwable t) {
                        //Tracking down unreproducible issue
                        String exceptionText;
                        try {
                            exceptionText = "Invalid People Into: " + peopleInto == null ? "null" : new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
                                @Override
                                public boolean hasIgnoreMarker(AnnotatedMember m) {
                                    return m.getDeclaringClass() == BaseModel.class || super.hasIgnoreMarker(m);
                                }
                            }).writeValueAsString(peopleInto);
                        } catch (JsonProcessingException e) {
                            exceptionText = e.getMessage();
                        }
                        Crashlytics.logException(new Exception(exceptionText));
                        return null;
                    }
                case LIKE_CREATED:
                    if (feedEvent.getSecondaryTarget().getWriting() != null) {
                        return feedEvent.getSecondaryTarget().getWriting().getHtmlBody();
                    } else if (feedEvent.getSecondaryTarget().getStatus() != null) {
                        return feedEvent.getSecondaryTarget().getStatus().getHtmlBody();
                    } else {
                        return null;
                    }
                case STATUS_COMMENT_CREATED:
                    return feedEvent.getSecondaryTarget().getStatus().getHtmlBody();
                case VIDEO_COMMENT_CREATED:
                    return feedEvent.getSecondaryTarget().getVideo().getBody();
                case GROUP_COMMENT_CREATED:
                    return feedEvent.getSecondaryTarget().getGroupPost().getHtmlBody();
                case STATUS_CREATED:
                    return feedEvent.getTarget().getStatus().getHtmlBody();
                case POST_COMMENT_CREATED:
                    return feedEvent.getSecondaryTarget().getWriting().getHtmlBody();
                case GROUP_MEMBERSHIP_CREATED:
                    return getGroup(feedEvent).getHtmlDescription();
                case GROUP_POST_CREATED:
                    return feedEvent.getTarget().getGroupPost().getHtmlBody();
                case WALL_POST_CREATED:
                    return feedEvent.getTarget().getWallPost().getHtmlBody();
                case POST_CREATED:
                    return feedEvent.getTarget().getWriting().getHtmlBody();
                case FOLLOW_CREATED:
                case FRIEND_CREATED:
                    return feedEvent.getSecondaryTarget().getMember().getMetaInfo();
                case RSVP_CREATED:
                    return feedEvent.getTarget().getRsvp().getEvent().getLocation() + " - " + feedEvent.getTarget().getRsvp().getEvent().getAddress();
                case COMMENT_CREATED:
                    return feedEvent.getTarget().getComment().getBody();
                default:
                    return null;
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public String getItemCaption(FeedEvent feedEvent) {
        try {
            switch (feedStoryType) {
                case VIDEO_COMMENT_CREATED:
                case POST_COMMENT_CREATED:
                case STATUS_COMMENT_CREATED:
                case GROUP_COMMENT_CREATED:
                    return feedEvent.getTarget().getComment().getBody();
                case GROUP_POST_CREATED:
                    return feedEvent.getTarget().getGroupPost().getGroup().getName();
                case GROUP_MEMBERSHIP_CREATED:
                    return fetLifeApplication.getString(R.string.feed_caption_member_count,getGroup(feedEvent).getMemberCount());
                case RSVP_CREATED:
                    return SimpleDateFormat.getDateTimeInstance().format(DateUtil.parseDate(feedEvent.getTarget().getRsvp().getEvent().getStartDateTime()));
                default:
                    return null;
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public boolean imageOnlyListItems() {
        switch (feedStoryType) {
            case PICTURE_CREATED:
            case VIDEO_CREATED:
                return true;
            case LIKE_CREATED:
                return (feedStory.getEvents().get(0).getSecondaryTarget().getWriting() == null && feedStory.getEvents().get(0).getSecondaryTarget().getStatus() == null);
            default:
                return false;
        }
    }

    public boolean listOnly() {
        switch (feedStoryType) {
            case LIKE_CREATED:
                return (feedStory.getEvents().get(0).getSecondaryTarget().getWriting() != null || feedStory.getEvents().get(0).getSecondaryTarget().getStatus() != null);
            case PEOPLE_INTO_CREATED:
            case GROUP_COMMENT_CREATED:
            case STATUS_CREATED:
            case POST_COMMENT_CREATED:
            case STATUS_COMMENT_CREATED:
            case GROUP_MEMBERSHIP_CREATED:
            case GROUP_POST_CREATED:
            case WALL_POST_CREATED:
            case POST_CREATED:
            case COMMENT_CREATED:
            case VIDEO_COMMENT_CREATED:
            case RSVP_CREATED:
                return true;
            default:
                return false;
        }
    }

    public boolean browseImageOnClick() {
        switch (feedStoryType) {
            case PICTURE_CREATED:
                return true;
            case COMMENT_CREATED:
            case LIKE_CREATED:
                return (feedStory.getEvents().get(0).getSecondaryTarget().getPicture() != null);
            default:
                return false;
        }
    }

    public boolean useImagePlaceHolder(FeedEvent feedEvent) {
        switch (feedStoryType) {
            case FRIEND_CREATED:
            case FOLLOW_CREATED:
                return true;
            default:
                return false;
        }
    }
}
