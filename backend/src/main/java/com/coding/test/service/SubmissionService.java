package com.coding.test.service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.coding.test.model.Question;
import com.coding.test.model.TestCase;
import com.coding.test.repository.QuestionsRepository;
import com.coding.test.repository.TestCaseRepository;
import com.coding.test.service.harness.TestHarnessFactory;
import com.coding.test.service.harness.TestHarnessGenerator;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.sqs.SqsClient;

@Service
public class SubmissionService {
    private final TestCaseRepository testCaseRepository;
    private final TestHarnessFactory testHarnessFactory;
    private final QuestionsRepository questionsRepository;
    private final S3Client s3Client;
    private final SqsClient sqsClient;

    @Value("${aws.s3.bucket.name}")
    private String BUCKET_NAME;
    @Value("${aws.sqs.request.queue.url}")
    private String QUEUE_NAME;

    public SubmissionService(TestCaseRepository testCaseRepository,
            TestHarnessFactory testHarnessFactory,
            QuestionsRepository questionsRepository,
            S3Client s3Client,
            SqsClient sqsClient) {
        this.testCaseRepository = testCaseRepository;
        this.testHarnessFactory = testHarnessFactory;
        this.questionsRepository = questionsRepository;
        this.s3Client = s3Client;
        this.sqsClient = sqsClient;
    }

    public String injectUserCode(String userCode, Long questionId, String language) throws Exception {
        try {
            String jobId = UUID.randomUUID().toString();

            TestHarnessGenerator generator = testHarnessFactory.getGenerator(language);

            Path path = new ClassPathResource("templates/code/" + language.toLowerCase() + "_skeleton.txt")
                    .getFile()
                    .toPath();
            String skeletonCode = Files.readString(path);

            List<TestCase> testCases = testCaseRepository.findByQuestionId(questionId);

            Question question = questionsRepository
                    .findByQuestionIdAndLanguage(questionId, language)
                    .orElseThrow(() -> new Exception("Question not found"));

            String methodName = question.getMethodName();

            String testCaseCode = generator.generateTestCode(testCases, methodName);

            String finalCode = skeletonCode
                    .replace("<<<SIGNATURE_PLACEHOLDER>>>", userCode)
                    .replace("<<<TEST_CASES_PLACEHOLDER>>>", testCaseCode);

            System.out.println("Final Code after injection:\n" + finalCode);

            String extension = generator.getFileExtension();
            String fileName = language.equalsIgnoreCase("java") ? "Main" + extension : "main" + extension;

            String s3Key = "submissions/" + jobId + "/" + fileName;

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(BUCKET_NAME)
                            .key(s3Key)
                            .contentType("text/plain")
                            .build(),
                    RequestBody.fromString(finalCode, StandardCharsets.UTF_8));

            String queueUrl = sqsClient.getQueueUrl(builder -> builder.queueName(QUEUE_NAME)).queueUrl();

            String messageBody = String.format(
                    "{\"jobId\":\"%s\",\"s3Key\":\"%s\",\"language\":\"%s\"}",
                    jobId, s3Key, language);

            sqsClient.sendMessage(builder -> builder.queueUrl(queueUrl).messageBody(messageBody));

            return jobId;
        } catch (java.io.IOException | java.lang.IllegalArgumentException e) {
            throw new Exception("Error injecting user code", e);
        }
    }

}
