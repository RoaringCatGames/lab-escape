package com.kasetagen.game.bubblerunner.util;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
    public static final String SPRITE_ATLAS = "sprites/sprites.atlas";

    //Skin
    public static final String DEFAULT_SKIN = "uiskin.json";

    //Font Paths
    public static final String NEUROPOL_64 = "fonts/neuropol-64.fnt";
    public static final String NEUROPOL_48 = "fonts/neuropol-48.fnt";
    public static final String NEUROPOL_32 = "fonts/neuropol-32.fnt";

    //Audio Paths
    public static final String MENU_BG_MUSIC = "audio/railjet-short.mp3";
    public static final String GAME_BG_MUSIC = "audio/railjet-long.mp3";
    public static final String SND_SHIELD_ON = "audio/shield.mp3";
    public static final String SND_SHIELD_WRONG = "audio/bad-shield.mp3";
    public static final String SND_WB_ELECTRIC = "audio/wallbreaks/electricity.mp3";
    public static final String SND_WB_GLASS = "audio/wallbreaks/glass-shatter.mp3";
    public static final String SND_WB_LASER = "audio/wallbreaks/laser-break.mp3";
    public static final String SND_DEATH_THUD = "audio/deaths/glass-thud.mp3";
    public static final String SND_DEATH_EDYN = "audio/deaths/edyn-scream.mp3";
    public static final String SND_DEATH_EDISON = "audio/deaths/edison-scream.mp3";
    public static final String SND_DEATH_SHOCK = "audio/deaths/electric-w-sparks.mp3";
    public static final String SND_DEATH_FIRE = "audio/deaths/flame-whoosh.mp3";
    public static final String SND_SICK_EDYN = "audio/edyn-sick.mp3";
    public static final String SND_SICK_EDISON = "audio/edison-sick.mp3";

    public static final String SND_EDISON_READY = "audio/edison-ready.mp3";
    public static final String SND_EDYN_READY = "audio/edyn-ready.mp3";

    public static final String NOT_BAD = "audio/sfx/not-bad_1.mp3";
    public static final String GREAT = "audio/sfx/great_1.mp3";
    public static final String AWESOME = "audio/sfx/awesome_1.mp3";
    public static final String AMAZING = "audio/sfx/amazing_1.mp3";
    public static final String UNBELIEVABLE = "audio/sfx/unbelievable.mp3";
    public static final String PERFECT = "audio/sfx/perfect.mp3";
    public static final String UNSTOPPABLE = "audio/sfx/unstoppable.mp3";
}
