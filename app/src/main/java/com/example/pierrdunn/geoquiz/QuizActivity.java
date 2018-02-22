package com.example.pierrdunn.geoquiz;

//Субкласс, наследующийся от класса Android Activity и обеспечиващий
//поддержку старых версий Android.
import android.app.Activity;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

//debug
import android.util.Log;

//Для работы с виджетами
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    //debug
    private static final String TAG = "QuizActivity";

    //Ключ для сохранения значения при изменении ориентации
    private static final String KEY_INDEX = "index";

    //Код запроса активности CheatActivity
    private static final int REQUEST_CODE_CHEAT = 0;

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mNextButton;
    private ImageButton mBackButton;
    private TextView mQuestionTextView;
    private Question [] mQuestionBank;
    private int mCurrentIndex;

    private boolean mIsCheater;

    private int truePercentage;

    private Toast toast;

    {
        mQuestionBank = new Question[]{
                new Question(R.string.question_australia, true),
                new Question(R.string.question_oceans, false),
                new Question(R.string.question_africa, true),
        };

        mCurrentIndex = 0;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate(Bundle) called");

        setContentView(R.layout.activity_quiz);

        //Проверка сохраненных данных
        if(savedInstanceState != null)
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);

        //Question Text View
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
         //Слушатель на сам TextView, который срабатывает при нажатии на него
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentIndex < mQuestionBank.length - 1) {
                    mTrueButton.setEnabled(true);
                    mFalseButton.setEnabled(true);

                    mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                    updateQuestion();
                }
                else
                    showTrueAnswersToPercentage();
            }
        });

        updateQuestion();

        //Buttons Next and Back
        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mBackButton = (ImageButton) findViewById(R.id.back_button);
        mCheatButton = (Button) findViewById(R.id.cheat_button);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrueButton.setEnabled(true);
                mFalseButton.setEnabled(true);
                if(mCurrentIndex < mQuestionBank.length - 1){
                    mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                    mIsCheater = false;
                    updateQuestion();
                }
                else
                    showTrueAnswersToPercentage();
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrueButton.setEnabled(true);
                mFalseButton.setEnabled(true);
                if(mCurrentIndex != 0) {
                    mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                    mIsCheater = false;
                    updateQuestion();
                }
            }
        });

        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                //startActivity(intent);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        //Возвращаемый объект View перед присваиванием следует преобразовать
        //в Button.
        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);
        //Создается анонимный внутренний класс
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFalseButton.setEnabled(false);
                checkAnswer(true);
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrueButton.setEnabled(false);
                checkAnswer(false);
            }
        });
    }

    //Получение результата об открытии activity_cheat
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK)
            return;
        if(requestCode == REQUEST_CODE_CHEAT){
            if(data == null)
                return;
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    //Переопределение метода для сохранения ключа между орентациями
    @Override
    protected void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        saveInstanceState.putInt(KEY_INDEX, mCurrentIndex);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void updateQuestion(){
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue){
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;

        if(mIsCheater){
            messageResId = R.string.judgment_toast;
        }
        else{
            if(userPressedTrue == answerIsTrue){
                messageResId = R.string.correct_toast;
                truePercentage++;
            }
            else{
                messageResId = R.string.incorrect_toast;
            }
        }

        toast = Toast.makeText(this, messageResId,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    private void showTrueAnswersToPercentage(){
        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);

        mNextButton.setEnabled(false);
        mBackButton.setEnabled(false);

        mQuestionTextView.setText("Percentage of correct answers: " +
                (Math.round(100 / mQuestionBank.length * truePercentage)) + "%");
    }
}
