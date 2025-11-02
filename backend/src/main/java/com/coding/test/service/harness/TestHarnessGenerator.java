package com.coding.test.service.harness;

import java.util.List;

import org.springframework.stereotype.Service;

import com.coding.test.model.TestCase;
@Service
public interface TestHarnessGenerator {
    String getLanguage();
    String getFileExtension();
    String generateTestCode(List<TestCase> testCases, String methodName);
}
