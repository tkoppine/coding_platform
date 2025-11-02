package com.coding.test.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.coding.test.model.TestResult;
import com.coding.test.repository.TestResultRepository;

@ExtendWith(MockitoExtension.class)
class TestResultServiceTest {

    @Mock
    private TestResultRepository testResultRepository;

    @InjectMocks
    private TestResultService testResultService;

    @Test
    void getTestResultByJobId_ReturnsTestResult_WhenFound() {
        String jobId = "job123";
        TestResult expectedResult = new TestResult();
        when(testResultRepository.findById(jobId)).thenReturn(Optional.of(expectedResult));

        TestResult actualResult = testResultService.getTestResultByJobId(jobId);

        assertEquals(expectedResult, actualResult);
        verify(testResultRepository, times(1)).findById(jobId);
    }

    @Test
    void getTestResultByJobId_ThrowsException_WhenNotFound() {
        String jobId = "job456";
        when(testResultRepository.findById(jobId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testResultService.getTestResultByJobId(jobId);
        });

        assertTrue(exception.getMessage().contains("Test result not found for jobId: " + jobId));
        verify(testResultRepository, times(1)).findById(jobId);
    }
}