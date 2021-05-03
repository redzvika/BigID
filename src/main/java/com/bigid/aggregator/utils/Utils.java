package com.bigid.aggregator.utils;


import java.util.ArrayList;
import java.util.List;



public class Utils {

    /**
     * Transforms a very large string to lines.
     * Adding absolute info ,line offset and character offset.
     * @param bigLine
     * @param linesStartPosition
     * @param characterStartPosition
     * @return
     */
    public static List<Line> parseStringToLines(String bigLine,int linesStartPosition,int characterStartPosition){
        List<Line> lines=new ArrayList<>();
        List<String> rawLines=new ArrayList<>();
        int linePosition=0;
        for (int i=0;i<bigLine.length();i++){
            if (bigLine.charAt(i)=='\r' && bigLine.charAt(i+1)=='\n' && i+1<bigLine.length()){
                rawLines.add(bigLine.substring(linePosition,i));
                linePosition=i+2;
            }
        }
        int characterPosition=0;
        for (int i=0;i<rawLines.size();i++){
            Line line=new Line(i+linesStartPosition,characterPosition+characterStartPosition,rawLines.get(i));
            characterPosition=characterPosition+rawLines.get(i).length()+2;
            lines.add(line);
        }
        return lines;
    }
}
