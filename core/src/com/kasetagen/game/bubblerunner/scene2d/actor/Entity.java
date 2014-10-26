package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Entity {
	protected float x;
	protected float y;
	protected float width;
	protected float height;
	protected TextureRegion textureRegion;
	protected Color color;
	
	public Entity(float x, float y, float width, float height, Color color){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		//this.textureRegion = textureRegion;
		this.color = color;
	}
	
	public float getWidth(){
		return width;
	}
	
	public float getHeight(){
		return height;
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public Color getColor(){
		return color;
	}
	
	public void setX(float x){
		this.x = x;
	}
	
	public void setY(float y){
		this.y = y;
	}
	
	public void setWidth(float width){
		this.width = width;
	}
	
	public void setHeight(float height){
		this.height = height;
	}
}
