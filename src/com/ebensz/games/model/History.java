package com.ebensz.games.model;

import com.ebensz.games.model.hand.Bomb;
import com.ebensz.games.model.hand.ColoredHand;
import com.ebensz.games.model.hand.Hand;
import com.ebensz.games.model.hand.Rocket;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Mike.Hu
 * Date: 11-12-15
 * Time: 上午11:46
 */
public class History {

    public static class Event {
        public enum Type {
            ChuPai, JiePai
        }

        public Event(Dir chuPaiDir, ColoredHand chuPai) {
            type = Type.ChuPai;
            this.chuPaiDir = chuPaiDir;
            hand = (chuPai == ColoredHand.BU_YAO)
                    ? null
                    : chuPai.getHand();
        }

        public Event(Dir jiePaiDir, ColoredHand jiePai, Dir chuPaiDir) {
            type = Type.JiePai;
            this.jiePaiDir = jiePaiDir;
            this.chuPaiDir = chuPaiDir;
            hand = (jiePai == ColoredHand.BU_YAO)
                    ? null
                    : jiePai.getHand();
        }

        public Hand getHand() {
            return hand;
        }

        public Dir getJiePaiDir() {
            return jiePaiDir;
        }

        public Dir getChuPaiDir() {
            return chuPaiDir;
        }

        public boolean isJiePai() {
            return type == Type.JiePai;
        }


        private Hand hand;
        private Dir jiePaiDir;
        private Dir chuPaiDir;
        private Type type;
    }

    public History() {
        events = new ArrayList<Event>();
    }

    public void reset() {
        events.clear();
    }

    public int size() {
        return events.size();
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public List<Event> getEvents() {
        return events;
    }

    public List<Event> filterBombEvent() {

        List<Event> bombEvents = new ArrayList<Event>();

        for (Event event : events) {
            Hand hand = event.getHand();
            if ((hand instanceof Bomb) || (hand instanceof Rocket)) {
                bombEvents.add(event);
            }
        }
        return bombEvents;
    }

    public List<Event> filterByDir(Dir dir) {
        ArrayList<Event> dirEvents = new ArrayList<Event>();

        for (Event event : events) {
            if (event.isJiePai()) {

                if (event.getJiePaiDir() == dir)
                    dirEvents.add(event);
            }
            else {

                if (event.getChuPaiDir() == dir)
                    dirEvents.add(event);
            }
        }


        return dirEvents;
    }

    private List<Event> events;
}
