package com.ekvilan.mvplayer.controllers.providers;


import com.ekvilan.mvplayer.utils.FileProvider;
import com.ekvilan.mvplayer.utils.StorageUtils;

import java.util.List;

public class VideoLinksProvider {
    private static VideoLinksProvider instance;

    private List<StorageUtils.StorageInfo> storageList;
    private List<FileProvider.VideoFolder> sdCardVideo;
    private List<FileProvider.VideoFolder> internalStorageVideo;
    private List<String> videoLinks;

    private VideoLinksProvider() {}

    public static VideoLinksProvider getInstance() {
        if(instance == null) {
            instance = new VideoLinksProvider();
        }
        return instance;
    }

    public void saveStorageList(List<StorageUtils.StorageInfo> storageList) {
        this.storageList = storageList;
    }

    public void saveInternalStorageVideo(List<FileProvider.VideoFolder> internalStorageVideo) {
        this.internalStorageVideo = internalStorageVideo;
    }

    public void saveExternalStorageVideo(List<FileProvider.VideoFolder> sdCardVideo) {
        this.sdCardVideo = sdCardVideo;
    }

    public void saveVideoLinks(List<String> videoLinks) {
        this.videoLinks = videoLinks;
    }

    public List<FileProvider.VideoFolder> getSdCardVideo() {
        return sdCardVideo;
    }

    public List<FileProvider.VideoFolder> getInternalStorageVideo() {
        return internalStorageVideo;
    }

    public String getStoragePath(int index) {
        return storageList.get(index).getPath();
    }

    public int getStorageListSize() {
        return storageList.size();
    }

    public FileProvider.VideoFolder getInternalVideoFolder(int position) {
        return internalStorageVideo.get(position);
    }

    public FileProvider.VideoFolder getSdCardFolder(int position) {
        return sdCardVideo.get(position);
    }

    public List<String> getVideoLinks() {
        return videoLinks;
    }

    public int getVideoLinksSize() {
        return videoLinks.size();
    }

    public String getVideo(int position) {
        return videoLinks.get(position);
    }
}
