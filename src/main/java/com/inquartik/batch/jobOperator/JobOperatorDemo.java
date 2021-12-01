package com.inquartik.batch.jobOperator;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ComponentScan(basePackages = {"com.inquartik.batch.jobOperator"})
public class JobOperatorDemo implements StepExecutionListener, ApplicationContextAware {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    private Map<String, JobParameter> parameters;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobRegistry jobRegistry;

    private ApplicationContext context;

    @Bean
    public JobRegistryBeanPostProcessor jobRegister() throws Exception{
        JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
        postProcessor.setJobRegistry(jobRegistry);
        //透過實作ApplicationContextAware，整個應用的applicationContext設為此類別當中的context變數，以取得上下文
        postProcessor.setBeanFactory(context.getAutowireCapableBeanFactory());
        postProcessor.afterPropertiesSet();
        return postProcessor;
    }
    @Bean
    public JobOperator jobOperator(){
        SimpleJobOperator operator = new SimpleJobOperator();

        operator.setJobLauncher(jobLauncher);
        //jobOperator需要設置參數轉換器，讓執行檔那裏的msg參數轉為parameters類型，使可以用get的方式取得(第88行)
        operator.setJobParametersConverter(new DefaultJobParametersConverter());
        operator.setJobRepository(jobRepository);
        //可以用來獲取job相關的訊息，例如job instance、execution等
        operator.setJobExplorer(jobExplorer);
        //註冊jobRegistry，讓執行檔那裏知道你叫jobOperatorDemoJob是在叫哪個物件，需要同時實作jobRegister這個方法
        operator.setJobRegistry(jobRegistry);

        return operator;
    }

    @Bean
    public Job jobOperatorDemoJob() {
        return jobBuilderFactory.get("jobOperatorDemoJob")
                .start(jobOperatorDemoStep())
                .build();
    }

    private Step jobOperatorDemoStep() {
        return stepBuilderFactory.get("jobOperatorDemoStep")
                .listener(this)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println(parameters);
                        System.out.println("msg from Job 2: " + parameters.get("msg").getValue());
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        parameters = stepExecution.getJobParameters().getParameters();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

}
