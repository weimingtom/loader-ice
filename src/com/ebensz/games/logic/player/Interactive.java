package com.ebensz.games.logic.player;

import android.util.Log;
import com.ebensz.games.Rule.Rule;
import com.ebensz.games.exception.ChuPaiErrorException;
import com.ebensz.games.logic.interactive.Feedback;
import com.ebensz.games.logic.interactive.Message;
import com.ebensz.games.model.*;
import com.ebensz.games.model.hand.ColoredHand;
import com.ebensz.games.model.poker.PokersInfo;

import java.util.Map;

import static com.ebensz.games.logic.interactive.Message.*;

public class Interactive extends DecisionMaker {
    private static final String TAG = Interactive.class.getSimpleName();
    private static final short[] D_G_F_LOCK = new short[1];

    public Interactive(Feedback feedback, Rule rule) {
        super(rule, feedback);
    }

    @Override
    public void onFeedback(Feedback feedback, Message msg) {
        if (released)
            return;

        switch (msg.what) {

            case SELECT_DAO:
            case SELECT_GEN:
            case SELECT_FAN:
                d_g_fResponseMsg = msg;
                synchronized (D_G_F_LOCK) {
                    D_G_F_LOCK.notify();
                }
                break;

            default:
                synchronized (this) {
                    responseMsg = msg;
                    notify();
                }
        }


    }

    @Override
    protected RobLoaderScore requestRobLoader(Dir self, RobLoaderScore lastHighestScore, PokersInfo pokersInfo) {
        if (released)
            return null;

        responseMsg = null;
        Message msg = new Message(SELECT_ROB_LOADER);
        msg.obj = lastHighestScore;
        feedback.onMessage(msg);

        waitReply();

        if (responseMsg != null && responseMsg.what == SELECT_ROB_LOADER && responseMsg.obj != null) {
            feedback.onHandled(responseMsg);
            return (RobLoaderScore) responseMsg.obj;
        }

        return null;
    }

    @Override
    public boolean dao(Dir loaderDir, Dir self, PokersInfo pokersInfo) {
        if (released)
            return false;

        d_g_fResponseMsg = null;
        feedback.onMessage(new Message(SELECT_DAO));

        waitReplyD_G_F();

        if (d_g_fResponseMsg != null && d_g_fResponseMsg.what == SELECT_DAO && d_g_fResponseMsg.obj != null) {
            feedback.onHandled(d_g_fResponseMsg);
            return (Boolean) d_g_fResponseMsg.obj;
        }

        return false;
    }

    @Override
    public boolean gen(Dir loaderDir, Dir self, PokersInfo pokersInfo) {
        if (released)
            return false;

        d_g_fResponseMsg = null;
        feedback.onMessage(new Message(SELECT_GEN));

        waitReplyD_G_F();

        if (d_g_fResponseMsg != null && d_g_fResponseMsg.what == SELECT_GEN && d_g_fResponseMsg.obj != null) {
            feedback.onHandled(d_g_fResponseMsg);
            return (Boolean) d_g_fResponseMsg.obj;
        }

        return false;
    }

    @Override
    public boolean fan(Map<Dir, DaoGenFan> daoGenFanMap, Dir self, PokersInfo pokersInfo) {
        if (released)
            return false;

        d_g_fResponseMsg = null;
        feedback.onMessage(new Message(SELECT_FAN));

        waitReplyD_G_F();

        if (d_g_fResponseMsg != null && d_g_fResponseMsg.what == SELECT_FAN && d_g_fResponseMsg.obj != null) {
            feedback.onHandled(d_g_fResponseMsg);
            return (Boolean) d_g_fResponseMsg.obj;
        }

        return false;
    }

    @Override
    ColoredHand chuPai(PokersInfo pokersInfo, Dir self, Dir loaderDir) {

        ColoredHand chuPai = requestChuPai(pokersInfo, self, loaderDir);

        System.out.println("chu pai" + self + ", " + chuPai);

        if (released || chuPai == null)
            return null;

        if (!rule.validateChuPai(chuPai.getHand()))
            throw new ChuPaiErrorException(chuPai.toString());

        return chuPai;
    }

    @Override
    protected ColoredHand requestChuPai(PokersInfo pokersInfo, Dir self, Dir loaderDir) {
        if (released)
            return null;

        responseMsg = null;
        ChuPaiEvent chuPaiEvent = new ChuPaiEvent(self);
        feedback.onMessage(new Message(SELECT_CHU_PAI, chuPaiEvent));

        waitReply();

        if (responseMsg != null && responseMsg.what == SELECT_CHU_PAI && responseMsg.obj != null) {
            feedback.onHandled(responseMsg);
            return chuPaiEvent.chuPai;
        }

        return null;
    }

    @Override
    protected ColoredHand requestJiePai(Dir chuPaiDir, ColoredHand chuPai, PokersInfo pokersInfo, Dir self, Dir loaderDir) {
        if (released)
            return null;

        responseMsg = null;

        JiePaiEvent jiePaiEvent = new JiePaiEvent(chuPaiDir, chuPai, self);
        Message msg = new Message(SELECT_JIE_PAI, jiePaiEvent);
        feedback.onMessage(msg);

        waitReply();

        if (responseMsg != null && responseMsg.what == Message.SELECT_JIE_PAI) {
            feedback.onHandled(responseMsg);

            if (responseMsg.obj == null)
                return null;

            return jiePaiEvent.jiePai;
        }

        return null;
    }

    /**
     * 释放所有的锁，拒绝接受新的请求
     */
    public void release() {

        if (!released) {
            released = true;

            synchronized (D_G_F_LOCK) {
                D_G_F_LOCK.notify();
            }

            synchronized (this) {
                notify();
            }
        }

    }

    /**
     * 询问是否继续游戏.
     */
    public void askForContinueGame() {
        if (released)
            return;

        responseMsg = null;

        Message msg = new Message(SELECT_CONTINUE_GAME);

        feedback.onMessage(msg);

        waitReply();

        feedback.onHandled(msg);
    }

    private void waitReply() {
        waitReply(0);
    }

    private void waitReplyD_G_F() {
        synchronized (D_G_F_LOCK) {
            try {
                D_G_F_LOCK.wait();
            }
            catch (InterruptedException e) {
                Log.w(TAG, "waitReplyD_G_F Interrupted !");
            }
        }

    }

    private synchronized void waitReply(long time) {

        try {
            wait(time);
        }
        catch (InterruptedException ignored) {
            Log.w(TAG, "Interrupted !");
        }
    }

    private boolean released;
    private Message responseMsg;
    private Message d_g_fResponseMsg;


}
