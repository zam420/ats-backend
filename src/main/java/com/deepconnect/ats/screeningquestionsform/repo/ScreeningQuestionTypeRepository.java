package com.deepconnect.ats.screeningquestionsform.repo;

import org.springframework.stereotype.Repository;

import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionType;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ScreeningQuestionTypeRepository extends JpaRepository<ScreeningQuestionType, Integer> {
}
