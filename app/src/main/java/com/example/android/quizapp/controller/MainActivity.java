package com.example.android.quizapp.controller;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import com.example.android.quizapp.R;
import com.example.android.quizapp.model.QuestionSet;

/**
 * MainActivity controller class
 */
public class MainActivity extends Activity {

    /**
     * Keys for saving activity states
     */
    private static final String STATE_CHECKED = "stateChecked";

    /**
     * List of questions
     */
    private QuestionSet mQuestionSet;

    /**
     * Flag is true when user has been checking his answers
     */
    private byte mIsChecked = 0;

    /**
     * All actions which are needed when activity is creating
     *
     * @param savedInstanceState saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQuestionSet = new QuestionSet(this, "questions.json");
        ScrollView container = findViewById(R.id.container);
        container.addView(mQuestionSet);
    }

    /**
     * Saving activity state
     *
     * @param outState state of the activity before destroying
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putByte(STATE_CHECKED, mIsChecked);
        super.onSaveInstanceState(outState);
    }

    /**
     * Saving activity state
     *
     * @param savedInstanceState state of the activity after creating
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mIsChecked = savedInstanceState.getByte(STATE_CHECKED);
        if (mIsChecked != 0) {
            checkAnswers(findViewById(R.id.btnCheck));
        }
    }

    /**
     * Submitting all answers by e-mail
     *
     * @param view button "Submit"
     */
    public void submitAnswers(View view) {
        String msg = mQuestionSet.getSurveyAnswers();
        msg += "\nScore: " + mQuestionSet.getCommonScore();
        msg += "\nStats: "
                + mQuestionSet.getPassedQuestionCount()
                + " / "
                + mQuestionSet.getQuestionCount();
        msg += "\n\n";
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        view.setVisibility(View.GONE);
        ((Button) findViewById(R.id.btnCheck)).setText(R.string.check_button);
    }

    /**
     * Checking answers due to answers' keys and resetting them for testing from the beginning
     *
     * @param view button "Check" / "Reset"
     */
    public void checkAnswers(View view) {
        if (((Button) view).getText().equals(getResources().getString(R.string.reset_button))) {
            mQuestionSet.resetQuestions();
            ((Button) view).setText(R.string.check_button);
            (findViewById(R.id.btnSubmit)).setVisibility(View.GONE);
            mIsChecked = 0;
        } else {
            (findViewById(R.id.btnSubmit)).setVisibility(View.VISIBLE);
            ((Button) view).setText(R.string.reset_button);
            setAnswersCountInTitle();
            mIsChecked = 1;
        }
    }

    /**
     * Setting up count of all and right answers for title of the Activity.
     * Calculating total score of the user after survey
     */
    public void setAnswersCountInTitle() {
        String title = getResources().getString(R.string.app_name)
                + " - Score: " + mQuestionSet.getCommonScore()
                + " (" + mQuestionSet.getPassedQuestionCount()
                + " / " + mQuestionSet.getQuestionCount()
                + ")";
        setTitle(title);
    }
}
