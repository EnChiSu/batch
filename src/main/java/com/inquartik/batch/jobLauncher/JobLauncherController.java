package com.inquartik.batch.jobLauncher;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class JobLauncherController {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job jobLauncherDemoJob;
    @RequestMapping("/job/{msg}")
    public String jobRun1(@PathVariable String msg) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        //把接受到的參數值傳給任務
        JobParameters parameters = new JobParametersBuilder()
                                        .addString("msg", msg)
                                        .addDate("time", new Date())
                                        .toJobParameters();

        //啟動任務，並把參數傳給任務，第一個參數給你要傳參數的job，第二個參數為要傳的參數
        //job那裏要讓他得到這個參數要透過實作StepExecutionListener的方式，在before的地方getParameters
        jobLauncher.run(jobLauncherDemoJob, parameters);
        return "job success";
    }
}
