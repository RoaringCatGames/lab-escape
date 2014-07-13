package com.kasetagen.game.bubblerunner.util;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/26/14
 * Time: 12:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class AssetsUtil {

    public static Class<BitmapFont> BITMAP_FONT = BitmapFont.class;
    public static Class<Texture> TEXTURE = Texture.class;
    public static Class<Sound> SOUND = Sound.class;
    public static Class<TextureAtlas> TEXTURE_ATLAS = TextureAtlas.class;
    public static Class<ParticleEffect> PARTICLE = ParticleEffect.class;

    //TextureAtlases
    public static final String ANIMATION_ATLAS = "animations/animations.atlas";

    //Images
    public static final String BLG_LOGO = "badlogic.jpg";
    public static final String TITLE_SCREEN = "images/title-screen.png";
    public static final String BUBBLE_PARTICLE_IMG = "particles/bubble.jpg";
    public static final String PLAYER_IMG = "images/player.png";
    public static final String LIGHTNING_WALL = "images/walls/blue_wall.png";
    public static final String PLASMA_WALL = "images/walls/yellow_wall.png";
    public static final String LASER_WALL = "images/walls/green_wall.png";

    //Images -- Buttons
    public static final String LIGHT_UP = "images/buttons/light-up.png";
    public static final String LIGHT_DOWN = "images/buttons/light-down.png";
    public static final String LIGHT_CHECKED = "images/buttons/light-down.png";
    public static final String PLASMA_UP = "images/buttons/s-up.png";
    public static final String PLASMA_DOWN = "images/buttons/s-down.png";
    public static final String PLASMA_CHECKED = "images/buttons/s-checked.png";
    public static final String LASER_UP = "images/buttons/d-up.png";
    public static final String LASER_DOWN = "images/buttons/d-down.png";
    public static final String LASER_CHECKED = "images/buttons/d-checked.png";

    //Font Paths
    public static final String COURIER_FONT_12 = "fonts/courier-new-bold-12.fnt";
    public static final String COURIER_FONT_18 = "fonts/courier-new-bold-18.fnt";
    public static final String COURIER_FONT_32 = "fonts/courier-new-bold-32.fnt";
    public static final String COURIER_FONT_IMG = "fonts/courier-new-bold.png";

    //Audio Paths
    public static final String ZAP_SOUND = "audio/zap.ogg";
    public static final String BACKGROUND_SOUND = "audio/bkmusic.ogg";
    
    //Particle Paths
    public static final String BUBBLE_PARTICLE = "particles/bubbleflame_right.p";

}
