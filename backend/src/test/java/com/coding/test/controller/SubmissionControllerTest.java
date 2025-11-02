package com.coding.test.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.ResponseEntity;

import com.coding.test.model.SubmitRequest;
import com.coding.test.service.SubmissionService;

class SubmissionControllerTest {

    private SubmissionService submissionService;
    private SubmissionController submissionController;

    @BeforeEach
    void setUp() {
        submissionService = mock(SubmissionService.class);
        submissionController = new SubmissionController(submissionService);
    }

    @Test
    void submitCode_ReturnsJobId_WhenSubmissionIsSuccessful() throws Exception {
        SubmitRequest request = new SubmitRequest();
        request.setCode("public class Test {}");
        request.setQuestionId(1L);
        request.setLanguage("java");

        String expectedJobId = "job-123";
        when(submissionService.injectUserCode(anyString(), anyLong(), anyString())).thenReturn(expectedJobId);

        ResponseEntity<String> response = submissionController.submitCode(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedJobId, response.getBody());
        verify(submissionService, times(1)).injectUserCode("public class Test {}", 1L, "java");
    }

    @Test
    void submitCode_ReturnsError_WhenExceptionIsThrown() throws Exception {
        SubmitRequest request = new SubmitRequest();
        request.setCode("public class Test {}");
        request.setQuestionId(2L);
        request.setLanguage("java");

        when(submissionService.injectUserCode(anyString(), anyLong(), anyString()))
                .thenThrow(new RuntimeException("Service error"));

        ResponseEntity<String> response = submissionController.submitCode(request);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Error processing submission", response.getBody());
        verify(submissionService, times(1)).injectUserCode("public class Test {}", 2L, "java");
    }
}