package com.example.wackah_mole;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class HighScore extends AppCompatActivity {


    private TextView top_first;
    private TextView top_second;
    private TextView top_third;
    private TextView top_fourth;
    private TextView top_fifth;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_high_score);
            // Init Start Button
            Button returnbtn = findViewById(R.id.return_btn);

            TextView final_score;
            top_first = findViewById(R.id.HS_1);
            top_second = findViewById(R.id.HS_2);
            top_third = findViewById(R.id.HS_3);
            top_fourth = findViewById(R.id.HS_4);
            top_fifth = findViewById(R.id.HS_5);

            final_score = findViewById(R.id.finScore);
            int Score = 0;

            Bundle extras = getIntent().getExtras();
            if(extras != null){
                Score = extras.getInt("Score");
            }

            final_score.setText("Your Score: " + Score);

            //set highscores
            highScoreLogic(Score);

            // Click Listener for Start Button
            returnbtn.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            });
        }


        /**
         * Checks saved highscores and adds latest score if it qualifes as well as displaying highscores
         * @param Score your final score for the round
         */
        private void highScoreLogic(int Score){
            //reading highscores from shared preferences and putting them into an array
            int[] Highscores = new int[6];
            SharedPreferences highScores_Manager = this.getSharedPreferences("Highscores", Context.MODE_PRIVATE);
            Highscores[0] =   highScores_Manager.getInt(getString(R.string.HighScore1Key),0);
            Highscores[1] =   highScores_Manager.getInt(getString(R.string.HighScore2Key),0);
            Highscores[2] =   highScores_Manager.getInt(getString(R.string.HighScore3Key),0);
            Highscores[3] =   highScores_Manager.getInt(getString(R.string.HighScore4Key),0);
            Highscores[4] =   highScores_Manager.getInt(getString(R.string.HighScore5Key),0);
            Highscores[5] =   Score;
            //sort the array so all of the highscores are in the correct order
            Arrays.sort(Highscores);

            SharedPreferences.Editor editor = highScores_Manager.edit();
            editor.putInt(getString(R.string.HighScore1Key), Highscores[5]);
            editor.putInt(getString(R.string.HighScore2Key), Highscores[4]);
            editor.putInt(getString(R.string.HighScore3Key), Highscores[3]);
            editor.putInt(getString(R.string.HighScore4Key), Highscores[2]);
            editor.putInt(getString(R.string.HighScore5Key), Highscores[1]);
            editor.apply();

            top_first.setText(Integer.toString(Highscores[5]));
            top_second.setText(Integer.toString(Highscores[4]));
            top_third.setText(Integer.toString(Highscores[3]));
            top_fourth.setText(Integer.toString(Highscores[2]));
            top_fifth.setText(Integer.toString(Highscores[1]));

        }

}
