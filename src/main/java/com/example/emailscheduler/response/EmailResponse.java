package com.example.emailscheduler.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
public class EmailResponse {
    private Boolean success;
    private String jobId;
    private String jobGroup;
    private String message;

    public EmailResponse(Boolean success, String message){
        this.success = success;
        this.message = message;
    }
}
