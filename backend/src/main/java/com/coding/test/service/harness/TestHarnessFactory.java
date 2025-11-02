package com.coding.test.service.harness;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class TestHarnessFactory {
    private final Map<String, TestHarnessGenerator> generatorMap = new HashMap<>();

    public TestHarnessFactory(List<TestHarnessGenerator> generators) {
        for (TestHarnessGenerator generator : generators) {
            generatorMap.put(generator.getLanguage().toLowerCase(), generator);
        }
    }

    public TestHarnessGenerator getGenerator(String language) {
        TestHarnessGenerator generator = generatorMap.get(language.toLowerCase());
        if (generator == null) {
            throw new IllegalArgumentException("Unsupported programming language: " + language);
        }
        return generator;
    }
}
