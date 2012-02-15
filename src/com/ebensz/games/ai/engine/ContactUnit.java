package com.ebensz.games.ai.engine;

import com.ebensz.games.ai.bean.*;
import com.ebensz.games.model.Dir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ebensz.games.ai.Ai.Helper.*;

public class ContactUnit {
    /**
     * 补充：根据出的牌 猜测对家手上可能存在哪些牌 猜测2 王 的剩余 计算可能（或者不可能）存在的顺子 10的顺子出现
     */
    private static int minCostValue = 1000;

    public static AiHand contact(AiPokerList remainderListAi, Phase phase, Dir self) {

        AiHand lastAiHand = phase.lastHand();

        if (lastAiHand.contains('y') && lastAiHand.contains('z'))  //火箭
            return null;

        boolean toContactEnemy = phase.isEnemy(self, phase.lastHandDir());

        //如果是盟友出的牌且盟友只有一张了(盟友位于自己的下家) 则不接
        if (!toContactEnemy && isLoaderXiaJia(self, phase.getLoader())) {
            int allyRemainder = phase.getRemainder(Dir.Right);
            if (allyRemainder == 1) return null;
        }
        List<AiHand> allContactPlan = new ArrayList<AiHand>();

        List<AiPokerList> plans = PokerTypeUnit.tryContact(remainderListAi, lastAiHand);
        for (AiPokerList aiPokerList : plans) {
            allContactPlan.add(new AiHand(aiPokerList, lastAiHand.getType()));
        }
        Collections.sort(allContactPlan, new Solution.SolutionComparator());

        if (allContactPlan.size() == 0) {
            //没有可接的起的牌 看是否有炸弹可接
            return contactWithBomb(remainderListAi, phase, lastAiHand, self);
        }

        List<AiHand> allMinAiHands = getMinContactCost(remainderListAi, allContactPlan, lastAiHand.getType());
        Collections.sort(allMinAiHands, new Solution.SolutionComparator());

        AiHand bestContactAiHand = new AiHand(allMinAiHands.get(allMinAiHands.size() - 1), lastAiHand.getType());                                                    //取得接牌的最小代价值 （已手数值为依据，扩充加入以牌值为依据）.
        if (contactFirstConsider(phase, remainderListAi, bestContactAiHand, self))
            return bestContactAiHand;

        //放行考虑（右边是盟家）
        if (isLoaderXiaJia(self, phase.getLoader()) && phase.getRemainder(Dir.Right) < 3 && toContactEnemy) {
            if (!Engine.isBig(lastAiHand)
                    && phase.getRemainder(phase.getLoader()) > lastAiHand.size()) {                //保证敌人不是存在最后一手回龙牌 才放行
                return null;
            }
            AiHand releaseAiHand = contactReleaseConsider(remainderListAi, allContactPlan);
            if (releaseAiHand != null) return releaseAiHand;
        }

        if (phase.getEnemyRemainder(self) < 3) {

            AiHand contactAiHand = contactWhenEnemyLessThree(
                    phase,
                    remainderListAi,
                    bestContactAiHand,
                    allContactPlan,
                    self
            );
            Solution solution = Engine.getDefault().split(remainderListAi);

            if (!(solution.peek(HandType.Single).size() >= 2
                    && phase.getEnemyRemainder(self) == 2)
                    && contactAiHand != null
                    && (contactAiHand.contains('y') || contactAiHand.contains('z'))
                    && solution.peek(HandType.Rocket).size() > 0) {

                return (AiHand) solution.peek(HandType.Rocket).get(0);
            }

            //如果不是自己剩两张单牌并且敌人有两张牌的情况下 ,则对王就不拆  ，反之则都拆开打
            return contactAiHand;
        }
        else {
            return contactWhenEnemyEqualOrBiggerThree(
                    remainderListAi,
                    phase,
                    allMinAiHands,
                    self
            );
        }
    }

    /**
     * 接牌总的考虑：
     * 无论接哪家的牌，接牌后，如果手上除了保证能打大的牌型之外，剩余手数<=1，就接；
     * 无论接哪家的牌，接牌后 ，如果全部或除1手外的牌型的数量都大于敌人手上的牌的数量，就接；
     *
     * @param phase
     * @param remainderListAi
     * @param aiHand
     * @return
     */
    private static boolean contactFirstConsider(
            Phase phase, AiPokerList remainderListAi, AiHand aiHand, Dir self) {

        remainderListAi.remove(aiHand);
        Solution solution = Engine.getDefault().split(remainderListAi);
        remainderListAi.add(aiHand);

        int notBiggestCount = 0;
        for (int i = 0; i < solution.size(); i++) {
            if (!Engine.isMaximal(solution.peek(i), phase)) {
                notBiggestCount++;
                if (notBiggestCount > 1) break;
            }
        }
        if (notBiggestCount <= 1) return true;

        int enemyRemainder = phase.getEnemyRemainder(self);
        boolean allGreaterEnemyRemainder = true;
        for (int i = 0; i < solution.size(); i++) {
            if (solution.peek(i).size() <= enemyRemainder) {
                allGreaterEnemyRemainder = false;
                break;
            }
        }

        return allGreaterEnemyRemainder;
    }

    /**
     * 接牌放行考虑：（右边是盟家，而且盟家手上的剩余牌数〈3，就要考虑为盟家放行；此时要接的一定要为敌人）
     * <p/>
     * 1.要接的牌不大，而且牌数小于或等盟家的牌数，就直接放行。
     * 2.有炸弹，就出炸弹（后边再考虑炸弹能不能打大），然后准备放行；
     * 3.有此种牌型的最大手，就出，然后准备放行；
     *
     * @param remainderListAi
     * @param allContactPlan
     * @return
     */
    private static AiHand contactReleaseConsider(
            AiPokerList remainderListAi, List<AiHand> allContactPlan) {

        Solution solution = Engine.getDefault().split(remainderListAi);
        for (int i = 0; i < solution.size(); i++) {
            if (solution.peek(i).getType() == HandType.Bomb)
                return solution.peek(i);
        }

        if (allContactPlan.size() > 0) {
            //出的是此种牌型的最大的 (不是经split划分后的最大手,而是可以接得住的最大牌）
            return allContactPlan.get(0);
        }

        return null;
    }

    /**
     * 接牌---敌人手上的张数小于3
     * 接敌人的牌：就死接；此时，如果要接的牌型的数量等于敌人手上的牌数量，就选接最大的，否则，只要接入就行了；
     * 接盟友的牌：如果我是上家，且要接的牌型的数量等于敌人（地主）手上的牌数量，
     * 就顶住（前提是盟家的牌还不是最大〈除我手上的牌外〉）。
     * 接盟友的牌：如果我是上家，且要接的牌型的数量“不”等于敌人（地主）手上的牌数量，就考虑顺着出。
     * 而要接的是盟友的牌：如果我是下家，如果不能顺着出，就不出。
     *
     * @param phase
     * @param remainderListAi
     * @param bestContactAiHand
     * @param allContactPlan
     * @return
     */
    private static AiHand contactWhenEnemyLessThree(
            Phase phase, AiPokerList remainderListAi,
            AiHand bestContactAiHand, List<AiHand> allContactPlan, Dir self) {

        boolean toContactEnemy = phase.isEnemy(self, phase.lastHandDir());
        AiHand lastAiHand = phase.lastHand();

        if (toContactEnemy) {

            if (lastAiHand.size() == phase.getEnemyRemainder(self)
                    && allContactPlan.size() > 0) {            //出的是此种牌行的最大牌 (不是经split划分后的最大手）

                return allContactPlan.get(0);
            }
            else {
                return bestContactAiHand;                        //否则选可以接得起的牌接  （不及代价多大）
            }
        }
        else if (isLoaderShangJia(self, phase.getLoader())) {                                                //我是上家

            if (lastAiHand.size() == phase.getEnemyRemainder(self)) {

                phase.getAiHistory().add(remainderListAi);

                if (!Engine.isMaximal(phase.lastHand(), phase)) {
                    //出此类型最大牌出
                    if (lastAiHand.size() == phase.getEnemyRemainder(self)
                            && allContactPlan.size() > 0) {            //出的是此种牌行的做大收 (不是经split划分后的最大手）

                        return allContactPlan.get(0);
                    }
                }

                phase.getAiHistory().remove(remainderListAi);
            }
            else {
                return bestContactAiHand;
            }
        }
        else if (isLoaderXiaJia(self, phase.getLoader()) && minCostValue <= -1 && !Engine.isBig(bestContactAiHand)) {
            return bestContactAiHand;
        }

        return null;
    }

    /**
     * 接牌---敌人手上的牌不少于3张时
     * 1.接敌人出的牌
     * 接单或对，如果接牌后，手数变化为〈＝0，接；
     * 如果我是上家，尽量出大牌(小于2的)，顶住（前提是没有手数减少）；
     * 如果我是下家，如果敌人的牌较大，尽量接（手数变化<=0），否则顺着走；
     * 接长牌型（不是单或对），如果接牌后，手数变化<=1，接；
     * 2.接盟家出的牌
     * 接单或对，
     * 如果我是上家，且盟家出的牌较小，就顶住（手数变化<=0）；
     * 如果我是下家，且可以接的牌不大，就顺着出；
     * 对于长牌型，直接不出；
     *
     * @param remainderListAi
     * @param phase
     * @param allMinAiHands
     * @return
     */
    private static AiHand contactWhenEnemyEqualOrBiggerThree(
            AiPokerList remainderListAi, Phase phase,
            List<AiHand> allMinAiHands, Dir self) {

        boolean toContactEnemy = phase.isEnemy(self, phase.lastHandDir());
        AiHand lastAiHand = phase.lastHand();
        AiHand bestContactAiHand = new AiHand(allMinAiHands.get(allMinAiHands.size() - 1), lastAiHand.getType());

        if (toContactEnemy) {

            if (lastAiHand.getType() == HandType.Pair
                    || lastAiHand.getType() == HandType.Single) {

                boolean isLast = isLoaderShangJia(self, phase.getLoader()) && minCostValue <= 0; //如果我是上家

                if (isLast) {
                    //有2就不出王 拆2出
                    if (bestContactAiHand.getSortChar() > 'x'
                            && remainderListAi.getCounter().get('x') > 0
                            && lastAiHand.getMaxChar() < 'x') {

                        return new AiHand(new char[]{'x'}, HandType.Single);
                    }

                    //bestContactAiHand 为拆的对王的情况  如果单张个数大于等于4 就有需要拆王打
                    if (bestContactAiHand.getSortChar() > 'x' && minCostValue == 0) {
                        Solution solution = Engine.getDefault().split(remainderListAi);
                        int singleSize = solution.peek(HandType.Single).size();
                        if (singleSize >= 4) return bestContactAiHand;
                        else return null;
                    }

                    //正常情况出2以下最大的 没有就出2
                    if (allMinAiHands.get(0).getMaxChar() <= 'x') {
                        if (allMinAiHands.size() > 1) return allMinAiHands.get(1);
                        return allMinAiHands.get(0);
                    }
                    else {
                        return bestContactAiHand;                                        //用单张王接牌的情况（上面已考虑了是否拆火箭）
                    }
                }
                else {
                    if (Engine.isBig(lastAiHand) && minCostValue <= 0) {

                        if (bestContactAiHand.getSortChar() > 'x'
                                && remainderListAi.getCounter().get('x') > 0
                                && lastAiHand.getMaxChar() < 'x') {

                            return new AiHand(new char[]{'x'}, HandType.Single);
                        }

                        //bestContactAiHand 为拆的对王的情况  如果单张个数大于等于4 就有需要拆王打
                        if (bestContactAiHand.getSortChar() > 'x' && minCostValue == 0) {
                            Solution solution = Engine.getDefault().split(remainderListAi);
                            int singleSize = solution.peek(HandType.Single).size();
                            return singleSize < 4 ? null : bestContactAiHand;
                        }
                        return bestContactAiHand;
                    }
                    else if (minCostValue < 0) {
                        if (remainderListAi.getCounter().get('x') == 0
                                || lastAiHand.getType() == HandType.Pair) {
                            //  是对子就直接   是单个没有2的情况下就接
                            return bestContactAiHand;
                        }
                    }
                }

                // 拆2打法 包括拆四个2 和三个2 （ 所以几乎不打四个2 -保守打法）
                if (lastAiHand.getMaxChar() < 'x') {

                    char[] pokeOf2 = lastAiHand.getType() == HandType.Single
                            ? new char[]{'x'}
                            : new char[]{'x', 'x'};

                    if (remainderListAi.getCounter().get('x') >= pokeOf2.length) {
                        return new AiHand(pokeOf2, lastAiHand.getType());
                    }
                }
            }
            else if (minCostValue <= 1) {
                return bestContactAiHand;
            }
        }
        else {
            if (lastAiHand.getType() == HandType.Pair || lastAiHand.getType() == HandType.Single) {

                if (isLoaderShangJia(self, phase.getLoader()) && minCostValue <= 0) { //如果我是上家

                    if (!Engine.isBig(bestContactAiHand) && minCostValue == -1) { //顺着溜牌
                        return bestContactAiHand;
                    }
                    else if (Engine.isSmall(lastAiHand) && minCostValue <= 0) {

                        AiHand biggerAiHand = allMinAiHands.get(0);
                        if (biggerAiHand.contains('y') || biggerAiHand.contains('z') || biggerAiHand.contains('x')) {
                            if (allMinAiHands.size() <= 1) return null;
                            biggerAiHand = allMinAiHands.get(1);
                        }

                        return biggerAiHand;
                    }
                }
                else if (isLoaderXiaJia(self, phase.getLoader()) && minCostValue <= -1 && Engine.isSmall(bestContactAiHand)) {                                        //如果我是下家
                    return bestContactAiHand;
                }
            }
        }

        return null;
    }

    /**
     * 计算出最大的
     *
     * @param pokers
     * @param allContactPlan
     * @return
     */
    private static List<AiHand> getMinContactCost(
            AiPokerList pokers, List<AiHand> allContactPlan, HandType lastHandType) {

        Solution solution = Engine.getDefault().split(pokers);
        int primaryMinHandCount = solution.size();
        int[] allHandContArr = new int[allContactPlan.size()];

        for (int i = 0; i < allContactPlan.size(); i++) {

            AiHand plan = allContactPlan.get(i);

            if (excludingState(lastHandType, plan, pokers)) {
                allHandContArr[i] = 100;
                continue;
            }

            pokers.remove(plan);
            solution = Engine.getDefault().split(pokers);
            //对于存在的炸弹或者三张或者飞机的 手数值对应减少
            int bombSize = solution.peek(HandType.Bomb).size();
            int rocketSize = solution.peek(HandType.Rocket).size();
            int type_x3_size = solution.peek(HandType.Type_x3).size();
            int threeLink_size = solution.peek(HandType.ThreeLink).size();
            int size = solution.size() - ((bombSize + rocketSize) * 2 + type_x3_size + threeLink_size * 2);

            allHandContArr[i] = size;
            pokers.add(plan);
        }

        int minHandCount = 100;
        //得出最小手数值
        for (int anAllHandContArr : allHandContArr) {
            if (anAllHandContArr < minHandCount) {
                minHandCount = anAllHandContArr;
            }
        }

        //如果手数最小的有几种情况则计算其牌值和
        int minPokeValueSum = 10000;
        int minSite = 0;

        //暂时只用手数 为依据 计算代价
        minCostValue = minHandCount - primaryMinHandCount;

        List<AiHand> allMinHandsPoke = new ArrayList<AiHand>();
        for (int i = 0; i < allHandContArr.length; i++) {
            if (allHandContArr[i] == minHandCount) {
                int currentValue = calPokeValue(allContactPlan.get(i));
                if (currentValue < minPokeValueSum) {
                    minSite = i;
                    minPokeValueSum = currentValue;
                }
                allMinHandsPoke.add(allContactPlan.get(i));
            }
        }

        AiHand bestContactPlan = allContactPlan.get(minSite);
        if (allMinHandsPoke.size() > 1) {
            // 最优的牌在第一个
            allMinHandsPoke.set(allMinHandsPoke.size() - 1, bestContactPlan);
        }

        return allMinHandsPoke;
    }

    private static boolean excludingState(
            HandType lastHandType, AiHand plan, AiPokerList pokers) {

        //带牌中 有带王、2的类型不做考虑
        //pokers.size()>=4  如果手里就只剩一首带牌了  则可带王、2

        //对于接牌类型为三带、对子、单张中有拆炸弹或者火箭的不做考虑
        if ((lastHandType == HandType.Type_x3_1
                || lastHandType == HandType.Type_x3_2
                || lastHandType == HandType.Type_x4_2
                || lastHandType == HandType.Type_x4_2x2)
                && (plan.contains('y') || plan.contains('z') || plan.contains('x'))
                && pokers.size() >= 4) {

            return true;
        }

        //noinspection SimplifiableIfStatement
        if ((plan.getType() == HandType.ThreeLink_1xn || plan.getType() == HandType.ThreeLink_2xn)
                && pokers.getCounter().get(plan.get(2)) == 4) {

            return true;
        }

        return plan.size() < 4 && pokers.getCounter().get(plan.get(0)) == 4;
    }

    /**
     * 使用炸弹接牌
     *
     * @param phase
     * @param remainderListAi
     * @param lastAiHand
     * @return
     */
    public static AiHand contactWithBomb(
            AiPokerList remainderListAi, Phase phase, AiHand lastAiHand, Dir self) {

        boolean toContactEnemy = phase.isEnemy(phase.lastHandDir(), self);
        if (!toContactEnemy) return null;

        Solution solution = Engine.getDefault().split(remainderListAi);
        List<AiPokerList> bombs = solution.peek(HandType.Bomb);

        if (lastAiHand.getType() == HandType.Bomb) {

            for (AiPokerList aiPokerList : bombs) {
                AiHand aiHand = (AiHand) aiPokerList;
                if (aiHand.getMaxChar() > lastAiHand.getMaxChar()) return aiHand;
            }

            List<AiPokerList> rocket = solution.peek(HandType.Rocket);
            if (rocket.size() > 0) return (AiHand) rocket.get(0);

            return null;                                //没有接得起炸弹的牌
        }

        int notBiggestHandCount = 0;
        for (int i = 0; i < solution.size(); i++) {
            if (!Engine.isMaximal(solution.peek(i), phase)) {
                notBiggestHandCount++;
            }
        }

        if (phase.getEnemyRemainder(self) < 3 || notBiggestHandCount <= 1) {                                        //可能补充遇到长牌型也炸

            // todo: 选择最小的炸弹
            for (AiPokerList aiPokerList : bombs) {
                return (AiHand) aiPokerList;
            }

            List<AiPokerList> rocket = solution.peek(HandType.Rocket);
            if (rocket.size() > 0) return (AiHand) rocket.get(0);
        }

        return null;
    }

    private static int calPokeValue(AiPokerList pokers) {

        int value = 0;
        char[] cArr = pokers.toString().toCharArray();

        for (char c : cArr) {
            value += c;
        }

        return value;
    }
}
