package com.ekvilan.mvplayer.controllers;


import com.ekvilan.mvplayer.controllers.providers.VideoLinksProvider;
import com.ekvilan.mvplayer.utils.StorageUtils;
import com.ekvilan.mvplayer.utils.FileProvider;

import java.util.List;


public class MainController {
    private static MainController instance;
    private FileProvider fileProvider = new FileProvider();
    private VideoLinksProvider videoLinksProvider = VideoLinksProvider.getInstance();

    private MainController() {}

    public static MainController getInstance() {
        if (instance == null) {
            instance = new MainController();
        }
        return instance;
    }

    public void cacheVideoLinks() {
        List<StorageUtils.StorageInfo> storageInfoList = StorageUtils.getStorageList();
        videoLinksProvider.saveStorageList(storageInfoList);

        cacheInternalStorageVideo(storageInfoList.get(0).getPath());
        if(storageInfoList.size() > 1) {
            cacheSdCardVideo(storageInfoList.subList(1, storageInfoList.size()));
        }
    }

    private void cacheInternalStorageVideo(String path) {
        videoLinksProvider.saveInternalStorageVideo(fileProvider.getVideoFromInternalStorage(path));
    }

    private void cacheSdCardVideo(List<StorageUtils.StorageInfo> storageList) {
        videoLinksProvider.saveExternalStorageVideo(fileProvider.getVideoFromExternalStorage(storageList));
    }

    public void cacheCurrentVideoLinks(List<String> videoLinks) {
        videoLinksProvider.saveVideoLinks(videoLinks);
    }

    public List<FileProvider.VideoFolder> getInternalStorageVideo() {
        return videoLinksProvider.getInternalStorageVideo();
    }

    public List<FileProvider.VideoFolder> getSdCardVideo() {
        return videoLinksProvider.getSdCardVideo();
    }

    public String getStoragePath(int index) {
        return videoLinksProvider.getStoragePath(index);
    }

    public int getStorageListSize() {
        return videoLinksProvider.getStorageListSize();
    }

    public FileProvider.VideoFolder getInternalVideoFolder(int position) {
        return videoLinksProvider.getInternalVideoFolder(position);
    }

    public FileProvider.VideoFolder getSdCardFolder(int position) {
        return videoLinksProvider.getSdCardFolder(position);
    }

    public List<String> getCurrentVideoLinks() {
        return videoLinksProvider.getVideoLinks();
    }

    public int getCurrentVideoLinksSize() {
        return videoLinksProvider.getVideoLinksSize();
    }

    public String getVideo(int position) {
        return videoLinksProvider.getVideo(position);
    }

    public List<String> getRecentVideo() {
        return videoLinksProvider.getRecentVideo();
    }

    public void addToRecentVideo(String link) {
        videoLinksProvider.addToRecentVideo(link);
    }

    public void saveRecentVideo(List<String> recentVideo) {
        videoLinksProvider.saveRecentVideo(recentVideo);
    }

    public List<String> findVideo(String text) {
        return videoLinksProvider.findVideo(text);
    }

    public void removeFromRecentVideo(int index) {
        videoLinksProvider.removeFromRecentVideo(index);
    }
}
