package com.ekvilan.mvplayer.utils;


import com.ekvilan.mvplayer.models.VideoInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.ekvilan.mvplayer.utils.StorageUtils.StorageInfo;


public class FileProvider {
    private String[] extensions = {".mp4", ".3gp"};
    private List<VideoInfo> internalStorageVideo = new ArrayList<>();
    private List<VideoInfo> externalStorageVideo = new ArrayList<>();

    public List<VideoFolder> getVideoFromInternalStorage(String path) {
        getFiles(new File(path), true);

        List<VideoFolder> video = new ArrayList<>();
        if(!internalStorageVideo.isEmpty()) {
            video = createVideoFolders(internalStorageVideo);
        }
        return video;
    }

    public List<VideoFolder> getVideoFromExternalStorage(List<StorageInfo> storageInfoList) {
        for(StorageInfo info : storageInfoList) {
            getFiles(new File(info.getPath()), false);
        }

        List<VideoFolder> video = new ArrayList<>();
        if(!externalStorageVideo.isEmpty()) {
            video = createVideoFolders(externalStorageVideo);
        }
        return video;
    }

    private void getFiles(File root, boolean isInternal) {
        File[] files = root.listFiles();

        for(File file : files) {
            if(file.isDirectory()) {
                getFiles(file, isInternal);
            } else {
                if(file.toString().endsWith(extensions[0]) ||
                        file.toString().endsWith(extensions[1])) {
                    VideoInfo videoInfo = new VideoInfo(
                            file.getName(), file.getAbsolutePath(), file.getParent());
                    if(isInternal) {
                        internalStorageVideo.add(videoInfo);
                    } else {
                        externalStorageVideo.add(videoInfo);
                    }
                }
            }
        }
    }

    List<VideoFolder> createVideoFolders(List<VideoInfo> videoInfo) {
        List<VideoFolder> folders = new ArrayList<>();
        List<String> videoPaths = new ArrayList<>();

        String folderName = videoInfo.get(0).getParent();
        videoPaths.add(videoInfo.get(0).getPath());

        if(videoInfo.size() > 0) {
            for(int i = 1; i < videoInfo.size(); i++) {
                if(videoInfo.get(i).getParent().equals(folderName)) {
                    videoPaths.add(videoInfo.get(i).getPath());
                } else {
                    folders.add(new VideoFolder(folderName, videoPaths));
                    folderName = videoInfo.get(i).getParent();
                    videoPaths = new ArrayList<>();
                    videoPaths.add(videoInfo.get(i).getPath());
                }
            }
            folders.add(new VideoFolder(folderName, videoPaths));
        }
        return folders;
    }

    public static class VideoFolder {
        private String folderName;
        private List<String> videoLinks;

        public VideoFolder(String folderName, List<String> videoLinks) {
            this.folderName = folderName;
            this.videoLinks = videoLinks;
        }

        public String getFolderName() {
            return folderName;
        }

        public List<String> getVideoLinks() {
            return videoLinks;
        }
    }
}
