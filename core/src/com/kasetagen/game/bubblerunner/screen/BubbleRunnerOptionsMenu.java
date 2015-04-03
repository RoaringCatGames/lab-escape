package com.kasetagen.game.bubblerunner.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
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

    private static final int MIN_INDEX = 0;
    private static final int MAX_INDEX = 3; //Zero-based index, with 4 controls
    private static final float VOLUME_INCREMENT = 0.1f;

    private static final float CHAR_SELECT_SIZE = 720f/3f;

    //CharSelect
    Label charSelect;
    Label charValue;
    TextButton charToggle;
    Animation character1Animation;
    Animation character2Animation;
    Image currentCharacter1Frame;
    Image currentCharacter2Frame;
    float timeElapsed = 0f;

    //BG Volume
    Label bgVolLbl;
    Slider bgVolumeSet;
    Label bgValue;

    //SFX Volume
    Label sfxVolLbl;
    Slider sfxVolumeSet;
    Label sfxValue;
    TextButton backToMainMenuButton;

    Table rootTable;

    Sound sfx;

    int focusIndex = 0;

    private String getFloatStringVal(float val){
        return Integer.toString((int) Math.floor(val * 10));
    }

    private IDataSaver bgDataSaver, sfxDataSaver, charDataSaver;
    public BubbleRunnerOptionsMenu(IGameProcessor delegate){
        super(delegate);

        stage = new BaseStage(delegate);

        float sfxVolValue = delegate.getStoredFloat(GameOptions.SFX_MUSIC_VOLUME_PREF_KEY);
        sfxVolValue = sfxVolValue < 0f ? 0f : sfxVolValue;
        float bgVolValue = delegate.getStoredFloat(GameOptions.BG_MUSIC_VOLUME_PREF_KEY);
        bgVolValue = bgVolValue < 0f ? 0f : bgVolValue;

        String charStoredValue = delegate.getStoredString(GameOptions.CHARACTER_SELECT_KEY);
        if(charStoredValue == null || "".equals(charStoredValue.trim())){
            charStoredValue = AnimationUtil.CHARACTER_2;
        }
        Skin skin = gameProcessor.getAssetManager().get(AssetsUtil.DEFAULT_SKIN, AssetsUtil.SKIN);

        sfx = gameProcessor.getAssetManager().get(AssetsUtil.ZAP_SOUND, AssetsUtil.SOUND);
        /*
         * Initialize BG controls
         */
        bgVolLbl = new Label("BKGD Volume: " , skin);
        bgValue = new Label(getFloatStringVal(bgVolValue), skin);
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
                gameProcessor.setBGMusicVolume(bgVolumeSet.getValue());
            }
        });


        /*
         * Initialize SFX controls
         */
        sfxVolLbl = new Label("SFX Volume: " , skin);
        sfxValue = new Label(getFloatStringVal(sfxVolValue), skin);
        sfxVolumeSet = new Slider(0f, 1f, 0.1f, false, skin);
        sfxVolumeSet.setValue(sfxVolValue);

        sfxDataSaver = new IDataSaver() {
            @Override
            public void updatePreferences(Preferences prefs) {
                prefs.putFloat(GameOptions.SFX_MUSIC_VOLUME_PREF_KEY, sfxVolumeSet.getValue());
                sfxValue.setText(getFloatStringVal(sfxVolumeSet.getValue()));
            }
        };

        //sfxVolumeSet.getStyle().
        sfxVolumeSet.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("UITest", "slider: " + sfxVolumeSet.getValue());
                gameProcessor.saveGameData(sfxDataSaver);
                sfx.play(sfxVolumeSet.getValue());
            }
        });

        /*
         * Initialize Character Controls
         */
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
        currentCharacter1Frame.setSize(CHAR_SELECT_SIZE, CHAR_SELECT_SIZE);

        currentCharacter2Frame = new Image(character2Animation.getKeyFrame(0f));
        currentCharacter2Frame.setSize(CHAR_SELECT_SIZE, CHAR_SELECT_SIZE);

        currentCharacter1Frame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectCharacter(true);
            }
        });

        currentCharacter2Frame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectCharacter(false);
            }
        });


        backToMainMenuButton = new TextButton("Back to Main Menu", skin);
        backToMainMenuButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                returnButtonPressed();
            }
        });


        float textRowHeight = stage.getHeight()/7;

        rootTable = new Table(skin);
        rootTable.setFillParent(true);
        rootTable.center();
        stage.addActor(rootTable);

        Table playerTable = new Table(skin);
        playerTable.columnDefaults(1).center().height(CHAR_SELECT_SIZE).width(CHAR_SELECT_SIZE);
        playerTable.columnDefaults(2).expandX();
        playerTable.columnDefaults(3).center().height(CHAR_SELECT_SIZE).width(CHAR_SELECT_SIZE);


        playerTable.row().colspan(5).height(textRowHeight);
        playerTable.add(charSelect).expandX();

        playerTable.row();
        playerTable.add(currentCharacter1Frame).maxSize(CHAR_SELECT_SIZE).prefSize(CHAR_SELECT_SIZE).minSize(CHAR_SELECT_SIZE);
        playerTable.add();
        playerTable.add(currentCharacter2Frame).maxSize(CHAR_SELECT_SIZE).prefSize(CHAR_SELECT_SIZE).minSize(CHAR_SELECT_SIZE);;

        playerTable.row().colspan(5).height(textRowHeight);
        playerTable.add(charValue);

        rootTable.add(playerTable).expandX();



        float centerColWidth = stage.getWidth()/2f;
        Table volumeTable = new Table(skin);
        volumeTable.columnDefaults(0).right();
        volumeTable.columnDefaults(1).center().width(centerColWidth).height(textRowHeight);
        volumeTable.columnDefaults(2).expandX().left().padLeft(50).width(50);

        volumeTable.row();
        volumeTable.add(bgVolLbl).right();
        volumeTable.add(bgVolumeSet);
        volumeTable.add(bgValue);

        volumeTable.row();
        volumeTable.add(sfxVolLbl).right();
        volumeTable.add(sfxVolumeSet);
        volumeTable.add(sfxValue);

        volumeTable.row().colspan(3);
        volumeTable.add(backToMainMenuButton).center();

        rootTable.row();
        rootTable.add(volumeTable).expandX();

        setDefaultColors();
        selectMenuItem(focusIndex);
    }



    @Override
    public void render(float delta) {
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

    @Override
    public void show() {
        super.show();    //To change body of overridden methods use File | Settings | File Templates.

        Gdx.app.log("OPTIONS", "Show Called");

        focusIndex = 0;
        setDefaultColors();
        selectMenuItem(focusIndex);
    }

    @Override
    public boolean keyDown(int keycode) {

        if(keycode == Input.Keys.DOWN){
            if(focusIndex < MAX_INDEX){
                focusIndex++;
            }
            selectMenuItem(focusIndex);
        }else if(keycode == Input.Keys.UP){
            if(focusIndex > MIN_INDEX){
                focusIndex--;
            }
            selectMenuItem(focusIndex);
        }else if(keycode == Input.Keys.RIGHT){
            //Adjust value right
            handleRightAdjust(focusIndex);
        }else if(keycode == Input.Keys.LEFT){
            //Adjust value left
            handleLeftAdjust(focusIndex);
        }else if(keycode == Input.Keys.SPACE || keycode == Input.Keys.ENTER){
            //if return button go home.
            returnButtonPressed();
        }

        return true;
    }

    private void setDefaultColors(){
        charSelect.setColor(Color.YELLOW);
        charValue.setColor(Color.YELLOW);
        charToggle.setColor(Color.YELLOW);
        bgVolLbl.setColor(Color.YELLOW);
        bgValue.setColor(Color.YELLOW);
        sfxVolLbl.setColor(Color.YELLOW);
        sfxValue.setColor(Color.YELLOW);
        backToMainMenuButton.setColor(Color.YELLOW);
    }

    private void selectCharacter(boolean isLeft){
        if(isLeft){
            charValue.setText(AnimationUtil.CHARACTER_1);
        }else{
            charValue.setText(AnimationUtil.CHARACTER_2);
        }

        gameProcessor.saveGameData(charDataSaver);
    }

    private boolean selectionIsCharacter2(){
        return AnimationUtil.CHARACTER_2.equals(charValue.getText().toString());
    }

    public void returnButtonPressed(){
        focusIndex = 0;
        gameProcessor.changeToScreen(BubbleRunnerGame.MENU);
    }

    public void selectMenuItem(int index){
        setDefaultColors();
        switch(index){
            case 0:
                charSelect.setColor(Color.RED);
                charValue.setColor(Color.RED);
                charToggle.setColor(Color.RED);
                break;
            case 1:
                bgVolLbl.setColor(Color.RED);
                bgValue.setColor(Color.RED);
                break;
            case 2:
                sfxVolLbl.setColor(Color.RED);
                sfxValue.setColor(Color.RED);
                break;
            case 3:
                backToMainMenuButton.setColor(Color.RED);
                break;
            default:
                break;
        }
    }

    public void handleRightAdjust(int index){
        switch(index){
            case 0:
                selectCharacter(false);
                break;
            case 1:
                bgVolumeSet.setValue(bgVolumeSet.getValue() + VOLUME_INCREMENT);
                break;
            case 2:
                sfxVolumeSet.setValue(sfxVolumeSet.getValue() + VOLUME_INCREMENT);
                break;
            default:
                break;
        }
    }

    public void handleLeftAdjust(int index){
        switch(index){
            case 0:
                selectCharacter(true);
                break;
            case 1:
                bgVolumeSet.setValue(bgVolumeSet.getValue() - VOLUME_INCREMENT);
                break;
            case 2:
                sfxVolumeSet.setValue(sfxVolumeSet.getValue() - VOLUME_INCREMENT);
                break;
            default:
                break;
        }
    }
}
