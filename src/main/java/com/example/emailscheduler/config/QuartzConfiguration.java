package com.example.emailscheduler.config;

import com.example.emailscheduler.quartz.job.EmailJob;
import com.example.emailscheduler.request.EmailRequest;
import org.quartz.*;
import org.springframework.context.annotation.Configuration;

import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.TimeZone;
import java.util.UUID;

@Configuration
public class QuartzConfiguration {

    public JobDetail buildJobDetail(EmailRequest emailRequest){
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("email", emailRequest.getEmail());
        jobDataMap.put("subject", emailRequest.getSubject());
        jobDataMap.put("body", emailRequest.getBody());
        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString(), "email-jobs")
                .withDescription("Send email job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    public Trigger buildTrigger(JobDetail jobDetail, ZonedDateTime dateTime){
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "email-triggers")
                .withDescription("Send email trigger")
                .startAt(Date.from(dateTime.toInstant()))
                .withSchedule(CronScheduleBuilder.cronSchedule("* * * ? * *").inTimeZone(TimeZone.getTimeZone(dateTime.getZone())))
                .endAt(java.util.Date.from(dateTime.toInstant().plusSeconds(5)))
                .build();
    }
    // for .endAt(5) the job would be executed for 6 times i.e, 0, 1, 2, 3, 4, 5
    /*
    {
    "email": "vinay.maxwell0703@gmail.com",
    "subject": "1 leetcode a day keeps employeement away",
    "body": "reverse a linked list using recursion",
    "dateTime": "2023-09-28T19:19:00",
    "timeZone": "Europe/London"
    }
     */
}
