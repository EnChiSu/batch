package com.inquartik.batch.jobLauncher;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
//@ComponentScan(basePackages = {"com.inquartik.batch.joblauncher"})
public class JobLauncherDemo implements StepExecutionListener {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    private Map<String, JobParameter> parameters;

    @Bean
    public Job jobLaucherDemoJob() {
        return jobBuilderFactory.get("jobLauncherDemoJob")
                .start(jobLaucherDemoStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    private Step jobLaucherDemoStep() {
        return stepBuilderFactory.get("jobLauncherDemoStep")
                .listener(this)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("msg from Job 1: " +parameters.get("msg").getValue());
                        return RepeatStatus.FINISHED;
                    }
                }).build();
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        parameters = stepExecution.getJobParameters().getParameters();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }
}
