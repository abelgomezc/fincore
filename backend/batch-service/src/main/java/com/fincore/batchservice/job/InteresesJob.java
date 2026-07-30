package com.fincore.batchservice.job;

import com.fincore.batchservice.batch.step.InteresesStep;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class InteresesJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final InteresesStep interesesStep;

    @Bean("interesesJob")
    public Job interesesJob(@Qualifier("interesesStep") Step interesesStep) {
        return jobBuilderFactory.get("interesesJob")
                .incrementer(new RunIdIncrementer())
                .start(interesesStep)
                .build();
    }
}
