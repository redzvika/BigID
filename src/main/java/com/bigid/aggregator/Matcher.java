package com.bigid.aggregator;

import com.bigid.aggregator.dataobjects.InternalReport;
import com.bigid.aggregator.dataobjects.FrequencyReport;
import com.bigid.aggregator.utils.Line;
import com.bigid.aggregator.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.bigid.aggregator.Constants.PATTERN;

public class Matcher extends Thread{

    private  static final Logger logger = LogManager.getLogger("Matcher");
    private final AtomicInteger terminatedCount;
    private final List<Line> lines;
    private final BlockingQueue<FrequencyReport> frequencyReportBlockingDeque;
    private final FrequencyReport frequencyReport=new FrequencyReport();
    public Matcher(String searchLine,BlockingQueue<FrequencyReport> frequencyReportBlockingDeque, AtomicInteger terminatedCount, int linesStartPosition, int characterStartPosition) {
        this.frequencyReportBlockingDeque=frequencyReportBlockingDeque;
        this.terminatedCount = terminatedCount;
        lines= Utils.parseStringToLines(searchLine,linesStartPosition,characterStartPosition);
        lines.forEach(line->{
            logger.trace("{}",line);
        });
    }

    public void run(){
        logger.debug("Thread id {} name {} started",Thread.currentThread().getId(),Thread.currentThread().getName());


        lines.forEach(line -> {
            java.util.regex.Matcher matcher = PATTERN.matcher(line.getLine());
            while(matcher.find()) {
                List<InternalReport> internalReportList = frequencyReport.getMatcherFrequency().computeIfAbsent(matcher.group(), k -> new ArrayList<>());
                internalReportList.add(new InternalReport(line.getPosition(),(matcher.start()+line.getCharacterOffset())));
            }
        });

        frequencyReportBlockingDeque.add(frequencyReport);
        logger.debug("update count to {}",terminatedCount.incrementAndGet());
        logger.debug("Thread id {} name {} finished",Thread.currentThread().getId(),Thread.currentThread().getName());
    }




}
