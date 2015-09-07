package com.ekvilan.mvplayer.controllers.providers;


import com.ekvilan.mvplayer.utils.FileProvider;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

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
        assertTrue(videoLinksProvider.splitVideo(result.get(0)).startsWith("test"));
        assertTrue(videoLinksProvider.splitVideo(result.get(1)).startsWith("test"));
        assertTrue(videoLinksProvider.splitVideo(result.get(3)).contains("test"));
    }

    private void saveVideo() {
        List<FileProvider.VideoFolder> sdCard = new ArrayList<>();
        List<String> sdFiles = new ArrayList<>();
        sdFiles.add("/storage/sdcard1/DCIM/Camera/VID_20120921_232121.3gp");
        sdFiles.add("/storage/sdcard1/DCIM/Camera/test.3gp");
        sdFiles.add("/storage/sdcard1/DCIM/Camera/camera test.3gp");
        sdFiles.add("/storage/sdcard1/DCIM/Camera/inside test camera.3gp");
        sdCard.add(new FileProvider.VideoFolder("folder1", sdFiles));

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
}
