package com.ebensz.games.logic;


import android.os.Environment;
import com.ebensz.games.debug.FaPaiCheatBean;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.poker.ColoredPoker;
import com.ebensz.games.model.poker.Poker;
import ice.util.XML2BeansUtil;

import java.io.*;
import java.util.*;

/**
 * User: Mike.Hu
 * Date: 11-11-17
 * Time: 下午5:59
 */
public class FaPaiUnit {

    public static final String CHEATS = "cheats.xml";

    public static List<ColoredPoker> getRandomPoker() {
        List<ColoredPoker> coloredPokers = new ArrayList<ColoredPoker>(54);
        Poker[] allPokers = Poker.values();

        for (int i = 0; i < 13; i++) {

            for (ColoredPoker.Color color : ColoredPoker.Color.values()) {
                coloredPokers.add(new ColoredPoker(allPokers[i], color));
            }

        }

        coloredPokers.add(new ColoredPoker(true));
        coloredPokers.add(new ColoredPoker(false));

        Collections.shuffle(coloredPokers);

        return coloredPokers;
    }

    public static List<ColoredPoker> getDebugPoker(Dir startDir) {

        List<ColoredPoker> allPokers = getRandomPoker();

        List<ColoredPoker> lastPokers = new ArrayList<ColoredPoker>(54);

        Map<Dir, List<Poker>> dirPokers = FaPaiUnit.getCheats();

        List<ColoredPoker> cheatsPokers = new ArrayList<ColoredPoker>();
        for (List<Poker> pokers : dirPokers.values()) {
            for (Poker poker : pokers) {
                for (int j = 0; j < allPokers.size(); j++) {
                    ColoredPoker coloredPoker = allPokers.get(j);
                    if (poker.ordinal() == coloredPoker.getPoker().ordinal()) {
                        cheatsPokers.add(coloredPoker);
                        allPokers.remove(coloredPoker);
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < 51; i++) {
            if (dirPokers.containsKey(startDir)) {
                List<Poker> thisDirPokers = dirPokers.get(startDir);
                if (thisDirPokers.size() > 0) {
                    int j = 0;
                    for (; j < cheatsPokers.size() - 1 && cheatsPokers.get(j).getPoker().ordinal() != thisDirPokers.get(0).ordinal(); j++) {
                    }
                    thisDirPokers.remove(0);
                    lastPokers.add(cheatsPokers.remove(j));
                }
            }
            else {
                lastPokers.add(allPokers.remove(0));
            }
            startDir = startDir.xiaJia();
        }
        lastPokers.addAll(allPokers);

        return lastPokers;
    }

    private static Map<Dir, List<Poker>> getCheats() {

        Map<Dir, List<Poker>> dirPokers = new HashMap<Dir, List<Poker>>();

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File storageDir = Environment.getExternalStorageDirectory();
            File storePath = new File(storageDir, CHEATS);

            if (storePath.exists()) {

                XML2BeansUtil xml2BeansUtil = new XML2BeansUtil();
                FaPaiCheatBean faPaiCheatBean = new FaPaiCheatBean();
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(storePath);
                    xml2BeansUtil.fill(faPaiCheatBean, inputStream);
                    dirPokers = faPaiCheatBean.getFaPaiMap();
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        if (inputStream != null)
                            inputStream.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        return dirPokers;
    }
}
