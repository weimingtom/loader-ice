package com.ebensz.games.ai.engine;

import com.ebensz.games.ai.bean.*;
import com.ebensz.games.ai.split.GeneralSplit;
import com.ebensz.games.model.Dir;

import java.util.List;
import java.util.Map.Entry;

/**
 * User: tosmart Date: 2010-6-30 Time: 11:09:22
 */
public abstract class Engine {

    private static EngineImpl engineImpl;

    public static Engine getDefault() {

        if (engineImpl == null) {
            engineImpl = new EngineImpl();
        }

        return engineImpl;
    }

    public abstract Solution split(AiPokerList pokers);

    public abstract int asLoader(AiPokerList pokers);

    public abstract AiHand deliver(AiPokerList pokers, Phase phase, Dir self);

    public abstract AiHand contact(AiPokerList pokers, Phase phase, Dir self);

    public abstract List<Entry<AiPokerList, Integer>> promptContact(AiPokerList pokers, AiHand lastAiHand);

    public abstract AiPokerList promptChosePoker(
            AiPokerList remainder, boolean isDeliver,
            AiPokerList selectedPokers, AiHand lastAiHand);

    public static boolean isMaximal(AiHand aiHand, Phase phase) {

        if (aiHand.getType() == HandType.Rocket) return true;

        AiPokerList remainderListAi = phase.getRemainderList();
        if (GeneralSplit.findRocket(remainderListAi) != null) return false;

        List<AiPokerList> bombs = GeneralSplit.findBomb(remainderListAi);
        if (aiHand.getType() != HandType.Bomb && bombs.size() > 0) return false;

        for (AiPokerList bomb : bombs) {
            if (bomb.getMaxChar() > aiHand.getSortChar()) return false;
        }

        List<AiPokerList> contactable = PokerTypeUnit.tryContact(remainderListAi, aiHand);

        return contactable.size() == 0;
    }

    public static boolean isBig(AiHand aiHand) {

        HandType type = aiHand.getType();

        switch (type) {

            case Single:
                return aiHand.getSortChar() > 'd';

            case Pair:
                return aiHand.getSortChar() > 'c';

            case SingleLink:
                return (aiHand.getSortChar() > 'c' || aiHand.size() > 6);

            case PairLink:
                return (aiHand.getSortChar() > 'b' || aiHand.size() > 6);
        }

        return true;
    }

    public static boolean isSmall(AiHand aiHand) {
        return aiHand.getSortChar() < '9';
    }

    public static boolean isBiggestInCurrPhase(Phase phase, AiHand aiHand) {

        HandType handType = aiHand.getType();

        if (handType == HandType.Bomb
                || handType == HandType.Rocket
                || handType == HandType.Type_x4_2
                || handType == HandType.Type_x4_2x2
                || handType == HandType.Type_x3
                || handType == HandType.Type_x3_1
                || handType == HandType.Type_x3_2) {

            return true;
        }

        if ((handType == HandType.PairLink && (aiHand.getMaxChar() >= 'e'))
                || aiHand.size() >= 8) {

            return true;
        }

        if ((handType == HandType.SingleLink && (aiHand.getMaxChar() == 'e'))
                || aiHand.size() >= 9) {

            return true;
        }

        if ((handType == HandType.ThreeLink
                || handType == HandType.ThreeLink_1xn
                || handType == HandType.ThreeLink_2xn)
                && (aiHand.getSortChar() >= 'd')) {

            return true;
        }

        AiPokerList history = phase.getAiHistory();
        if (handType == HandType.Pair) {

            //对2归为最大牌
            if (aiHand.contains('x')) return true;

            if (aiHand.contains('e')
                    && history.getCounter().get('x') >= 3) {
                //2已经出了3张  对A归为最大牌
                return true;
            }
        }

        if (handType == HandType.Single) {

            if (aiHand.contains('z')) return true;

            if (aiHand.contains('y')
                    && history.getCounter().get('z') == 1) {

                return true;
            }

            if (aiHand.contains('x')
                    && history.getCounter().get('y') == 1
                    && history.getCounter().get('z') == 1) {

                return true;
            }
        }

        return false;
    }
}
