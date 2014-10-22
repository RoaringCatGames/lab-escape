package com.kasetagen.game.bubblerunner.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.kasetagen.game.bubblerunner.BubbleRunnerGame;
import com.kasetagen.game.bubblerunner.data.GameOptions;
import com.kasetagen.game.bubblerunner.data.IDataSaver;
import com.kasetagen.game.bubblerunner.delegate.IGameProcessor;
import com.kasetagen.game.bubblerunner.scene2d.BaseStage;
import com.kasetagen.game.bubblerunner.scene2d.actor.AnimatedActor;
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
    Label charSelect;
    Label charValue;
    TextButton charToggle;
    Animation womanAnimation;
    Animation manAnimation;
    Image currentFrame;
    float timeElapsed = 0f;

    private String getFloatStringVal(float val){
        return Integer.toString((int) Math.floor(val * 10));
    }

    private IDataSaver bgDataSaver, sfxDataSaver, charDataSaver;
    public BubbleRunnerOptionsMenu(IGameProcessor delegate){
        super(delegate);

        stage = new BaseStage();

        float sfxVolValue = delegate.getStoredFloat(GameOptions.SFX_MUSIC_VOLUME_PREF_KEY);
        float bgVolValue = delegate.getStoredFloat(GameOptions.BG_MUSIC_VOLUME_PREF_KEY);
        String charStoredValue = delegate.getStoredString(GameOptions.CHARACTER_SELECT_KEY);
        if(charStoredValue == null || "".equals(charStoredValue.trim())){
            charStoredValue = "Woman";
        }
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

        charSelect = new Label("Select Character: ", skin);
        charValue = new Label(charStoredValue, skin);
        charDataSaver = new IDataSaver() {
            @Override
            public void updatePreferences(Preferences prefs) {
                prefs.putString(GameOptions.CHARACTER_SELECT_KEY, charValue.getText().toString());
            }
        };

        charToggle = new TextButton("Switch Character", skin);
        charToggle.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(selectionIsWoman()){
                    charValue.setText("Man");
                }else{
                    charValue.setText("Woman");
                }
                gameProcessor.saveGameData(charDataSaver);
            }
        });

        TextureAtlas atlas = gameProcessor.getAssetManager().get(AssetsUtil.ANIMATION_ATLAS, AssetsUtil.TEXTURE_ATLAS);

        manAnimation = new Animation(1f/8f, atlas.findRegions("player/Male_Run"));
        womanAnimation = new Animation(1f/8f, atlas.findRegions("player/Female_Run"));

        currentFrame = new Image(manAnimation.getKeyFrame(0f));
        currentFrame.setSize(180f, 180f);

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

        table.add(charSelect).right();
        table.add(currentFrame);
        table.add(charToggle);

        table.row();
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

    private boolean selectionIsWoman(){
        return "Woman".equals(charValue.getText().toString());
    }

    @Override
    public void render(float delta) {
        Gdx.app.log("RENDER MENU", "Delta: " + delta);
        timeElapsed += delta;
        if(selectionIsWoman()){
            TextureRegion tr = womanAnimation.getKeyFrame(timeElapsed, true);
            if(!tr.isFlipX())
                tr.flip(true, false);
            currentFrame.setDrawable(new TextureRegionDrawable(tr));
        }else{
            TextureRegion tr = manAnimation.getKeyFrame(timeElapsed, true);
            if(!tr.isFlipX())
                tr.flip(true, false);
            currentFrame.setDrawable(new TextureRegionDrawable(tr));
        }
        super.render(delta);
}
}
