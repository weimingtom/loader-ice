package com.ebensz.games.scene_providers;

import com.ebensz.games.scenes.LoadingScene;
import ice.animation.Interpolator.AccelerateInterpolator;
import ice.animation.Interpolator.Interpolator;
import ice.engine.App;
import ice.engine.EngineContext;
import ice.engine.Scene;
import ice.engine.SceneProvider;

/**
 * User: Mike.Hu
 * Date: 12-1-12
 * Time: 上午11:51
 */
public class Loading extends SceneProvider {
    public static final long LOADING_TIME = 1000;

    @Override
    public void onCreate() {
        super.onCreate();

        scene = new LoadingScene();
    }

    @Override
    protected void onResume() {
        super.onResume();

        new LoadingThread(LOADING_TIME).start();
    }

    @Override
    protected Scene getScene() {
        return scene;
    }

    private class LoadingThread extends Thread {

        private LoadingThread(long during) {
            this.during = during;
            interpolator = new AccelerateInterpolator();
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();

            while (true) {

                long currentTime = System.currentTimeMillis();

                long time = currentTime - startTime;

                if (time > during) {
                    ((LoadingScene) scene).updateProgress(1);
                    break;
                }
                else {
                    float normalizedTime = ((float) time) / (float) during;

                    //根据归一化时间调整时间插值
                    float interpolatedTime = interpolator.getInterpolation(normalizedTime);

                    ((LoadingScene) scene).updateProgress(interpolatedTime);
                }
            }

            App app = EngineContext.getInstance().getApp();
            app.intent(Main.class);
        }

        /**
         * 加载游戏资源
         */
        private void loadResources() {
//        Context context = app.getContext();
//        MjResources.build(context);
        }

        private long during;
        private Interpolator interpolator;
    }

    private Scene scene;
}
