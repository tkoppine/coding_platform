package com.coding.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coding.test.model.TestResult;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, String> {

}
