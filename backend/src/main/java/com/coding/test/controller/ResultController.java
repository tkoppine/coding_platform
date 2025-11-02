package com.coding.test.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coding.test.model.TestResult;
import com.coding.test.service.TestResultService;

@RestController
@RequestMapping("/api/results")
public class ResultController {
    private final TestResultService testResultService;

    public ResultController(TestResultService testResultService) {
        this.testResultService = testResultService;
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<TestResult> getTestResult(@PathVariable String jobId) {
        try {
            TestResult result = testResultService.getTestResultByJobId(jobId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
