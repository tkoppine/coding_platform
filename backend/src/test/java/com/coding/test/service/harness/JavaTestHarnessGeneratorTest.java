package com.coding.test.service.harness;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.coding.test.model.TestCase;

class JavaTestHarnessGeneratorTest {

    private TestCase createPrimitiveTestCase() {
        TestCase tc = new TestCase();
        tc.setInput("1, 2");
        tc.setExpectedOutput("3");
        tc.setExpectedType("int");
        return tc;
    }

    private TestCase createStringTestCase() {
        TestCase tc = new TestCase();
        tc.setInput("\"abc\"");
        tc.setExpectedOutput("\"abc\"");
        tc.setExpectedType("String");
        return tc;
    }

    private TestCase createIntArrayTestCase() {
        TestCase tc = new TestCase();
        tc.setInput("new int[]{1,2}");
        tc.setExpectedOutput("new int[]{2,1}");
        tc.setExpectedType("int[]");
        return tc;
    }

    private TestCase createStringArrayTestCase() {
        TestCase tc = new TestCase();
        tc.setInput("new String[]{\"a\",\"b\"}");
        tc.setExpectedOutput("new String[]{\"b\",\"a\"}");
        tc.setExpectedType("String[]");
        return tc;
    }

    private TestCase createTreeNodeTestCase() {
        TestCase tc = new TestCase();
        tc.setInput("tree1");
        tc.setExpectedOutput("tree2");
        tc.setExpectedType("TreeNode");
        return tc;
    }

    private TestCase createListTestCase() {
        TestCase tc = new TestCase();
        tc.setInput("list1");
        tc.setExpectedOutput("list2");
        tc.setExpectedType("List<Integer>");
        return tc;
    }

    private TestCase createResultOutputTestCase() {
        TestCase tc = new TestCase();
        tc.setInput("1");
        tc.setExpectedOutput("2");
        tc.setExpectedType("int");
        return tc;
    }

    @Test
    void testGenerateTestCodeForPrimitiveTypes() {
        JavaTestHarnessGenerator generator = new JavaTestHarnessGenerator();
        TestCase tc = createPrimitiveTestCase();
        String code = generator.generateTestCode(List.of(tc), "add");
        assertTrue(code.contains("add(1, 2) == 3"));
        assertFalse(code.contains("arraysEqual"));
        assertFalse(code.contains("isSameTree"));
    }

    @Test
    void testGenerateTestCodeForStringType() {
        JavaTestHarnessGenerator generator = new JavaTestHarnessGenerator();
        TestCase tc = createStringTestCase();
        String code = generator.generateTestCode(List.of(tc), "reverse");
        assertTrue(code.contains("reverse(\"abc\").equals(\"abc\")"));
    }

    @Test
    void testGenerateTestCodeForIntArrayType() {
        JavaTestHarnessGenerator generator = new JavaTestHarnessGenerator();
        TestCase tc = createIntArrayTestCase();
        String code = generator.generateTestCode(List.of(tc), "swap");
        assertTrue(code.contains("arraysEqual(swap(new int[]{1,2}), new int[]{2,1})"));
        assertTrue(code.contains("public static boolean arraysEqual(int[] a, int[] b)"));
    }

    @Test
    void testGenerateTestCodeForStringArrayType() {
        JavaTestHarnessGenerator generator = new JavaTestHarnessGenerator();
        TestCase tc = createStringArrayTestCase();
        String code = generator.generateTestCode(List.of(tc), "swap");
        assertTrue(code.contains("java.util.Arrays.deepEquals(swap(new String[]{\"a\",\"b\"}), new String[]{\"b\",\"a\"})"));
    }

    @Test
    void testGenerateTestCodeForTreeNodeType() {
        JavaTestHarnessGenerator generator = new JavaTestHarnessGenerator();
        TestCase tc = createTreeNodeTestCase();
        String code = generator.generateTestCode(List.of(tc), "invertTree");
        assertTrue(code.contains("isSameTree(invertTree(tree1), tree2)"));
        assertTrue(code.contains("public static boolean isSameTree(TreeNode p, TreeNode q)"));
    }

    @Test
    void testGenerateTestCodeForListType() {
        JavaTestHarnessGenerator generator = new JavaTestHarnessGenerator();
        TestCase tc = createListTestCase();
        String code = generator.generateTestCode(List.of(tc), "processList");
        assertTrue(code.contains("processList(list1).equals(list2)"));
    }

    @Test
    void testGenerateTestCodeResultOutput() {
        JavaTestHarnessGenerator generator = new JavaTestHarnessGenerator();
        TestCase tc = createResultOutputTestCase();
        String code = generator.generateTestCode(List.of(tc), "f");
        assertTrue(code.contains("System.out.println(\"RESULT:{\\\"passed\\\":"));
    }
}
