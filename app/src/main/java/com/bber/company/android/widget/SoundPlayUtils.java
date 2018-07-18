package com.bber.company.android.widget;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.bber.company.android.R;

import java.util.Random;

/**
 * Created by bn on 2017/7/20.
 */

public class SoundPlayUtils {
    private static final int[] musicId = {R.raw.game_background_music};
    // SoundPool对象
    public static SoundPool mSoundPlayer = new SoundPool(10,
            AudioManager.STREAM_SYSTEM, 5);
    public static SoundPlayUtils soundPlayUtils;
    // 上下文
    static Context mContext;
    private static MediaPlayer music;
    private static boolean musicSt = false; //音乐开关
    private static boolean soundSt = true; //音效开关

    /**
     * 初始化
     *
     * @param context
     */
    public static SoundPlayUtils init(Context context) {
        if (soundPlayUtils == null) {
            soundPlayUtils = new SoundPlayUtils();
        }

        // 初始化声音
        mContext = context;
        initMusic();
        mSoundPlayer.load(mContext, R.raw.bet_please, 1);// 1
        mSoundPlayer.load(mContext, R.raw.buy_model, 1);// 2
        mSoundPlayer.load(mContext, R.raw.girls_rotasi, 1);// 3
        mSoundPlayer.load(mContext, R.raw.model_rotasi, 1);// 4
        mSoundPlayer.load(mContext, R.raw.win_girl, 1);// 5
        mSoundPlayer.load(mContext, R.raw.win_money, 1);// 6
//        mSoundPlayer.load(mContext, R.raw.ying, 1);// 8

        return soundPlayUtils;
    }

    //初始化音乐播放器
    public static void initMusic() {
        int r = new Random().nextInt(musicId.length);
        music = MediaPlayer.create(mContext, musicId[r]);
        music.setLooping(true);
    }

    /**
     * 暂停音乐
     */

    public static void pauseMusic() {
        if (music.isPlaying())
            music.pause();
    }

    /**
     * 播放音乐
     */
    public static void startMusic() {
        if (musicSt)
            music.start();
    }

    /**
     * 获得音乐开关状态
     *
     * @return
     */
    public static boolean isMusicSt() {
        return musicSt;
    }

    /**
     * 设置音乐开关
     *
     * @param musicSt
     */
    public static void setMusicSt(boolean musicSt) {
        SoundPlayUtils.musicSt = musicSt;
        if (musicSt)
            music.start();
        else
            music.stop();
    }

    /**
     * 播放声音
     * priority —— 流的优先级，值越大优先级高，影响当同时播放数量超出了最大支持数时SoundPool对该流的处理；
     * loop —— 循环播放的次数，0为值播放一次，-1为无限循环，其他值为播放loop+1次（例如，3为一共播放4次）.
     * rate —— 播放的速率，范围0.5-2.0(0.5为一半速率，1.0为正常速率，2.0为两倍速率)
     *
     * @param soundID
     */
    public static void play(int soundID) {
        mSoundPlayer.play(soundID, 0.3f, 0.3f, 0, 0, 1);
    }

    public static void play(int soundID, int cycle) {
        mSoundPlayer.play(soundID, 1, 1, 0, cycle, 1);
    }

}
