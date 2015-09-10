package com.ekvilan.mvplayer.utils;

import com.ekvilan.mvplayer.models.VideoInfo;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class FileProviderUnitTest {
    private FileProvider fileProvider;

    @Before
    public void init() {
        fileProvider = new FileProvider();
    }

    @Test
    public void testCreateVideoFoldersWithTwoParents() {
        List<VideoInfo> infoList = new ArrayList<>();
        infoList.add(new VideoInfo("name1", "path1", "parent1"));
        infoList.add(new VideoInfo("name2", "path2", "parent1"));
        infoList.add(new VideoInfo("name3", "path3", "parent2"));

        List<FileProvider.VideoFolder> actual = fileProvider.createVideoFolders(infoList);

        assertEquals(2, actual.size());
        assertEquals("parent1", actual.get(0).getFolderName());
        assertEquals("parent2", actual.get(1).getFolderName());
        assertEquals(2, actual.get(0).getVideoLinks().size());
        assertEquals(1, actual.get(1).getVideoLinks().size());
    }

    @Test
    public void testCreateVideoFoldersWithOneParentOneVideoLink() {
        List<VideoInfo> infoList = new ArrayList<>();
        infoList.add(new VideoInfo("name1", "path1", "parent1"));

        List<FileProvider.VideoFolder> actual = fileProvider.createVideoFolders(infoList);

        assertEquals(1, actual.size());
        assertEquals("parent1", actual.get(0).getFolderName());
        assertEquals(1, actual.get(0).getVideoLinks().size());
    }

    @Test
    public void testCreateVideoFoldersWithOneParentManyVideoLink() {
        List<VideoInfo> infoList = new ArrayList<>();
        infoList.add(new VideoInfo("name1", "path1", "parent1"));
        infoList.add(new VideoInfo("name2", "path2", "parent1"));
        infoList.add(new VideoInfo("name3", "path3", "parent1"));

        List<FileProvider.VideoFolder> actual = fileProvider.createVideoFolders(infoList);

        assertEquals(1, actual.size());
        assertEquals("parent1", actual.get(0).getFolderName());
        assertEquals(3, actual.get(0).getVideoLinks().size());
    }

    @Test
    public void testCreateVideoFoldersWithManyParentsManyVideoLink() {
        List<VideoInfo> infoList = new ArrayList<>();
        infoList.add(new VideoInfo("name1", "path1", "parent1"));
        infoList.add(new VideoInfo("name2", "path2", "parent1"));
        infoList.add(new VideoInfo("name3", "path3", "parent2"));
        infoList.add(new VideoInfo("name4", "path4", "parent2"));
        infoList.add(new VideoInfo("name5", "path5", "parent3"));
        infoList.add(new VideoInfo("name6", "path6", "parent4"));

        List<FileProvider.VideoFolder> actual = fileProvider.createVideoFolders(infoList);

        assertEquals(4, actual.size());
        assertEquals("parent1", actual.get(0).getFolderName());
        assertEquals("parent2", actual.get(1).getFolderName());
        assertEquals("parent3", actual.get(2).getFolderName());
        assertEquals("parent4", actual.get(3).getFolderName());
        assertEquals(2, actual.get(0).getVideoLinks().size());
        assertEquals(2, actual.get(1).getVideoLinks().size());
        assertEquals(1, actual.get(2).getVideoLinks().size());
        assertEquals(1, actual.get(3).getVideoLinks().size());
    }

    @Test
    public void testChangeName() {
        String path = "/storage/sdcard1/test/file name.3gp";
        String newName = "changed name.3gp";
        String expected = "/storage/sdcard1/test/changed name.3gp";

        assertEquals(expected, fileProvider.changeName(path, newName));
    }
}
