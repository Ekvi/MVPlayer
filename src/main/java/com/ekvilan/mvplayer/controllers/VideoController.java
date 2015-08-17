package com.ekvilan.mvplayer.controllers;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;

import java.io.IOException;


public class VideoController implements MediaPlayer.OnPreparedListener {
    private MediaPlayer mediaPlayer;

    public VideoController() {
        mediaPlayer = new MediaPlayer();
    }

    public void createPlayer(SurfaceHolder surfaceHolder, String uri) {
        try {
            //mediaPlayer = new MediaPlayer();
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

    public String calculateDuration(String uri) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(uri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int duration = mediaPlayer.getDuration();
        duration /= 1000;
        int minutes = duration / 60;
        int seconds = duration % 60;
        int hours = 0;
        if(minutes >= 60) {
            hours = minutes / 60;
            minutes %= 60;
        }

        String strHours = hours > 0 ? hours + ":" : "";
        String strMinutes = minutes < 10 ? "0" + minutes + ":" : minutes + ":";
        String strSeconds = seconds < 10 ? "0" + seconds : seconds + "";

        return strHours + strMinutes + strSeconds;
    }
}
