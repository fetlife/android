package com.bitlove.fetlife.legacy.util;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.View;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.Mention;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.MessageEntities;
import com.bitlove.fetlife.legacy.view.screen.resource.profile.ProfileActivity;
import com.crashlytics.android.Crashlytics;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import com.vladsch.flexmark.ast.Node;
//import com.vladsch.flexmark.html.HtmlRenderer;
//import com.vladsch.flexmark.parser.Parser;
//import com.vladsch.flexmark.util.options.MutableDataSet;

public class StringUtil {

    static boolean isInitialized = false;
//    private static Parser parser;
//    private static HtmlRenderer renderer;

    public static synchronized void init() {
        if (isInitialized) {
            return;
        }
//        MutableDataSet options = new MutableDataSet();

        // uncomment to set optional extensions
        //options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));

        // uncomment to convert soft-breaks to hard breaks
//        options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
//
//        parser = Parser.builder(options).build();
//        renderer = HtmlRenderer.builder(options).build();
//        isInitialized = true;
    }

    public static final CharSequence parseMarkedHtml(String htmlString) {

        if (!isInitialized) {
            init();
        }

        if (htmlString == null) {
            return null;
        }

        htmlString = htmlString.replace("\n", "<br/>");

        //Disabled due to performance issues
//        Node document = parser.parse(htmlString.trim());
//        String markedHtml = renderer.render(document).trim();
//        String referenceText = markedHtml.toLowerCase();
//        if (referenceText.startsWith("<p")) {
//            markedHtml = markedHtml.substring(markedHtml.indexOf(">")+1);
//            if (referenceText.endsWith("</p>")) {
//                markedHtml = markedHtml.substring(0,markedHtml.length()-"</p>".length());
//            }
//        }

        String markedHtml = htmlString;

        return linkifyHtml(markedHtml, Linkify.WEB_URLS);
    }

    public static CharSequence parseMarkedHtmlWithMentions(String htmlString, List<Mention> mentions) {
        List<String> mentionTexts = new ArrayList<>();
        for (Mention mention : mentions) {
            try {
                mentionTexts.add(htmlString.subSequence(mention.getOffset(),mention.getOffset()+mention.getLength()).toString());
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }

        Spannable linkifiedText = (Spannable) parseMarkedHtml(htmlString);
        String tempStringVersion = linkifiedText.toString();

        int i = 0;
        for (String mentionText : mentionTexts) {
            final Mention mention = mentions.get(i++);
            ClickableSpan clickableSpan = new ClickableSpan() {
                public static final long CLICK_OFFSET = 500;
                private long lastClick = 0;
                @Override
                public void onClick(View textView) {
                    if (System.currentTimeMillis() - lastClick > CLICK_OFFSET) {
                        mention.getMember().mergeSave();
                        ProfileActivity.startActivity(FetLifeApplication.getInstance(),mention.getMember().getId());
                    }
                    lastClick = System.currentTimeMillis();
                }
            };

            int index = tempStringVersion.indexOf(mentionText);
            if (index >= 0) {
                linkifiedText.setSpan(clickableSpan,index,index + mentionText.length(), 0);
            }
        }

        return linkifiedText;
    }

//    protected static void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
//        int start = strBuilder.getSpanStart(span);
//        int end = strBuilder.getSpanEnd(span);
//        int flags = strBuilder.getSpanFlags(span);
//        ClickableSpan clickable = new ClickableSpan() {
//            public void onClick(View view) {
//                // Do something with span.getURL() to handle the link click...
//            }
//        };
//        strBuilder.setSpan(clickable, start, end, flags);
//        strBuilder.removeSpan(span);
//    }

    public static final String toString(List<String> list, String separator) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (String item : list) {
            if (!first) {
                stringBuilder.append(separator);
            } else {
                first = false;
            }
            stringBuilder.append(item);
        }
        return stringBuilder.toString();
    }

    private static Spannable linkifyHtml(String html, int linkifyMask) {
        Spanned text = Html.fromHtml(html);
        URLSpan[] currentSpans = text.getSpans(0, text.length(), URLSpan.class);

        SpannableString buffer = new SpannableString(text);
        Linkify.addLinks(buffer, linkifyMask);

        for (URLSpan span : currentSpans) {
            int end = text.getSpanEnd(span);
            int start = text.getSpanStart(span);
            buffer.setSpan(span, start, end, 0);
        }
        return buffer;
    }

    public static MessageEntities getMessageEntities(String entitiesJson) {
        MessageEntities messageEntities;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            messageEntities = objectMapper.readValue(entitiesJson, MessageEntities.class);
        } catch (IOException |NullPointerException e) {
            messageEntities = new MessageEntities();
        }
        return messageEntities;
    }
}

