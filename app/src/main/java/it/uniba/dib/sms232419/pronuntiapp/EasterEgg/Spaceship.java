package it.uniba.dib.sms232419.pronuntiapp.EasterEgg;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import java.util.ArrayList;
import java.util.Random;

import it.uniba.dib.sms232419.pronuntiapp.R;


public class Spaceship extends View{
    //------DON'T EDIT THESE VARIABLES---------
    ArrayList<SpaceJunk> jl = new ArrayList<SpaceJunk>(); // Array for space junk objects
    ArrayList<Missile> ml = new ArrayList<Missile>();     // Array for missile objects
    ArrayList<Missile> dm = new ArrayList<Missile>();     // Array for missile objects to be deleted
    long timePassed = SystemClock.uptimeMillis();         // Time passed based on uptime used for junk decay
    boolean doitonetimeyall = true;                       // Flag variable used to set initial coordinates of the spaceship
    static public boolean dahFlag = false;   // Flag to fire missiles
    static boolean boomPlay = false;         // Used to play sound from main activity
    static public int cx = 0;                // X coordinate of equilateral spaceship.
    static public int cy = 0;                // Y coordinate of equilateral spaceship.
    int junkSelector = 0;                    // Iterate though junk in array and remove if decayed
    Paint paint = new Paint();
    Point pt = new Point();
    Paint p = new Paint();
    static int score;



    // ------EDIT THESE VARIABLES---------------
    int maxDecayRate = 6000, minDecayRate = 4000; // The random decay rate ranges for space junk
    static public Direction dr = Direction.NORTH; // Spaceship starting direction
    boolean spaceshipShadow = true; // Spaceship shadow (yes I know it space)
    int shadowColor = Color.BLACK;   // Spaceship shadow color
    int missileColor = Color.BLACK; // Color of missiles
    boolean dualFire = false;       // Dual fire mode
    static public int vl = 20;      // Velocity of spaceship
    int junkDecayRate = 5000;       // Time for space junk to move Coordinates
    int maxJunk = 3;                // max amount of space junk
    int r = 100;                    // Size of spaceship


    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);


        //Get the width and height of screen used in many places
        int w = getWidth();
        int h = getHeight();

        //One time run setup positioning the ship at startup
        //Places spaceship above up button
        if (doitonetimeyall){
            cx = (w/2); //Find middle of screen
            cy = h-100;     //Set 100px? off bottom of canvas
            doitonetimeyall = false; //Set flag
        }

        //Spaceship shadow lol, in space where there is no surface to cast a shadow on
        //but this is a game, and games are not always realistic so deal with it or shut it off
        if (spaceshipShadow){
            pt.set(cx+3,cy+4);
            p.setStyle(Paint.Style.FILL);
            p.setColor(shadowColor);
            Path shadow = tri(pt, r);
            c.drawPath(shadow, p);
        }

        // Draw the Spaceship!
        pt.set(cx,cy);
        p.setStyle(Paint.Style.FILL);
        p.setColor(getResources().getColor(R.color.thirdDeserto));
        Path path = tri(pt, r);
        c.drawPath(path, p);

        /*
        //Draw circle the radius of the triangle spaceship (Possible shield later on?)
        paint.setColor(Color.GREEN);
        float tr = (float)(r/Math.sqrt(3.0));
        c.drawCircle(cx,cy, tr, paint);
        */

        //Space Junk Machine
        while (maxJunk !=0){
            jl.add(new SpaceJunk());
            maxJunk--;
        }

        //Space Junk Decay Timer
        if (SystemClock.uptimeMillis() - timePassed > junkDecayRate){
            //Remove Space Junk and Re-Add on Object at a time
            SpaceJunk j = jl.get(junkSelector);
            jl.remove(j);            //Die
            jl.add(new SpaceJunk()); //Reborn!
            if (junkSelector >= jl.size()-1){junkSelector = 0;}else {junkSelector++;}
            timePassed = SystemClock.uptimeMillis(); //Get current time
            junkDecayRate = new Random().nextInt(maxDecayRate - minDecayRate) + minDecayRate;
        }

        //Draw the space junk!
        for (SpaceJunk j: jl){
            paint.setColor(j.color);                //Color set by object instance
            c.drawCircle(j.cx, j.cy, j.r, paint);   //Coordinates set by object instance
        }


        // Creates new missile, sets the missile initial coordinates and adds to list of missiles
        fireInTheHole();

        // Remove missiles that are destroyed
        ml.removeAll(dm);

        // FIRE / Draw missiles!
        for (Missile m: ml) {
            m.w = w; //Set width for instance of missile
            m.h = h; //Set height for instance of missile

            // Check for collisions with nasty space junk!
            for (SpaceJunk j: jl){
                // Checks if the coordinates of center of the missile are within the area of the space junk
                if (inCircle(m.mx,m.my,j.cx,j.cy,j.r)){
                    //RIGHT ON TARGET GOT EM!
                    m.destroyME = true;                 //Destroy missile flag
                    jl.remove(j);                       //Remove pesky space junk
                    maxJunk++;                          //Increase amount of allowed space junk
                    boomPlay = true;                    //play wonderful 8 bit explosion sound
                    score += j.scoreMply;               //Increase Score
                    break;
                }
            }


            paint.setColor(missileColor);           //Set color of missile
            c.drawCircle(m.mx, m.my, m.mr, paint);  //Draw the instance of the missile
            if (dualFire && (dr == Direction.NORTH | dr == Direction.SOUTH)){c.drawCircle(m.mx+r, m.my, m.mr, paint);}
            if (dualFire && (dr == Direction.EAST | dr == Direction.WEST)){c.drawCircle(m.mx, m.my+r, m.mr, paint);}

            // Call the missile's instance of lightSpeed()
            // This function moves the missile in the direction it was facing until it hits a wall
            m.lightSpeed();

            //Add spent missiles to the land fill
            if (m.destroyME){
                dm.add(m);
            }
        }

        // Check for collision with my beloved ship and nasty space junk
        // Checks if the radius of a circle around the triangle ship
        // touches the center coordinates of the space junk
        for (SpaceJunk j: jl){
            switch (dr){
                case NORTH:
                    if (inCircle(j.cx,j.cy,cx,cy,r)){
                        Log.d("INFO","YOU DEAD!");
                        nooooooooo();
                    }
                    break;
                case SOUTH:
                    if (inCircle(j.cx,j.cy,cx,cy,r)){
                        Log.d("INFO","YOU DEAD!");
                        nooooooooo();
                    }
                    break;
                case EAST:
                    if (inCircle(j.cx,j.cy,cx,cy,r)){
                        Log.d("INFO","YOU DEAD!");
                        nooooooooo();
                    }
                    break;
                case WEST:
                    if (inCircle(j.cx,j.cy,cx,cy,r)){
                        Log.d("INFO","YOU DEAD!");
                        nooooooooo();
                    }
                    break;
            }
        }


        // Wall detection
        // Allows ship to pass to opposite side "through" the wall
        if (cx + r > w) {       //right wall
            cx -= w - vl;
        }
        if (cx + r < r) {       // left wall
            cx += w - vl;
        }
        if (cy < r) {           // top wall
            cy += h - vl;
        }
        if (cy + r > h + r) {   // bottom wall
            cy -= h - vl;
        }
    }

    // Resets the ships position, score and plays explosion sound when you die
    void nooooooooo(){
        dr = Direction.NORTH;
        boomPlay = true;
        score=0;
        doitonetimeyall = true;
    }

    // Find if given coordinates are within the area of a circle
    private boolean inCircle(float x, float y, float circleCenterX, float circleCenterY, float circleRadius) {
        double dx = Math.pow(x - circleCenterX, 2);
        double dy = Math.pow(y - circleCenterY, 2);
        if ((dx + dy) < Math.pow(circleRadius, 2)) {
            return true;
        } else {
            return false;
        }
    }

    // Draws path of an equilateral triangle(Spaceship) at provided point to size of the provided width
    public Path tri (Point p1, int width){
        Point p2 = null, p3 = null;

        if (dr == Direction.NORTH) {
            p1.x = p1.x-width/2;
            p1.y = p1.y+width/2-13;
            p2 = new Point(p1.x + width, p1.y);
            p3 = new Point(p1.x + (width / 2), p1.y - width);
        }
        else if (dr == Direction.SOUTH) {
            p1.x = p1.x-width/2;
            p1.y = p1.y-width/2+13;
            p2 = new Point(p1.x + width,p1.y);
            p3 = new Point(p1.x + (width / 2), p1.y + width);
        }
        else if (dr == Direction.WEST) {
            p1.x = p1.x+width/2-13;
            p1.y = p1.y-width/2;
            p2 = new Point(p1.x, p1.y + width);
            p3 = new Point(p1.x - width, p1.y + (width / 2));
        }
        else if (dr == Direction.EAST) {
            p1.x = p1.x-width/2+13;
            p1.y = p1.y-width/2;
            p2 = new Point(p1.x, p1.y + width);
            p3 = new Point(p1.x + width, p1.y + (width / 2));
        }
        Path path = new Path();
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);

        return path;
    }

    // Creates new missile instances and initial starting coordinates
    public void fireInTheHole(){
        if (dahFlag) {
            int oldR = r;
            if (dualFire){r=0;}
            Missile m = new Missile();
            switch (dr) {
                case EAST:
                    m.my = cy;
                    m.mx = cx+r/2+13;
                    m.mr = 5;
                    m.warpDr = Missile.Direction.EAST;
                    dahFlag = false;
                    break;
                case WEST:
                    m.my = cy;
                    m.mx = cx-r/2-13;
                    m.mr = 5;
                    m.warpDr = Missile.Direction.WEST;
                    dahFlag = false;
                    break;
                case NORTH:
                    m.mx = cx;
                    m.my = cy-r/2-13;
                    m.mr = 5;
                    m.warpDr = Missile.Direction.NORTH;
                    dahFlag = false;
                    break;
                case SOUTH:
                    m.mx = cx;
                    m.my = cy+r/2+13;
                    m.mr = 5;
                    m.warpDr = Missile.Direction.SOUTH;
                    dahFlag = false;
                    break;
            }
            ml.add(m);
            r=oldR;
        }
    }

    public enum Direction {
        NORTH, SOUTH, EAST, WEST, NONE;
    }

    public Spaceship(Context context) {
        super(context);
    }

    public Spaceship(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



}