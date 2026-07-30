package com.fincore.batchservice.job;

import com.fincore.batchservice.batch.step.ConciliacionStep;
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
public class ConciliacionJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final ConciliacionStep conciliacionStep;

    @Bean("conciliacionJob")
    public Job conciliacionJob(@Qualifier("conciliacionStep") Step conciliacionStep) {
        return jobBuilderFactory.get("conciliacionJob")
                .incrementer(new RunIdIncrementer())
                .start(conciliacionStep)
                .build();
    }
}
