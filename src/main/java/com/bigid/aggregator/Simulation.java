package com.bigid.aggregator;


import com.bigid.aggregator.dataobjects.FrequencyReport;
import com.bigid.aggregator.dataobjects.InternalReport;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import static com.bigid.aggregator.utils.Utils.generateRegexForWords;

@Slf4j
@Component
public class Simulation {

    private static final int MAX_LINES = 1000;




    @Autowired
    MatcherService matcherService;

    private List<CompletableFuture<FrequencyReport>> hanldeFile(int maxLines, String filePath,Pattern namesPattern) {
        log.debug("Thread start");
        List<CompletableFuture<FrequencyReport>> list = new ArrayList<>();
        LineIterator it = null;
        int linesCounter = 0;
        int reportLine = 1;
        int characterPosition = 1;
        int characterReporting = 1;
        StringBuilder longLine = new StringBuilder();
        try {
            it = FileUtils.lineIterator(Path.of(filePath).toFile(), "UTF-8");
            while (it.hasNext()) {
                log.trace("create thread for {}", reportLine);
                String line = it.nextLine();
                linesCounter = linesCounter + 1;
                log.trace("read line {}", linesCounter);
                longLine.append(line).append("\r\n");
                characterPosition = characterPosition + line.length() + 2;
                if (linesCounter % (maxLines) == 0 && linesCounter > 0) {
                    // create a thread pass to StringBuilder
                    list.add(matcherService.searchInLines(longLine.toString(), reportLine, characterReporting,namesPattern));
                    longLine = new StringBuilder();
                    reportLine = linesCounter + 1;
                    characterReporting = characterPosition;
                }
            }
            if (longLine.length() > 0) {
                log.trace("create thread {}", reportLine);
                list.add(matcherService.searchInLines(longLine.toString(), reportLine, characterReporting,namesPattern));
            }
        } catch (Exception e) {
            log.error("error ", e);

        } finally {
            log.debug("Finished reading ");
            if (it != null) {
                LineIterator.closeQuietly(it);
            }
        }
        return list;
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


    @SneakyThrows
    public void start(String names,String filePath) {
        //"src/main/resources/big.txt"
        Pattern namesPattern = Pattern.compile(generateRegexForWords(names.split(",")));

        List<CompletableFuture<FrequencyReport>> futureList = hanldeFile(MAX_LINES, filePath,namesPattern);
        Map<String, List<InternalReport>> aggregatorMap=new HashMap<>();
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()])).join();
        for (CompletableFuture<FrequencyReport> frequencyReportCompletableFuture:futureList){
            FrequencyReport frequencyReport=frequencyReportCompletableFuture.get();
            frequencyReport.getMatcherFrequency().forEach((k,v)->{
                List<InternalReport> list=aggregatorMap.computeIfAbsent(k, key -> new ArrayList<>());
                list.addAll(v);
            });
        }
        aggregatorMap.forEach((k,v)->{
            v.sort(Comparator.comparing(InternalReport::getLineOffset).thenComparing(InternalReport::getCharOffset));
            System.out.println(frequencyPrintOut(k,v));
        });
    }



}
