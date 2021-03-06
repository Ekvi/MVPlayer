package com.ekvilan.mvplayer.controllers.providers;


import com.ekvilan.mvplayer.utils.FileProvider;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class VideoLinksProviderUnitTest {
    private VideoLinksProvider videoLinksProvider;

    @Before
    public void init() {
        videoLinksProvider = VideoLinksProvider.getInstance();
    }

    @Test
    public void testAddToRecentVideoWhenSizeLessLimit() {
        videoLinksProvider.addToRecentVideo("test link");

        assertEquals(1, videoLinksProvider.getRecentVideo().size());
    }

    @Test
    public void testAddToRecentVideoWhenSizeEqualsLimit() {
        addToRecentVideo();
        videoLinksProvider.addToRecentVideo("new test link");

        assertEquals(10, videoLinksProvider.getRecentVideo().size());
        assertEquals("new test link", videoLinksProvider.getRecentVideo().get(0));
        assertEquals("test link9", videoLinksProvider.getRecentVideo().get(9));
    }

    private void addToRecentVideo() {
        for(int i = 11; i > 0; i--) {
            videoLinksProvider.addToRecentVideo("test link" + i);
        }
    }

    @Test
    public void testFindVideo() {
        saveVideo();

        List<String> result = videoLinksProvider.findVideo("test");

        assertEquals(6, result.size());
        assertTrue(FileProvider.extractName(result.get(0)).startsWith("test"));
        assertTrue(FileProvider.extractName(result.get(1)).startsWith("test"));
        assertTrue(FileProvider.extractName(result.get(3)).contains("test"));
    }

    @Test
    public void testValidateRecentVideo() {
        saveVideo();
        saveRecentVideo();

        assertEquals(5, videoLinksProvider.getRecentVideo().size());

        videoLinksProvider.validateRecentVideo();

        List<String> recentVideo = videoLinksProvider.getRecentVideo();
        assertEquals(4, recentVideo.size());
        assertTrue(recentVideo.get(0).endsWith("VID_20120921_232121.3gp"));
        assertTrue(recentVideo.get(1).endsWith("test.3gp"));
        assertTrue(recentVideo.get(2).endsWith("camera test.3gp"));
        assertTrue(recentVideo.get(3).endsWith("inside test camera.3gp"));
    }

    @Test
    public void testRemoveFromStorageLastVideoInFolder() {
        saveVideo();

        String removeVideo = "/storage/sdcard1/REMOVED/removed file.mp4";
        String storage = "sd card";
        String sdCard = "sd card";

        assertEquals(2, videoLinksProvider.getSdCardVideo().size());
        assertEquals("folder1", videoLinksProvider.getSdCardVideo().get(0).getFolderName());
        assertEquals("REMOVED", videoLinksProvider.getSdCardVideo().get(1).getFolderName());

        videoLinksProvider.removeFromStorage(removeVideo, storage, sdCard);

        List<FileProvider.VideoFolder> sdCardStorage = videoLinksProvider.getSdCardVideo();

        assertEquals(1, sdCardStorage.size());
        assertEquals("folder1", sdCardStorage.get(0).getFolderName());
    }

    private void saveVideo() {
        List<FileProvider.VideoFolder> sdCard = new ArrayList<>();
        List<String> sdFiles = new ArrayList<>();
        sdFiles.add("/storage/sdcard1/DCIM/Camera/VID_20120921_232121.3gp");
        sdFiles.add("/storage/sdcard1/DCIM/Camera/test.3gp");
        sdFiles.add("/storage/sdcard1/DCIM/Camera/camera test.3gp");
        sdFiles.add("/storage/sdcard1/DCIM/Camera/inside test camera.3gp");

        List<String> sdFiles2 = new ArrayList<>();
        sdFiles2.add("/storage/sdcard1/REMOVED/removed file.mp4");

        sdCard.add(new FileProvider.VideoFolder("folder1", sdFiles));
        sdCard.add(new FileProvider.VideoFolder("REMOVED", sdFiles2));

        List<FileProvider.VideoFolder> internalStorage = new ArrayList<>();
        List<String> internalFiles = new ArrayList<>();
        sdFiles.add("/storage/sdcard0/TEST/VID_20120921_232121.3gp");
        sdFiles.add("/storage/sdcard0/TEST/test folder.3gp");
        sdFiles.add("/storage/sdcard0/Folder/folder camera.3gp");
        sdFiles.add("/storage/sdcard0/Folder2/folder camera test.3gp");
        sdFiles.add("/storage/sdcard0/Folder/test inside camera.3gp");
        internalStorage.add(new FileProvider.VideoFolder("folder2", internalFiles));

        videoLinksProvider.saveExternalStorageVideo(sdCard);
        videoLinksProvider.saveInternalStorageVideo(internalStorage);
    }

    private void saveRecentVideo() {
        List<String> recentVideo = new ArrayList<>();
        recentVideo.add("/storage/sdcard1/DCIM/Camera/VID_20120921_232121.3gp");
        recentVideo.add("/storage/sdcard1/DCIM/Camera/test.3gp");
        recentVideo.add("/storage/sdcard1/DCIM/Camera/camera test.3gp");
        recentVideo.add("/storage/sdcard0/movies/removed video.mp4");
        recentVideo.add("/storage/sdcard1/DCIM/Camera/inside test camera.3gp");

        videoLinksProvider.saveRecentVideo(recentVideo);
    }
}
