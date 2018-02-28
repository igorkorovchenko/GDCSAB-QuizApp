package com.example.android.quizapp.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.quizapp.R;

/**
 * Class for working with questions
 *
 * @package com.example.android.quizapp
 * (c) 2018, Igor Korovchenko
 */
@SuppressLint("ViewConstructor")
public class Question extends LinearLayout {

    /**
     * Score for this question
     */
    private float mScore = 0;

    /**
     * Answer for this question
     */
    private Answer mAnswer;

    /**
     * TextView with question
     */
    private TextView mQuestion;

    /**
     * Constructor of the class
     *
     * @param context context of the app
     * @param questionCode number of the question
     * @param question text of question
     * @param answer answer's info as a string
     * @param score score of the question
     */
    public Question(Context context, int questionCode, String question, Answer answer, float score) {
        super(context);
        this.setOrientation(LinearLayout.VERTICAL);
        this.mScore = score;
        this.mAnswer = answer;
        mQuestion = new TextView(context);
        mQuestion.setId(Answer.getQuestionCodeOffset(questionCode));
        mQuestion.setText(question);
        mQuestion.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT)
        );
        this.setPadding(
                context.getResources().getDimensionPixelOffset(R.dimen.padding_between_views),
                context.getResources().getDimensionPixelOffset(R.dimen.padding_between_views),
                context.getResources().getDimensionPixelOffset(R.dimen.padding_between_views),
                context.getResources().getDimensionPixelOffset(R.dimen.padding_between_views)
        );
        mQuestion.setPadding(
                context.getResources().getDimensionPixelOffset(R.dimen.padding_left_subviews),
                context.getResources().getDimensionPixelOffset(R.dimen.padding_between_views),
                context.getResources().getDimensionPixelOffset(R.dimen.padding_between_views),
                context.getResources().getDimensionPixelOffset(R.dimen.padding_between_views)
        );
        mQuestion.setTextSize(context.getResources().getDimension(R.dimen.questions_text_size));
        this.addView(mQuestion);
        for (int i = 0; i < mAnswer.getCount(); i++) {
            this.addView(mAnswer.getViewAtIndex(i));
        }
    }

    /**
     * Check answer of the user due to key
     *
     * @return true if answer is correct and false if not
     */
    public boolean checkAnswer() {
        return mAnswer.checkAnswer(mAnswer.getCheckedAnswersIds());
    }

    public String getSurveyAnswers() {
        String msg = mQuestion.getText().toString() + "\n";
        msg += mAnswer.getSurveyAnswers();
        return msg;
    }

    /**
     * Getting score of the question due to correctness of the user's answer
     *
     * @return score of the question
     */
    public float getScore() {
        return this.mScore;
    }
}
