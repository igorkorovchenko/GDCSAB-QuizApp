package com.example.android.quizapp;

import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class
 *
 * @package com.example.android.quizapp
 * (c) 2018, Igor Korovchenko.
 */

public class QuestionSet extends LinearLayout {

    private final String TAG = "Answer";

    public static final String QUESTION_TYPE_KEY = "type";
    public static final String ANSWER_KEY = "test";
    public static final String RIGHT_ANSWER_KEY = "answer";
    public static final String SCORE_KEY = "score";
    public static final String CONTENT_KEY = "text";

    private Question[] questions;

    public QuestionSet(Context context) {
        super(context);
        this.setOrientation(LinearLayout.VERTICAL);
    }

    public QuestionSet(Context context, JSONObject questionDictionary) {
        super(context);
        this.setOrientation(LinearLayout.VERTICAL);
        importQuestions(context, questionDictionary);
        for (int i = 0; i < questions.length; i++) {
            this.addView(questions[i]);
        }
    }

    public QuestionSet(Context context, String questionJSONString) {
        super(context);
        this.setOrientation(LinearLayout.VERTICAL);
        try {
            importQuestions(context, new JSONObject(questionJSONString));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < questions.length; i++) {
            this.addView(questions[i]);
        }
    }

    public void importQuestions(Context context, JSONObject questionDictionary) {

        int questionsCount = questionDictionary.names().length();
        questions = new Question[questionsCount];

        for (int i = 0; i < questionsCount; i++) {
            try {
                int number = questionDictionary.names().getInt(i);
                JSONObject questionObject = questionDictionary.getJSONObject(
                        String.valueOf(number));
                String question = questionObject.getString(CONTENT_KEY);
                int questionType = questionObject.getInt(QUESTION_TYPE_KEY);
                double score = questionObject.getDouble(SCORE_KEY);
                JSONObject answerObject = questionObject.getJSONObject(ANSWER_KEY);
                JSONArray rightAnswer = questionObject.getJSONArray(RIGHT_ANSWER_KEY);
                int[] answers = new int[rightAnswer.length()];
                for (int j = 0; j < rightAnswer.length(); j++) {
                    answers[j] = rightAnswer.getInt(j);
                }
                int answersCount = answerObject.names().length();
                String[] answerList = new String[answersCount];
                for (int j = 0; j < answersCount; j++) {
                    answerList[j] = (answerObject.getJSONObject(
                            answerObject.names().getString(j))
                    ).getString(CONTENT_KEY);
                }
                Answer answer = new Answer(context, number, questionType, answerList, answers);
                questions[i] = new Question(context, number, question, answer, score);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public double getCommonScore() {
        double result = 0;

        for (int i = 0; i < questions.length; i++) {
            if (questions[i].checkAnswer()) result += questions[i].getScore();
        }

        return result;
    }

    public String getSurveyAnswers() {
        String message = "";

        for (int i = 0; i < questions.length; i++) {
            message += questions[i].getSurveyAnswers() + "\n\n";
        }

        return message;
    }

}
