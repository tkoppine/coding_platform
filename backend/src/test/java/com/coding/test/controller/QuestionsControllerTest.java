package com.coding.test.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.coding.test.model.Question;
import com.coding.test.service.QuestionsService;

public class QuestionsControllerTest {
    
    @Mock
    private QuestionsService questionsService;
    
    @InjectMocks
    private QuestionsController questionsController;
    
    private Question question1;
    private Question question2;
    private List<Question> questionList;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        question1 = new Question();
        question1.setId(1L);
        question1.setTitle("Java Question");
        
        question2 = new Question();
        question2.setId(2L);
        question2.setTitle("Python Question");
        
        questionList = Arrays.asList(question1, question2);
    }
    
    @Test
    void testGetAllQuestions() {
        when(questionsService.getAllQuestions("java")).thenReturn(questionList);
        
        List<Question> response = questionsController.getAllQuestions("java");
        
        assertEquals(2, response.size());
        assertEquals("Java Question", response.get(0).getTitle());
        assertEquals("Python Question", response.get(1).getTitle());
        
        verify(questionsService).getAllQuestions("java");
    }
    
    @Test
    void testGetAllQuestionsWithLanguage() {
        List<Question> pythonQuestions = Arrays.asList(question2);
        when(questionsService.getAllQuestions("python")).thenReturn(pythonQuestions);
        
        List<Question> response = questionsController.getAllQuestions("python");
        
        assertEquals(1, response.size());
        assertEquals("Python Question", response.get(0).getTitle());
        
        verify(questionsService).getAllQuestions("python");
    }
    
    @Test
    void testGetQuestionById() {
        when(questionsService.getQuestionById(1L, "java")).thenReturn(Optional.of(question1));
        
        Question response = questionsController.getQuestionById(1L, "java");
        
        assertNotNull(response);
        assertEquals(question1, response);
        assertEquals("Java Question", response.getTitle());
        
        verify(questionsService).getQuestionById(1L, "java");
    }
    
    @Test
    void testGetQuestionByIdWithLanguage() {
        when(questionsService.getQuestionById(2L, "python")).thenReturn(Optional.of(question2));
        
        Question response = questionsController.getQuestionById(2L, "python");
        
        assertNotNull(response);
        assertEquals(question2, response);
        assertEquals("Python Question", response.getTitle());
        
        verify(questionsService).getQuestionById(2L, "python");
    }
    
    @Test
    void testGetQuestionByIdNotFound() {
        when(questionsService.getQuestionById(99L, "java")).thenReturn(Optional.empty());
        
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
            RuntimeException.class,
            () -> questionsController.getQuestionById(99L, "java")
        );
        
        assertEquals("Question not found", exception.getMessage());
        verify(questionsService).getQuestionById(99L, "java");
    }
}
