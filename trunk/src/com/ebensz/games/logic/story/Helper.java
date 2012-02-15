package com.ebensz.games.logic.story;

import com.ebensz.games.Rule.Rule;
import com.ebensz.games.Rule.RuleHelper;
import com.ebensz.games.logic.RuntimeData;
import com.ebensz.games.logic.round.Round;
import com.ebensz.games.logic.settle.NormalSettleTool;
import com.ebensz.games.logic.settle.SettleTool;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.History;
import com.ebensz.games.model.hand.Hand;
import com.ebensz.games.model.hand.SingleLink;
import com.ebensz.games.model.poker.ColoredPoker;
import com.ebensz.games.model.poker.Poker;
import com.ebensz.games.model.poker.PokerUtils;
import com.ebensz.games.model.poker.PokersInfo;

import java.util.*;

/**
 * User: jason
 * Date: 11-12-21
 * Time: 下午6:25
 */
class Helper {


    static List<ColoredPoker> chuPaiAutoComplete(List<Hand> chuPaiSuggestions, List<ColoredPoker> selectPokers, PokersInfo pokersInfo) {
        //完成顺子的补足

        if (selectPokers.size() != 2) return null;

        Collections.sort(selectPokers);
        Poker maxPoker = selectPokers.get(0).getPoker();
        Poker minPoker = selectPokers.get(1).getPoker();

        if (maxPoker.ordinal() - minPoker.ordinal() != 1) return null;

        if (!minPoker.isSmallerThan(Poker.J)) return null;

        SingleLink singleLink = new SingleLink(minPoker, 5);

        if (chuPaiSuggestions.contains(singleLink))
            return null;

        List<Poker> shouPai = pokersInfo.getShouPai(Dir.Outside);

        List<Poker> linkPokers = Arrays.asList(singleLink.getPokers());

        if (!shouPai.containsAll(linkPokers)) return null;

        List<Poker> otherThreePokers = new ArrayList<Poker>(linkPokers);

        for (ColoredPoker coloredPoker : selectPokers) {
            Poker poker = coloredPoker.getPoker();
            if (otherThreePokers.contains(poker)) {
                otherThreePokers.remove(poker);
            }
        }

        Map<Dir, List<ColoredPoker>> shouPaiMap = pokersInfo.getShouPaiMap();

        List<ColoredPoker> otherThree = PokerUtils.mapColoredPokers(otherThreePokers, shouPaiMap.get(Dir.Outside));

        chuPaiSuggestions.add(singleLink);

        return otherThree;
    }

    static List<ColoredPoker> jiePaiAutoComplete(List<Hand> jiePaiSuggestions, List<ColoredPoker> selectPokers, PokersInfo pokersInfo) {

        if (jiePaiSuggestions == null || jiePaiSuggestions.size() == 0)
            return null;

        List<ColoredPoker> others = null;

        List<Poker> selectedPokers = new ArrayList<Poker>(selectPokers.size());

        for (ColoredPoker coloredPoker : selectPokers) {
            selectedPokers.add(coloredPoker.getPoker());
        }

        for (Iterator<Hand> iterator = jiePaiSuggestions.iterator(); iterator.hasNext(); ) {

            Hand hand = iterator.next();
            Poker[] pokers = hand.getPokers();
            List<Poker> handPokers = new ArrayList<Poker>(Arrays.asList(pokers));

            if (handPokers.containsAll(selectedPokers)) {
                List<Poker> otherPokers = handPokers;

                for (Poker selectedPoker : selectedPokers) {
                    otherPokers.remove(selectedPoker);
                }

                List<ColoredPoker> humanPokers = pokersInfo.getShouPaiMap().get(Dir.Outside);
                List<ColoredPoker> humanPokersCopy = new ArrayList<ColoredPoker>(humanPokers);
                for (ColoredPoker coloredPoker : selectPokers) {
                    humanPokersCopy.remove(coloredPoker);
                }

                others = PokerUtils.mapColoredPokers(otherPokers, humanPokersCopy);

                iterator.remove();
            }

        }

        return others;
    }

    private static List<ColoredPoker> x3JiePaiCompletion(PokersInfo pokersInfo, List<ColoredPoker> selectPokers, Hand chuPaiHand) {
        // 如果点了一张比出牌大的牌，以x3补足
        // 如果点了一张比出牌小的牌，以炸弹补足
        // 如果点了大小王，以火箭补足
        if (selectPokers.size() != 1) return null;


        return null;
    }

    private static List<ColoredPoker> bombJiePaiCompletion(PokersInfo pokersInfo, List<ColoredPoker> selectPokers, Poker chuPaiHandKey) {
        // 如果点了一张比出牌大的牌（不包括大小王），以炸弹补足
        // 如果点了大小王，以火箭补足
        if (selectPokers.size() != 1) return null;

        ColoredPoker selectedColoredPoker = selectPokers.get(0);
        Poker selectedPoker = selectedColoredPoker.getPoker();

        List<Poker> shouPai = pokersInfo.getShouPai(Dir.Outside);
        Map<Poker, Integer> counter = PokerUtils.counter(shouPai);

        if (!selectedPoker.isBiggerThan(chuPaiHandKey))
            return null;

        if (selectedPoker == Poker.XiaoWang || selectedPoker == Poker.DaWang) {  // 如果点了大小王，以火箭补足
            return rockerCompletion(selectedPoker, shouPai);
        }
        else { // 如果点了一张比出牌大的牌（不包括大小王），以炸弹补足

            if (counter.get(selectedPoker) == 4) {

                List<ColoredPoker> coloredPokers = pokersInfo.getShouPaiMap().get(Dir.Outside);

                List<ColoredPoker> remain = new ArrayList<ColoredPoker>(coloredPokers);
                remain.remove(selectedColoredPoker);

                Poker[] otherThree = {selectedPoker, selectedPoker, selectedPoker};
                return PokerUtils.mapColoredPokers(otherThree, remain);
            }

            return null;

        }


    }

    private static List<ColoredPoker> rockerCompletion(Poker selectedPoker, List<Poker> shouPai) {
        if (RuleHelper.existRocket(shouPai)) {

            List<ColoredPoker> rocker = new ArrayList<ColoredPoker>(1);
            rocker.add(new ColoredPoker(selectedPoker != Poker.XiaoWang));
            return rocker;
        }

        return null;
    }

    private static List<ColoredPoker> pairJiePaiCompletion(PokersInfo pokersInfo, List<ColoredPoker> selectPokers, Poker chuPaiHandKey) {
        //如果点了一张跟出牌一样大的牌，无效
        //如果点了一张比出牌小的牌，看是否能做炸弹
        // 如果点了一张比出牌大的牌（不包括大小王），以对子补足
        // 如果点了大小王，以火箭补足

        if (selectPokers.size() != 1) return null;

        ColoredPoker selectedColoredPoker = selectPokers.get(0);
        Poker selectedPoker = selectedColoredPoker.getPoker();

        if (selectedPoker == chuPaiHandKey)   //如果点了一张跟出牌一样大的牌，无效
            return null;

        List<Poker> shouPai = pokersInfo.getShouPai(Dir.Outside);
        Map<Poker, Integer> counter = PokerUtils.counter(shouPai);

        if (selectedPoker.isSmallerThan(chuPaiHandKey)) { //如果点了一张比出牌小的牌，看是否能做炸弹

            if (counter.get(selectedPoker) == 4) {

                List<ColoredPoker> otherThree = new ArrayList<ColoredPoker>(3);

                for (ColoredPoker.Color color : ColoredPoker.Color.values()) {
                    if (color != selectedColoredPoker.getColor())
                        otherThree.add(new ColoredPoker(selectedPoker, color));
                }

                return otherThree;
            }

            return null;
        }
        else {

            if (selectedPoker == Poker.XiaoWang || selectedPoker == Poker.DaWang) {  // 如果点了大小王，以火箭补足
                return rockerCompletion(selectedPoker, shouPai);
            }
            else { // 如果点了一张比出牌大的牌（不包括大小王），以对子补足

                if (counter.get(selectedPoker) >= 2) {

                    List<ColoredPoker> coloredPokers = pokersInfo.getShouPaiMap().get(Dir.Outside);

                    List<ColoredPoker> remain = new ArrayList<ColoredPoker>(coloredPokers);
                    remain.remove(selectedColoredPoker);

                    return PokerUtils.mapColoredPokers(new Poker[]{selectedPoker}, remain);
                }

                return null;

            }

        }
    }

    private static List<ColoredPoker> singleJiePaiCompletion(List<Poker> shouPai, List<ColoredPoker> selectPokers, Poker chuPaiHandKey) {
        //如果点了一张比出牌小的牌，看是否能做炸弹

        if (selectPokers.size() != 1) return null;

        ColoredPoker coloredPoker = selectPokers.get(0);
        Poker selectedPoker = coloredPoker.getPoker();

        if (!selectedPoker.isSmallerThan(chuPaiHandKey)) return null;

        Map<Poker, Integer> counter = PokerUtils.counter(shouPai);
        if (counter.get(selectedPoker) == 4) {

            List<ColoredPoker> otherThree = new ArrayList<ColoredPoker>(3);

            for (ColoredPoker.Color color : ColoredPoker.Color.values()) {
                if (color != coloredPoker.getColor())
                    otherThree.add(new ColoredPoker(selectedPoker, color));
            }

            return otherThree;
        }

        return null;
    }

    /**
     * 反春
     *
     * @param loaderEvents
     */
    static boolean checkLoaderJiePai(List<History.Event> loaderEvents) {
        loaderEvents.remove(0);

        for (History.Event loaderEvent : loaderEvents) {
            if (!loaderEvent.isJiePai() || loaderEvent.getHand() != null)
                return false;
        }

        return true;
    }

    static List<List<ColoredPoker>> prepareSuggestions(Rule rule, Hand chuPai, List<Poker> shouPai, List<ColoredPoker> humanPokers) {

        List<Hand> suggestions = rule.requestSuggestion(chuPai, shouPai);

        List<List<ColoredPoker>> theSuggestions = new ArrayList<List<ColoredPoker>>(suggestions.size());


        for (Hand hand : suggestions) {
            Poker[] biggerHand = hand.getPokers();
            List<ColoredPoker> coloredPokers = PokerUtils.mapColoredPokers(biggerHand, humanPokers);
            theSuggestions.add(coloredPokers);
        }

        return theSuggestions;
    }

    static List<Dir> makeDirOrder(Dir startDir) {
        List<Dir> order = new ArrayList<Dir>(3);
        switch (startDir) {
            case Left:
                order.add(Dir.Left);
                order.add(Dir.Outside);
                order.add(Dir.Right);
                break;
            case Right:
                order.add(Dir.Right);
                order.add(Dir.Left);
                order.add(Dir.Outside);
                break;
            case Outside:
                order.add(Dir.Outside);
                order.add(Dir.Right);
                order.add(Dir.Left);
                break;
        }
        return order;
    }

    static Dir ensureRobLoaderStartDir() {
        Random random = new Random();
        Dir[] allDir = Dir.values();
        return allDir[random.nextInt(10000) % allDir.length];
    }

    static void fillInput(SettleTool.Input input, RuntimeData runtimeData, Round round) {
        Dir loaderDir = runtimeData.getLoaderDir();
        input.loaderWin = round.getWinDir() == loaderDir;

        input.loaderDir = loaderDir;

        input.baseScore = NormalSettleTool.BASE_SCORE;

        input.multiple = runtimeData.getHighestScore().getMultiple();

        History history = round.getHistory();
        List<History.Event> bombs = history.filterBombEvent();
        input.bombNum = bombs.size();

        input.daoGenFanMap = runtimeData.getDaoGenFanMap();

        List<History.Event> loaderEvents = history.filterByDir(loaderDir);
        if (input.loaderWin) {
            input.isSpring = (loaderEvents.size() == history.size());
        }
        else {
            input.isSpring = checkLoaderJiePai(loaderEvents);
        }
    }

}
