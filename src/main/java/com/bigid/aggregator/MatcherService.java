package com.bigid.aggregator;

import com.bigid.aggregator.dataobjects.InternalReport;
import com.bigid.aggregator.dataobjects.FrequencyReport;
import com.bigid.aggregator.utils.Line;
import com.bigid.aggregator.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@Slf4j
@Service
public class MatcherService {



    @Async
    public CompletableFuture<FrequencyReport> searchInLines(String searchLine, int linesStartPosition, int characterStartPosition, Pattern namesPattern) throws InterruptedException {
        log.debug("Search START linesStartPosition={}  characterStartPosition={}", linesStartPosition, characterStartPosition);
        FrequencyReport frequencyReport = new FrequencyReport();
        List<Line> lines = Utils.parseStringToLines(searchLine, linesStartPosition, characterStartPosition);
        lines.forEach(line -> {
            java.util.regex.Matcher matcher = namesPattern.matcher(line.getLine());
            while (matcher.find()) {
                List<InternalReport> internalReportList = frequencyReport.getMatcherFrequency().computeIfAbsent(matcher.group(), k -> new ArrayList<>());
                internalReportList.add(new InternalReport(line.getPosition(), (matcher.start() + line.getCharacterOffset())));
            }
        });
        log.debug("Search FINISH linesStartPosition={}  characterStartPosition={}", linesStartPosition, characterStartPosition);
        return CompletableFuture.completedFuture(frequencyReport);
    }
}
