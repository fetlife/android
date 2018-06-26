package com.bitlove.fetlife.util;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.text.util.Linkify;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;

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
        //options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // You can re-use parser and renderer instances
        Node document = parser.parse(htmlString);
        String markedHtml = renderer.render(document);  // "<p>This is <em>Sparta</em></p>\n"

//        CharSequence sequence = Html.fromHtml(htmlString);
//        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
//        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
//        for(URLSpan span : urls) {
//            makeLinkClickable(strBuilder, span);
//        }
//        return strBuilder;

        return Html.fromHtml(markedHtml);
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

    public static Spannable linkifyHtml(String html, int linkifyMask) {
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

