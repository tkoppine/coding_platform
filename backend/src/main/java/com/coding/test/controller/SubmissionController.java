package com.coding.test.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coding.test.model.SubmitRequest;
import com.coding.test.service.SubmissionService;

@RestController
@RequestMapping("/api/submit")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping
    public ResponseEntity<String> submitCode(@RequestBody SubmitRequest submission) throws Exception {
        try {
            String userCode = submission.getCode();
            Long questionId = submission.getQuestionId();
            String language = submission.getLanguage();

            String jobId = submissionService.injectUserCode(userCode, questionId, language);

            System.out.println("Job ID for the submission:\n" + jobId);

            return ResponseEntity.ok(jobId);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing submission");
        }
    }
}
