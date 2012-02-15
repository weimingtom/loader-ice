package com.ebensz.games.logic.interactive;

/**
 * User: Mike.Hu
 * Date: 11-11-29
 * Time: 下午4:34
 */
public class Message implements Cloneable {

    public static final int NOTHING = 0;
    public static final int SELECT_ROB_LOADER = 1;
    public static final int SELECT_DAO = 2;
    public static final int SELECT_GEN = 3;
    public static final int SELECT_FAN = 4;
    public static final int SELECT_CHU_PAI = 5;
    public static final int SELECT_JIE_PAI = 6;
    public static final int SELECT_CONTINUE_GAME = 7;


    public int what;
    public Object obj;

    public Message() {
    }

    public Message(int what) {
        this.what = what;
    }

    public Message(int what, Object obj) {
        this.what = what;
        this.obj = obj;
    }

}
