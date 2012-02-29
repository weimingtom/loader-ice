package com.ebensz.games.graphic;

import android.opengl.GLU;
import ice.graphic.GlRenderer;
import ice.graphic.projection.PerspectiveProjection;

import javax.microedition.khronos.opengles.GL11;

import static javax.microedition.khronos.opengles.GL11.*;

/**
 * User: jason
 * Date: 12-2-29
 * Time: 上午9:25
 */
public class LoaderRenderer extends GlRenderer {

    public LoaderRenderer() {
        super(new PerspectiveProjection(new GLU(), 60));
    }

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


}
