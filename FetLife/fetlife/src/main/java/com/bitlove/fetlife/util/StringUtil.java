package com.bitlove.fetlife.util;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.View;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.model.pojos.fetlife.json.Mention;
import com.bitlove.fetlife.view.screen.resource.profile.ProfileActivity;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {

    public static final CharSequence parseMarkedHtml(String htmlString) {

        if (htmlString == null) {
            return null;
        }

        //htmlString = htmlString.replace("\n", "<br/>");

        MutableDataSet options = new MutableDataSet();

        // uncomment to set optional extensions
        //options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));

        // uncomment to convert soft-breaks to hard breaks
        options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // You can re-use parser and renderer instances
        Node document = parser.parse(htmlString.trim());
        String markedHtml = renderer.render(document).trim();
        String referenceText = markedHtml.toLowerCase();
        if (referenceText.startsWith("<p")) {
            markedHtml = markedHtml.substring(markedHtml.indexOf(">")+1);
            if (referenceText.endsWith("</p>")) {
                markedHtml = markedHtml.substring(0,markedHtml.length()-"</p>".length());
            }
        }

        return linkifyHtml(markedHtml, Linkify.WEB_URLS);
    }

    public static CharSequence parseMarkedHtmlWithMentions(String htmlString, List<Mention> mentions) {
        List<String> mentionTexts = new ArrayList<>();
        for (Mention mention : mentions) {
            mentionTexts.add(htmlString.subSequence(mention.getOffset(),mention.getOffset()+mention.getLength()).toString());
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
            linkifiedText.setSpan(clickableSpan,index,index + mentionText.length(), 0);
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

}

