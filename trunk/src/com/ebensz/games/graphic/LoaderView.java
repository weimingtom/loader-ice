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

        PerspectiveProjection projection = new PerspectiveProjection(new GLU(), 60);

        return new GlRenderer(projection);
    }


}
