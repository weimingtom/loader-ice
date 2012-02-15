package com.ebensz.games.ai.bean;

import com.ebensz.games.ai.engine.PokerTypeUnit;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.History;
import com.ebensz.games.model.hand.Hand;
import com.ebensz.games.model.poker.Poker;
import com.ebensz.games.model.poker.PokerUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: tosmart
 * Date: 2010-6-30
 * Time: 11:11:32
 */
public class AiHistory {

    public AiHistory() {
        produces = new ArrayList<Produce>();
    }

    public AiHistory(History history) {

        this();

        List<History.Event> events = history.getEvents();

        for (History.Event event : events) {

            Hand hand = event.getHand();

            if (hand == null)
                continue;

            Poker[] pokerArray = hand.getPokers();

            List<Poker> pokers = new ArrayList<Poker>(Arrays.asList(pokerArray));

            AiPokerList pokerList = new AiPokerList();

            for (Poker poker : pokers) {
                pokerList.add(PokerUtils.toChar(poker));
            }

            SplitType splitType = PokerTypeUnit.findSplitType(pokerList);
            HandType handType = splitType.handType;

            AiHand aiHand = new AiHand(pokerList, handType);

            Dir dir = event.isJiePai() ? event.getJiePaiDir() : event.getChuPaiDir();
            add(dir, aiHand);
        }
    }

    public void add(Dir dir, AiHand aiHand) {
        produces.add(new Produce(dir, aiHand));
    }

    public Produce lastProduce() {
        if (produces.size() == 0) return null;
        return produces.get(produces.size() - 1);
    }

    public AiHand lastHand(Dir dir) {

        for (int i = produces.size() - 1; i >= 0; i--) {
            Produce produce = produces.get(i);
            if (produce.dir == dir) return produce.aiHand;
        }

        return null;
    }

    public AiPokerList getPokerList() {

        AiPokerList all = new AiPokerList();

        for (Produce produce : produces) {
            all.add(produce.aiHand);
        }

        return all;
    }

    public AiPokerList getPokerList(Dir dir) {

        AiPokerList all = new AiPokerList();

        for (Produce produce : produces) {
            if (produce.dir == dir) all.add(produce.aiHand);
        }

        return all;
    }

    public static class Produce {

        public Produce(Dir dir, AiHand aiHand) {
            this.dir = dir;
            this.aiHand = aiHand;
        }

        public Dir dir;
        public AiHand aiHand;
    }

    private List<Produce> produces;
}
