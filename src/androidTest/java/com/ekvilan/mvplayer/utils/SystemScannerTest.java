package com.ekvilan.mvplayer.utils;

import android.test.ActivityInstrumentationTestCase2;

import com.ekvilan.mvplayer.view.MainActivity;


import java.io.File;
import java.util.List;


public class SystemScannerTest extends ActivityInstrumentationTestCase2<MainActivity> {
    public SystemScannerTest() {
        super(MainActivity.class);
    }

    public void testGetVideo() {
        SystemScanner systemScanner = new SystemScanner();
        systemScanner.findVideoFiles();
        List<File> video = systemScanner.getVideoFiles();

        System.out.println(video.size());

    }
}
