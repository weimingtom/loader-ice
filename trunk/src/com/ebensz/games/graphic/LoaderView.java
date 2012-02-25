package com.ebensz.games.graphic;

import android.content.Context;
import android.opengl.GLU;
import ice.engine.GameView;
import ice.graphic.GlRenderer;
import ice.graphic.projection.PerspectiveProjection;

import javax.microedition.khronos.opengles.GL11;

import static javax.microedition.khronos.opengles.GL11.*;

/**
 * User: jason
 * Date: 12-2-17
 * Time: 上午11:34
 */
public class LoaderView extends GameView {

    public LoaderView(Context context) {
        super(context, createRender());
    }

    private static GlRenderer createRender() {

        PerspectiveProjection projection = new PerspectiveProjection(new GLU(), 60);

        return new GlRenderer(projection) {

            @Override
            protected void onInit(GL11 gl) {
                gl.glEnable(GL_BLEND);
                gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

//                gl.glShadeModel(GL_SMOOTH);
//                gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

                /**不全局深度测试了，但会影响到其他开启局部深度测试的地方*/
                //gl.glEnable(GL_DEPTH_TEST);
                //gl.glDepthFunc(GL_LEQUAL);
                /**不全局深度测试了，但会影响到其他开启局部深度测试的地方*/
            }

        };

    }

}