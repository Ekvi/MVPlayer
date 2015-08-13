package com.ekvilan.mvplayer.models;


public class VideoInfo {
    private String name;
    private String path;
    private String parent;

    public VideoInfo(String name, String path, String parent) {
        this.name = name;
        this.path = path;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getParent() {
        return parent;
    }
}
