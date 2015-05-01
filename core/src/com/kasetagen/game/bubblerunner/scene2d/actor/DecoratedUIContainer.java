package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.utils.Array;
import com.kasetagen.engine.gdx.scenes.scene2d.ActorDecorator;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 11/8/14
 * Time: 10:47 PM
 */
public class DecoratedUIContainer extends Container {

    private Array<ActorDecorator> decorations;
    public DecoratedUIContainer(Actor a){
        super(a);
        this.setTransform(true);
        decorations = new Array<ActorDecorator>();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        for(ActorDecorator d:decorations){
            d.applyAdjustment(this, delta);
        }
    }

    public void addDecorator(ActorDecorator d){
        this.decorations.add(d);
    }

    public void removeDecorator(ActorDecorator d){
        this.decorations.removeValue(d, true);
    }

    public boolean hasDecorator(ActorDecorator d){
        return this.decorations.contains(d, true);
    }
}
