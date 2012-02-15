package com.ebensz.games.ai.engine;

import com.ebensz.games.ai.bean.*;
import com.ebensz.games.model.Dir;

import java.util.ArrayList;
import java.util.List;

import static com.ebensz.games.ai.Ai.Helper.*;

public class DeliverUnit {

    public static AiHand deliver(AiPokerList pokers, Solution solution, Phase phase, Dir self) {

        if (solution.size() == 1) return solution.pop(0);

        solution.sort(); // 按牌型倒序

        // 优先考虑
        AiHand aiHand = deliverAllMaximal(solution, phase);
        if (aiHand != null) return aiHand;

        // 放行考虑
        aiHand = pasaAndPost(pokers, phase, solution, self);
        if (aiHand != null) return aiHand;

        // 最后关头的考虑

        aiHand = thinkEndPhase(solution, phase, self);
        if (aiHand != null) return aiHand;

        // 正常流程

        return deliverNormal(solution, phase, self);
    }

    private static AiHand thinkEndPhase(Solution solution, Phase phase, Dir self) {

        if (solution.size() == 2) {
            AiHand h1 = solution.peek(0), h2 = solution.peek(1);
            if (h1.size() > h2.size() && h1.getSortChar() < 'x') return h1;
            if (h1.size() < h2.size() && h2.getSortChar() < 'x') return h2;
        }

        int enemyRemainder = phase.getEnemyRemainder(self);

        if (phase.getLoader() == self) { // 地主打法：
            AiHand aiHand = loaderThinkAbout(solution, enemyRemainder);
            if (aiHand != null) return aiHand;
        }
        else if (isLoaderShangJia(self, phase.getLoader())) { // 上家打法：
            AiHand aiHand = leftThinkAbout(solution, enemyRemainder);
            if (aiHand != null) return aiHand;
        }
        else if (isLoaderXiaJia(self, phase.getLoader())) { // 下家打法：
            AiHand aiHand = rightThinkAbout(solution, enemyRemainder);
            if (aiHand != null) return aiHand;
        }

        return null;
    }

    private static AiHand loaderThinkAbout(Solution solution, int enemyRemainder) {

        if (enemyRemainder == 1) {
            // 敌人牌数为1时：先排除最小的单张
            solution.popMinSingle();
        }
        else if (enemyRemainder == 2) {

            // 敌人剩余牌数为2张时，排除所有的对子
            List<AiPokerList> pairs = solution.pop(HandType.Pair);

            // 除对子之外，无其它牌
            if (solution.size() == 0) {
                AiPokerList minPair = pairs.get(pairs.size() - 1);
                char[] ids = new char[]{minPair.getMinChar()};
                return new AiHand(ids, HandType.Single);
            }
        }

        return null;
    }

    private static AiHand leftThinkAbout(Solution solution, int enemyRemainder) {

        if (enemyRemainder == 1) {

            // 如果地主剩1张牌，先排除单牌；
            List<AiPokerList> singles = solution.pop(HandType.Single);

            // 如果只有单牌，就出最大的一张；
            if (solution.size() == 0) return (AiHand) singles.get(0);
        }
        else if (enemyRemainder == 2) {

            // 如果地主剩2张牌，先排除对子和单张；
            List<AiPokerList> pairs = solution.pop(HandType.Pair);
            List<AiPokerList> singles = solution.pop(HandType.Single);
            int pairCount = pairs.size(), singleCount = singles.size();

            // 如果手上只剩单张和对子，就出最小的单张；
            if (solution.size() == 0) {

                if (singleCount > 0) {
                    return (AiHand) singles.get(singleCount - 1);
                }

                // 如果只剩余对子，就拆一个小对子打单张
                char[] ids = {pairs.get(pairCount - 1).getMinChar()};
                return new AiHand(ids, HandType.Single);
            }
        }

        return null;
    }

    private static AiHand rightThinkAbout(Solution solution, int enemyRemainder) {

        if (enemyRemainder == 1) {

            // 如果地主剩1张牌，先排除单牌
            List<AiPokerList> singles = solution.pop(HandType.Single);

            // 如果只有单牌，就出最倒数第二小的一张
            if (solution.size() == 0) {
                return (AiHand) singles.get(singles.size() - 2);
            }
        }
        else if (enemyRemainder == 2) {

            // 如果地主剩2张牌，先排除对子和单张
            List<AiPokerList> pairs = solution.pop(HandType.Pair);
            List<AiPokerList> singles = solution.pop(HandType.Single);
            // 排除炸弹
            List<AiPokerList> rocket = solution.pop(HandType.Rocket);
            List<AiPokerList> bombs = solution.pop(HandType.Bomb);

            int pairCount = pairs.size(), singleCount = singles.size();

            // 如果手上除了单张、对子和炸弹之外，无其它牌
            if (solution.size() == 0) {

                // 就出最小的单张；
                if (singleCount > 0) {
                    return (AiHand) singles.get(singleCount - 1);
                }

                // 如果只剩余对子，就拆一个小对子打单张
                if (pairCount > 0) {
                    AiPokerList minPair = pairs.get(pairCount - 1);
                    char[] ids = {minPair.getMinChar()};
                    return new AiHand(ids, HandType.Single);
                }

                // 如果只剩余炸弹三个以上的炸弹，就将它们还入solution，然后在正常流程中顺次出
                for (AiPokerList hand : rocket) {
                    solution.addHand((AiHand) hand);
                }
                for (AiPokerList hand : bombs) {
                    solution.addHand((AiHand) hand);
                }

                solution.sort();
            }
        }

        return null;
    }

    /**
     * 上家： 如果盟家的剩余牌数为1，且还没有试过放单张：拆分手上的牌型，如果有单张，就出单张；
     * 如果上回放单张，盟家有出牌机会而不出，就标记已经放过单张；
     * <p/>
     * 如果盟家的剩余牌数为2，且还没有试过放对子：拆分手上的牌型，如果有对子，而且有一个对子不大，
     * 就出这个对子；同上例，如果前边已经诉过对子，而盟家不能接，即标记已经放过对子，不再放对子， 此时如果拆分的牌型中有单，就放单张；
     * <p/>
     * 否则按正常的出牌流程选择出牌；
     * <p/>
     * 下家：
     * <p/>
     * 如果盟家的剩余牌数为1，且还没有试过放单张：直接选择手上最小的牌放行；
     * <p/>
     * 如果盟家的剩余牌数为2，且还没有试过放对子：拆分手上的牌，如果有小的对子牌型，就出； 否则出单张，如果没有单张，则按正常的流程选择出牌；
     *
     * @param pokers   手上的牌
     * @param phase    局面
     * @param solution 拆分方案
     * @param self     engine if thinking for
     * @return 可放行的一手牌，为空时表示无法放行
     */
    private static AiHand pasaAndPost(AiPokerList pokers, Phase phase, Solution solution, Dir self) {

        AiHand aiHand = null;

        if (isLoaderXiaJia(self, phase.getLoader())) {
            aiHand = pasaIfPossible(pokers, solution, phase, self);
        }
        else if (isLoaderShangJia(self, phase.getLoader())) {
            aiHand = postIfPossible(pokers, solution, phase, self);
        }

        return aiHand;
    }

    /**
     * 手上的牌，如果只有一手以内（一手或没有）的牌型不是最大的，就排除不大的牌型，打其它的；
     *
     * @param solution 拆分方案
     * @param phase    局面
     * @return 手上的牌是否只有一手不大，如果是，就返回一手大牌，否则返回null
     */
    private static AiHand deliverAllMaximal(Solution solution, Phase phase) {

        int nonMaxHands = 0, nonMaxIndex = -1;

        for (int i = 0, size = solution.size(); i < size; i++) {
            AiHand aiHand = solution.peek(i);
            if (!Engine.isMaximal(aiHand, phase)) {
                nonMaxHands++;
                nonMaxIndex = i;
                if (nonMaxHands > 1) break;
            }
        }

        if (nonMaxHands <= 1) {
            if (nonMaxIndex != -1) solution.pop(nonMaxIndex);
            return solution.pop(solution.size() - 1);
        }

        return null;
    }

    private static AiHand deliverNormal(Solution solution, Phase phase, Dir self) {

        // 第0步：排除手上和敌人剩余牌张数相同的牌型

        int enemyRemainder = phase.getEnemyRemainder(self);

        List<Integer> willPopIndexes = new ArrayList<Integer>();
        for (int i = 0, size = solution.size(); i < size; i++) {
            if (solution.peek(i).size() == enemyRemainder) {
                willPopIndexes.add(i);
            }
        }

        List<AiPokerList> hands = new ArrayList<AiPokerList>();
        for (int i = willPopIndexes.size() - 1; i >= 0; i--) {
            hands.add(solution.pop(willPopIndexes.get(i)));
        }

        // 如果每手长度都正好和敌人剩余张数相同，就不排除它们了，也不再做特殊处理

        if (solution.size() == 0) {
            for (AiPokerList hand : hands) {
                solution.addHand((AiHand) hand);
            }
        }
        solution.sort();

        // 第1步：查看单和对子中是否有点数小于7的，有就出；

        boolean selectPair = Math.random() > 0.5;

        // 为了不每次都先出单张，后出对子，做一个随机选择

        List<AiPokerList> listAi = selectPair ?
                solution.peek(HandType.Pair) : solution.peek(HandType.Single);

        for (int i = listAi.size() - 1; i >= 0; i--) {
            AiHand aiHand = (AiHand) listAi.get(i);
            if (aiHand.getSortChar() < '7') return aiHand;
        }

        listAi = selectPair ?
                solution.peek(HandType.Single) : solution.peek(HandType.Pair);

        for (int i = listAi.size() - 1; i >= 0; i--) {
            AiHand aiHand = (AiHand) listAi.get(i);
            if (aiHand.getSortChar() < '7') return aiHand;
        }

        // 第2步：查看是否有小顺子，小于Q；

        listAi = solution.peek(HandType.SingleLink);
        for (int i = listAi.size() - 1; i >= 0; i--) {
            AiHand aiHand = (AiHand) listAi.get(i);
            if (aiHand.getSortChar() < 'Q') return aiHand;
        }

        // 第3步：列出三带，如果手上即有大的三带（A/K/Q），又有小的三带，就出小的三带；

        listAi = solution.peek(HandType.Type_x3_1); // 三带一
        int count = listAi.size();

        if (count > 1) {

            AiHand max = (AiHand) listAi.get(0);
            AiHand min = (AiHand) listAi.get(count - 1);

            if (Engine.isBig(max) && Engine.isSmall(min)) {
                return min;
            }
        }

        listAi = solution.peek(HandType.Type_x3_2); // 三带二
        count = listAi.size();

        if (count > 1) {

            AiHand max = (AiHand) listAi.get(0);
            AiHand min = (AiHand) listAi.get(count - 1);

            if (Engine.isBig(max) && Engine.isSmall(min)) {
                return min;
            }
        }

        // 第4步：列出对子，如果手上即有大的对子（2/A/K/Q），又有小的对子，就出小的对子；

        listAi = solution.peek(HandType.Pair);
        count = listAi.size();

        if (count > 1) {

            AiHand max = (AiHand) listAi.get(0);
            AiHand min = (AiHand) listAi.get(count - 1);

            if (Engine.isBig(max) && Engine.isSmall(min)) {
                return min;
            }
        }

        // 第5步：列出单张，如果手上即有大的单张（2/A/K/Q），又有小的单张，就出小单张；

        listAi = solution.peek(HandType.Single);
        count = listAi.size();

        if (count > 1) {

            AiHand max = (AiHand) listAi.get(0);
            AiHand min = (AiHand) listAi.get(count - 1);

            if (Engine.isBig(max) && Engine.isSmall(min)) {
                return min;
            }
        }

        // 第6步：找出最小的牌型(除炸弹外)，如果它小于'e'，出之

        char minSortChar = 'z';
        AiHand minAiHand = null;

        for (int i = solution.size() - 1; i >= 0; i--) {

            AiHand aiHand = solution.peek(i);
            if (aiHand.getType() == HandType.Bomb) continue;

            char sortChar = aiHand.getSortChar();
            if (sortChar < minSortChar) {
                minSortChar = sortChar;
                minAiHand = aiHand;
            }
        }

        if (minSortChar < 'e') return minAiHand;

        // 第7步：找出2张以上的最长牌(除炸弹外)，打之

        int maxLength = 0;
        AiHand maxLengthAiHand = null;

        for (int i = solution.size() - 1; i >= 0; i--) {

            AiHand aiHand = solution.peek(i);
            if (aiHand.getType() == HandType.Bomb) continue;

            int length = aiHand.size();
            if (length > maxLength) {
                maxLength = length;
                maxLengthAiHand = aiHand;
            }
        }

        if (maxLength > 2) return maxLengthAiHand;

        // 第8步：计算对子和单张的数量，那个牌型的数量大些出哪个牌型中最小的一个；

        List<AiPokerList> pairs = solution.peek(HandType.Pair);
        List<AiPokerList> singles = solution.peek(HandType.Single);
        int pairCount = pairs.size(), singleCount = singles.size();

        if (pairCount > singleCount) {
            AiHand aiHand = (AiHand) pairs.get(pairCount - 1);
            if (aiHand.getSortChar() < 'e') return aiHand;
        }
        else if (singleCount > 0) {
            AiHand aiHand = (AiHand) singles.get(singleCount - 1);
            if (aiHand.getSortChar() < 'e') return aiHand;
        }

        // 第10步：保底策略：依次出顺子、三带、飞机、四带、炸弹、火箭；
        return solution.peek(solution.size() - 1);
    }

    private static AiHand pasaIfPossible(AiPokerList pokers, Solution solution, Phase phase, Dir self) {

        int rightRemainder = phase.getRemainder(Dir.Right);
        int enemyRemainder = phase.getEnemyRemainder(self);

        if (rightRemainder == 1) {

            if (phase.isTrySinglePasa()) return null;

            AiHand aiHand = new AiHand(new char[]{pokers.getMinChar()}, HandType.Single);
            if (!Engine.isSmall(aiHand)) return null;

            pokers.remove(aiHand);
            phase.setTrySinglePasa(true);

            return aiHand;
        }
        else if (rightRemainder == 2) {

            if (!phase.isTryPairPasa()) {

                List<AiPokerList> pairs = solution.peek(HandType.Pair);
                List<AiPokerList> singles = solution.peek(HandType.Single);
                int pairCount = pairs.size(), singleCount = singles.size();

                if (pairCount > 0) {

                    if (enemyRemainder == 2) return null;

                    AiHand aiHand = new AiHand(pairs.remove(pairCount - 1), HandType.Pair);

                    if (Engine.isSmall(aiHand)) {

                        pokers.remove(aiHand);
                        phase.setTryPairPasa(true);

                        return aiHand;
                    }
                    else {

                        if (singleCount == 0) return null;
                        if (enemyRemainder == 1) return null;

                        aiHand = new AiHand(singles.get(singleCount - 1), HandType.Single);
                        if (!Engine.isSmall(aiHand)) return null;

                        pokers.remove(aiHand);
                        phase.setTryPairPasa(true);
                        phase.setTrySinglePasa(false);

                        return aiHand;
                    }
                }
                else {

                    if (enemyRemainder == 1) return null;

                    AiHand aiHand = new AiHand(new char[]{pokers.getMinChar()}, HandType.Single);
                    if (!Engine.isSmall(aiHand)) return null;

                    pokers.remove(aiHand);
                    phase.setTryPairPasa(true);
                    phase.setTrySinglePasa(false);

                    return aiHand;
                }
            }
            else if (!phase.isTrySinglePasa()) {

                if (enemyRemainder == 1) return null;

                AiHand aiHand = new AiHand(new char[]{pokers.getMinChar()}, HandType.Single);
                if (!Engine.isSmall(aiHand)) return null;

                pokers.remove(aiHand);
                phase.setTrySinglePasa(true);

                return aiHand;
            }
        }

        return null;
    }

    private static AiHand postIfPossible(AiPokerList pokers, Solution solution, Phase phase, Dir self) {

        int leftRemainder = phase.getRemainder(Dir.Left);
        int enemyRemainder = phase.getEnemyRemainder(self);

        if (leftRemainder == 1) {

            if (phase.isTrySinglePasa()) return null;
            if (enemyRemainder <= 2) return null;

            List<AiPokerList> singles = solution.peek(HandType.Single);
            int singleCount = singles.size();

            if (singleCount > 0) {

                AiHand aiHand = new AiHand(singles.remove(singleCount - 1), HandType.Single);
                if (!Engine.isSmall(aiHand)) return null;

                pokers.remove(aiHand);
                phase.setTrySinglePasa(true);

                return aiHand;
            }
        }
        else if (leftRemainder == 2) {

            if (enemyRemainder == 2) return null;

            if (!phase.isTryPairPasa()) {

                List<AiPokerList> pairs = solution.peek(HandType.Pair);
                int pairCount = pairs.size();

                if (pairCount > 0) {

                    AiHand aiHand = new AiHand(pairs.remove(pairCount - 1), HandType.Pair);

                    if (Engine.isSmall(aiHand)) {
                        pokers.remove(aiHand);
                        phase.setTryPairPasa(true);
                        return aiHand;
                    }
                    else {

                        List<AiPokerList> singles = solution.peek(HandType.Single);
                        int singleCount = singles.size();

                        if (singleCount <= 0) return null;

                        aiHand = new AiHand(singles.remove(singleCount - 1), HandType.Single);
                        if (!Engine.isSmall(aiHand)) return null;

                        pokers.remove(aiHand);
                        phase.setTryPairPasa(true);
                        phase.setTrySinglePasa(false);

                        return aiHand;
                    }
                }
                else {

                    List<AiPokerList> singles = solution.peek(HandType.Single);
                    int singleCount = singles.size();

                    if (singleCount > 0) {

                        AiHand aiHand = new AiHand(singles.remove(singleCount - 1), HandType.Single);
                        if (!Engine.isSmall(aiHand)) return null;

                        pokers.remove(aiHand);
                        phase.setTryPairPasa(true);
                        phase.setTrySinglePasa(false);

                        return aiHand;
                    }
                }
            }
            else if (!phase.isTrySinglePasa()) {

                List<AiPokerList> singles = solution.peek(HandType.Single);
                int singleCount = singles.size();

                if (singleCount > 0) {

                    AiHand aiHand = new AiHand(singles.remove(singleCount - 1), HandType.Single);
                    if (!Engine.isSmall(aiHand)) return null;

                    pokers.remove(aiHand);
                    phase.setTryPairPasa(true);
                    phase.setTrySinglePasa(false);

                    return aiHand;
                }
            }
        }

        return null;
    }
}
