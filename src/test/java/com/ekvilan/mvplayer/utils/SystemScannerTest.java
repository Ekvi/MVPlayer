package com.ekvilan.mvplayer.utils;

import org.junit.Test;

import java.io.File;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class SystemScannerTest {

    @Test
    public void testGetVideo() {
        SystemScanner systemScanner = new SystemScanner();
        systemScanner.findVideoFiles();
        List<File> video = systemScanner.getVideoFiles();

        System.out.println(video.size());

    }
}
