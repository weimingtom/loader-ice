package com.ebensz.games.model.Role;

import com.ebensz.games.model.Dir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Mike.Hu
 * Date: 11-11-1
 * Time: 下午4:17
 */
public class NpcChooser {

    public static Map<Dir, Role> choose(Role human, List<Role> allNpc) {
        Map<Dir, Role> otherTwoNpc = new HashMap<Dir, Role>(2);

        otherTwoNpc.put(Dir.Left, allNpc.get(0));
        otherTwoNpc.put(Dir.Right, allNpc.get(1));

        return otherTwoNpc;
    }

    public static Map<Dir, Role> replaceNpcs(Map<Dir, Role> loseNpcs, RoleCenter roleCenter) {
        List<Role> availableNpcs = new ArrayList<Role>(roleCenter.getAllNpc());
        availableNpcs.removeAll(loseNpcs.values());

        Map<Dir, Role> newNpcs = new HashMap<Dir, Role>(loseNpcs.size());
        int index = 0;
        for (Dir dir : loseNpcs.keySet()) {
            Role loseRole = loseNpcs.get(dir);

            loseRole.setWealth(Role.ROLE_LOSE_WEALTH);
            roleCenter.update(loseRole);

            newNpcs.put(dir, availableNpcs.get(index++));
        }

        return newNpcs;
    }
}
