package com.deepconnect.ats.screeningquestiontemplate.repo;

import org.springframework.stereotype.Repository;

import com.deepconnect.ats.screeningquestiontemplate.model.ScreeningQuestionType;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ScreeningQuestionTypeRepository extends JpaRepository<ScreeningQuestionType, Integer> {
}
