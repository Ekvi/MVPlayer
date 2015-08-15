package com.ekvilan.mvplayer.view.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.ekvilan.mvplayer.R;
import com.ekvilan.mvplayer.controllers.MainController;
import com.ekvilan.mvplayer.utils.FileProvider;
import com.ekvilan.mvplayer.utils.StorageUtils;
import com.ekvilan.mvplayer.view.adapters.VideoFileAdapter;
import com.ekvilan.mvplayer.view.adapters.VideoFoldersAdapter;
import com.ekvilan.mvplayer.view.listeners.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ekvilan.mvplayer.utils.FileProvider.*;
import static com.ekvilan.mvplayer.utils.StorageUtils.StorageInfo;


public class MainActivity extends AppCompatActivity {
    private List<VideoFolder> sdCardVideo;
    private List<VideoFolder> internalStorageVideo;

    private MainController mainController;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainController = new MainController();

        List<StorageInfo> storageList = mainController.getStorageList();
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


        if(internalStorageVideo != null && !internalStorageVideo.isEmpty()) {
            setUpFolderList(sdCardVideo, internalStorageVideo);
        } else {
            setUpFolderList(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        }

        addListeners();

    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }

    private void setUpFolderList(List<VideoFolder> sdCardVideo, List<VideoFolder> internalVideo) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new VideoFoldersAdapter(this, internalVideo));
    }

    private void setUpVideoFileList(VideoFolder videoFolder) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new VideoFileAdapter(this, videoFolder.getVideoLinks()));
    }

    private void addListeners() {
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(internalStorageVideo != null && internalStorageVideo.size() > 0) {
                            setUpVideoFileList(internalStorageVideo.get(position));
                        }
                    }
                })
        );
    }
}
