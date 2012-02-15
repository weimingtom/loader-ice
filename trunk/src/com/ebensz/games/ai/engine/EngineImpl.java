package com.ebensz.games.ai.engine;

import com.ebensz.games.ai.bean.*;
import com.ebensz.games.ai.split.GeneralSplit;
import com.ebensz.games.ai.split.SecondPhaseSplit;
import com.ebensz.games.ai.split.ThirdPhaseSplit;
import com.ebensz.games.model.Dir;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: tosmart Date: 2010-6-30 Time: 11:38:06
 */
public class EngineImpl extends Engine {

    public static final int ONE_POINT_VALUE = 6;
    public static final int TWO_POINT_VALUE = 9;
    public static final int THREE_POINT_VALUE = 12;
    public static final int FAN_POINT_VALUE = 15;

    public Solution split(AiPokerList pokers) {

        Solution solution = ThirdPhaseSplit.parse(
                SecondPhaseSplit.parse(
                        GeneralSplit.parse(pokers)
                )
        );

        if (solution != null) solution.sort();

        return solution;
    }

    public AiHand deliver(AiPokerList pokers, Phase phase, Dir self) {
        Solution solution = split(pokers);
        return DeliverUnit.deliver(pokers, solution, phase, self);
    }

    public AiHand contact(AiPokerList remainderListAi, Phase phase, Dir self) {
        return ContactUnit.contact(remainderListAi, phase, self);
    }

    public int robLoader(AiPokerList aiPokerList) {

        int value = asLoader(aiPokerList);

        if (value >= THREE_POINT_VALUE) return 3;
        if (value >= TWO_POINT_VALUE) return 2;
        if (value >= ONE_POINT_VALUE) return 1;

        return 0;
    }

    public int asLoader(AiPokerList aiPokerList) {

        Solution solution = split(aiPokerList);
        Map<Character, Integer> counter = aiPokerList.getCounter();

        // 双王必抓
        if (solution.peek(HandType.Rocket).size() > 0) {
            return THREE_POINT_VALUE;
        }

        // 四个2必抓
        Integer countOf2 = counter.get('x');
        if (countOf2 == 4) return THREE_POINT_VALUE;

        int result = 0;

        if (aiPokerList.contains('y')) result += 3;
        if (aiPokerList.contains('z')) result += 4;

        result += 2 * counter.get('x');
        result += 4 * solution.peek(HandType.Bomb).size();

        if (solution.size() < 7) {
            result += (7 - solution.size()) * 3;
        }

        int threeBlockCount = 0;
        Set<Character> idSet = aiPokerList.charSet();

        for (char id : idSet) {
            if (counter.get(id) == 3) threeBlockCount++;
        }

        result += threeBlockCount;

        return result;
    }

    @Override
    public AiPokerList promptChosePoker(
            AiPokerList remainder, boolean isDeliver, AiPokerList selectedPokers, AiHand lastAiHand) {

        if (isDeliver) {
            return PromptDeliverUnit.deliverPrompt(remainder, selectedPokers);
        }
        else if (lastAiHand != null) {
            return PromptDeliverUnit.contactPrompt(remainder, lastAiHand, selectedPokers);
        }

        return null;
    }

    @Override
    public List<Map.Entry<AiPokerList, Integer>> promptContact(
            AiPokerList remainderListAi, AiHand lastAiHand) {

        return PromptContactUnit.promptContact(remainderListAi, lastAiHand);
    }

}
