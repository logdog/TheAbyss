package com.example.logandihel.abyss;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {

    private GamePanel gp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // first creation
        // do initialization
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_game);

        gp = (GamePanel) findViewById(R.id.gamePanel);
    }

    @Override
    protected void onResume() {
        Constants.HIGHSCORE = getHighScore();
        // start the game!
        super.onResume();
        gp.startGame(this);
    }

    public void gameOver() {
        // save the game, then make a new intent to
        // switch the game to the main activity


        // maybe add some cool fade effect or switch
        // to another activity that displays the user's
        // score or something of the sort
        int highScore = Constants.HIGHSCORE;
        int score = Constants.SCORE;

        if(score > highScore) {
            setHighScore(score);
        }
        // display the home screen
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private int getHighScore() {
        SharedPreferences sharedPref = getSharedPreferences(Constants.HIGH_SCORE_FILE, 0);
        int highScore = sharedPref.getInt("highScore", 0); // default is 0
        return highScore;
    }

    public void setHighScore(int highScore) {
        SharedPreferences sharedPref = getSharedPreferences(Constants.HIGH_SCORE_FILE, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("highScore", highScore);
        editor.commit();
    }
}
