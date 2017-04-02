package com.example.logandihel.abyss;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

/**
 * Created by logandihel on 3/30/17.
 */

public class ObstacleManager implements GameObject {

    private ArrayList<ObstaclePair> ops;
    private int sinceLastSwing = 0;
    private int swing = 0;
    private int spaceTrend = 0;    // - = left, + = right
    private int sinceLastSpace = 0;

    public ObstacleManager() {
        ops = new ArrayList<>();
        for(int i = -1; i < (Constants.SCREEN_HEIGHT/Constants.BLOCK_HEIGHT); i++)
            ops.add(0, new ObstaclePair(i * Constants.BLOCK_HEIGHT));  // highest index made first
    }

    public int playerCollide(RectPlayer player) {
        int collision = 0;
        for(int i = 0; i < ops.size(); i++) {
            collision |= ops.get(i).playerCollide(player);
        }
        return collision;
    }

    @Override
    public void update() {
        for(int i = 0; i < ops.size(); i++) {
            ops.get(i).update();
        }

        if(ops.size() > 0) {
            // if the lowest level crosses the bottom
            // and hasn't already been copied
            if (ops.get(0).bottomCross() && !ops.get(0).hadChildren) {
                // make some new obstacles based on the spaces of the highest ones
                ops.add(new ObstaclePair(ops.get(ops.size()-1)));
                // make sure that no more children are made for the bottom one
                ops.get(0).hadChildren = true;
            }

            // if it goes off the screen its game over for it
            if (ops.get(0).topCross()) {
                ops.remove(0);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        for(int i = 0; i < ops.size(); i++)
            ops.get(i).draw(canvas);
    }

    private class ObstaclePair implements GameObject {

        public ArrayList<Obstacle> obstacles;
        private ArrayList<Integer> spaces;
        public boolean hadChildren = false;

        public ObstaclePair(ObstaclePair old_op) {
            this.spaces = new ArrayList<>();
            this.obstacles = new ArrayList<>();

            // update swing every 100 frames
            sinceLastSwing++;
            if(sinceLastSwing % 100 == 0) {
                sinceLastSwing = 0;
                // flip flop starting out random
                if (swing == 0) swing = Constants.MAX_DELTA / 2 * (Math.random() > 0.5 ? -1 : 1);
                swing *= -1;
            }

            // (swing-MD, swing+MD)
            int offset = (int) (Math.random() * 2 * Constants.MAX_DELTA) - (Constants.MAX_DELTA - swing);


            // if too far to the right, shift left (50 is a buffer)
            if(old_op.obstacles.get(1).getRectangle().left+offset > Constants.SCREEN_WIDTH - 50) {
                offset = -1 * Math.abs(offset);
                sinceLastSwing = 0;
                swing = -Constants.MAX_DELTA / 2;
            }
            // if too far to the left, shift right (50 is a buffer)
            else if(old_op.obstacles.get(0).getRectangle().right+offset< 50) {
                offset = Math.abs(offset);
                sinceLastSwing = 0;
                swing = Constants.MAX_DELTA / 2;
            }

            // 1 pushes left and right towards center
            // -1 pushes left and right to outside
            // MAX = 600. MIN = 300 for space
            // space adjuster will work similar to sinceLastSwing
            // spa is (

            sinceLastSpace++;

            if(spaceTrend == 0) {
                spaceTrend = Constants.MAX_DELTA / 8;
            }
            else if(sinceLastSpace % 300 == 0) {
                spaceTrend *= -1;
                sinceLastSpace = 0;
            }
            //(-DELTA/2+st, DELTA/2+st)
            int space_adjuster = (int) (Math.random() * Constants.MAX_DELTA/2) - Constants.MAX_DELTA /4 + spaceTrend;


            int new_space = (old_op.obstacles.get(1).getRectangle().left-space_adjuster)
                    - (old_op.obstacles.get(0).getRectangle().right+space_adjuster);

            if(new_space > Constants.MAX_SPACE) {
                sinceLastSpace = 0;
                spaceTrend = Constants.MAX_DELTA / 8;   // get smaller(more spa)
                space_adjuster = Math.abs(space_adjuster);
            }
            else if(new_space < Constants.MIN_SPACE) {
                sinceLastSpace = 0;
                spaceTrend = -Constants.MAX_DELTA / 8;  // get bigger (less spa)
                space_adjuster = -Math.abs(space_adjuster);
            }

            // add the obstacles


            obstacles.add(new Obstacle(new Rect(
                    0,
                    old_op.obstacles.get(0).getRectangle().top-Constants.BLOCK_HEIGHT,
                    old_op.obstacles.get(0).getRectangle().right+offset+space_adjuster,
                    old_op.obstacles.get(0).getRectangle().bottom-Constants.BLOCK_HEIGHT),
                    Color.BLACK));

            obstacles.add(new Obstacle(new Rect(
                    old_op.obstacles.get(1).getRectangle().left+offset-space_adjuster,
                    old_op.obstacles.get(1).getRectangle().top-Constants.BLOCK_HEIGHT,
                    Constants.SCREEN_WIDTH,
                    old_op.obstacles.get(1).getRectangle().bottom-Constants.BLOCK_HEIGHT),
                    Color.BLACK));
        }

        public ObstaclePair(int height) {
            this.spaces = new ArrayList<>();
            spaces.add(Constants.MAX_SPACE);

            this.obstacles = new ArrayList<>();
            // left puppy
            this.obstacles.add(new Obstacle(new Rect(
                    0,           // left
                    height,      // top
                    (Constants.SCREEN_WIDTH-Constants.MAX_SPACE)/2,     // right
                    height+Constants.BLOCK_HEIGHT),     // down
                    Color.BLACK));
            // right puppy
            this.obstacles.add(new Obstacle(new Rect(
                    (Constants.SCREEN_WIDTH+Constants.MAX_SPACE)/2,
                    height,
                    Constants.SCREEN_WIDTH,
                    height+Constants.BLOCK_HEIGHT),
                    Color.BLACK));
        }

        public int playerCollide(RectPlayer player) {
            int collision = 0;
            for(int i = 0; i < obstacles.size(); i++)
                collision |= obstacles.get(i).playerCollide(player);
            return collision;
        }

        @Override
        public void update() {
            for(int i = 0; i < obstacles.size(); i++)
                obstacles.get(i).update();
        }

        @Override
        public void draw(Canvas canvas) {
            for(int i = 0; i < obstacles.size(); i++)
                obstacles.get(i).draw(canvas);
        }

        @Override
        public String toString() {
            return "NumObs: " + obstacles.size() + ", Top: " + obstacles.get(0).getRectangle().top + ", Bottom: " + obstacles.get(0).getRectangle().bottom;
        }

        public boolean topCross() {
            return obstacles.get(0).getRectangle().top >= Constants.SCREEN_HEIGHT;
        }

        public boolean bottomCross() {
            return obstacles.get(0).getRectangle().bottom >= Constants.SCREEN_HEIGHT;
        }

    }

}
