package com.coding.test.service.harness;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.coding.test.model.TestCase;

class PythonHarnessGeneratorTest {

    @Test
    void testGetLanguage() {
        PythonHarnessGenerator generator = new PythonHarnessGenerator();
        assertEquals("Python", generator.getLanguage());
    }

    @Test
    void testGetFileExtension() {
        PythonHarnessGenerator generator = new PythonHarnessGenerator();
        assertEquals(".py", generator.getFileExtension());
    }

    @Test
    void testGenerateTestCode() {
        PythonHarnessGenerator generator = new PythonHarnessGenerator();
        TestCase tc1 = new TestCase();
        tc1.setInput("1, 2");
        tc1.setExpectedOutput("3");

        TestCase tc2 = new TestCase();
        tc2.setInput("2, 3");
        tc2.setExpectedOutput("5");

        List<TestCase> testCases = Arrays.asList(tc1, tc2);
        String methodName = "add";

        String code = generator.generateTestCode(testCases, methodName);

        assertTrue(code.contains("passed = 0"));
        assertTrue(code.contains("total = 2"));
        assertTrue(code.contains("if add(1, 2) == 3"));
        assertTrue(code.contains("if add(2, 3) == 5")); 
    }
}