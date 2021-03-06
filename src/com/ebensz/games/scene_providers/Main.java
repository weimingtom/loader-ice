package com.ebensz.games.scene_providers;

import com.ebensz.games.R;
import com.ebensz.games.model.Role.Role;
import com.ebensz.games.model.Role.RoleCenter;
import com.ebensz.games.scenes.MainScene;
import com.ebensz.games.scenes.dialogs.ServiceDialog;
import ice.animation.AlphaAnimation;
import ice.engine.App;
import ice.engine.EngineContext;
import ice.engine.Scene;
import ice.engine.SceneProvider;
import ice.node.widget.ButtonOverlay;
import ice.node.widget.RadioButtonOverlay;
import ice.node.widget.RadioGroup;
import ice.res.Res;

/**
 * User: Jason
 * Date: 12-2-4
 * Time: 下午3:09
 */
public class Main extends SceneProvider {
    public final static int LoaderModelWealth = 100000;
    public final static int NoLimitModelWealth = 500000;

    @Override
    public void onCreate() {

        super.onCreate();

        scene = new MainScene();
    }

    @Override
    protected void onResume() {
        super.onResume();

        entryLockStatus = validateEntryLock();

        MainScene mainScene = (MainScene) scene;

        mainScene.updateLockStates(entryLockStatus);

        bindButtonAction(entryLockStatus);

        ServiceDialog serviceDialog = mainScene.getServiceDialog();
        serviceDialog.startEntryAnimation(Res.getText(R.string.welcome));
    }

    @Override
    protected boolean isEntry() {
        return true;
    }

    @Override
    protected Scene getScene() {
        return scene;
    }

    private boolean[] validateEntryLock() {
        boolean[] lockStates = new boolean[3];

        RoleCenter roleCenter = RoleCenter.getInstance(Res.getContext());
        Role human = roleCenter.getHuman();

        int wealth = human.getWealth();

        //普通场
        lockStates[0] = false;

        //地主场
        lockStates[1] = wealth < LoaderModelWealth;

        //极限场
        lockStates[2] = wealth < NoLimitModelWealth;

        return lockStates;
    }

    private void bindButtonAction(final boolean[] lockStates) {

        RadioButtonOverlay.OnToggledListener onToggledListener = new RadioButtonOverlay.OnToggledListener() {

            @Override
            public void onToggled(RadioButtonOverlay radioButton) {
                handleToggleChange(radioButton);
            }
        };

        RadioGroup radioGroup = ((MainScene) scene).getRadioGroup();
        radioGroup.setOnToggledListener(onToggledListener);
    }

    private void hideConfirmBtn(ServiceDialog serviceDialog) {
        ButtonOverlay confirmButton = serviceDialog.getConfirmButton();
        if (confirmButton.isVisible()) {
            confirmButton.setVisible(false);
        }
    }

    private void handleToggleChange(RadioButtonOverlay radioButton) {
        final MainScene mainScene = (MainScene) scene;

        final ServiceDialog serviceDialog = mainScene.getServiceDialog();
        final ButtonOverlay confirmButton = serviceDialog.getConfirmButton();

        if (!confirmButton.isVisible())
            confirmButton.startAnimation(AlphaAnimation.createFadeIn(200));

        final RadioButtonOverlay normalEntry = mainScene.getNormalEntry();
        final RadioButtonOverlay loaderEntry = mainScene.getLoaderEntry();
        final RadioButtonOverlay superEntry = mainScene.getSuperEntry();

        ButtonOverlay.OnClickListener confirmBtnListener = null;

        if (radioButton == normalEntry) {
            serviceDialog.showMsg(Res.getText(R.string.normal_game_desc));

            if (entryLockStatus[0]) //地主场仍处于锁定状态
                hideConfirmBtn(serviceDialog);

            confirmBtnListener = new ButtonOverlay.OnClickListener() {
                @Override
                public void onClick(ButtonOverlay btn) {
                    App app = EngineContext.getInstance().getApp();
                    app.intent(GameEntry.class, GameEntry.Mode.Normal);
                }
            };
        }
        else if (radioButton == loaderEntry) {
            serviceDialog.showMsg(Res.getText(R.string.loader_game_desc));

            if (entryLockStatus[1])  //地主场仍处于锁定状态
                hideConfirmBtn(serviceDialog);

            confirmBtnListener = new ButtonOverlay.OnClickListener() {
                @Override
                public void onClick(ButtonOverlay btn) {
                    App app = EngineContext.getInstance().getApp();
                    app.intent(GameEntry.class, GameEntry.Mode.Loader);
                }
            };

        }
        else if (radioButton == superEntry) {
            serviceDialog.showMsg(Res.getText(R.string.super_game_desc));

            if (entryLockStatus[2])  //极限场仍处于锁定状态
                hideConfirmBtn(serviceDialog);

            confirmBtnListener = new ButtonOverlay.OnClickListener() {
                @Override
                public void onClick(ButtonOverlay btn) {
                    App app = EngineContext.getInstance().getApp();
                    app.intent(GameEntry.class, GameEntry.Mode.Super);
                }
            };
        }

        confirmButton.setOnClickListener(confirmBtnListener);
    }

    private boolean[] entryLockStatus;
    private Scene scene;
}
