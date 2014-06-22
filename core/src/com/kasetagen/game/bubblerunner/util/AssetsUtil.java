package com.kasetagen.game.bubblerunner.util;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

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
    public static Class<ParticleEffect> PARTICLE = ParticleEffect.class;
    //Images
    public static final String BLG_LOGO = "badlogic.jpg";
    public static final String BUBBLE_PARTICLE_IMG = "particles/bubble.jpg";
    public static final String PLAYER_IMG = "images/player.png";

    //Images -- Buttons
    public static final String A_UP = "images/buttons/a-up.png";
    public static final String A_DOWN = "images/buttons/a-down.png";
    public static final String A_CHECKED = "images/buttons/a-checked.png";
    public static final String S_UP = "images/buttons/s-up.png";
    public static final String S_DOWN = "images/buttons/s-down.png";
    public static final String S_CHECKED = "images/buttons/s-checked.png";
    public static final String D_UP = "images/buttons/d-up.png";
    public static final String D_DOWN = "images/buttons/d-down.png";
    public static final String D_CHECKED = "images/buttons/d-checked.png";

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
