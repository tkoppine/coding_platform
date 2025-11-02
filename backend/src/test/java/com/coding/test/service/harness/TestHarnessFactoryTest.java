package com.coding.test.service.harness;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TestHarnessFactoryTest {

    private TestHarnessGenerator javaGenerator;
    private TestHarnessGenerator pythonGenerator;
    private TestHarnessFactory factory;

    @BeforeEach
    void setUp() {
        javaGenerator = mock(TestHarnessGenerator.class);
        when(javaGenerator.getLanguage()).thenReturn("Java");

        pythonGenerator = mock(TestHarnessGenerator.class);
        when(pythonGenerator.getLanguage()).thenReturn("Python");

        factory = new TestHarnessFactory(Arrays.asList(javaGenerator, pythonGenerator));
    }

    @Test
    void testGetGeneratorReturnsCorrectGenerator() {
        TestHarnessGenerator result = factory.getGenerator("Java");
        assertSame(javaGenerator, result);

        result = factory.getGenerator("Python");
        assertSame(pythonGenerator, result);
    }

    @Test
    void testGetGeneratorIsCaseInsensitive() {
        TestHarnessGenerator result = factory.getGenerator("java");
        assertSame(javaGenerator, result);

        result = factory.getGenerator("PYTHON");
        assertSame(pythonGenerator, result);
    }

    @Test
    void testGetGeneratorThrowsExceptionForUnsupportedLanguage() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            factory.getGenerator("Ruby");
        });
        assertEquals("Unsupported programming language: Ruby", exception.getMessage());
    }
}