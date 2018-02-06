package com.example.android.quizapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;

/**
 * Class Answer
 *
 * @package com.example.android.quizapp
 * (c) 2018, Igor Korovchenko
 */

class Answer extends View {

    private final String TAG = "Answer";
    private final String TAG_MSG_UNAVAILABLE_TYPE = "This type of the answer is unavailable";
    private final String TAG_MSG_OUT_OF_RANGE = "This index is out of range";

    public static final int QUESTION_TYPE_SHORT_TEXT = 0;
    public static final int QUESTION_TYPE_CHECK_BOX = 3;
    public static final int QUESTION_TYPE_RADIO_BOX = 4;

    public static final int COMMON_CODE_OFFSET = 1000;
    public static final int QUESTION_CODE_OFFSET = 1000;
    public static final int ANSWER_CODE_OFFSET = 10;
    public static final int ANSWER_GROUP_CODE_OFFSET = 1;
    public static final int QUESTION_TYPE_RADIO_BOX_OFFSET = 1;

    private final int paddingDp = 16; // 16dp
    private final int textSizeSp = 16; // 16sp

    private View[] viewsSet;
    private int answerCount = 0;
    private int type;
    private int[] rightAnswer;

    private Context context;

    public static int getQuestionCodeOffset(int questionCode) {
        return COMMON_CODE_OFFSET + questionCode * QUESTION_CODE_OFFSET;
    }

    public static int getAnswerId(int index, int questionCode) {
        int id = getQuestionCodeOffset(questionCode) + index + ANSWER_CODE_OFFSET;
        return id;
    }

    public Answer(Context context, int questionCode, int questionType, String[] answersSet, int[] rightAnswer) {
        super(context);

        this.context = context;

        this.answerCount = answersSet.length;
        this.type = questionType;
        this.rightAnswer = rightAnswer;

        switch (questionType) {
            case QUESTION_TYPE_SHORT_TEXT:
                viewsSet = new View[1];
                newEditText(context, 0, questionCode, answersSet);
                break;
            case QUESTION_TYPE_CHECK_BOX:
                viewsSet = new View[answerCount];
                for (int i = 0; i < answerCount; i++) {
                    newCheckBox(context, i, questionCode, answersSet);
                }
                break;
            case QUESTION_TYPE_RADIO_BOX:
                viewsSet = new View[answerCount + QUESTION_TYPE_RADIO_BOX_OFFSET];
                RadioGroup radioGroup = new RadioGroup(context);
                radioGroup.setOrientation(RadioGroup.VERTICAL);
                radioGroup.setId(getQuestionCodeOffset(questionCode) + ANSWER_GROUP_CODE_OFFSET);
                for (int i = QUESTION_TYPE_RADIO_BOX_OFFSET; i < answerCount + QUESTION_TYPE_RADIO_BOX_OFFSET; i++) {
                    newRadioButton(context, i, questionCode, answersSet, QUESTION_TYPE_RADIO_BOX_OFFSET);
                    radioGroup.addView(viewsSet[i]);
                }
                viewsSet[0] = radioGroup;
                break;
            default:
                Log.d(TAG, TAG_MSG_UNAVAILABLE_TYPE);
        }
    }

    public int getCount() {
        switch (type) {
            case QUESTION_TYPE_SHORT_TEXT:
            case QUESTION_TYPE_RADIO_BOX:
                return 1;
            default:
                return answerCount;
        }
    }

    public View getViewAtIndex(int index) {
        if ((index >= 0) && (index < answerCount)) {
            return viewsSet[index];
        }
        Log.d(TAG, TAG_MSG_OUT_OF_RANGE);
        return null;
    }

    @SuppressLint("ResourceAsColor")
    public void showRightAnswers() {
        if (type == QUESTION_TYPE_CHECK_BOX) {
            for (int i = 0; i < rightAnswer.length; i++) {
                ((CheckBox) viewsSet[rightAnswer[i] - 1]).setTextColor(getResources().getColor(R.color.colorRightAnswer));
            }
        } else if (type == QUESTION_TYPE_RADIO_BOX) {
            for (int i = 0; i < rightAnswer.length; i++) {
                ((RadioButton) viewsSet[rightAnswer[i]]).setTextColor(getResources().getColor(R.color.colorRightAnswer));
            }
        }
    }

    public boolean checkAnswer(ArrayList<Integer> id) {

        int rightAnswerCount = 0;
        int wrongAnswerCount = id.size();

        if (type == QUESTION_TYPE_CHECK_BOX) {
            for (int i = 0; i < id.size(); i++) {
                for (int j = 0; j < viewsSet.length; j++) {
                    if (id.get(i) == viewsSet[j].getId()) {
                        for (int k = 0; k < rightAnswer.length; k++) {
                            if (j == rightAnswer[k] - 1) {
                                rightAnswerCount += 1;
                                wrongAnswerCount -= 1;
                            }
                        }
                    }
                }
            }
        } else if (type == QUESTION_TYPE_RADIO_BOX) {
            for (int i = 0; i < id.size(); i++) {
                for (int j = 1; j < viewsSet.length; j++) {
                    if (id.get(i) == viewsSet[j].getId()) {
                        for (int k = 0; k < rightAnswer.length; k++) {
                            if (j == rightAnswer[k]) {
                                rightAnswerCount += 1;
                                wrongAnswerCount -= 1;
                            }
                        }
                    }
                }
            }
        }

        showRightAnswers();
        return (rightAnswerCount == rightAnswer.length) && (wrongAnswerCount == 0);
    }

    public int getType() {
        return this.type;
    }

    @SuppressLint("ResourceAsColor")
    public ArrayList<Integer> getCheckedAnswersIds() {
        ArrayList<Integer> answerIndex = new ArrayList<Integer>();
        if (type == QUESTION_TYPE_CHECK_BOX) {
            for (int i = 0; i < viewsSet.length; i++) {
                if (((CheckBox) viewsSet[i]).isChecked()) {
                    answerIndex.add(viewsSet[i].getId());
                    ((CheckBox) viewsSet[i]).setTextColor(getResources().getColor(R.color.colorWrongAnswer));
                }
            }
        } else if (type == QUESTION_TYPE_RADIO_BOX) {
            for (int i = 1; i < viewsSet.length; i++) {
                if (((RadioButton) viewsSet[i]).isChecked()) {
                    answerIndex.add(viewsSet[i].getId());
                    ((RadioButton) viewsSet[i]).setTextColor(getResources().getColor(R.color.colorWrongAnswer));
                }
            }
        } else if (type == QUESTION_TYPE_SHORT_TEXT) {
            answerIndex.add(viewsSet[0].getId());
        }
        return answerIndex;
    }

    public String getSurveyAnswers() {
        String answers = "";
        if (type == QUESTION_TYPE_SHORT_TEXT) {
            answers += ((EditText) viewsSet[0]).getText().toString() + "\n";
        } else {
            ArrayList<Integer> arr = getCheckedAnswersIds();
            for (int i = 0; i < viewsSet.length; i++) {
                for (int j = 0; j < arr.size(); j++) {
                    if (viewsSet[i].getId() == arr.get(j)) {
                        switch (type) {
                            case QUESTION_TYPE_CHECK_BOX:
                                answers += ((CheckBox) viewsSet[i]).getText().toString() + "\n";
                                break;
                            case QUESTION_TYPE_RADIO_BOX:
                                answers += ((RadioButton) viewsSet[i]).getText().toString() + "\n";
                                break;
                        }
                    }
                }
            }
        }
        return answers;
    }

    private void newEditText(Context context, int index, int questionCode, String[] answersSet) {
        viewsSet[index] = new EditText(context);
        viewsSet[index].setId(getAnswerId(index, questionCode));
        ((EditText) viewsSet[index]).setHint((answersSet.length > 0) ? answersSet[index] : "");
        ((EditText) viewsSet[index]).setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp);
        setLayoutParams(index);
    }

    private void newCheckBox(Context context, int index, int questionCode, String[] answersSet) {
        viewsSet[index] = new CheckBox(context);
        viewsSet[index].setId(getAnswerId(index, questionCode));
        ((CheckBox) viewsSet[index]).setText((answersSet.length > 0) ? answersSet[index] : "");
        ((CheckBox) viewsSet[index]).setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp);
        setLayoutParams(index);
    }

    private void newRadioButton(Context context, int index, int questionCode, String[] answersSet, int offset) {
        viewsSet[index] = new RadioButton(context);
        viewsSet[index].setId(getAnswerId(index, questionCode));
        ((RadioButton) viewsSet[index]).setText((answersSet.length > 0) ? answersSet[index - offset] : "");
        ((RadioButton) viewsSet[index]).setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp);
        setLayoutParams(index);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    private void setLayoutParams(int index) {
        viewsSet[index].setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        viewsSet[index].setPadding(dp2px(paddingDp), dp2px(paddingDp), dp2px(paddingDp), dp2px(paddingDp));
    }
}
