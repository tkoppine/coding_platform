package com.coding.test.service;

import org.springframework.stereotype.Service;
import com.coding.test.model.TestResult;
import com.coding.test.repository.TestResultRepository;

@Service
public class TestResultService {
    private final TestResultRepository testResultRepository;

    public TestResultService(TestResultRepository testResultRepository) {
        this.testResultRepository = testResultRepository;
    }

    public TestResult getTestResultByJobId(String jobId) {
        return testResultRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Test result not found for jobId: " + jobId));
    }
}
