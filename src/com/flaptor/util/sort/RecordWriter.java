package com.flaptor.util.sort;

import java.io.IOException;

/**
 *  This interface allows the sorting algorithm to write records to a file.
 */
public interface RecordWriter {

    /**
     * Write a record to a file.
     * @param rec the record that has to be written.
     */
    public void writeRecord(Record rec) throws IOException ;

    /**
     *  Close the writer.
     */
    public void close() throws IOException ;

}

