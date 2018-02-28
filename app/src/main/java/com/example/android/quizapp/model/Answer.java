package com.example.android.quizapp.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.android.quizapp.R;

import java.util.ArrayList;

/**
 * Class for working with answers
 *
 * @package com.example.android.quizapp
 * (c) 2018, Igor Korovchenko
 */
@SuppressLint("ViewConstructor")
class Answer extends View {

    /**
     * Tag for debugging
     */
    private final String TAG = "Answer";

    /**
     * Debugging messages
     */
    private final String TAG_MSG_UNAVAILABLE_TYPE = "This type of the answer is unavailable";
    private final String TAG_MSG_OUT_OF_RANGE = "This index is out of range";

    /**
     * Codes of questions' types
     */
    public static final int QUESTION_TYPE_SHORT_TEXT = 0;
    public static final int QUESTION_TYPE_CHECK_BOX = 3;
    public static final int QUESTION_TYPE_RADIO_BOX = 4;

    /**
     * Integer offset for defining views' ids
     */
    public static final int COMMON_CODE_OFFSET = 1000;
    public static final int QUESTION_CODE_OFFSET = 1000;
    public static final int ANSWER_CODE_OFFSET = 10;
    public static final int ANSWER_GROUP_CODE_OFFSET = 1;
    public static final int QUESTION_TYPE_RADIO_BOX_OFFSET = 1;

    /**
     * Array of views with answers
     */
    private View[] viewsSet;

    /**
     * Count of answers
     */
    private int answerCount = 0;

    /**
     * Type of question
     */
    private int type;

    /**
     * Array of right answers' indexes
     */
    private int[] rightAnswer;

    /**
     * Context of the app
     */
    private Context context;

    /**
     * Static method for getting offset for defining question's view id
     * @param questionCode code of the question
     * @return offset for the view id
     */
    public static int getQuestionCodeOffset(int questionCode) {
        return COMMON_CODE_OFFSET + questionCode * QUESTION_CODE_OFFSET;
    }

    /**
     * Static method for getting offset for defining answer's view id
     * @param questionCode code of the question
     * @return offset for the view id
     */
    public static int getAnswerId(int index, int questionCode) {
        return getQuestionCodeOffset(questionCode) + index + ANSWER_CODE_OFFSET;
    }

    /**
     * Constructor for the class with following info of the answers
     *
     * @param context context of the app
     * @param questionCode code of the question
     * @param questionType type of the question
     * @param answersSet string array with texts for all answers
     * @param rightAnswer array of right answers' codes
     */
    public Answer(Context context, int questionCode, int questionType, String[] answersSet, int[] rightAnswer) {
        super(context);
        this.context = context;
        this.answerCount = answersSet.length;
        this.type = questionType;
        this.rightAnswer = rightAnswer;
        switch (questionType) {
            case QUESTION_TYPE_SHORT_TEXT:
                viewsSet = new View[1];
                newEditText(context, questionCode, answersSet);
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

    /**
     * Getting count of the answers
     *
     * @return answers' count
     */
    public int getCount() {
        switch (type) {
            case QUESTION_TYPE_SHORT_TEXT:
            case QUESTION_TYPE_RADIO_BOX:
                return 1;
            default:
                return answerCount;
        }
    }

    /**
     * Getting answer's view at index
     *
     * @param index index of the answers in the array
     * @return view of the answer
     */
    public View getViewAtIndex(int index) {
        if ((index >= 0) && (index < answerCount)) {
            return viewsSet[index];
        }
        Log.d(TAG, TAG_MSG_OUT_OF_RANGE);
        return null;
    }

    /**
     * Change text color for right answers
     */
    @SuppressLint("ResourceAsColor")
    public void showRightAnswers() {
        if (type == QUESTION_TYPE_CHECK_BOX) {
            for (int aRightAnswer : rightAnswer) {
                ((CheckBox) viewsSet[aRightAnswer - 1]).setTextColor(getResources().getColor(R.color.rightAnswerTextColor));
            }
        } else if (type == QUESTION_TYPE_RADIO_BOX) {
            for (int aRightAnswer : rightAnswer) {
                ((RadioButton) viewsSet[aRightAnswer]).setTextColor(getResources().getColor(R.color.rightAnswerTextColor));
            }
        } else if (type == QUESTION_TYPE_SHORT_TEXT) {
            EditText et = (EditText) viewsSet[0];
            et.setTextColor(getResources().getColor(R.color.rightAnswerTextColor));
        }
    }

    /**
     * Checking answers
     * @param id ids of the all answers' views
     * @return true if answer is correct, false if not
     */
    public boolean checkAnswer(ArrayList<Integer> id) {
        int rightAnswerCount = 0;
        int wrongAnswerCount = id.size();
        if (type == QUESTION_TYPE_CHECK_BOX) {
            for (int i = 0; i < id.size(); i++) {
                for (int j = 0; j < viewsSet.length; j++) {
                    if (id.get(i) == viewsSet[j].getId()) {
                        for (int aRightAnswer : rightAnswer) {
                            if (j == aRightAnswer - 1) {
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
                        for (int aRightAnswer : rightAnswer) {
                            if (j == aRightAnswer) {
                                rightAnswerCount += 1;
                                wrongAnswerCount -= 1;
                            }
                        }
                    }
                }
            }
        } else if (type == QUESTION_TYPE_SHORT_TEXT) {
            EditText et = (EditText) viewsSet[0];
            if (!et.getEditableText().toString().equals("")) {
                rightAnswerCount += 1;
                wrongAnswerCount -= 1;
            }
        }

        showRightAnswers();
        return (rightAnswerCount == rightAnswer.length) && (wrongAnswerCount == 0);
    }

    /**
     * Getting ids of checked answers
     *
     * @return array of ids of checked answers
     */
    @SuppressLint("ResourceAsColor")
    public ArrayList<Integer> getCheckedAnswersIds() {
        ArrayList<Integer> answerIndex = new ArrayList<>();
        if (type == QUESTION_TYPE_CHECK_BOX) {
            for (View aViewsSet : viewsSet) {
                if (((CheckBox) aViewsSet).isChecked()) {
                    answerIndex.add(aViewsSet.getId());
                    ((CheckBox) aViewsSet).setTextColor(getResources().getColor(R.color.wrongAnswerTextColor));
                }
            }
        } else if (type == QUESTION_TYPE_RADIO_BOX) {
            for (int i = 1; i < viewsSet.length; i++) {
                if (((RadioButton) viewsSet[i]).isChecked()) {
                    answerIndex.add(viewsSet[i].getId());
                    ((RadioButton) viewsSet[i]).setTextColor(getResources().getColor(R.color.wrongAnswerTextColor));
                }
            }
        } else if (type == QUESTION_TYPE_SHORT_TEXT) {
            answerIndex.add(viewsSet[0].getId());
        }
        return answerIndex;
    }

    /**
     * Getting all answers for creating e-mail
     *
     * @return text of all user's answers
     */
    public String getSurveyAnswers() {
        StringBuilder answers = new StringBuilder();
        if (type == QUESTION_TYPE_SHORT_TEXT) {
            answers.append(((EditText) viewsSet[0]).getText().toString()).append("\n");
        } else {
            ArrayList<Integer> arr = getCheckedAnswersIds();
            for (int i = 1; i < viewsSet.length; i++) {
                for (int j = 0; j < arr.size(); j++) {
                    if (viewsSet[i].getId() == arr.get(j)) {
                        switch (type) {
                            case QUESTION_TYPE_CHECK_BOX:
                                answers.append(((CheckBox) viewsSet[i]).getText().toString()).append("\n");
                                break;
                            case QUESTION_TYPE_RADIO_BOX:
                                answers.append(((RadioButton) viewsSet[i]).getText().toString()).append("\n");
                                break;
                        }
                    }
                }
            }
        }
        return answers.toString();
    }

    /**
     * Creating new EditText
     *
     * @param context context of the app
     * @param questionCode code of the question
     * @param answersSet array of answers
     */
    private void newEditText(Context context, int questionCode, String[] answersSet) {
        viewsSet[0] = new EditText(context);
        viewsSet[0].setId(getAnswerId(0, questionCode));
        ((EditText) viewsSet[0]).setHint((answersSet.length > 0) ? answersSet[0] : "");
        ((EditText) viewsSet[0]).setTextSize(context.getResources().getDimension(R.dimen.answers_text_size));
        setLayoutParams(0);
    }

    /**
     * Creating new CheckBox
     *
     * @param context context of the app
     * @param index index of the answer in views array
     * @param questionCode code of the question
     * @param answersSet array of answers
     */
    private void newCheckBox(Context context, int index, int questionCode, String[] answersSet) {
        viewsSet[index] = new CheckBox(context);
        viewsSet[index].setId(getAnswerId(index, questionCode));
        ((CheckBox) viewsSet[index]).setText((answersSet.length > 0) ? answersSet[index] : "");
        ((CheckBox) viewsSet[index]).setTextSize(context.getResources().getDimension(R.dimen.answers_text_size));
        setLayoutParams(index);
    }

    /**
     * Creating new RadioButton
     *
     * @param context context of the app
     * @param index index of the answer in views array
     * @param questionCode code of the question
     * @param answersSet array of answers
     */
    private void newRadioButton(Context context, int index, int questionCode, String[] answersSet, int offset) {
        viewsSet[index] = new RadioButton(context);
        viewsSet[index].setId(getAnswerId(index, questionCode));
        ((RadioButton) viewsSet[index]).setText((answersSet.length > 0) ? answersSet[index - offset] : "");
        ((RadioButton) viewsSet[index]).setTextSize(context.getResources().getDimension(R.dimen.answers_text_size));
        setLayoutParams(index);
    }

    /**
     * Setting of the layout parameters for the view of answer
     *
     * @param index index of the answer in views array
     */
    private void setLayoutParams(int index) {
        viewsSet[index].setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        viewsSet[index].setPadding(
                context.getResources().getDimensionPixelOffset(R.dimen.padding_left_subviews),
                context.getResources().getDimensionPixelOffset(R.dimen.padding_between_views),
                context.getResources().getDimensionPixelOffset(R.dimen.padding_between_views),
                context.getResources().getDimensionPixelOffset(R.dimen.padding_between_views)
        );
    }
}
