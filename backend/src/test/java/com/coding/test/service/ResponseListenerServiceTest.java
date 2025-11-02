package com.coding.test.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.coding.test.model.TestResult;
import com.coding.test.repository.TestResultRepository;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

class ResponseListenerServiceTest {

    @Mock
    private SqsClient sqsClient;

    @Mock
    private TestResultRepository testResultRepository;

    @InjectMocks
    private ResponseListenerService responseListenerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(responseListenerService, "RESPONSE_QUEUE_URL", "test-queue-url");
    }

    @Test
    void pollResponseQueue_shouldProcessValidMessageAndSaveResult() throws Exception {
        String messageBody = "{ \"jobId\": \"123\", \"result\": \"{ \\\"executionTimeMs\\\": 100, \\\"result\\\": { \\\"passed\\\": 2, \\\"total\\\": 3, \\\"status\\\": \\\"success\\\", \\\"message\\\": \\\"All good\\\" } }\" }";
        Message message = Message.builder()
                .body(messageBody)
                .receiptHandle("receipt-handle-1")
                .build();

        ReceiveMessageResponse response = ReceiveMessageResponse.builder()
                .messages(message)
                .build();

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(response);
        when(testResultRepository.save(any(TestResult.class))).thenReturn(new TestResult());

        responseListenerService.pollResponseQueue();

        verify(testResultRepository, times(1)).save(any(TestResult.class));
        verify(sqsClient, times(1)).deleteMessage(any(DeleteMessageRequest.class));
    }

    @Test
    void pollResponseQueue_shouldHandleJsonProcessingException() {
        String invalidJson = "{ invalid json }";
        Message message = Message.builder()
                .body(invalidJson)
                .receiptHandle("receipt-handle-2")
                .build();

        ReceiveMessageResponse response = ReceiveMessageResponse.builder()
                .messages(message)
                .build();

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(response);

        responseListenerService.pollResponseQueue();

        verify(testResultRepository, never()).save(any(TestResult.class));
        verify(sqsClient, never()).deleteMessage(any(DeleteMessageRequest.class));
    }

    @Test
    void pollResponseQueue_shouldHandleSdkClientException() {
        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenThrow(software.amazon.awssdk.core.exception.SdkClientException.builder().message("SDK error").build());

        responseListenerService.pollResponseQueue();

        verify(testResultRepository, never()).save(any(TestResult.class));
    }
}


