package com.kasetagen.game.bubblerunner.scene2d;

import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.kasetagen.engine.IGameProcessor;
import com.kasetagen.engine.gdx.scenes.scene2d.Kitten2dStage;
import com.kasetagen.game.bubblerunner.util.ViewportUtil;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 8/4/14
 * Time: 10:13 PM
 */
public class BaseStage extends Kitten2dStage {

    public BaseStage(IGameProcessor gp){
        super(new FitViewport(ViewportUtil.VP_WIDTH, ViewportUtil.VP_HEIGHT), gp);
    }

}
