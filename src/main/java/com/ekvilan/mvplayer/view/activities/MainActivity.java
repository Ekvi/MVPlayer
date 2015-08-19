package com.ekvilan.mvplayer.view.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ekvilan.mvplayer.R;
import com.ekvilan.mvplayer.controllers.MainController;
import com.ekvilan.mvplayer.view.adapters.VideoFileAdapter;
import com.ekvilan.mvplayer.view.adapters.VideoFoldersAdapter;
import com.ekvilan.mvplayer.view.listeners.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ekvilan.mvplayer.utils.FileProvider.*;
import static com.ekvilan.mvplayer.utils.StorageUtils.StorageInfo;


public class MainActivity extends AppCompatActivity {
    private List<StorageInfo> storageList;
    private List<VideoFolder> sdCardVideo;
    private List<VideoFolder> internalStorageVideo;

    private MainController mainController;

    private RecyclerView recyclerView;
    private TextView memoryPath;
    private TextView tvInternalStorage;
    private TextView tvSdCard;
    private TextView tvRecentVideo;

    private List<String> videoLinks;
    private String storage;
    private boolean isFolderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainController = new MainController();

        storageList = mainController.getStorageList();
        internalStorageVideo = mainController.getVideoFiles(storageList.get(0).getPath());
        if(storageList.size() > 1) {
            sdCardVideo = mainController.getVideoFiles(storageList.subList(1, storageList.size()));
        }

        /*Log.d("my", "first");
        for(VideoFolder f : internalStorageVideo) {
            Log.d("my", f.getFolderName());
            List<String> str = f.getVideoLinks();
            for(String s : str) {
                Log.d("my", s);
            }
        }

        if(sdCardVideo != null) {
            Log.d("my", "second");
            for (VideoFolder f : sdCardVideo) {
                Log.d("my", f.getFolderName());
                List<String> str = f.getVideoLinks();
                for (String s : str) {
                    Log.d("my", s);
                }
            }
        }*/

        initView();
        clickInternalStorage();
        addListeners();

    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        memoryPath = (TextView) findViewById(R.id.memoryPath);
        tvInternalStorage  = (TextView) findViewById(R.id.tvInternalMemory);
        tvSdCard  = (TextView) findViewById(R.id.tvSdCard);
        tvRecentVideo = (TextView) findViewById(R.id.tvRecently);
    }

    private void setUpFolderList(List<VideoFolder> folders) {
        isFolderList = true;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(folders != null && !folders.isEmpty()) {
            recyclerView.setAdapter(new VideoFoldersAdapter(this, folders));
        } else {
            recyclerView.setAdapter(new VideoFoldersAdapter(this, Collections.EMPTY_LIST));
        }
    }

    private void setUpVideoFileList(VideoFolder videoFolder) {
        isFolderList = false;
        videoLinks = videoFolder.getVideoLinks();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new VideoFileAdapter(this, videoFolder.getVideoLinks()));
    }

    private void setMemoryPath(String path) {
        memoryPath.setText(path);
    }

    private void addListeners() {
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(isFolderList) {
                            showVideoList(position);
                        } else {
                            playVideo(position, videoLinks);
                        }
                    }
                })
        );

        tvInternalStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickInternalStorage();
            }
        });

        tvSdCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSdCardStorage();
            }
        });

        tvRecentVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRecentVideo();
            }
        });
    }

    private void clickInternalStorage() {
        storage = getResources().getString(R.string.sliderInternalMemory);
        setUpFolderList(internalStorageVideo);
        setMemoryPath(storageList.get(0).getPath());
        setStorageTextColor(getResources().getColor(R.color.white),
                getResources().getColor(R.color.black), getResources().getColor(R.color.black));
    }

    private void clickSdCardStorage() {
        storage = getResources().getString(R.string.sliderSdCard);
        setUpFolderList(sdCardVideo);
        if(storageList.size() > 1) {
            setMemoryPath(storageList.get(1).getPath());
        }
        setStorageTextColor(getResources().getColor(R.color.black),
                getResources().getColor(R.color.white), getResources().getColor(R.color.black));
    }

    private void clickRecentVideo() {
        storage = getResources().getString(R.string.sliderRecently);
        //setUpFolderList(); todo set list of recent video
        setMemoryPath(MainActivity.this.getResources().getString(R.string.memPathRecentVideo));
        setStorageTextColor(getResources().getColor(R.color.black),
                getResources().getColor(R.color.black), getResources().getColor(R.color.white));
    }

    private void showVideoList(int position) {
        if(storage.equals(getResources().getString(R.string.sliderInternalMemory))) {
            setUpVideoFileList(internalStorageVideo.get(position));
        } else if (storage.equals(getResources().getString(R.string.sliderSdCard))) {
            setUpVideoFileList(sdCardVideo.get(position));
        } else {
            //todo set up recent video list
        }
    }

    private void playVideo(int position, List<String> videoLinks) {
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra("position", position);
        intent.putStringArrayListExtra("videoLinks", (ArrayList<String>)videoLinks);
        startActivity(intent);
    }

    private void setStorageTextColor(int intStorageColor, int sdCardColor, int recentVideoColor) {
        tvInternalStorage.setTextColor(intStorageColor);
        tvSdCard.setTextColor(sdCardColor);
        tvRecentVideo.setTextColor(recentVideoColor);
    }
}
