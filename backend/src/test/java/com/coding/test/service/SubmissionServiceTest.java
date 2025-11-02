
package com.coding.test.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

import com.coding.test.repository.QuestionsRepository;
import com.coding.test.repository.TestCaseRepository;
import com.coding.test.service.harness.TestHarnessFactory;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;


class SubmissionServiceTest {

    @Test
    void testConstructorInitializesFields() {
        TestCaseRepository testCaseRepository = mock(TestCaseRepository.class);
        TestHarnessFactory testHarnessFactory = mock(TestHarnessFactory.class);
        QuestionsRepository questionsRepository = mock(QuestionsRepository.class);
        S3Client s3Client = mock(S3Client.class);
        SqsClient sqsClient = mock(SqsClient.class);

        SubmissionService service = new SubmissionService(
                testCaseRepository,
                testHarnessFactory,
                questionsRepository,
                s3Client,
                sqsClient);

        assertNotNull(service);
    }
}