package com.ekvilan.mvplayer.controllers;


import com.ekvilan.mvplayer.utils.StorageUtils;
import com.ekvilan.mvplayer.utils.FileProvider;

import java.io.File;
import java.util.List;


public class MainController {
    private FileProvider fileProvider;

    public MainController() {
        fileProvider = new FileProvider();
    }

    public List<StorageUtils.StorageInfo> getStorageList() {
        return StorageUtils.getStorageList();
    }

    public List<File> getVideoFiles(List<StorageUtils.StorageInfo> storageList) {
        return fileProvider.getVideoFromExternalStorage(storageList);
    }

    public List<File> getVideoFiles(String path) {
        return fileProvider.getVideoFromInternalStorage(path);
    }
}
