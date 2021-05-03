package com.bigid.aggregator;

import java.util.regex.Pattern;

public class Constants {

    public static String[] NAMES = ("James,John,Robert,Michael,William,David,Richard,Charles,Joseph,Thomas,Christopher,Daniel,Paul,Mark,Donal" +
            "d,George,Kenneth,Steven,Edward,Brian,Ronald,Anthony,Kevin,Jason,Matthew,Gary,Timothy,Jose,Larry,Jeffrey," +
            "Frank,Scott,Eric,Stephen,Andrew,Raymond,Gregory,Joshua,Jerry,Dennis,Walter,Patrick,Peter,Harold,Douglas,H" +
            "enry,Carl,Arthur,Ryan,Roger").split(",");

    public static Pattern PATTERN = Pattern.compile(generateRegexForWords(Constants.NAMES));

    static String generateRegexForWords(String [] words){
        StringBuilder patternString=new StringBuilder();
        patternString.append("(");
        for (int i=0;i<words.length;i++){
            patternString.append("\\b").append(words[i]).append("\\b");
            if (i!=words.length-1){
                patternString.append("|");
            }
        }
        patternString.append(")");
        return  patternString.toString();
    }
}
