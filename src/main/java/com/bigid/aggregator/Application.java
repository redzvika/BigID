package com.bigid.aggregator;



import com.beust.jcommander.JCommander;
import com.bigid.aggregator.console.FileNameArgs;
import com.bigid.aggregator.console.NamesArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Slf4j
@EnableScheduling
@Component
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    Simulation simulation;

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(500);
        //executor.setThreadNamePrefix("GithubLookup-");
        executor.initialize();
        return executor;
    }


    public static void main(String[] args) {
        SpringApplication.run(com.bigid.aggregator.Application.class, args);
    }

    @Override
    public void run(String... args){
        log.debug("EXECUTING : command line runner");

        FileNameArgs fileArgs = new FileNameArgs();
        NamesArgs namesArgs = new NamesArgs();
        JCommander consoleCmd = JCommander
                .newBuilder()
                .addObject(fileArgs)
                .addObject(namesArgs)
                .build();

        consoleCmd.parse(args);

        simulation.start(namesArgs.getNames(),fileArgs.getBigFile());
    }
}



