package com.kasetagen.game.bubblerunner.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kasetagen.engine.IDataSaver;
import com.kasetagen.engine.IGameProcessor;
import com.kasetagen.game.bubblerunner.BubbleRunnerGame;
import com.kasetagen.game.bubblerunner.data.GameOptions;

import com.kasetagen.game.bubblerunner.scene2d.BaseStage;
import com.kasetagen.game.bubblerunner.util.AnimationUtil;
import com.kasetagen.game.bubblerunner.util.AssetsUtil;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 7/20/14
 * Time: 8:32 PM
 */
public class BubbleRunnerOptionsMenu extends BaseBubbleRunnerScreen{

    Slider bgVolumeSet;
    Slider sfxVolumeSet;
    TextButton backToMainMenuButton;

    Label bgVolLbl;
    Label bgValue;
    Label sfxVolLbl;
    Label sfxValue;
    Label charSelect;
    Label charValue;
    TextButton charToggle;
    Animation character1Animation;
    Animation character2Animation;
    Image currentCharacter1Frame;
    Image currentCharacter2Frame;
    float timeElapsed = 0f;

    private String getFloatStringVal(float val){
        return Integer.toString((int) Math.floor(val * 10));
    }

    private IDataSaver bgDataSaver, sfxDataSaver, charDataSaver;
    public BubbleRunnerOptionsMenu(IGameProcessor delegate){
        super(delegate);

        stage = new BaseStage(delegate);

        float sfxVolValue = delegate.getStoredFloat(GameOptions.SFX_MUSIC_VOLUME_PREF_KEY);
        float bgVolValue = delegate.getStoredFloat(GameOptions.BG_MUSIC_VOLUME_PREF_KEY);
        String charStoredValue = delegate.getStoredString(GameOptions.CHARACTER_SELECT_KEY);
        if(charStoredValue == null || "".equals(charStoredValue.trim())){
            charStoredValue = AnimationUtil.CHARACTER_2;
        }
        Skin skin = gameProcessor.getAssetManager().get(AssetsUtil.DEFAULT_SKIN, AssetsUtil.SKIN);

        bgVolLbl = new Label("BKGD Volume: " , skin);
        bgValue = new Label(getFloatStringVal(bgVolValue), skin);
        sfxVolLbl = new Label("SFX Volume: " , skin);
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
                if(selectionIsCharacter2()){
                    charValue.setText(AnimationUtil.CHARACTER_1);
                }else{
                    charValue.setText(AnimationUtil.CHARACTER_2);
                }
                gameProcessor.saveGameData(charDataSaver);
            }
        });



        TextureAtlas atlas = gameProcessor.getAssetManager().get(AssetsUtil.ANIMATION_ATLAS, AssetsUtil.TEXTURE_ATLAS);

        character1Animation = new Animation(AnimationUtil.RUNNER_CYCLE_RATE, atlas.findRegions("player/Male_Run"));
        character2Animation = new Animation(AnimationUtil.RUNNER_CYCLE_RATE, atlas.findRegions("player/Female_Run"));

        currentCharacter1Frame = new Image(character1Animation.getKeyFrame(0f));
        currentCharacter1Frame.setSize(180f, 180f);

        currentCharacter2Frame = new Image(character2Animation.getKeyFrame(0f));
        currentCharacter2Frame.setSize(180f, 180f);

        currentCharacter1Frame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                charValue.setText(AnimationUtil.CHARACTER_1);
                gameProcessor.saveGameData(charDataSaver);
            }
        });

        currentCharacter2Frame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                charValue.setText(AnimationUtil.CHARACTER_2);
                gameProcessor.saveGameData(charDataSaver);
            }
        });


        backToMainMenuButton = new TextButton("Back to Main Menu", skin);
        backToMainMenuButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameProcessor.changeToScreen(BubbleRunnerGame.MENU);
            }
        });

        Table rootTable = new Table(skin);
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        Table playerTable = new Table(skin);
        float playerSelectSize = currentCharacter1Frame.getWidth()*2;
        playerTable.columnDefaults(0).center().height(playerSelectSize).width(playerSelectSize);
        playerTable.columnDefaults(1).center().height(playerSelectSize).width(playerSelectSize);
        playerTable.add(currentCharacter1Frame);
        playerTable.add(currentCharacter2Frame);
        rootTable.add(playerTable);

        float colWidth = stage.getWidth()/3;
        float colHeight = stage.getHeight()/5;

        Table volumeTable = new Table(skin);
        volumeTable.columnDefaults(0).right();
        volumeTable.columnDefaults(1).center().width(colWidth).height(colHeight);
        volumeTable.columnDefaults(2).expandX().left().padLeft(50);

        volumeTable.row();
        volumeTable.add(bgVolLbl).right();
        volumeTable.add(bgVolumeSet);
        volumeTable.add(bgValue);

        volumeTable.row();
        volumeTable.add(sfxVolLbl).right();
        volumeTable.add(sfxVolumeSet);
        volumeTable.add(sfxValue);

        volumeTable.row();
        volumeTable.add();
        volumeTable.add(backToMainMenuButton).height(colHeight/2).padBottom(20f);
        volumeTable.add();

        rootTable.row();
        rootTable.add(volumeTable);
    }

    private boolean selectionIsCharacter2(){
        return AnimationUtil.CHARACTER_2.equals(charValue.getText().toString());
    }

    @Override
    public void render(float delta) {
        Gdx.app.log("RENDER MENU", "Delta: " + delta);
        timeElapsed += delta;
        if(selectionIsCharacter2()){
            TextureRegion tr = character2Animation.getKeyFrame(timeElapsed, true);
            if(!tr.isFlipX())
                tr.flip(true, false);
            currentCharacter2Frame.setDrawable(new TextureRegionDrawable(tr));
            TextureRegion mTr = character1Animation.getKeyFrame(0f, false);
            if(!mTr.isFlipX()){
                mTr.flip(true, false);
            }
            currentCharacter1Frame.setDrawable(new TextureRegionDrawable(mTr));
        }else{
            TextureRegion tr = character1Animation.getKeyFrame(timeElapsed, true);
            if(!tr.isFlipX())
                tr.flip(true, false);
            currentCharacter1Frame.setDrawable(new TextureRegionDrawable(tr));
            TextureRegion wTr = character2Animation.getKeyFrame(0f, false);
            if(!wTr.isFlipX()){
                wTr.flip(true, false);
            }
            currentCharacter2Frame.setDrawable(new TextureRegionDrawable(wTr));
        }
        super.render(delta);
    }

    private void processRight(){

    }

    @Override
    public boolean keyDown(int keycode) {

        sfxValue.setColor(Color.CYAN);

        return super.keyDown(keycode);
    }
}
