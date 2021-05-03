package com.bigid.aggregator;

import com.bigid.aggregator.dataobjects.FrequencyReport;
import com.bigid.aggregator.dataobjects.InternalReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PrinterAggregator extends Thread {

    private static Logger logger = LogManager.getLogger("PrinterAggregator");
    private final BlockingQueue<FrequencyReport> frequencyReportBlockingDeque;
    private final AtomicInteger terminatedCount;
    private final int expectedCount;
    private boolean finish = false;
    private final Map<String, List<InternalReport>> aggregatorMap=new HashMap<>();
    /*Timothy --> [[lineOffset=13000, charOffset=19775], [lineOffset=13000, charOffset=42023]]*/

    public PrinterAggregator(BlockingQueue<FrequencyReport> frequencyReportBlockingDeque, AtomicInteger terminatedCount, int expectedCount) {
        this.frequencyReportBlockingDeque = frequencyReportBlockingDeque;
        this.terminatedCount = terminatedCount;
        this.expectedCount = expectedCount;
    }

    public void run() {
        try {
            while (!finish) {
                if (terminatedCount.get() == expectedCount && frequencyReportBlockingDeque.isEmpty()) {
                    finish = true;
                }
                FrequencyReport frequencyReport = frequencyReportBlockingDeque.poll(100, TimeUnit.MILLISECONDS);
                if (frequencyReport == null) {
                    logger.debug("frequencyReport is null don't handle");
                }else{
                    frequencyReport.getMatcherFrequency().forEach((k,v)->{
                        List<InternalReport> list=aggregatorMap.computeIfAbsent(k, key -> new ArrayList<>());
                        list.addAll(v);
                    });
                    logger.debug(frequencyReport.getMatcherFrequency());
                }

            }
        } catch (Exception e) {
            logger.error(e);
        }
        printResults();
    }

    private static String frequencyPrintOut(String name,List<InternalReport> list){
        StringBuilder builder=new StringBuilder();
        builder.append(name).append("-->");
        builder.append("[");
        for (int i=0;i<list.size();i++){
            builder.append("[lineOffset=").append(list.get(i).getLineOffset());
            builder.append(",charOffset=").append(list.get(i).getCharOffset()).append("]");
            if (i!=list.size()-1){
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    private void printResults(){
        aggregatorMap.forEach((k,v)->{
            v.sort(Comparator.comparing(InternalReport::getLineOffset).thenComparing(InternalReport::getCharOffset));
            System.out.println(frequencyPrintOut(k,v));
        });
    }
}
