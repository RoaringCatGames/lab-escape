package com.kasetagen.game.bubblerunner.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.AnimatedActor;
import com.kasetagen.game.bubblerunner.BubbleRunnerGame;
import com.kasetagen.game.bubblerunner.delegate.IGameProcessor;
import com.kasetagen.game.bubblerunner.scene2d.BaseStage;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 10/25/14
 * Time: 6:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoadingScreen extends BaseBubbleRunnerScreen{

    TextureAtlas loadingAtlas;
    Animation loadingAnimation;
    AnimatedActor loadingImage;

    public LoadingScreen(IGameProcessor delegate) {
        super(delegate);
        stage = new BaseStage();

        loadingAtlas = new TextureAtlas(Gdx.files.internal("animations/loading.atlas"));
        loadingAnimation = new Animation(1f/6f, loadingAtlas.findRegions("Loading"));

        loadingImage = new AnimatedActor(0, 0, 1280f, 720f, loadingAnimation, 0f);
        stage.addActor(loadingImage);
    }


    @Override
    public void render(float delta) {
        if(gameProcessor.isLoaded()){
            gameProcessor.changeToScreen(BubbleRunnerGame.MENU);
        }else{
            int numberOfFrames = loadingAnimation.getKeyFrames().length;
            float loadingProgress = gameProcessor.getLoadingProgress();

            int targetFrame = Math.round(loadingProgress * numberOfFrames);
            loadingImage.setTargetKeyFrame(targetFrame);
        }

        super.render(delta);
    }
}
