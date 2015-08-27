package com.ekvilan.mvplayer.view.activities;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ekvilan.mvplayer.R;
import com.ekvilan.mvplayer.controllers.MainController;
import com.ekvilan.mvplayer.controllers.VideoController;
import com.ekvilan.mvplayer.utils.DurationConverter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class VideoPlayerActivity extends Activity implements SurfaceHolder.Callback {
    private final int DELAY = 3000;

    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private RelativeLayout topPanel;
    private RelativeLayout bottomPanel;
    private ImageView btnPlay;
    private ImageView btnPrev;
    private ImageView btnNext;
    private SeekBar progressBar;
    private TextView tvName;
    private TextView tvTimer;
    private TextView tvDuration;

    private VideoController videoController;
    private MainController mainController;
    private DurationConverter durationConverter;
    private Handler handler = new Handler();
    private ScheduledExecutorService scheduledExecutorService;

    private int position;
    private boolean isShow = false;
    private String outsideAppLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        mainController = MainController.getInstance();
        videoController = new VideoController();
        durationConverter = new DurationConverter();

        position = getIntent().getIntExtra(MainActivity.POSITION, 0);

        Uri uri = getIntent().getData();
        if(uri != null) {
            outsideAppLink = uri.getPath();
        }

        initView();
        initVideoHolder();
        addListeners();
    }

    private void initView() {
        topPanel = (RelativeLayout) findViewById(R.id.top_panel);
        bottomPanel = (RelativeLayout) findViewById(R.id.bottom_panel);
        btnPlay = (ImageView) findViewById(R.id.play);
        btnPrev = (ImageView) findViewById(R.id.prev);
        btnNext = (ImageView) findViewById(R.id.next);
        progressBar = (SeekBar) findViewById(R.id.progressBar);
        tvName = (TextView) findViewById(R.id.video_name);
        tvTimer = (TextView) findViewById(R.id.tvTimer);
        tvDuration = (TextView) findViewById(R.id.tvDuration);
    }

    private void initVideoHolder() {
        surfaceView = (SurfaceView) findViewById(R.id.surfView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
    }

    private void addListeners() {
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isShow) {
                    showPanels();
                    isShow = true;
                    startTimer();
                } else {
                    startTimer();
                }
            }
        });

        topPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        bottomPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable;
                if(videoController.isPlaying()) {
                    videoController.pause();
                    drawable = ResourcesCompat
                            .getDrawable(getResources(), R.drawable.ic_media_play, null);
                } else {
                    videoController.play();
                    drawable = ResourcesCompat
                            .getDrawable(getResources(), R.drawable.ic_media_pause, null);
                    startTimer();
                }
                setImage(btnPlay, drawable);
            }
        });

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(timerUpdater);
                handler.removeCallbacks(progressUpdater);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoController.setCurrentPosition(seekBar.getProgress());
                progressBar.setProgress(seekBar.getProgress());
                startTimer();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position > 0) {
                    startNewVideo(--position);
                    setText(tvName, getName(mainController.getVideo(position)));
                }
                startTimer();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position < mainController.getCurrentVideoLinksSize() - 1) {
                    startNewVideo(++position);
                    setText(tvName, getName(mainController.getVideo(position)));
                }
                startTimer();
            }
        });
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(outsideAppLink != null) {
            videoController.createPlayer(holder, outsideAppLink);
            setText(tvName, getName(outsideAppLink));
            addToRecentVideo(outsideAppLink);
        } else {
            videoController.createPlayer(holder, mainController.getVideo(position));
            setText(tvName, getName(mainController.getVideo(position)));
            addToRecentVideo(mainController.getVideo(position));
        }
        updateProgressBar();

        setImage(btnPlay, ResourcesCompat
                .getDrawable(getResources(), R.drawable.ic_media_pause, null));
        setText(tvDuration, durationConverter.convertDuration(videoController.getDuration()));
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
    }

    private void showPanels() {
        topPanel.setVisibility(View.VISIBLE);
        bottomPanel.setVisibility(View.VISIBLE);
    }

    private void hidePanels() {
        topPanel.setVisibility(View.GONE);
        bottomPanel.setVisibility(View.GONE);
    }

    private void startTimer() {
        handler.removeCallbacks(hide);
        handler.postDelayed(hide, DELAY);
    }

    private Runnable hide = new Runnable() {
        public void run() {
            if(videoController.isPlaying()) {
                hidePanels();
                isShow = false;
            }
        }
    };

    private void updateProgressBar() {
        scheduledExecutorService = Executors.newScheduledThreadPool(1);

        scheduledExecutorService.scheduleWithFixedDelay(
                new Runnable(){
                    @Override
                    public void run() {
                        handler.post(progressUpdater);
                    }}, 200, 200, TimeUnit.MILLISECONDS);

        scheduledExecutorService.scheduleWithFixedDelay(
                new Runnable(){
                    @Override
                    public void run() {
                        handler.post(timerUpdater);
                    }}, 1000, 1000, TimeUnit.MILLISECONDS);
    }

    private Runnable progressUpdater = new Runnable() {
        public void run() {
            progressBar.setMax(videoController.getDuration());
            progressBar.setProgress(videoController.getCurrentPosition());
        }
    };

    private Runnable timerUpdater = new Runnable() {
        public void run() {
            setText(tvTimer, durationConverter.convertDuration(videoController.getCurrentPosition()));
        }
    };

    private void setImage(ImageView imageView, Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }

    private void setText(TextView textView, String value) {
        textView.setText(value);
    }

    private String getName(String path) {
        String[] split = path.split("/");
        return split[split.length - 1];
    }

    private void startNewVideo(int position) {
        videoController.finish();
        videoController.createPlayer(surfaceHolder, mainController.getVideo(position));
        this.position = position;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        scheduledExecutorService.shutdown();
        videoController.finish();
    }

    private void addToRecentVideo(String videoLink) {
        mainController.addToRecentVideo(videoLink);
    }
}
