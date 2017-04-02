package com.example.logandihel.abyss;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by logandihel on 3/30/17.
 */

public class Obstacle implements GameObject {

    private Rect rectangle;
    private int color;

    public Obstacle(Rect rectangle, int color) {
        this.rectangle = rectangle;
        this.color = color;
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(rectangle, paint);
    }

    @Override
    public void update() {
        // make rectangle move down the screen
        int adder = Constants.ADDER;
        rectangle.top += adder;
        rectangle.bottom += adder;
    }

    public Rect getRectangle() {
        return rectangle;
    }


    public int playerCollide(RectPlayer player) {
        int collision = 0;

        if(rectangle.contains(player.getRectangle().left, player.getRectangle().top)
                || rectangle.contains(player.getRectangle().left, player.getRectangle().bottom))
            collision |= Constants.LEFT_COLLISION;

        if(rectangle.contains(player.getRectangle().left, player.getRectangle().top)
                || rectangle.contains(player.getRectangle().right, player.getRectangle().top))
            collision |= Constants.TOP_COLLISION;

        if(rectangle.contains(player.getRectangle().right, player.getRectangle().top)
                || rectangle.contains(player.getRectangle().right, player.getRectangle().bottom))
            collision |= Constants.RIGHT_COLLISION;

        if(rectangle.contains(player.getRectangle().left, player.getRectangle().bottom)
                || rectangle.contains(player.getRectangle().right, player.getRectangle().bottom))
            collision |= Constants.BOTTOM_COLLISION;

        return collision;
    }

}
