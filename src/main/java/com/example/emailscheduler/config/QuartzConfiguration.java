package com.example.emailscheduler.config;

import com.example.emailscheduler.quartz.job.EmailJob;
import com.example.emailscheduler.request.EmailRequest;
import org.quartz.*;
import org.springframework.context.annotation.Configuration;

import java.sql.Date;
import java.time.ZoneId;
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
                .build();
    }

    public Trigger buildTrigger(JobDetail jobDetail, ZonedDateTime dateTime){
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "email-triggers")
                .withIdentity("Send email trigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 * * ? * *").inTimeZone(TimeZone.getTimeZone(ZoneId.of("Europe/London"))))
                .build();
    }
}
