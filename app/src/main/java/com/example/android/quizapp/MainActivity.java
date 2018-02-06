package com.example.android.quizapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private QuestionSet questionSet;

    private TextView tvScore;
    private ScrollView container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvScore = findViewById(R.id.tvScore);
        container = findViewById(R.id.container);
        String jsonQuestions =
                "{\n" +
                "  \"1\": {\n" +
                "    \"type\": 4,\n" +
                "    \"text\": \"Do you like coding for Androis?\",\n" +
                "    \"test\": {\n" +
                "      \"1\": {\n" +
                "        \"text\": \"Yes! I love it!\"\n" +
                "      },\n" +
                "      \"2\": {\n" +
                "        \"text\": \"No! It's the most bad experience!\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"answer\":[1],\n" +
                "    \"score\":20.0\n" +
                "  },\n" +
                "  \"2\": {\n" +
                "    \"type\": 3,\n" +
                "    \"text\": \"Do you want to be the Android Developer Expert?\",\n" +
                "    \"test\": {\n" +
                "      \"1\": {\n" +
                "        \"text\": \"It's my favourite dream!\"\n" +
                "      },\n" +
                "      \"2\": {\n" +
                "        \"text\": \"I'd like to be the Apple Developer Expert! Do you know about that? How I can achieve this certificate?\"\n" +
                "      },\n" +
                "      \"3\": {\n" +
                "        \"text\": \"I don't like coding! I've said already!\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"answer\":[1,2],\n" +
                "    \"score\":20.0\n" +
                "  },\n" +
                "  \"3\": {\n" +
                "    \"type\": 4,\n" +
                "    \"text\": \"Have you Android device for developing?\",\n" +
                "    \"test\": {\n" +
                "      \"1\": {\n" +
                "        \"text\": \"Sure!\"\n" +
                "      },\n" +
                "      \"2\": {\n" +
                "        \"text\": \"I like iPhone!\"\n" +
                "      },\n" +
                "      \"3\": {\n" +
                "        \"text\": \"No!\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"answer\":[1],\n" +
                "    \"score\":20.0\n" +
                "  },\n" +
                "  \"4\": {\n" +
                "    \"type\": 3,\n" +
                "    \"text\": \"Have you ever met the real Android Developer Expert?\",\n" +
                "    \"test\": {\n" +
                "      \"1\": {\n" +
                "        \"text\": \"Mmmm.... I would like to realize this meetup!\"\n" +
                "      },\n" +
                "      \"2\": {\n" +
                "        \"text\": \"I want to be the expert!\"\n" +
                "      },\n" +
                "      \"3\": {\n" +
                "        \"text\": \"It's anavailable for me...((\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"answer\":[1,2],\n" +
                "    \"score\":20.0\n" +
                "  },\n" +
                "  \"5\": {\n" +
                "    \"type\": 0,\n" +
                "    \"text\": \"What do you want to say?\",\n" +
                "    \"test\": {\n" +
                "      \"1\": {\n" +
                "        \"text\": \"Thank you for this course! It was amazing!\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"answer\":[1],\n" +
                "    \"score\":20.0\n" +
                "  }\n" +
                "}";

        questionSet = new QuestionSet(this, jsonQuestions);
        container.addView(questionSet);
    }

    public void submitAnswers(View view) {
        String msg = questionSet.getSurveyAnswers() + "\nScore: " + questionSet.getCommonScore();
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void checkAnswers(View view) {
        double score = questionSet.getCommonScore();
        String result = getString(R.string.score_prefix_text) + String.valueOf(score);
        tvScore.setText(result);
    }
}
