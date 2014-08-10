package com.kasetagen.game.bubblerunner.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
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

    Label bgValue;
    Label sfxValue;


    private String getFloatStringVal(float val){
        return Integer.toString((int) Math.floor(val * 10));
    }

    private IDataSaver bgDataSaver, sfxDataSaver;
    public BubbleRunnerOptionsMenu(IGameProcessor delegate){
        super(delegate);

        stage = new BaseStage();

        float sfxVolValue = delegate.getStoredFloat(GameOptions.SFX_MUSIC_VOLUME_PREF_KEY);
        float bgVolValue = delegate.getStoredFloat(GameOptions.BG_MUSIC_VOLUME_PREF_KEY);
        Skin skin = gameProcessor.getAssetManager().get(AssetsUtil.DEFAULT_SKIN, AssetsUtil.SKIN);

        Label bgVolLbl = new Label("Background Volume: " , skin);
        bgValue = new Label(getFloatStringVal(bgVolValue), skin);
        Label sfxVolLbl = new Label("SFX Volume: " , skin);
        sfxValue = new Label(getFloatStringVal(sfxVolValue), skin);

        bgVolumeSet = new Slider(0f, 1f, 0.1f, false, skin);
        bgVolumeSet.setValue(bgVolValue);

        bgDataSaver = new IDataSaver() {
            @Override
            public void updatePreferences(Preferences prefs) {
                prefs.putFloat(GameOptions.BG_MUSIC_VOLUME_PREF_KEY, bgVolumeSet.getValue());

                bgValue.setText(getFloatStringVal(bgVolumeSet.getValue()));
            }
        };

        bgVolumeSet.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("UITest", "slider: " + bgVolumeSet.getValue());
                gameProcessor.saveGameData(bgDataSaver);
            }
        });


        sfxVolumeSet = new Slider(0f, 1f, 0.1f, false, skin);
        sfxVolumeSet.setValue(sfxVolValue);

        sfxDataSaver = new IDataSaver() {
            @Override
            public void updatePreferences(Preferences prefs) {
                prefs.putFloat(GameOptions.SFX_MUSIC_VOLUME_PREF_KEY, sfxVolumeSet.getValue());
                sfxValue.setText(getFloatStringVal(sfxVolumeSet.getValue()));
            }
        };

        sfxVolumeSet.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("UITest", "slider: " + sfxVolumeSet.getValue());
                gameProcessor.saveGameData(sfxDataSaver);
            }
        });


//        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
//        style.font = gameProcessor.getAssetManager().get(AssetsUtil.COURIER_FONT_32, AssetsUtil.BITMAP_FONT);
//        style.fontColor =  Color.CYAN;
//        style.overFontColor = Color.RED;
//        style.downFontColor = Color.GRAY;
//        float fontScale = 2f;
//        style.font.setScale(fontScale);

        backToMainMenuButton = new TextButton("Back to Main Menu", skin);
        backToMainMenuButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameProcessor.changeToScreen(BubbleRunnerGame.MENU);
            }
        });

        float colWidth = stage.getWidth()/3;
        float colHeight = stage.getHeight()/5;
        Table table = new Table(skin);
        table.setFillParent(true);
        stage.addActor(table);
        table.columnDefaults(0).right();
        table.columnDefaults(1).center().width(colWidth).height(colHeight);
        table.columnDefaults(2).expandX().left().padLeft(50);

        table.add(bgVolLbl).right();
        table.add(bgVolumeSet);
        table.add(bgValue);

        table.row();
        table.add(sfxVolLbl).right();
        table.add(sfxVolumeSet);
        table.add(sfxValue);

        table.row();
        table.add();
        table.add(backToMainMenuButton).height(colHeight/2);
        table.add();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }
}
