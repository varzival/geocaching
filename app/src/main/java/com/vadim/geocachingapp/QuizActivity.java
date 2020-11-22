package com.vadim.geocachingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;

public class QuizActivity extends AppCompatActivity {

    // Unique tag for the intent reply.
    public static final String EXTRA_CORRECT =
            "quizactivity.CORRECT";
    public static final String EXTRA_GEOPOINT =
            "quizactivity.GEOPOINT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Get the intent that launched this activity, and the message in
        // the intent extra.
        Intent intent = getIntent();
        QuizInfo quiz = (QuizInfo) intent.getSerializableExtra(CustomMarker.EXTRA_QUIZ);
        final GeoPoint geoPoint = (GeoPoint) intent.getSerializableExtra(CustomMarker.EXTRA_GEOPOINT);
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
                    replyIntent.putExtra(EXTRA_GEOPOINT, (Serializable) geoPoint);
                    setResult(RESULT_OK, replyIntent);
                    finish();
                }
            });
        }
    }
}