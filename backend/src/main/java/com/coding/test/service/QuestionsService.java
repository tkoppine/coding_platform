package com.coding.test.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.coding.test.model.Question;
import com.coding.test.repository.QuestionsRepository;

@Service
public class QuestionsService {
    private final QuestionsRepository questionsRepository;

    public QuestionsService(QuestionsRepository questionsRepository) {
        this.questionsRepository = questionsRepository;
    }

    public List<Question> getAllQuestions(String language) {
        List<Question> questions = questionsRepository.findByLanguage(language);
        questions.forEach(q -> {
            if (q.getSignature() != null) {
                q.setSignature(q.getSignature().replace("\\n", "\n"));
            }
        });
        return questions;
    }

    public Optional<Question> getQuestionById(Long questionId, String language) {
        Optional<Question> questionOpt = questionsRepository.findByQuestionIdAndLanguage(questionId, language);
        questionOpt.ifPresent(q -> {
            if (q.getSignature() != null) {
                q.setSignature(q.getSignature().replace("\\n", "\n"));
            }
        });
        return questionOpt;
    }
}
