package com.bigid.aggregator;

import com.bigid.aggregator.utils.Line;
import com.bigid.aggregator.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class UtilsTester {
    private static final Logger logger = LogManager.getLogger("UtilsTester");

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class LineWrapper {
        List<Line> lines = new ArrayList<>();
        String rawString;
    }


    private LineWrapper readFile(String fileName) throws Exception {
        LineWrapper lineWrapper = new LineWrapper();
        LineIterator it = null;
        int linesCounter = 1;

        int characterPosition = 1;
        StringBuilder builder=new StringBuilder();
        try {
            it = FileUtils.lineIterator(Path.of(fileName).toFile(), "UTF-8");
            while (it.hasNext()) {
                Line line=new Line();
                String lineRead = it.nextLine();
                builder.append(lineRead).append("\r\n");
                line.setPosition(linesCounter);
                line.setCharacterOffset(characterPosition);
                line.setLine(lineRead);
                characterPosition=characterPosition+lineRead.length()+2;
                logger.debug("line {} read {}",linesCounter,line );
                linesCounter=linesCounter+1;
                lineWrapper.getLines().add(line);
            }
            lineWrapper.setRawString(builder.toString());
        } catch (Exception e) {
            logger.error("error ", e);

        } finally {
            logger.debug("Finished reading ");
            if (it != null) {
                LineIterator.closeQuietly(it);
            }
        }
        return lineWrapper;
    }


    @Test
    public void testEmptyFileSingleNewLine() throws Exception {
        LineWrapper lineWrapper = readFile("src/test/resources/small2.txt");
        List<Line> linesFromBigString=Utils.parseStringToLines(lineWrapper.getRawString(),1,1);
        Assert.assertTrue(CollectionUtils.isEqualCollection(linesFromBigString, lineWrapper.getLines()));
    }

    @Test
    public void testFileWithTrailingNewLine() throws Exception {
        LineWrapper lineWrapper = readFile("src/test/resources/small.txt");
        List<Line> linesFromBigString=Utils.parseStringToLines(lineWrapper.getRawString(),1,1);
        Assert.assertTrue(CollectionUtils.isEqualCollection(linesFromBigString, lineWrapper.getLines()));
    }


    @Test
    public void testFileWithTabs() throws Exception {
        LineWrapper lineWrapper = readFile("src/test/resources/small_withTabs.txt");
        List<Line> linesFromBigString=Utils.parseStringToLines(lineWrapper.getRawString(),1,1);
        Assert.assertTrue(CollectionUtils.isEqualCollection(linesFromBigString, lineWrapper.getLines()));
    }
}