/*
Copyright 2008 Flaptor (flaptor.com) 

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

    http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License.
*/

package com.flaptor.util.sort;

import java.io.IOException;

/**
 * This interface allows the sorting algorithm to read records from a file.
 * 
 * This code is based on the code found in the book
 * "Developing Java Software" by Winder and Roberts
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

