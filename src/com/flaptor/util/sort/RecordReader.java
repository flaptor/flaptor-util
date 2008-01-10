package com.flaptor.util.sort;

import java.io.IOException;

/**
 *  This interface allows the sorting algorithm to read records from a file.
 */
public interface RecordReader {

    /**
     * Read a record and return it.
     * @return the read record.
     */
    public Record readRecord() throws IOException;

    /**
     * Close the reader.
     */
    public void close() throws IOException;

}

