package com.ebensz.games.scenes;

import com.ebensz.games.R;
import com.ebensz.games.ui.widget.LoadingOverlay;
import ice.engine.Scene;
import ice.node.widget.BitmapOverlay;

/**
 * User: Mike.Hu
 * Date: 12-1-12
 * Time: 上午11:51
 */
public class LoadingScene extends Scene {

    public LoadingScene() {
        bg = new BitmapOverlay(R.drawable.bg);

        bg.setPos(getWidth() / 2, getHeight() / 2);

        loadingOverlay = new LoadingOverlay();

        addChildren(bg, loadingOverlay);
    }

    public void updateProgress(float progress) {
        loadingOverlay.updateProgress(progress);
    }

    private LoadingOverlay loadingOverlay;
    protected BitmapOverlay bg;


}
