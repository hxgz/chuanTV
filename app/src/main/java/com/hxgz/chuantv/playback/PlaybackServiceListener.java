package com.hxgz.chuantv.playback;

/**
 * @author zhoujianwu
 * @date 2020/11/18
 * @description：
 */
public interface PlaybackServiceListener {

    /**
     * player 初始化结束
     */
    void onPlayerInitialized();

    /**
     * player 开始播放
     */
    void onPlayerPlay();

    /**
     * player 暂停播放
     */
    void onPlayerPause();

    /**
     * player 播放完了
     */
    default void onPlayerEnd() {
    }

    /**
     * player load数据中
     */
    default void onPlayerBuffering() {
    }

}



    