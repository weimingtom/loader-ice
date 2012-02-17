package com.ebensz.games.ui.widget;

import android.graphics.Bitmap;
import android.graphics.Color;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.Role.Role;
import com.ebensz.games.res.LoadRes;
import ice.node.OverlayParent;
import ice.node.widget.BitmapOverlay;
import ice.node.widget.TextOverlay;

import java.util.List;

/**
 * User: Mike.Hu
 * Date: 11-12-14
 * Time: 上午11:15
 */
public class RoleTile extends OverlayParent {

    public RoleTile(Dir dir, Role role) {

        List<Bitmap> headIcons = LoadRes.getHeadIcons();

        Bitmap head = headIcons.get(role.getIconIndex());
        float width = 200;
        headIconTile = new BitmapOverlay(head);

        nameTile = new TextOverlay(width, 23);
        nameTile.setText(role.getName(), Color.WHITE, true);
        nameTile.setPos(0, -40);

        wealthTile = new TextOverlay(width, 23);
        currentScore = role.getWealth();
        wealthTile.setText("" + currentScore, Color.WHITE, true);
        wealthTile.setPos(0, -70);

        switch (dir) {
            case Left:
                setPos(210, 650);
                break;
            case Right:
                setPos(800, 650);
                break;
            case Outside:
                setPos(70, 50);
        }

        addChildren(headIconTile, nameTile, wealthTile);
    }

    public void update(int winScore) { //TODO
        wealthTile.setText("" + (currentScore + winScore));
    }

    public void update(Role role) {
        List<Bitmap> headIcons = LoadRes.getHeadIcons();
        Bitmap head = headIcons.get(role.getIconIndex());

        headIconTile.setBitmap(head);
        nameTile.setText(role.getName());
        currentScore = role.getWealth();
        wealthTile.setText("" + currentScore);
    }

    private int currentScore;
    private BitmapOverlay headIconTile;
    private TextOverlay nameTile;
    private TextOverlay wealthTile;


}
