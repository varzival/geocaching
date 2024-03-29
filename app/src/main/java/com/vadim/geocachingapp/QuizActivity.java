package com.vadim.geocachingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class QuizActivity extends AppCompatActivity {

    // Unique tag for the intent reply.
    public static final String EXTRA_CORRECT =
            "quizactivity.CORRECT";
    public static final String EXTRA_QUIZINDEX =
            "quizactivity.QUIZINDEX";
    public static final String EXTRA_QUIZ =
            "quizactivity.QUIZ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Get the intent that launched this activity, and the message in
        // the intent extra.
        Intent intent = getIntent();
        QuizInfo quiz = (QuizInfo) intent.getSerializableExtra(EXTRA_QUIZ);
        final int quizIndex = intent.getIntExtra(EXTRA_QUIZINDEX, -1);
        assert quiz != null;

        ((TextView)findViewById(R.id.quiz_text)).setText(quiz.quizText);

        LinearLayout layout = findViewById(R.id.quiz_options);
        for (int i = 0; i < quiz.options.length; i++) {
            final String option = quiz.options[i];
            final boolean correct = (quiz.correct == i);
            Button optionButton = new Button(this);
            optionButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            optionButton.setText(option);
            layout.addView(optionButton);
            optionButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    Intent replyIntent = new Intent();
                    replyIntent.putExtra(EXTRA_CORRECT, correct);
                    replyIntent.putExtra(EXTRA_QUIZINDEX, quizIndex);
                    setResult(RESULT_OK, replyIntent);
                    finish();
                }
            });
        }
    }
}