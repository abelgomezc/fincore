package com.fincore.batchservice.batch.step;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ConciliacionStep {

    private final StepBuilderFactory stepBuilderFactory;

    @Bean("conciliacionStep")
    @StepScope
    public Step conciliacionStep(@Qualifier("conciliacionReader") ItemReader<String> reader,
                                 @Qualifier("conciliacionProcessor") ItemProcessor<String, String> processor,
                                 @Qualifier("conciliacionWriter") ItemWriter<String> writer,
                                 JobRepository jobRepository) {
        return stepBuilderFactory.get("conciliacionStep")
                .<String, String>chunk(100)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .repository(jobRepository)
                .build();
    }
}
