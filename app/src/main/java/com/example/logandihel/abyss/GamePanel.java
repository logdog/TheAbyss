package com.example.logandihel.abyss;

/**
 * Created by logandihel on 3/30/17.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private static final int PLAYER_SIZE = 100;
    private MainThread thread;

    private RectPlayer player;
    private Point playerPoint;
    private ObstacleManager om;

    private GameActivity gameActivity;

    private boolean RUNNING;
    private boolean showGameOver;


    public GamePanel(Context context) {
        this(context, null, 0);
    }

    public GamePanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GamePanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getHolder().addCallback(this);
    }

    public void startGame(GameActivity gameActivity) {
        this.gameActivity = gameActivity;

        thread = new MainThread(getHolder(), this);
        thread.setRunning(true);
        RUNNING = true;
        thread.start();

        Constants.CURRENT_Y = 0;
        Constants.SCORE = 0;
        Constants.ADDER = 15;
        showGameOver = false;
        setFocusable(true);

        player = new RectPlayer(new Rect(0,0,PLAYER_SIZE, PLAYER_SIZE), Color.YELLOW);
        playerPoint = new Point(Constants.SCREEN_WIDTH/2-PLAYER_SIZE/2,Constants.SCREEN_HEIGHT-7*PLAYER_SIZE);

        om = new ObstacleManager();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //super.onTouchEvent(event);
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                int x = (int)event.getX();
                playerPoint.set(x, playerPoint.y);
                break;
        }
        return true;
    }

    public void update() {
        if(RUNNING) {
            Constants.CURRENT_Y++;
            if(!showGameOver)
                Constants.SCORE = Constants.CURRENT_Y;
            if (Constants.CURRENT_Y % 100 == 0) {
                Constants.ADDER++;
            }

            player.update(playerPoint);
            om.update();

            int collide = om.playerCollide(player);

            // were testing the top
            if ((collide & Constants.TOP_COLLISION) == Constants.TOP_COLLISION) {
                // add the score to it
                playerPoint.set(playerPoint.x, playerPoint.y + Constants.ADDER);
            }

            if (player.getRectangle().bottom > Constants.SCREEN_HEIGHT) {
                showGameOver = true;
            }

            if(showGameOver) {
                if(Constants.CURRENT_Y - Constants.SCORE > 30 * 3)  // 3 seconds
                    RUNNING = false;
            }
        } else {
            try {
                thread.setRunning(false);
                //thread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }

            gameActivity.gameOver();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.LTGRAY);
        player.draw(canvas);
        om.draw(canvas);

        Paint paint = new Paint();
        if(Constants.SCORE > Constants.HIGHSCORE)
            paint.setColor(Color.GREEN);
        else
            paint.setColor(Color.BLUE);

        paint.setTextSize(100);

        canvas.drawText("" + Constants.SCORE, 10, 100, paint);
        if(showGameOver) {
            paint.setColor(Color.RED);
            canvas.drawText("Game Over", Constants.SCREEN_WIDTH/2 - 200, Constants.SCREEN_HEIGHT/2, paint);
        }
    }
}
