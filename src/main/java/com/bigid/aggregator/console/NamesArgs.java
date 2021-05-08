package com.bigid.aggregator.console;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter

public class NamesArgs {


    @Parameter(
            names = "--names",
            description = "names",
            required = true,
            validateWith = ModeValidator.class
    )
    private String names;


    @Component
    public static class ModeValidator implements IParameterValidator {

        @Override
        public void validate(String name, String value) throws ParameterException {

        }

    }
}