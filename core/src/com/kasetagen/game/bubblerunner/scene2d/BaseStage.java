package com.kasetagen.game.bubblerunner.scene2d;

import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.kasetagen.engine.IGameProcessor;
import com.kasetagen.engine.gdx.scenes.scene2d.Kitten2dStage;
import com.kasetagen.game.bubblerunner.util.ViewportUtil;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 8/4/14
 * Time: 10:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseStage extends Kitten2dStage {

    public BaseStage(IGameProcessor gp){
        super(new StretchViewport(ViewportUtil.VP_WIDTH, ViewportUtil.VP_HEIGHT), gp);
    }


}
