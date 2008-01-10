package com.flaptor.util.sort;

import java.io.File;
import java.io.IOException;

/**
 *  An interface encapsulating Record information.
 */
public interface RecordInformation {

    /**
     * Return a comparator for the Record so it can be sorted.
     * @return a comparator for the Record.
     */
    public Comparator getComparator();

    /**
     * Returns a BufferedReader so that Records can be read from a file.
     * @param filein the file from which the records will be read.
     */
    public RecordReader newRecordReader(File filein) throws IOException;

    /**
     * Returns a BufferedWriter so that Records can be written to a file.
     * @param fileout the file to which the records will be written.
     */
    public RecordWriter newRecordWriter(File fileout) throws IOException;


}

