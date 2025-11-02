package com.coding.test.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.coding.test.model.TestResult;
import com.coding.test.service.TestResultService;

class ResultControllerTest {

    @Mock
    private TestResultService testResultService;

    @InjectMocks
    private ResultController resultController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTestResult_ReturnsResult_WhenJobIdExists() {
        // Arrange
        String jobId = "test-job-123";
        TestResult mockResult = new TestResult();
        mockResult.setJobId(jobId);

        when(testResultService.getTestResultByJobId(jobId)).thenReturn(mockResult);

        // Act
        ResponseEntity<TestResult> response = resultController.getTestResult(jobId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(jobId, response.getBody().getJobId());
        verify(testResultService, times(1)).getTestResultByJobId(jobId);
    }

    @Test
    void getTestResult_ReturnsNotFound_WhenJobIdDoesNotExist() {
        // Arrange
        String jobId = "non-existent-job";

        when(testResultService.getTestResultByJobId(jobId)).thenThrow(new RuntimeException("Job not found"));

        // Act
        ResponseEntity<TestResult> response = resultController.getTestResult(jobId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(testResultService, times(1)).getTestResultByJobId(jobId);
    }
}