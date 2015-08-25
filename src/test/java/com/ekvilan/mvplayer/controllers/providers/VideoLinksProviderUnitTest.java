package com.ekvilan.mvplayer.controllers.providers;


import org.junit.Before;
import org.junit.Test;

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
}
