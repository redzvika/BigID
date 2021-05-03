package com.bigid.aggregator.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Line {

    int position;
    int characterOffset;
    String line;

    public String toString(){
        return "LineOffset:"+position + " CharacterOffset:"+ characterOffset;
    }
}
