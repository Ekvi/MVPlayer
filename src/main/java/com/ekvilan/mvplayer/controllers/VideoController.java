package com.ekvilan.mvplayer.controllers;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;


public class VideoController implements MediaPlayer.OnPreparedListener {
    private MediaPlayer mediaPlayer;

    public void createPlayer(SurfaceHolder surfaceHolder, String uri) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.setDataSource(uri);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void play() {
        mediaPlayer.start();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void setVolume(float value) {
        mediaPlayer.setVolume(value, value);
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public void setOffset(int value) {
        mediaPlayer.seekTo(value);
    }

    public void finish() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void setCurrentPosition(int currentPosition) {
        mediaPlayer.seekTo(currentPosition);
    }
}
