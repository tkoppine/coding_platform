package com.coding.test.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.coding.test.model.TestResult;
import com.coding.test.repository.TestResultRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

@Service
public class ResponseListenerService {
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final TestResultRepository testResultRepository;

    @Value("${aws.sqs.response.queue.url}")
    private String RESPONSE_QUEUE_URL;

    public ResponseListenerService(SqsClient sqsClient, TestResultRepository testResultRepository) {
        this.sqsClient = sqsClient;
        this.testResultRepository = testResultRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Scheduled(fixedDelay = 5000)
    public void pollResponseQueue() {
        try {
            List<Message> messages = sqsClient.receiveMessage(
                    ReceiveMessageRequest.builder()
                            .queueUrl(RESPONSE_QUEUE_URL)
                            .maxNumberOfMessages(5)
                            .waitTimeSeconds(5)
                            .build())
                    .messages();

            for (Message message : messages) {

                try {
                    System.out.println("Received response: " + message.body());

                    JsonNode json = objectMapper.readTree(message.body());
                    String jobId = json.path("jobId").asText("unknown");

                    String rawResult = json.path("result").asText("{}");
                    JsonNode resultJson = objectMapper.readTree(rawResult);

                    long executionTimeMs = resultJson.path("executionTimeMs").asLong(0);
                    JsonNode resultNode = resultJson.path("result");

                    int passed = resultNode.path("passed").asInt(0);
                    int total = resultNode.path("total").asInt(0);
                    String status = resultNode.path("status").asText("error");
                    String messageText = resultNode.has("message") ? resultNode.get("message").asText() : null;

                    TestResult result = new TestResult();
                    result.setJobId(jobId);
                    result.setExecutionTime(executionTimeMs);
                    result.setPassedTestCases(passed);
                    result.setTotalTestCases(total);
                    result.setStatus(status);
                    result.setMessage(messageText);

                    testResultRepository.save(result);
                    System.out.println("Saved result for jobId=" + jobId);

                    sqsClient.deleteMessage(DeleteMessageRequest.builder()
                            .queueUrl(RESPONSE_QUEUE_URL)
                            .receiptHandle(message.receiptHandle())
                            .build());

                } catch (JsonProcessingException e) {
                    System.err.println("Failed to parse SQS message JSON: " + e.getMessage());
                }
            }
        } catch (SqsException e) {
            System.err.println("AWS SQS service error: " + e.awsErrorDetails().errorMessage());
        } catch (SdkClientException e) {
            System.err.println("AWS SDK client/network error: " + e.getMessage());
        } catch (IllegalArgumentException | NullPointerException e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}
