package com.coding.test.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coding.test.model.Question;
import com.coding.test.service.QuestionsService;

@RestController
@RequestMapping("/api/questions")
public class QuestionsController {
    private final QuestionsService questionsService;

    public QuestionsController(QuestionsService questionsService) {
        this.questionsService = questionsService;
    }

    @GetMapping
    public List<Question> getAllQuestions(@RequestParam(defaultValue = "java") String language) {
        return questionsService.getAllQuestions(language);
    }

    @GetMapping("/{id}")
    public Question getQuestionById(@PathVariable Long id, @RequestParam(defaultValue = "java") String language) {
        return questionsService.getQuestionById(id, language).orElseThrow(() -> new RuntimeException("Question not found"));
    }
}
