package com.ebensz.games.ai.bean;

import com.ebensz.games.model.Dir;

/**
 * User: tosmart
 * Date: 2010-6-28
 * Time: 8:55:10
 */
public class Phase {

    public static final String ALL_POKERS = "zyxedcba9876543";
    public static final String LINK_POKERS = "edcba9876543";
    public static final String FULL_POKERS = "3333444455556666777788889999aaaabbbbccccddddeeeexxxxyz";

    public Phase(Dir loader, AiPokerList aceOfBase, AiHistory aiHistory) {

        this.loader = loader;
        this.aceOfBase = aceOfBase;
        this.aiHistory = aiHistory;
    }

    public void addHand(Dir dir, AiHand aiHand) {
        aiHistory.add(dir, aiHand);
    }

    public boolean isEnemy(Dir self, Dir lastChuPaiDir) {
        return loader == lastChuPaiDir || self == loader;
    }

    public Dir lastHandDir() {
        return aiHistory.lastProduce().dir;
    }

    public AiHand lastHand() {
        AiHistory.Produce last = aiHistory.lastProduce();

        return last == null ? null : last.aiHand;
    }

    public AiPokerList getAiHistory() {
        return aiHistory.getPokerList();
    }

    public AiPokerList getHistory(Dir dir) {
        return aiHistory.getPokerList(dir);
    }

    public AiPokerList getRemainderList() {
        AiPokerList all = new AiPokerList(FULL_POKERS.toCharArray());
        return all.remove(getAiHistory());
    }

    public int getRemainder(Dir dir) {
        int total = loader == dir ? 20 : 17;
        return total - getHistory(dir).size();
    }

    public int getEnemyRemainder(Dir self) {

        if (loader == self) {

            Dir myLeft = self.shangJia();
            Dir myRight = self.xiaJia();

            return 17 - (
                    Math.max(
                            getHistory(myLeft).size(),
                            getHistory(myRight).size()
                    )
            );
        }

        return 20 - getHistory(loader).size();
    }

    public Dir getLoader() {
        return loader;
    }

    public AiPokerList getAceOfBase() {
        return aceOfBase;
    }

    public boolean isTryPairPasa() {
        return tryPairPasa;
    }

    public void setTryPairPasa(boolean tryPairPasa) {
        this.tryPairPasa = tryPairPasa;
    }

    public boolean isTrySinglePasa() {
        return trySinglePasa;
    }

    public void setTrySinglePasa(boolean trySinglePasa) {
        this.trySinglePasa = trySinglePasa;
    }

    private Dir loader;
    private AiHistory aiHistory;
    private AiPokerList aceOfBase;

    private boolean trySinglePasa, tryPairPasa;
}
