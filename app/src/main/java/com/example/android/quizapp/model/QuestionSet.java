package com.example.android.quizapp.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class for working with list of questions
 *
 * @package com.example.android.quizapp
 * (c) 2018, Igor Korovchenko.
 */
@SuppressLint("ViewConstructor")
public class QuestionSet extends LinearLayout {

    /**
     * Keys for parsing JSON with questions
     */
    public static final String QUESTION_TYPE_KEY = "type";
    public static final String ANSWER_KEY        = "test";
    public static final String RIGHT_ANSWER_KEY  = "answer";
    public static final String SCORE_KEY         = "score";
    public static final String CONTENT_KEY       = "text";

    /**
     * Context of the app
     */
    private Context mContext;

    /**
     * File name of the JSON with questions' info
     */
    private String mFileName;

    /**
     * Array of questions
     */
    private Question[] mQuestions;

    /**
     * Counter for calculation of questions with user's answer
     */
    private int mPassedQuestionCount = 0;

    /**
     * Constructor of the class which set file name with all questions' settings in JSON format
     *
     * @param context context of the app
     * @param fileName file name in assets folder with questions' settings in JSON format
     */
    public QuestionSet(Context context, String fileName) {
        super(context);
        this.mContext = context;
        this.mFileName = fileName;
        this.setOrientation(LinearLayout.VERTICAL);
        setQuestions();
    }

    /**
     * Parsing JSON with questions' settings
     *
     * @param context context of the app
     * @param questionDictionary dictionary with questions' info
     */
    public void importQuestions(Context context, JSONObject questionDictionary) {
        int questionsCount = questionDictionary.names().length();
        mQuestions = new Question[questionsCount];
        for (int i = 0; i < questionsCount; i++) {
            try {
                int number = questionDictionary.names().getInt(i);
                JSONObject questionObject = questionDictionary.getJSONObject(
                        String.valueOf(number));
                String question = questionObject.getString(CONTENT_KEY);
                int questionType = questionObject.getInt(QUESTION_TYPE_KEY);
                float score = Float.valueOf(String.valueOf(questionObject.getDouble(SCORE_KEY)));
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
                mQuestions[i] = new Question(context, number, question, answer, score);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Getting count of questions in this set
     *
     * @return question count
     */
    public int getQuestionCount() {
        return mQuestions.length;
    }

    /**
     * Getting count of questions with right answer in this set
     *
     * @return passed question count
     */
    public int getPassedQuestionCount() {
        return mPassedQuestionCount;
    }

    /**
     * Getting scores due to all corrected answers
     *
     * @return scores in float format
     */
    public float getCommonScore() {
        float result = 0;
        mPassedQuestionCount = 0;
        for (Question mQuestion : mQuestions) {
            if (mQuestion.checkAnswer()) {
                mPassedQuestionCount += 1;
                result += mQuestion.getScore();
            }
        }
        return result;
    }

    /**
     * Getting all answers for this list of question
     *
     * @return all right answers for this question set in String format
     */
    public String getSurveyAnswers() {
        StringBuilder message = new StringBuilder();
        for (Question mQuestion : mQuestions) {
            message.append(mQuestion.getSurveyAnswers()).append("\n\n");
        }
        return message.toString();
    }

    /**
     * Reset all questions from JSON file
     */
    public void resetQuestions() {
        this.removeAllViews();
        setQuestions();
    }

    /**
     * Setting up all questions and adding them to LinearLayout
     */
    private void setQuestions() {
        try {
            importQuestions(mContext, new JSONObject(loadJSONFromAsset(mContext, mFileName)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (Question mQuestion : mQuestions) {
            this.addView(mQuestion);
        }
    }

    /**
     * Getting JSON string from the file in assets folder
     *
     * @param context context of the app
     * @param jsonFileName file name in assets folder
     * @return JSON object as a string from the file
     */
    private String loadJSONFromAsset(Context context, String jsonFileName) {
        String jsonString;
        try {
            InputStream inputStream = context.getAssets().open(jsonFileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return jsonString;
    }

}
