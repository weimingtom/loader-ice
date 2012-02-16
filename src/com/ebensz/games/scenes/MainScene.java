package com.ebensz.games.scenes;

/**
 * User: Jason
 * Date: 12-2-4
 * Time: 下午3:10
 */
public class MainScene extends MainSceneBase {

    public void updateLockStates(boolean[] lockStates) {
        normalEntry.setLock(lockStates[0]);
        loaderEntry.setLock(lockStates[1]);
        superEntry.setLock(lockStates[2]);
    }

}
