package com.ebensz.games.ui.widget;

import android.graphics.Bitmap;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.Role.Role;
import com.ebensz.games.res.LoadRes;
import ice.node.Drawable;
import ice.node.DrawableParent;
import ice.node.widget.TextGrid;
import ice.node.widget.TextureGrid;

import java.util.List;

/**
 * User: Mike.Hu
 * Date: 11-12-14
 * Time: 上午11:15
 */
public class RoleTile extends DrawableParent<Drawable> {

    public RoleTile(Dir dir, Role role) {

        List<Bitmap> headIcons = LoadRes.getHeadIcons();

        Bitmap head = headIcons.get(role.getIconIndex());
        float width = getWidth();
        headIconTile = new TextureGrid(head);
        headIconTile.setPos((width - head.getWidth()) / 2, 0);

        nameTile = new TextGrid(width, 23);
        //nameTile.setText(role.getName(), Color.WHITE, true);
        nameTile.setPos(0, 60);

        wealthTile = new TextGrid(width, 23);
        currentScore = role.getWealth();
        //wealthTile.setText("" + currentScore, Color.WHITE, true);
        wealthTile.setPos(0, 90);

        switch (dir) {
            case Left:
                setPos(100, 50);
                break;
            case Right:
                setPos(800, 50);
                break;
            case Outside:
                setPos(70, 650);
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
    private TextureGrid headIconTile;
    private TextGrid nameTile;
    private TextGrid wealthTile;


}
