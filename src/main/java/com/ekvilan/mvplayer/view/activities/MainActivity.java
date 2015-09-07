package com.ekvilan.mvplayer.view.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ekvilan.mvplayer.R;
import com.ekvilan.mvplayer.controllers.MainController;
import com.ekvilan.mvplayer.controllers.providers.VideoLinksProvider;
import com.ekvilan.mvplayer.view.adapters.VideoFileAdapter;
import com.ekvilan.mvplayer.view.adapters.VideoFoldersAdapter;
import com.ekvilan.mvplayer.view.listeners.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ekvilan.mvplayer.utils.FileProvider.*;


public class MainActivity extends AppCompatActivity {
    public static final String POSITION = "position";
    private final String RECENT_VIDEO = "recentVideo";
    private final String IS_FOLDER_LIST = "isFolderList";
    private final String IS_SEARCH = "isSearch";
    private final String STORAGE = "storage";
    private final String SAVED_POSITION = "savedPosition";
    private final String CURRENT_VIDEO_LINKS = "currentVideoLinks";

    private RecyclerView recyclerView;
    private TextView memoryPath;
    private TextView tvInternalStorage;
    private TextView tvSdCard;
    private TextView tvRecentVideo;
    private EditText searchEditText;

    private MainController mainController;
    private VideoFileAdapter videoFileAdapter;
    private SharedPreferences preferences;
    private String storage;
    private boolean isFolderList;
    private boolean isSearch;
    private int savedPosition;
    private int black;
    private int white;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainController = MainController.getInstance();
        loadFromPreferences();

        if(mainController.getInternalStorageVideo() == null
                                || mainController.getSdCardVideo() == null) {
            mainController.cacheVideoLinks();
        }

        black = getResources().getColor(R.color.black);
        white = getResources().getColor(R.color.white);

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        searchEditText = (EditText) findViewById(R.id.et_search);
        searchEditText.setVisibility(View.INVISIBLE);
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
        videoFileAdapter = new VideoFileAdapter(this, videoFolder.getVideoLinks());
        isFolderList = false;
        mainController.cacheCurrentVideoLinks(videoFolder.getVideoLinks());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(videoFileAdapter);
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
                            savedPosition = position;
                            showVideoList(position);
                        } else {
                            playVideo(position);
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

        searchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findVideo(searchEditText.getText().toString());
            }
        });
    }

    private void clickInternalStorage() {
        storage = getResources().getString(R.string.sliderInternalMemory);
        isSearch = false;
        setUpFolderList(mainController.getInternalStorageVideo());
        fillPathLayout(mainController.getStoragePath(0), white, black, black);
    }

    private void clickSdCardStorage() {
        storage = getResources().getString(R.string.sliderSdCard);
        isSearch = false;
        setUpFolderList(mainController.getSdCardVideo());
        if(mainController.getStorageListSize() > 1) {
            setMemoryPath(mainController.getStoragePath(1));
        }
        setStorageTextColor(black, white, black);
    }

    private void clickRecentVideo() {
        storage = getResources().getString(R.string.sliderRecently);
        isSearch = false;
        showVideoList(0);
        fillPathLayout(getResources().getString(R.string.memPathRecentVideo), black, black, white);
    }

    private void showVideoList(int position) {
        if(storage.equals(getResources().getString(R.string.sliderInternalMemory))) {
            setUpVideoFileList(mainController.getInternalVideoFolder(position));
            fillPathLayout(mainController.getStoragePath(0), white, black, black);
        } else if (storage.equals(getResources().getString(R.string.sliderSdCard))) {
            setUpVideoFileList(mainController.getSdCardFolder(position));
            if(mainController.getStorageListSize() > 1) {
                setMemoryPath(mainController.getStoragePath(1));
            }
            setStorageTextColor(black, white, black);
        } else {
            setUpRecentVideoFileList(mainController.getRecentVideo());
            fillPathLayout(getResources().getString(R.string.memPathRecentVideo), black, black, white);
        }
    }

    private void playVideo(int position) {
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra(POSITION, position);

        startActivity(intent);
    }

    private void setStorageTextColor(int intStorageColor, int sdCardColor, int recentVideoColor) {
        tvInternalStorage.setTextColor(intStorageColor);
        tvSdCard.setTextColor(sdCardColor);
        tvRecentVideo.setTextColor(recentVideoColor);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_FOLDER_LIST, isFolderList);
        outState.putString(STORAGE, storage);
        outState.putInt(SAVED_POSITION, savedPosition);
        if(isSearch) {
            outState.putBoolean(IS_SEARCH, true);
            outState.putStringArrayList(
                    CURRENT_VIDEO_LINKS, new ArrayList<>(mainController.getCurrentVideoLinks()));
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isFolderList = savedInstanceState.getBoolean(IS_FOLDER_LIST);
        storage = savedInstanceState.getString(STORAGE);
        savedPosition = savedInstanceState.getInt(SAVED_POSITION);

        if (isFolderList) {
            if(storage.equals(getResources().getString(R.string.sliderInternalMemory))) {
                clickInternalStorage();
            } else {
                clickSdCardStorage();
            }
        } else {
            isSearch = savedInstanceState.getBoolean(IS_SEARCH);
            if(isSearch) {
                setUpFoundVideo(savedInstanceState.getStringArrayList(CURRENT_VIDEO_LINKS));
            } else {
                showVideoList(savedPosition);
            }
        }
    }

    private void fillPathLayout(
            String path, int internalStorageColor, int sdCardColor, int recentVideoColor) {
        setMemoryPath(path);
        setStorageTextColor(internalStorageColor, sdCardColor, recentVideoColor);
    }

    private void setUpRecentVideoFileList(List<String> recentVideos) {
        showVideo(recentVideos);
    }

    private void showVideo(List<String> videos) {
        videoFileAdapter = new VideoFileAdapter(this, videos);

        isFolderList = false;
        mainController.cacheCurrentVideoLinks(videos);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(videos!= null && !videos.isEmpty()) {
            recyclerView.setAdapter(videoFileAdapter);
        } else {
            recyclerView.setAdapter(new VideoFileAdapter(this, Collections.EMPTY_LIST));
        }
    }

    @Override
    protected void onDestroy() {
        saveAppState();
        super.onDestroy();
    }

    private void saveAppState() {
        preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        for(int i = 0; i < mainController.getRecentVideo().size(); i++) {
            editor.putString(RECENT_VIDEO + i, mainController.getRecentVideo().get(i));
        }
        editor.commit();
    }

    private void loadFromPreferences() {
        preferences = getPreferences(MODE_PRIVATE);

        List<String> recent = new ArrayList<>();
        for(int i = 0; i < VideoLinksProvider.RECENT_VIDEO_SIZE; i++) {
            String value = preferences.getString(RECENT_VIDEO + i, "");
            if(!value.isEmpty()) {
                recent.add(value);
            }
        }
        mainController.saveRecentVideo(recent);
        mainController.cacheCurrentVideoLinks(recent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(videoFileAdapter != null) {
            videoFileAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_search) {
            setToolBar(true, true, false, View.VISIBLE);
        }
        if(id == android.R.id.home) {
            setToolBar(false, false, true, View.INVISIBLE);
            previousStorage();
        }

        return super.onOptionsItemSelected(item);
    }

    private void findVideo(String text) {
        setUpFoundVideo(mainController.findVideo(text.toLowerCase()));
    }

    private void setUpFoundVideo(List<String> foundVideos) {
        fillPathLayout(getResources().getString(R.string.memFoundVideo), black, black, black);
        showVideo(foundVideos);
        isSearch = true;
    }

    private void setToolBar(boolean isHomeBtnEnabled, boolean isDisplayHomeAsUpEnabled,
                            boolean isShowTitle, int visibleType) {
        getSupportActionBar().setHomeButtonEnabled(isHomeBtnEnabled);
        getSupportActionBar().setDisplayHomeAsUpEnabled(isDisplayHomeAsUpEnabled);
        getSupportActionBar().setDisplayShowTitleEnabled(isShowTitle);
        searchEditText.setVisibility(visibleType);
    }

    private void previousStorage() {
        if(storage.equals(getResources().getString(R.string.sliderInternalMemory))) {
            clickInternalStorage();
        } else if(storage.equals(getResources().getString(R.string.sliderSdCard))) {
            clickSdCardStorage();
        } else {
            clickRecentVideo();
        }
    }
}
