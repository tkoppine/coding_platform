package com.coding.test.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coding.test.model.Question;

@Repository
public interface QuestionsRepository extends JpaRepository<Question, Long> {
    List<Question> findByLanguage(String language);

    Optional<Question> findByQuestionIdAndLanguage(Long questionId, String language);
}
