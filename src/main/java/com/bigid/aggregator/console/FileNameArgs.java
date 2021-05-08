package com.bigid.aggregator.console;

import com.beust.jcommander.Parameter;
import lombok.Getter;

@Getter
public  class FileNameArgs {

    @Parameter(
            names = "--bigFile",
            description = "bigFile",
            required = true
    )
    private String bigFile;

}