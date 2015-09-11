package com.ekvilan.mvplayer.controllers;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;

import com.ekvilan.mvplayer.view.listeners.VideoFinishedListener;


public class VideoController implements MediaPlayer.OnPreparedListener {
    private MediaPlayer mediaPlayer;

    private int buffedPercentage;

    private VideoFinishedListener videoFinishedListener;

    public void setVideoFinishedListener(VideoFinishedListener videoFinishedListener) {
        this.videoFinishedListener = videoFinishedListener;
    }

    public void createPlayer(SurfaceHolder surfaceHolder, String uri) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.setDataSource(uri);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(videoFinishedListener != null) {
                        videoFinishedListener.onFinished();
                    }
                }
            });
            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    buffedPercentage = percent;
                }
            });
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
        return mediaPlayer != null && mediaPlayer.isPlaying();
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
        if (mediaPlayer == null) {
            return 0;
        }
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        if (mediaPlayer == null) {
            return 0;
        }
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

    public int getBufferedPercentage() {
        return buffedPercentage;
    }
}
