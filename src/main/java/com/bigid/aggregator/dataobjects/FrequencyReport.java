package com.bigid.aggregator.dataobjects;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Data
public class FrequencyReport {
    Map<String, List<InternalReport>> matcherFrequency=new HashMap<>();
}
