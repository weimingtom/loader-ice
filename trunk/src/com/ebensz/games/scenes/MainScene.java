package com.ebensz.games.scenes;

import android.graphics.Bitmap;
import android.graphics.Color;
import com.ebensz.games.R;
import com.ebensz.games.scenes.dialogs.ServiceDialog;
import ice.engine.Scene;
import ice.node.widget.RadioButton;
import ice.node.widget.RadioGroup;
import ice.node.widget.TextGrid;
import ice.node.widget.TextureGrid;
import ice.res.Res;

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
