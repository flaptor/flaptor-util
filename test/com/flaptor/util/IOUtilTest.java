package com.flaptor.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

/**
 * Tests for IOUtil
 * @author Martin Massera
 *
 */
public class IOUtilTest extends TestCase{

    /**
     * tests that tail allows to write at the same time
     */
    public void testConcurrentTail() throws IOException {
        final File f = TestUtils.createTempFileWithContent(TestUtils.randomText(1000, 20000));
        new Thread() {
            public void run() {
                for (int i = 0; i < 50000; ++i) {
                    try {
                        IOUtil.tail(f, 100);
                    } catch (IOException e) {
                        fail();
                    }
                }
            }
        }.start();
        
        for (int i = 0; i < 50000; ++i) {
            RandomAccessFile rafile = new RandomAccessFile(f, "rw");
            rafile.seek(new Random().nextInt((int)rafile.length()));
            rafile.write("holalola".getBytes());
            rafile.close();
        }
    }
}
