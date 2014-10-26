package com.kasetagen.game.bubblerunner.util;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

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
    public static Class<Music> MUSIC = Music.class;
    public static Class<TextureAtlas> TEXTURE_ATLAS = TextureAtlas.class;
    public static Class<ParticleEffect> PARTICLE = ParticleEffect.class;
    public static Class<Skin> SKIN = Skin.class;

    //TextureAtlases
    public static final String ANIMATION_ATLAS = "animations/animations.atlas";

    //Skin
    public static final String DEFAULT_SKIN = "uiskin.json";

    //Images
    public static final String TITLE_SCREEN = "images/title-screen.png";
    public static final String BUBBLE_PARTICLE_IMG = "particles/bubble.jpg";
    public static final String PLAYER_IMG = "images/player.png";
    public static final String LIGHTNING_WALL = "images/walls/blue_wall.png";
    public static final String PLASMA_WALL = "images/walls/yellow_wall.png";
    public static final String LASER_WALL = "images/walls/green_wall.png";
    public static final String INDICATOR_SHEET = "images/warning-indicator.png";
    public static final String FLOOR_CONC = "images/floor/floor_conc.png";
    public static final String FLOOR_PILLAR = "images/floor/floor_pillar.png";
    public static final String WALL = "images/floor/wall.png";
    public static final String ENERGY_BAR = "images/energy_bar.png";
    public static final String BACKGROUND = "images/bubblerun_bk.png";
    public static final String CONTROLS = "images/controls-screen.png";
    
    //Images -- Buttons
    public static final String LIGHT_UP = "images/buttons/button_light_off.png";
    public static final String LIGHT_DOWN = "images/buttons/button_light_on.png";
    public static final String LIGHT_CHECKED = "images/buttons/button_light_off.png";
    public static final String PLASMA_UP = "images/buttons/button_plasma_on.png";
    public static final String PLASMA_DOWN = "images/buttons/button_plasma_off.png";
    public static final String PLASMA_CHECKED = "images/buttons/button_plasma_off.png";
    public static final String LASER_UP = "images/buttons/button_discharge_on.png";
    public static final String LASER_DOWN = "images/buttons/button_discharge_off.png";
    public static final String LASER_CHECKED = "images/buttons/button_discharge_off.png";

    //Model Object Paths
    public static final String MODELS_TEACUP = "models/teacup.g3db";
    public static final String MODELS_LABWALL = "models/lab.g3dj";
    
    //Font Paths
    public static final String REXLIA_64 = "fonts/rexlia-64.fnt";
    public static final String REXLIA_48 = "fonts/rexlia-48.fnt";
    public static final String REXLIA_32 = "fonts/rexlia-32.fnt";
    public static final String REXLIA_24 = "fonts/rexlia-24.fnt";
    public static final String REXLIA_16 = "fonts/rexlia-16.fnt";

    //Audio Paths
    public static final String EIGHT_BIT_BKG_MUSIC = "audio/NeverStopRunning.mp3";
    public static final String DISTORTION_BKG_MUSIC = "audio/bkmusic.mp3";
    public static final String ZAP_SOUND = "audio/zap.mp3";
    public static final String EXPLOSION_SOUND = "audio/explosion.mp3";
    public static final String POWER_ON_SOUND = "audio/power-on.mp3";

    public static final String NOT_BAD = "audio/sfx/not-bad_1.mp3";
    public static final String GREAT = "audio/sfx/great_1.mp3";
    public static final String AWESOME = "audio/sfx/awesome_1.mp3";
    public static final String AMAZING = "audio/sfx/amazing_1.mp3";
    public static final String BONKERS = "audio/sfx/bonkers.mp3";
    public static final String RIDICULOUS = "audio/sfx/ridiculous_1.mp3";
    public static final String ATOMIC = "audio/sfx/atomic.mp3";
    
    //Particle Paths
    public static final String BUBBLE_PARTICLE = "particles/bubbleflame_right.p";

}
