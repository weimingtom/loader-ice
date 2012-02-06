package com.ebensz.games.graphic;

import android.content.Context;
import android.opengl.GLU;
import com.ebensz.games.Loader;
import ice.engine.EngineContext;
import ice.engine.GameView;
import ice.engine.GlRenderer;
import ice.graphic.projection.PerspectiveProjection;

import javax.microedition.khronos.opengles.GL11;

import static javax.microedition.khronos.opengles.GL10.*;

/**
 * User: Jason
 * Date: 12-2-4
 * Time: 下午3:07
 */
public class LoaderView extends GameView {


    public LoaderView(Context context) {
        super(context, createRender());
    }

    private static GlRenderer createRender() {

        PerspectiveProjection projection = new PerspectiveProjection(
                new GLU(),
                60,
                Loader.Z_NEAR,
                Loader.Z_FAR
        );

        return new GlRenderer(projection) {


            @Override
            protected void onInit(GL11 gl) {
                // gl.glEnable(GL_DEPTH_TEST);
                setupLight(gl);
                //setupMaterial(gl);
            }

        };

    }


    private static void setupLight(GL11 gl) {
        float[] lightAmbient = {0.5f, 0.5f, 0.5f, 1.0f}; // ['æmbiənt] 环境光
        float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};  //[di'fju:s] 漫射光
        float[] specularParams = {0.4f, 0.4f, 0.4f, 1.0f}; //镜面光设置

        int height = EngineContext.getInstance().getApp().getHeight();
        float[] lightPosition = {0, height >> 1, -100f, 1.0f};


        gl.glLightfv(GL_LIGHT1, GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GL_LIGHT1, GL_DIFFUSE, lightDiffuse, 0);
        gl.glLightfv(GL_LIGHT1, GL_SPECULAR, specularParams, 0);
        gl.glLightfv(GL_LIGHT1, GL_POSITION, lightPosition, 0);
        gl.glEnable(GL_LIGHT1);
        gl.glEnable(GL_LIGHTING);

        gl.glEnable(GL_COLOR_MATERIAL);
        //gl.glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);
    }

}
