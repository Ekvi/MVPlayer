package com.ekvilan.mvplayer.utils;


import com.ekvilan.mvplayer.models.VideoInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ekvilan.mvplayer.utils.StorageUtils.StorageInfo;


public class FileProvider {
    private List<String> extensions = Arrays.asList(
            ".mp4", ".3gp", ".3gpp", ".3gpp2", ".webm", ".avi", ".mkv", "webm");
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

        if(files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    getFiles(file, isInternal);
                } else {
                    storeToCollection(file, isInternal);
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

        public boolean contains(String name) {
            return videoLinks.contains(name);
        }
    }

    public void removeFile(String path) {
        File file = new File(path);
        if(file.exists()) {
            file.delete();
        }
    }

    public void renameFile(String path, String newName) {
        File file = new File(path);
        if(file.exists()) {
            file.renameTo(new File(changeName(path, newName)));
        }
    }

    public static String extractName(String path) {
        String[] split = path.split("/");
        return split[split.length - 1];
    }

    public String changeName(String path, String newName) {
        String[] split = path.split("/");
        split[split.length - 1] = newName;

        StringBuilder builder = new StringBuilder();

        builder.append(split[0]);
        for(int i = 1; i < split.length; i++) {
            builder.append("/").append(split[i]);
        }

        return builder.toString();
    }

    private void storeToCollection(File file, boolean isInternal) {
        if (extensions.contains(extractExtension(file.toString()))) {
            VideoInfo videoInfo = new VideoInfo(
                    file.getName(), file.getAbsolutePath(), file.getParent());
            if (isInternal) {
                internalStorageVideo.add(videoInfo);
            } else {
                externalStorageVideo.add(videoInfo);
            }
        }
    }

    private String extractExtension(String fileName) {
        String[] split = fileName.split("\\.");
        return split.length > 0 ? "." + split[split.length - 1].toLowerCase() : "";
    }
}
