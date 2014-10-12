package com.kasetagen.game.bubblerunner.scene2d;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.kasetagen.game.bubblerunner.util.ViewportUtil;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 8/4/14
 * Time: 10:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseStage extends Stage {

    public BaseStage(){
        super(new StretchViewport(ViewportUtil.VP_WIDTH, ViewportUtil.VP_HEIGHT));
    }


}
