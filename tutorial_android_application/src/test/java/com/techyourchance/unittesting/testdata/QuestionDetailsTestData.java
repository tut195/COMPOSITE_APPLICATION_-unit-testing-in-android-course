package com.techyourchance.unittesting.testdata;

import com.techyourchance.unittesting.questions.QuestionDetails;

/**
 * Это общий класс для всех методов, которые будут учавствовать в тестировании!!!
 *
 */
public class QuestionDetailsTestData {

    public static QuestionDetails getQuestionDetails1() {
        return new QuestionDetails("id1", "title1", "body1");
    }

    public static QuestionDetails getQuestionDetails2() {
        return new QuestionDetails("id2", "title2", "body2");
    }
}
