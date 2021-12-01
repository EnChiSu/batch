package com.inquartik.batch.jobOperator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobInstanceAlreadyExistsException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("job")
public class JobOperatorController {

    @Autowired
    private JobOperator jobOperator;
    @GetMapping("/{msg}")
    public String jobRun2(@PathVariable String msg) throws JobInstanceAlreadyExistsException, NoSuchJobException, JobParametersInvalidException {
        //把接受到的參數值傳給任務
//        JobParameters parameters = new JobParametersBuilder()
//                                        .addString("msg", msg)
//                                        .addDate("time", new Date())
//                                        .toJobParameters();

        //啟動任務，同時傳參數
        jobOperator.start("jobOperatorDemoJob", "msg="+msg+",time="+new Date());
//        jobOperator.start("jobOperatorDemoJob", String.valueOf(parameters));


        return "job success";
    }
}
