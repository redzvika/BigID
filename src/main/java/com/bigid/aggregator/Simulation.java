package com.bigid.aggregator;


import com.bigid.aggregator.dataobjects.FrequencyReport;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class Simulation {

    private static final int MAX_LINES=1000;

    private static final Logger logger = LogManager.getLogger("Simulation");

    private static final AtomicInteger terminatedCount = new AtomicInteger(0);
    private static final BlockingQueue<FrequencyReport> frequencyReportBlockingDeque = new LinkedBlockingDeque<>();


    private static List<Matcher> readFile(int maxLines,String filePath) {
        logger.debug("Thread start");
        List<Matcher> list = new ArrayList<>();
        LineIterator it = null;
        int linesCounter = 0;
        int reportLine = 1;
        int characterPosition = 1;
        int characterReporting = 1;
        StringBuilder longLine = new StringBuilder();
        try {
            it = FileUtils.lineIterator(Path.of(filePath).toFile(), "UTF-8");
            while (it.hasNext()) {
                String line = it.nextLine();
                linesCounter = linesCounter + 1;
                logger.trace("read line {}", linesCounter);
                longLine.append(line).append("\r\n");
                characterPosition = characterPosition + line.length() + 2;
                if (linesCounter % (maxLines) == 0 && linesCounter > 0) {
                    logger.trace("create thread {}", linesCounter);
                    // create a thread pass to StringBuilder
                    list.add(new Matcher(longLine.toString(), frequencyReportBlockingDeque, terminatedCount, reportLine, characterReporting));
                    longLine = new StringBuilder();
                    reportLine = linesCounter + 1;
                    characterReporting = characterPosition;
                }
            }
            if (longLine.length() > 0) {
                logger.trace("create thread {}", reportLine);
                list.add(new Matcher(longLine.toString(), frequencyReportBlockingDeque, terminatedCount, reportLine, characterReporting));
            }
        } catch (Exception e) {
            logger.error("error ", e);

        } finally {
            logger.debug("Finished reading ");
            if (it != null) {
                LineIterator.closeQuietly(it);
            }
        }
        return list;
    }


    public static void main(String[] args) {
        List<Matcher> matcherList = readFile(MAX_LINES,"src/main/resources/big.txt");
        new PrinterAggregator(frequencyReportBlockingDeque, terminatedCount, matcherList.size()).start();
        matcherList.forEach(Thread::start);
    }

}
