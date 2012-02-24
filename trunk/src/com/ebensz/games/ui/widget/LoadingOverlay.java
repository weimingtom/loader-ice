package com.ebensz.games.ui.widget;

import com.ebensz.games.R;
import ice.engine.EngineContext;
import ice.graphic.gl_status.ScissorController;
import ice.model.Point3F;
import ice.node.OverlayParent;
import ice.node.widget.BitmapOverlay;

/**
 * User: jason
 * Date: 12-2-22
 * Time: 下午2:49
 */
public class LoadingOverlay extends OverlayParent {


    public LoadingOverlay() {

        BitmapOverlay bg = new BitmapOverlay(R.drawable.progress_total);

        progressOverlay = new BitmapOverlay(
                bg.getWidth() * 0.95f,
                bg.getHeight() * 0.5f
        );
        progressOverlay.setBitmap(R.drawable.progress_now);

        controller = new ScissorController();
        progressOverlay.addGlStatusController(controller);

        addChildren(bg, progressOverlay);

        setPos(EngineContext.getAppWidth() / 2, EngineContext.getAppHeight() / 2);
    }


    public void updateProgress(float progress) {
        if (progress >= 1) {
            removeGlStatusController(controller);
        }
        else {
            float width = progressOverlay.getWidth();
            float height = progressOverlay.getHeight();

            Point3F absolutePos = progressOverlay.getAbsolutePos();

            controller.set(
                    (int) (absolutePos.x - width / 2),
                    (int) (absolutePos.y - height / 2),
                    (int) (progress * width),
                    (int) height
            );
        }
    }

    private ScissorController controller;
    private BitmapOverlay progressOverlay;
}
