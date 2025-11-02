package com.coding.test.service.harness;

import java.util.List;

import org.springframework.stereotype.Service;

import com.coding.test.model.TestCase;

@Service
public class JavaTestHarnessGenerator implements TestHarnessGenerator {

    @Override
    public String getLanguage() {
        return "Java";
    }

    @Override
    public String getFileExtension() {
        return ".java";
    }

    @Override
    public String generateTestCode(List<TestCase> testCases, String methodName) {
        StringBuilder testCaseCode = new StringBuilder();
        testCaseCode.append("int passed = 0;\n");
        testCaseCode.append("int total = ").append(testCases.size()).append(";\n\n");

        boolean needsTreeHelper = false;
        boolean needsDeepArrayHelper = false;

        for (TestCase testCase : testCases) {
            String type = testCase.getExpectedType();
            String input = testCase.getInput();
            String expected = testCase.getExpectedOutput();

            String comparisonCode;

            switch (type) {
                case "int", "double", "long", "boolean" -> {
                    comparisonCode = methodName + "(" + input + ") == " + expected;
                }
                case "String" -> {
                    comparisonCode = methodName + "(" + input + ").equals(" + expected + ")";
                }
                case "int[]", "double[]" -> {
                    needsDeepArrayHelper = true;
                    comparisonCode = "arraysEqual(" + methodName + "(" + input + "), " + expected + ")";
                }
                case "String[]", "Object[]" -> {
                    needsDeepArrayHelper = true;
                    comparisonCode = "java.util.Arrays.deepEquals(" + methodName + "(" + input + "), " + expected + ")";
                }
                case "List<Integer>", "List<String>", "ArrayList", "LinkedList", "Queue", "Map" -> {
                    comparisonCode = methodName + "(" + input + ").equals(" + expected + ")";
                }
                case "TreeNode" -> {
                    needsTreeHelper = true;
                    comparisonCode = "isSameTree(" + methodName + "(" + input + "), " + expected + ")";
                }
                default -> {
                    comparisonCode = methodName + "(" + input + ").equals(" + expected + ")";
                }
            }

            String ifLine = "        if (" + comparisonCode + ") {\n";
            String failLine = "            System.out.println(\"Test case failed: Input: " + input + ", Expected: "
                    + expected + "\");\n";
            testCaseCode.append(ifLine)
                    .append("            passed++;\n")
                    .append("        } else {\n")
                    .append(failLine)
                    .append("        }\n");
        }

        testCaseCode.append("""
                System.out.println("RESULT:{\\"passed\\":" + passed + "," +
                        "\\"total\\":" + total + "," +
                        "\\"status\\":\\"" + (passed == total ? "success" : "failed") + "\\"}");
                """);

        if (needsTreeHelper) {
            testCaseCode.append("\n")
                    .append("    public static boolean isSameTree(TreeNode p, TreeNode q) {\n")
                    .append("        if (p == null && q == null) return true;\n")
                    .append("        if (p == null || q == null) return false;\n")
                    .append("        return p.val == q.val && isSameTree(p.left, q.left) && isSameTree(p.right, q.right);\n")
                    .append("    }\n");
        }

        if (needsDeepArrayHelper) {
            testCaseCode.append("\n")
                    .append("    public static boolean arraysEqual(int[] a, int[] b) {\n")
                    .append("        return java.util.Arrays.equals(a, b);\n")
                    .append("    }\n")
                    .append("    public static boolean arraysEqual(double[] a, double[] b) {\n")
                    .append("        return java.util.Arrays.equals(a, b);\n")
                    .append("    }\n");
        }

        return testCaseCode.toString();
    }

}
