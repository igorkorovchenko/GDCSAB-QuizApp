package com.example.android.quizapp;

import android.content.Context;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Class Question
 *
 * @package com.example.android.quizapp
 * (c) 2018, Igor Korovchenko
 */

public class Question extends LinearLayout {

    private final int paddingDp = 20; // 16dp
    private final int textSizeSp = 20; // 16sp

    private double score = 0;
    private Answer answer;
    private Context context;

    private TextView tv;

    public Question(Context context, int questionCode, String question, Answer answer, double score) {
        super(context);

        this.context = context;

        this.setOrientation(LinearLayout.VERTICAL);

        this.score = score;
        this.answer = answer;

        tv = new TextView(context);
        tv.setId(Answer.getQuestionCodeOffset(questionCode));
        tv.setText(question);

        tv.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        tv.setPadding(dp2px(paddingDp), dp2px(paddingDp), dp2px(paddingDp), dp2px(paddingDp));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp);

        this.addView(tv);

        for (int i = 0; i < answer.getCount(); i++) {
            this.addView(answer.getViewAtIndex(i));
        }
    }

    public boolean checkAnswer() {
        return answer.checkAnswer(answer.getCheckedAnswersIds());
    }

    public String getSurveyAnswers() {
        String msg = tv.getText().toString() + "\n";
        msg += answer.getSurveyAnswers();
        return msg;
    }

    public double getScore() {
        return this.score;
    }

    public int getType() {
        return this.answer.getType();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
