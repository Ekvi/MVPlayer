package com.ekvilan.mvplayer.controllers.providers;



import com.ekvilan.mvplayer.utils.FileProvider;
import com.ekvilan.mvplayer.utils.StorageUtils;

import java.util.ArrayList;
import java.util.List;

public class VideoLinksProvider {
    public static final int RECENT_VIDEO_SIZE = 10;

    private static VideoLinksProvider instance;

    private List<StorageUtils.StorageInfo> storageList;
    private List<FileProvider.VideoFolder> sdCardVideo;
    private List<FileProvider.VideoFolder> internalStorageVideo;
    private List<String> recentVideo;
    private List<String> videoLinks;

    private VideoLinksProvider() {
        recentVideo = new ArrayList<>(10);
    }

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

    public void addToRecentVideo(String videoLink) {
        if(!recentVideo.contains(videoLink)) {
            if (recentVideo.size() == RECENT_VIDEO_SIZE) {
                recentVideo.remove(RECENT_VIDEO_SIZE - 1);
            }
            recentVideo.add(0, videoLink);
        } else {
            recentVideo.remove(videoLink);
            recentVideo.add(0, videoLink);
        }
    }

    public List<String> getRecentVideo() {
        return recentVideo;
    }

    public void saveRecentVideo(List<String> recentVideo) {
        this.recentVideo = recentVideo;
    }

    public List<String> findVideo(String text) {
        List<String> requiredVideos = new ArrayList<>();

        requiredVideos.addAll(find(text, internalStorageVideo));
        requiredVideos.addAll(find(text, sdCardVideo));

        return requiredVideos;
    }

    private List<String> find(String text, List<FileProvider.VideoFolder> folders) {
        List<String> videos = new ArrayList<>();
        for(FileProvider.VideoFolder videoFolder : folders) {
            List<String> paths = videoFolder.getVideoLinks();
            for(String path : paths) {
                String fileName = splitVideo(path).toLowerCase();
                if(fileName.startsWith(text)) {
                    videos.add(0, path);
                } else {
                    String[] splitName = fileName.split(" ");
                    if(splitName.length > 1) {
                        for (int i = 1; i < splitName.length; i++) {
                            if (splitName[i].startsWith(text)) {
                                videos.add(path);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return videos;
    }

    String splitVideo(String path) {
        String[] split = path.split("/");
        return split[split.length - 1];
    }

    public void removeFromRecentVideo(int index) {
        recentVideo.remove(index);
    }
}
