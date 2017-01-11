package com.bitlove.fetlife.util;

import java.util.ArrayList;

/**
  Created by ReluctantSub
 This class is built & monitored by me... drop me a message on GitHub if you have any further questions.

 Note: The class currently handles all HEADER formatting for text. Instering HTML tags which can be used
 to parse later on down the line... such as is done by the MessagesRecyclerAdaptor class.

 I will be adding full formatting support soon... So don't bother.
 */

public class TextFormattingUtil {

    public String getFormatted(String messageBody) {

        final String RED = "<font color=red>";
        final String GREY = "<font color=grey>";

        int lastMarker = 0;
        int totalLines = 1;

        boolean previousLineWasMarkup = false;
        String temp;

        ArrayList<String> markDownList = new ArrayList<String>();

        for (int i=0; i<messageBody.length(); i++){
            // Store each line individually
            if (messageBody.charAt(i) == '\n') {
                markDownList.add(messageBody.substring(lastMarker, i));
                lastMarker = i;
                totalLines++;
            }
        }

        // Add the last line (had \n trunicated)
        markDownList.add(messageBody.substring(lastMarker,messageBody.length()));

        try {
            /*
            // PRINT THE LIST OUT!!
            System.out.println("===========================\n");
            System.out.println(markDownList);
            System.out.println("===========================\n");
            */

            for (int i=0; i<totalLines; i++) {
                //System.out.println(markDownList.get(i));
                if (markDownList.get(i).trim().equals("---")) {
                    temp = markDownList.get(i);
                    temp = temp.replaceFirst("---", "←-→");

                    markDownList.set(i, "<h6>" + GREY + temp.trim() + "</h6>");
                    // ---   →   horizontal line
                    //System.out.println("Horizontal Line\n");
                    previousLineWasMarkup = true;
                } else if ( markDownList.get(i).startsWith("# ") ||
                        markDownList.get(i).startsWith("\n# ")) {
                    temp = "<h1>" + markDownList.get(i).trim() + "</h1>";
                    temp = temp.replaceFirst("# ", "");
                    markDownList.set(i, temp);
                    //System.out.println(temp);
                    previousLineWasMarkup = true;
                } else if ( markDownList.get(i).startsWith("## ") ||
                        markDownList.get(i).startsWith("\n## ")) {
                    temp = "<h2>"+ RED + markDownList.get(i).trim() + "</h2>";
                    temp = temp.replaceFirst("## ", "");
                    markDownList.set(i, temp);
                    //System.out.println(temp);
                    previousLineWasMarkup = true;
                } else if ( markDownList.get(i).startsWith("### ") ||
                        markDownList.get(i).startsWith("\n### ")) {
                    temp = "<h3>" + RED + markDownList.get(i).trim() + "</h3>";
                    temp = temp.replaceFirst("### ", "");
                    markDownList.set(i, temp);
                    //System.out.println(temp);
                    previousLineWasMarkup = true;
                } else if ( markDownList.get(i).startsWith("#### ") ||
                        markDownList.get(i).startsWith("\n#### ")) {
                    temp = "<h4>" + GREY + markDownList.get(i).trim() + "</h4>";
                    temp = temp.replaceFirst("#### ", "");
                    markDownList.set(i, temp);
                    //System.out.println(temp);
                    previousLineWasMarkup = true;
                } else {
                    // No markup on this line to handle...
                    if (previousLineWasMarkup == true) {
                        // Already is a \n so time to strip extra leading one off.
                        markDownList.set(i, markDownList.get(i).substring(1));
                    }
                    // No MarkUp
                    // System.out.println("Debug: " + markDownList.get(i));
                    previousLineWasMarkup = false;
                }

            }
            /*
            System.out.println("===========================\n");
            System.out.println("Message Length: " + messageBody.length() + "\n");
            System.out.println("--------------------\n" + messageBody);
            System.out.println("Index of: " + messageBody.indexOf('\n'));
            System.out.println("\n Last marker pos: " + lastMarker);
            System.out.println("\n Total Lines: " + totalLines);
            */

            temp = "";
            // Final recomposited messageBody with HTML tags
            for (int i=0; i<totalLines; i++) {
                temp = temp + markDownList.get(i);

            }
            messageBody = temp;


        } catch (Exception e) {
            e.printStackTrace();

        } return messageBody;

    }

}
