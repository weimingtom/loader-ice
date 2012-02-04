package com.ebensz.games.scenes;

import com.ebensz.games.R;
import ice.engine.EngineContext;
import ice.engine.Scene;
import ice.node.widget.TextureGrid;

/**
 * User: Jason
 * Date: 12-2-4
 * Time: 下午3:10
 */
public class MainScene extends Scene {


    public MainScene() {
        int appWidth = EngineContext.getAppWidth();
        int appHeight = EngineContext.getAppHeight();

        //addChild(new TextureGrid(appWidth, appHeight, 0));
    }


}
