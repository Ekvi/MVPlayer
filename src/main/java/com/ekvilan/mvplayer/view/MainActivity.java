package com.ekvilan.mvplayer.view;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ekvilan.mvplayer.R;
import com.ekvilan.mvplayer.controllers.MainController;
import com.ekvilan.mvplayer.models.VideoInfo;
import com.ekvilan.mvplayer.utils.FileProvider;
import com.ekvilan.mvplayer.utils.StorageUtils;

import java.io.File;
import java.util.List;


public class MainActivity extends Activity {
    private MainController mainController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainController = new MainController();


        List<StorageUtils.StorageInfo> storageList = mainController.getStorageList();

        List<FileProvider.VideoFolder> sdCardVideo = null;
        List<FileProvider.VideoFolder> internalStorageVideo = mainController.getVideoFiles(storageList.get(0).getPath());

        if(storageList.size() > 1) {
            sdCardVideo = mainController.getVideoFiles(storageList.subList(1, storageList.size()));
        }

        Log.d("my", "first");
        for(FileProvider.VideoFolder f : internalStorageVideo) {
            Log.d("my", f.getFolderName());
            List<String> str = f.getVideoLinks();
            for(String s : str) {
                Log.d("my", s);
            }
        }

        if(sdCardVideo != null) {
            Log.d("my", "second");
            for (FileProvider.VideoFolder f : sdCardVideo) {
                Log.d("my", f.getFolderName());
                List<String> str = f.getVideoLinks();
                for (String s : str) {
                    Log.d("my", s);
                }
            }
        }
    }
}
