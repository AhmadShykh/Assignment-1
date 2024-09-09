package com.example.quizapp;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView questionText,obtainedMarksText,timeText,correctAnsText,totalMarksText,percentText;
    RadioGroup optionsGroup;
    Button correctOptBtn;
    RadioButton[] options = new RadioButton[4];
    LinearLayout correctAnsLayout;

    int totalMarks = 0;
    int currentIndex = 0;
    ArrayList<MCQ> mcqs = new ArrayList<MCQ>();
    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Define the questions and options
        String[] questions = getResources().getStringArray(R.array.mcq_questions);
        String[][] optionsArray = {
                getResources().getStringArray(R.array.mcq_options_1),
                getResources().getStringArray(R.array.mcq_options_2),
                getResources().getStringArray(R.array.mcq_options_3),
                getResources().getStringArray(R.array.mcq_options_4),
                getResources().getStringArray(R.array.mcq_options_5),
                getResources().getStringArray(R.array.mcq_options_6),
                getResources().getStringArray(R.array.mcq_options_7),
                getResources().getStringArray(R.array.mcq_options_8),
                getResources().getStringArray(R.array.mcq_options_9),
                getResources().getStringArray(R.array.mcq_options_10),
                getResources().getStringArray(R.array.mcq_options_11),
                getResources().getStringArray(R.array.mcq_options_12),
                getResources().getStringArray(R.array.mcq_options_13),
                getResources().getStringArray(R.array.mcq_options_14),
                getResources().getStringArray(R.array.mcq_options_15),
                getResources().getStringArray(R.array.mcq_options_16),
                getResources().getStringArray(R.array.mcq_options_17),
                getResources().getStringArray(R.array.mcq_options_18),
                getResources().getStringArray(R.array.mcq_options_19),
                getResources().getStringArray(R.array.mcq_options_20)
        };
        // Define the correct answers (index-based)
        int[] correctAnswers = {0, 2, 3, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 3, 0};

        // Populate the ArrayList with MCQ objects
        for (int i = 0; i < questions.length; i++) {

            MCQ mcq = new MCQ(questions[i], optionsArray[i], correctAnswers[i]);
            mcqs.add(mcq);
        }


        Button nextBtn = findViewById(R.id.next_btn);
        Button prevBtn = findViewById(R.id.previous_btn);
        Button retryBtn = findViewById(R.id.retry_btn);
        correctOptBtn = findViewById(R.id.correct_opt_btn);

        correctAnsLayout = findViewById(R.id.correct_ans_layout);

        questionText = findViewById(R.id.question_text);
        obtainedMarksText = findViewById(R.id.obtained_marks_text);
        correctAnsText = findViewById(R.id.correct_ans_text);
        timeText = findViewById(R.id.time_text);
        totalMarksText = findViewById(R.id.total_marks_text);
        percentText = findViewById(R.id.percent_text);

        optionsGroup = findViewById(R.id.options_group);

        for(int i = 0 ; i < options.length ; i++){
            options[i] = (RadioButton) optionsGroup.getChildAt(i);
        }

        obtainedMarksText.setText("Obtained Marks: 0");


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentIndex < getResources().getInteger(R.integer.QUESNUMS)-1){
                    if(currentIndex == 0)
                        prevBtn.setEnabled(true);

                    CheckAnswer();

                    currentIndex++;

                    SetQuestion(mcqs.get(currentIndex));


                    if(currentIndex == getResources().getInteger(R.integer.QUESNUMS)-1)
                        nextBtn.setText("Submit");
                }
                else{
                    CheckAnswer();
                    SubmitQuiz();
                }

            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentIndex > 0){
                    if(currentIndex == getResources().getInteger(R.integer.QUESNUMS)-1)
                        nextBtn.setText("Next");

                    currentIndex--;

                    SetQuestion(mcqs.get(currentIndex));

                    if(currentIndex == 0)
                        prevBtn.setEnabled(false);
                }

            }
        });

        correctOptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalMarks--;
                obtainedMarksText.setText("Obtained Marks: "+totalMarks);
                correctAnsText.setVisibility(View.VISIBLE);
                correctOptBtn.setEnabled(false);
                mcqs.get(currentIndex).correctAnsBtnSelection = true;
            }
        });

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SetQuestion(mcqs.get(0));
                totalMarks = 0;
                currentIndex = 0;
                timer.cancel();
                nextBtn.setText("Next");
                obtainedMarksText.setText("Obtained Marks: 0");
                prevBtn.setEnabled(false);
                StartTimer();

                findViewById(R.id.main_content).setVisibility(View.VISIBLE);
                findViewById(R.id.ending_content).setVisibility(View.INVISIBLE);

            }
        });


        SetQuestion(mcqs.get(0));
        StartTimer();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    public void StartTimer() {
        timer = new CountDownTimer(120000, 1000) {
            public void onTick(long timeRemaining) {
                int min = (int) (timeRemaining / 1000) / 60;
                int sec = (int) (timeRemaining / 1000) % 60;


                timeText.setText(String.format("%2d:%2d", min, sec));
            }

            public void onFinish() {
                SubmitQuiz();
            }
        }.start();
    }

    private void SubmitQuiz() {

        ResetQuestions();

        findViewById(R.id.main_content).setVisibility(View.INVISIBLE);
        findViewById(R.id.ending_content).setVisibility(View.VISIBLE);

        int quizMarks = getResources().getInteger(R.integer.QUESNUMS) * 5;
        double totalMarksPercent = (totalMarks / (double) quizMarks) * 100.0;

        Log.d("debug", "" + quizMarks + " " + totalMarksPercent);

        totalMarksText.setText("Total Marks: " + totalMarks);
        percentText.setText(String.format("Percentage: %.2f%%", totalMarksPercent));


    }

    private void ResetQuestions() {
        for (MCQ mcq : mcqs) {
            mcq.correctAnsBtnSelection = false;
            mcq.optAnswered = -1;
        }
    }


    private void CheckAnswer() {
        if (mcqs.get(currentIndex).optAnswered == -1) {
            int checkedRadioBtnId = optionsGroup.getCheckedRadioButtonId();
            if (checkedRadioBtnId != -1) {
                correctAnsLayout.setVisibility(View.VISIBLE);
                if (checkedRadioBtnId - 1 == mcqs.get(currentIndex).correctAns) {
                    totalMarks += 5;
                } else {
                    totalMarks -= 1;
                }
                obtainedMarksText.setText("Obtained Marks: " + totalMarks);
                mcqs.get(currentIndex).optAnswered = checkedRadioBtnId;
            }
        }
    }

    private void SetQuestion(MCQ question) {
        questionText.setText(question.question);

        for (int i = 0; i < 4; i++) {
            options[i].setText(question.options[i]);
        }
        if (question.optAnswered != -1) {
            optionsGroup.check(question.optAnswered);
            SetRadioButtonsEnable(false);
            correctAnsLayout.setVisibility(View.VISIBLE);
            correctAnsText.setText("Correct Option: " + question.correctAns);
            if (question.correctAnsBtnSelection) {
                correctAnsText.setVisibility(View.VISIBLE);
                correctOptBtn.setEnabled(false);
            } else {
                correctAnsText.setVisibility(View.INVISIBLE);
                correctOptBtn.setEnabled(true);
            }

        } else {
            optionsGroup.clearCheck();
            SetRadioButtonsEnable(true);
            correctAnsLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void SetRadioButtonsEnable(boolean val) {
        for (RadioButton btn : options) {
            btn.setEnabled(val);
        }
    }
}