package com.example.wackah_mole;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * <p>
 * Settings activity where a player can change name, set difficulty, erase highscore etc.
 * </p>
 *
 * @author John Brittain
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
}
