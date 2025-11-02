package com.coding.test.service.harness;

import java.util.List;

import org.springframework.stereotype.Service;

import com.coding.test.model.TestCase;

@Service
public class PythonHarnessGenerator implements TestHarnessGenerator {

    @Override
    public String getLanguage() {
        return "Python";
    }

    @Override
    public String getFileExtension() {
        return ".py";
    }

    @Override
    public String generateTestCode(List<TestCase> testCases, String methodName) {
        StringBuilder code = new StringBuilder();
        code.append("    passed = 0\n");
        code.append("    total = ").append(testCases.size()).append("\n\n");

        for (TestCase testCase : testCases) {
            String input = testCase.getInput();
            String expected = testCase.getExpectedOutput();

            code.append("    if ").append(methodName).append("(").append(input).append(") == ").append(expected)
                    .append(":\n")
                    .append("        passed += 1\n")
                    .append("    else:\n")
                    .append("        print(f\"Test case failed: Input: ").append(input)
                    .append(", Expected: ").append(expected).append("\")\n\n");
        }

        code.append("    status = \"success\" if passed == total else \"failed\"\n");
        code.append("    print(f'RESULT:{{\"passed\":{passed},\"total\":{total},\"status\":\"{status}\"}}')\n");

        return code.toString();
    }

}
