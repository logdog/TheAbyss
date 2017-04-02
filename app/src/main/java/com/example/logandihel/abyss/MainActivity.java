package com.example.logandihel.abyss;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button button;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_WIDTH = dm.widthPixels;
        Constants.SCREEN_HEIGHT = dm.heightPixels;

        Constants.BLOCK_HEIGHT = Constants.SCREEN_HEIGHT/10;
        Constants.PLAYER_SIZE = Constants.SCREEN_WIDTH/10;
        Constants.MIN_SPACE = Constants.PLAYER_SIZE * 3;
        Constants.MAX_SPACE = Constants.PLAYER_SIZE * 6;

        button = (Button) findViewById(R.id.startButton);
        button.setOnClickListener(this);

        textView = (TextView) findViewById(R.id.high_score);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPref = getSharedPreferences(Constants.HIGH_SCORE_FILE, 0);
        int highScore = sharedPref.getInt("highScore", 0); // default is 0

        textView.setText("High score: " + highScore);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), GameActivity.class);
        startActivity(intent);
    }
}
