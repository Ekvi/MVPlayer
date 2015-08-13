package com.ekvilan.mvplayer.utils;


import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FileProvider {
    private String[] extensions = {".mp4", ".3gp"};
    private List<File> internalStorageVideo = new ArrayList<>();
    private List<File> externalStorageVideo = new ArrayList<>();

    public List<File> getVideoFromInternalStorage(String path) {
        getFiles(new File(path), true);
        return internalStorageVideo;
    }

    public List<File> getVideoFromExternalStorage(List<StorageUtils.StorageInfo> storageInfoList) {
        for(StorageUtils.StorageInfo info : storageInfoList) {
            getFiles(new File(info.getPath()), false);
        }
        return externalStorageVideo;
    }

    private void getFiles(File root, boolean isInternal) {
        File[] files = root.listFiles();

        for(File file : files) {
            if(file.isDirectory()) {
                getFiles(file, isInternal);
            } else {
                if(file.toString().endsWith(extensions[0]) ||
                        file.toString().endsWith(extensions[1])) {
                    if(isInternal) {
                        internalStorageVideo.add(file);
                    } else {
                        externalStorageVideo.add(file);
                    }
                }
            }
        }
    }
}
