package com.ekvilan.mvplayer.view.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ekvilan.mvplayer.R;
import com.ekvilan.mvplayer.controllers.MainController;
import com.ekvilan.mvplayer.utils.FileProvider;
import com.ekvilan.mvplayer.utils.StorageUtils;
import com.ekvilan.mvplayer.view.adapters.VideoFoldersAdapter;

import java.util.List;

import static com.ekvilan.mvplayer.utils.FileProvider.*;


public class MainActivity extends AppCompatActivity {
    private MainController mainController;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainController = new MainController();


        List<StorageUtils.StorageInfo> storageList = mainController.getStorageList();

        List<VideoFolder> sdCardVideo = null;
        List<VideoFolder> internalStorageVideo = mainController.getVideoFiles(storageList.get(0).getPath());

        if(storageList.size() > 1) {
            sdCardVideo = mainController.getVideoFiles(storageList.subList(1, storageList.size()));
        }

        Log.d("my", "first");
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
        }

        initView();
        setUpFolderList(sdCardVideo, internalStorageVideo);
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }

    private void setUpFolderList(List<VideoFolder> sdCardVideo, List<VideoFolder> internalVideo) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new VideoFoldersAdapter(this, internalVideo));
    }
}
