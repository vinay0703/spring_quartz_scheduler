package com.example.emailscheduler.controller;

import com.example.emailscheduler.config.QuartzConfiguration;
import com.example.emailscheduler.request.EmailRequest;
import com.example.emailscheduler.response.EmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.ZonedDateTime;

@Slf4j
@RestController
public class EmailSchedulerController {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private QuartzConfiguration quartzConfiguration;

    @PostMapping("/schedule/email")
    public ResponseEntity<EmailResponse> scheduleEmail(@Valid @RequestBody EmailRequest emailRequest){
        try{
            ZonedDateTime dateTime = ZonedDateTime.of(emailRequest.getDateTime(), emailRequest.getTimeZone());
            if(dateTime.isBefore(ZonedDateTime.now())){
                EmailResponse emailResponse = new EmailResponse(false, "dateTime must be after current time.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(emailResponse);
            }
            JobDetail jobDetail = quartzConfiguration.buildJobDetail(emailRequest);
            Trigger trigger = quartzConfiguration.buildTrigger(jobDetail, dateTime);
            scheduler.scheduleJob(jobDetail, trigger);
            EmailResponse emailResponse = new EmailResponse(true, jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), "Email scheduled successfully!");
            return ResponseEntity.ok(emailResponse);
        } catch(SchedulerException se){
            log.error("Error while scheduling email: " + se);
            EmailResponse emailResponse = new EmailResponse(false, "Error while scheduling email. Please try again later!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emailResponse);
        }
    }
}