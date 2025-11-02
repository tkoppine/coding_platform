package com.coding.test.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coding.test.model.Question;
import com.coding.test.repository.QuestionsRepository;

class QuestionsServiceTest {

    private QuestionsRepository questionsRepository;
    private QuestionsService questionsService;

    @BeforeEach
    void setUp() {
        questionsRepository = mock(QuestionsRepository.class);
        questionsService = new QuestionsService(questionsRepository);
    }

    @Test
    void testGetAllQuestions_ReplacesEscapedNewlines() {
        Question q1 = new Question();
        q1.setSignature("public void foo()\\n// comment");
        Question q2 = new Question();
        q2.setSignature(null);
        List<Question> mockQuestions = Arrays.asList(q1, q2);

        when(questionsRepository.findByLanguage("Java")).thenReturn(mockQuestions);

        List<Question> result = questionsService.getAllQuestions("Java");

        assertEquals(2, result.size());
        assertEquals("public void foo()\n// comment", result.get(0).getSignature());
        assertNull(result.get(1).getSignature());
        verify(questionsRepository).findByLanguage("Java");
    }

    @Test
    void testGetQuestionById_ReplacesEscapedNewlines() {
        Question q = new Question();
        q.setSignature("int bar()\\nreturn 1;");
        Optional<Question> mockOpt = Optional.of(q);

        when(questionsRepository.findByQuestionIdAndLanguage(1L, "Java")).thenReturn(mockOpt);

        Optional<Question> result = questionsService.getQuestionById(1L, "Java");

        assertTrue(result.isPresent());
        assertEquals("int bar()\nreturn 1;", result.get().getSignature());
        verify(questionsRepository).findByQuestionIdAndLanguage(1L, "Java");
    }

    @Test
    void testGetQuestionById_EmptyOptional() {
        when(questionsRepository.findByQuestionIdAndLanguage(2L, "Python")).thenReturn(Optional.empty());

        Optional<Question> result = questionsService.getQuestionById(2L, "Python");

        assertFalse(result.isPresent());
        verify(questionsRepository).findByQuestionIdAndLanguage(2L, "Python");
    }

    @Test
    void testGetAllQuestions_WithNullSignature() {
        Question q1 = new Question();
        q1.setSignature(null);
        List<Question> mockQuestions = Arrays.asList(q1);

        when(questionsRepository.findByLanguage("Python")).thenReturn(mockQuestions);

        List<Question> result = questionsService.getAllQuestions("Python");

        assertEquals(1, result.size());
        assertNull(result.get(0).getSignature());
        verify(questionsRepository).findByLanguage("Python");
    }

    @Test
    void testGetAllQuestions_WithNoQuestions() {
        when(questionsRepository.findByLanguage("JavaScript")).thenReturn(Arrays.asList());

        List<Question> result = questionsService.getAllQuestions("JavaScript");

        assertTrue(result.isEmpty());
        verify(questionsRepository).findByLanguage("JavaScript");
    }

    @Test
    void testGetAllQuestions_MultipleQuestionsWithEscapedNewlines() {
        Question q1 = new Question();
        q1.setSignature("sig1\\nline2");
        Question q2 = new Question();
        q2.setSignature("sig2\\nlineB");
        List<Question> mockQuestions = Arrays.asList(q1, q2);

        when(questionsRepository.findByLanguage("C++")).thenReturn(mockQuestions);

        List<Question> result = questionsService.getAllQuestions("C++");

        assertEquals("sig1\nline2", result.get(0).getSignature());
        assertEquals("sig2\nlineB", result.get(1).getSignature());
        verify(questionsRepository).findByLanguage("C++");
    }

    @Test
    void testGetQuestionById_WithNullSignature() {
        Question q = new Question();
        q.setSignature(null);
        Optional<Question> mockOpt = Optional.of(q);

        when(questionsRepository.findByQuestionIdAndLanguage(10L, "Go")).thenReturn(mockOpt);

        Optional<Question> result = questionsService.getQuestionById(10L, "Go");

        assertTrue(result.isPresent());
        assertNull(result.get().getSignature());
        verify(questionsRepository).findByQuestionIdAndLanguage(10L, "Go");
    }

    @Test
    void testGetQuestionById_WithNoQuestionFound() {
        when(questionsRepository.findByQuestionIdAndLanguage(99L, "Ruby")).thenReturn(Optional.empty());

        Optional<Question> result = questionsService.getQuestionById(99L, "Ruby");

        assertFalse(result.isPresent());
        verify(questionsRepository).findByQuestionIdAndLanguage(99L, "Ruby");
    }

    @Test
    void testGetQuestionById_WithMultipleEscapedNewlines() {
        Question q = new Question();
        q.setSignature("def foo():\\n    pass\\n# end");
        Optional<Question> mockOpt = Optional.of(q);

        when(questionsRepository.findByQuestionIdAndLanguage(5L, "Python")).thenReturn(mockOpt);

        Optional<Question> result = questionsService.getQuestionById(5L, "Python");

        assertTrue(result.isPresent());
        assertEquals("def foo():\n    pass\n# end", result.get().getSignature());
        verify(questionsRepository).findByQuestionIdAndLanguage(5L, "Python");
    }

}