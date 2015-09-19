package com.ekvilan.mvplayer.view.activities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ekvilan.mvplayer.R;
import com.ekvilan.mvplayer.controllers.MainController;
import com.ekvilan.mvplayer.controllers.providers.VideoLinksProvider;
import com.ekvilan.mvplayer.utils.FileProvider;
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
        mainController.validateRecentVideo();

        black = getResources().getColor(R.color.black);
        white = getResources().getColor(R.color.white);

        initView();
        clickInternalStorage();
        addListeners();

        registerForContextMenu(recyclerView);
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
        videoFileAdapter = new VideoFileAdapter(
                this, videoFolder.getVideoLinks(), isRecent(), isSearch);
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
                getSupportActionBar().setTitle(getResources().getString(R.string.foundFiles));
            }
        });
    }

    private void clickInternalStorage() {
        storage = getResources().getString(R.string.sliderInternalMemory);
        isSearch = false;
        invalidateOptionsMenu();
        setUpFolderList(mainController.getInternalStorageVideo());
        fillPathLayout(mainController.getStoragePath(0), white, black, black);
        setToolBar(false, false, true, View.INVISIBLE);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
    }

    private void clickSdCardStorage() {
        storage = getResources().getString(R.string.sliderSdCard);
        isSearch = false;
        invalidateOptionsMenu();
        setUpFolderList(mainController.getSdCardVideo());
        if(mainController.getStorageListSize() > 1) {
            setMemoryPath(mainController.getStoragePath(1));
        }
        setStorageTextColor(black, white, black);
        setToolBar(false, false, true, View.INVISIBLE);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
    }

    private void clickRecentVideo() {
        storage = getResources().getString(R.string.sliderRecently);
        isSearch = false;
        invalidateOptionsMenu();
        showVideoList(0);
        fillPathLayout(getResources().getString(R.string.memPathRecentVideo), black, black, white);
        setToolBar(false, false, true, View.INVISIBLE);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
    }

    private void showVideoList(int position) {
        if(storage.equals(getResources().getString(R.string.sliderInternalMemory))) {
            VideoFolder folder = mainController.getInternalVideoFolder(position);
            setUpVideoFileList(folder);
            fillPathLayout(mainController.getStoragePath(0), white, black, black);
            getSupportActionBar().setTitle(FileProvider.extractName(folder.getFolderName()));
        } else if (storage.equals(getResources().getString(R.string.sliderSdCard))) {
            VideoFolder folder = mainController.getSdCardFolder(position);
            setUpVideoFileList(folder);
            if(mainController.getStorageListSize() > 1) {
                setMemoryPath(mainController.getStoragePath(1));
            }
            setStorageTextColor(black, white, black);
            getSupportActionBar().setTitle(FileProvider.extractName(folder.getFolderName()));
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
        videoFileAdapter = new VideoFileAdapter(this, videos, isRecent(), isSearch);
        isFolderList = false;
        mainController.cacheCurrentVideoLinks(videos);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(videos!= null && !videos.isEmpty()) {
            recyclerView.setAdapter(videoFileAdapter);
        } else {
            recyclerView.setAdapter(
                    new VideoFileAdapter(this, Collections.EMPTY_LIST, isRecent(), isSearch));
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
        editor.clear();

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
        updateRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem itemMenu = menu.findItem(R.id.clean);
        if(storage.equals(getResources().getString(R.string.sliderRecently))) {
            itemMenu.setVisible(true);
        } else {
            itemMenu.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            setToolBar(true, true, false, View.VISIBLE);
        }
        if(id == android.R.id.home) {
            setToolBar(false, false, true, View.INVISIBLE);
            previousStorage();
        }
        if(id == R.id.clean) {
            mainController.cleanRecentVideoList();
            updateRecyclerView();
        }

        return super.onOptionsItemSelected(item);
    }

    private void findVideo(String text) {
        setUpFoundVideo(mainController.findVideo(text.toLowerCase()));
        setToolBar(false, false, true, View.INVISIBLE);
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void setUpFoundVideo(List<String> foundVideos) {
        isSearch = true;
        fillPathLayout(getResources().getString(R.string.memFoundVideo), black, black, black);
        showVideo(foundVideos);
    }

    private void setToolBar(boolean isHomeBtnEnabled, boolean isDisplayHomeAsUpEnabled,
                            boolean isShowTitle, int searchVisibility) {
        getSupportActionBar().setHomeButtonEnabled(isHomeBtnEnabled);
        getSupportActionBar().setDisplayHomeAsUpEnabled(isDisplayHomeAsUpEnabled);
        getSupportActionBar().setDisplayShowTitleEnabled(isShowTitle);
        searchEditText.setVisibility(searchVisibility);
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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        try {
            position = videoFileAdapter.getPosition();
        } catch (Exception e) {
            return super.onContextItemSelected(item);
        }

        switch (item.getItemId()) {
            case VideoFileAdapter.REMOVE_RECENT:
                removeFromRecentVideoList(position);
                break;
            case VideoFileAdapter.REMOVE:
                removeFromFileSystem(videoFileAdapter.getFilePath(), position);
                break;
            case VideoFileAdapter.RENAME:
                renameVideoFile(videoFileAdapter.getFilePath(), position);
                break;
        }

        return super.onContextItemSelected(item);
    }

    private boolean isRecent() {
        return storage.equals(getResources().getString(R.string.sliderRecently));
    }

    private void removeFromRecentVideoList(int position) {
        if(position != -1) {
            mainController.removeFromRecentVideo(position);
            updateRecyclerView();
        }
    }

    private void removeFromFileSystem(String path, int position) {
        if(position != -1) {
            mainController.removeVideo(
                    path, position, storage, getResources().getString(R.string.sliderSdCard));
            updateRecyclerView();
        }
    }

    private void renameVideoFile(String path, int position) {
        View view = getLayoutInflater().inflate(R.layout.rename_dialog, null);
        EditText editText = (EditText)view.findViewById(R.id.etRename);
        editText.setText(FileProvider.extractName(path));

        showRenameDialog(view, editText, path, position);
    }

    private void updateRecyclerView() {
        if(videoFileAdapter != null) {
            videoFileAdapter.notifyDataSetChanged();
        }
    }

    private void showRenameDialog(
            final View view, final EditText editText, final String path, final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.renameDialogTitle));
        builder.setView(view);
        builder.setNegativeButton(getResources().getString(R.string.btnCancelDialog), null);
        builder.setPositiveButton(getResources().getString(R.string.btnOkDialog),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mainController.renameVideo(path, position, editText.getText().toString());
                updateRecyclerView();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
