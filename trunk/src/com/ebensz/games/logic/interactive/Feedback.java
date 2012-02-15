package com.ebensz.games.logic.interactive;

public interface Feedback {

    public interface Handler {
        void onFeedback(Feedback feedback, Message msg);
    }

    void registerHandler(Handler handler);

    void onHandled(Message msg);

    void onMessage(Message msg);
}
