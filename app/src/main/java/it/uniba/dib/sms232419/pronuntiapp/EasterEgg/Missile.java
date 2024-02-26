package it.uniba.dib.sms232419.pronuntiapp.EasterEgg;

public class Missile {
    //Missile vars
    int mx = 0;
    int my = 0;
    int mr = 0;
    int mv = 10;
    int w = 0; //width
    int h = 0; //height
    boolean destroyME = false;

    Direction warpDr = Direction.NONE;

    public enum Direction {
        NORTH, SOUTH, EAST, WEST, NONE;
    }

    public void lightSpeed(){
        switch(warpDr){
            case SOUTH:
                my += mv;
                if (my + mr > h){warpDr = Direction.NONE; destroyME=true;}
                break;
            case NORTH:
                my -= mv;
                if (my < mr){warpDr = Direction.NONE; destroyME=true;}
                break;
            case EAST:
                mx += mv;
                if (mx + mr > w){warpDr = Direction.NONE; destroyME=true;}
                break;
            case WEST:
                mx -= mv;
                if (mx < mr){warpDr = Direction.NONE; destroyME=true;}
                break;
        }
    }
}