package com.ebensz.games.debug;

import com.ebensz.games.model.Dir;
import com.ebensz.games.model.poker.Poker;
import ice.util.NormalEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Mike.Hu
 * Date: 11-12-29
 * Time: 下午5:59
 */
public class FaPaiCheatBean extends NormalEntity {

    public FaPaiCheatBean() {

        faPaiMap = new HashMap<Dir, List<Poker>>();

        for (Dir dir : Dir.values())
            faPaiMap.put(dir, new ArrayList<Poker>());
    }

    @Override
    public void setAttributeByTag(String tag, String value) {
        super.setAttributeByTag(tag, value);

        if (tag.equalsIgnoreCase("poker")) {
            Poker poker = parse(value);
            if (poker != null)
                faPaiMap.get(focusDir).add(poker);
        }
    }

    private Poker parse(String value) {

        for (Poker poker : Poker.values()) {
            if (value.toLowerCase().equals(poker.toString().toLowerCase()))
                return poker;
        }

        return null;
    }

    @Override
    public void setAttributeByAtt(String name, String value) {
        super.setAttributeByAtt(name, value);

        if (name.equalsIgnoreCase("dir")) {
            String theValue = value.toLowerCase();
            for (Dir dir : Dir.values()) {
                if (theValue.equalsIgnoreCase(dir.toString())) {
                    focusDir = dir;
                    break;
                }
            }
        }
    }

    public Map<Dir, List<Poker>> getFaPaiMap() {
        for (Dir dir : Dir.values()) {
            if (faPaiMap.get(dir).size() == 0)
                faPaiMap.remove(dir);
        }
        return faPaiMap;
    }

    private Map<Dir, List<Poker>> faPaiMap;
    private Dir focusDir;
}
