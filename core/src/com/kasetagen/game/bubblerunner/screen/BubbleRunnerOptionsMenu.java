package com.kasetagen.game.bubblerunner.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.kasetagen.game.bubblerunner.BubbleRunnerGame;
import com.kasetagen.game.bubblerunner.data.GameOptions;
import com.kasetagen.game.bubblerunner.data.IDataSaver;
import com.kasetagen.game.bubblerunner.delegate.IGameProcessor;
import com.kasetagen.game.bubblerunner.scene2d.BaseStage;
import com.kasetagen.game.bubblerunner.util.AssetsUtil;
import com.kasetagen.game.bubblerunner.util.ViewportUtil;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 7/20/14
 * Time: 8:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class BubbleRunnerOptionsMenu extends BaseBubbleRunnerScreen{

    Slider bgVolumeSet;
    Slider sfxVolumeSet;
    TextButton backToMainMenuButton;

    private IDataSaver bgDataSaver, sfxDataSaver;
    public BubbleRunnerOptionsMenu(IGameProcessor delegate){
        super(delegate);

        stage = new BaseStage();

        Skin skin = gameProcessor.getAssetManager().get(AssetsUtil.DEFAULT_SKIN, AssetsUtil.SKIN);

        bgVolumeSet = new Slider(0f, 1f, 0.1f, false, skin); //new Slider(0f, 1f, 0.1f, false, sliderStyle);
        bgVolumeSet.setPosition(ViewportUtil.VP_WIDTH / 3, ViewportUtil.VP_HEIGHT*(5f/8f));
        bgVolumeSet.setValue(delegate.getStoredFloat(GameOptions.BG_MUSIC_VOLUME_PREF_KEY));

        bgDataSaver = new IDataSaver() {
            @Override
            public void updatePreferences(Preferences prefs) {
                prefs.putFloat(GameOptions.BG_MUSIC_VOLUME_PREF_KEY, bgVolumeSet.getValue());
            }
        };

        bgVolumeSet.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("UITest", "slider: " + bgVolumeSet.getValue());
                gameProcessor.saveGameData(bgDataSaver);
            }
        });

        stage.addActor(bgVolumeSet);



        sfxVolumeSet = new Slider(0f, 1f, 0.1f, false, skin); //new Slider(0f, 1f, 0.1f, false, sliderStyle);
        sfxVolumeSet.setPosition(ViewportUtil.VP_WIDTH / 3, ViewportUtil.VP_HEIGHT*(3f/8f));
        sfxVolumeSet.setValue(delegate.getStoredFloat(GameOptions.SFX_MUSIC_VOLUME_PREF_KEY));

        sfxDataSaver = new IDataSaver() {
            @Override
            public void updatePreferences(Preferences prefs) {
                prefs.putFloat(GameOptions.SFX_MUSIC_VOLUME_PREF_KEY, sfxVolumeSet.getValue());
            }
        };

        sfxVolumeSet.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("UITest", "slider: " + sfxVolumeSet.getValue());
                gameProcessor.saveGameData(sfxDataSaver);
            }
        });

        stage.addActor(sfxVolumeSet);


        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = gameProcessor.getAssetManager().get(AssetsUtil.COURIER_FONT_32, AssetsUtil.BITMAP_FONT);
        style.fontColor =  Color.CYAN;
        style.overFontColor = Color.RED;
        style.downFontColor = Color.GRAY;
        float fontScale = 2f;
        style.font.setScale(fontScale);

        backToMainMenuButton = new TextButton("Back to Main Menu", skin);
        backToMainMenuButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameProcessor.changeToScreen(BubbleRunnerGame.MENU);
            }
        });
        backToMainMenuButton.setPosition(((stage.getWidth()/4) * 3) - backToMainMenuButton.getWidth()/2, (stage.getHeight()/4));

        stage.addActor(backToMainMenuButton);

    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }


}
