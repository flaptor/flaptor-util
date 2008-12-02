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

package com.flaptor.util;

import com.flaptor.util.compression.CompressedIntSequence;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class TextSignature implements Serializable {

    private static final long serialVersionUID = 1L;
    protected CompressedIntSequence components = null;

    // Adds a word to the hash.
    private int addWord (int hash, String word) {
        int code = word.hashCode();
        code = (code < 0) ? -code : code;
        hash ^= code;
        return hash;
    }

    // Removes a word from the hash. This is simply a call to addWord,
    // because it uses xor, which is reversed by reapplying.
    private int removeWord (int hash, String word) {
        return addWord(hash, word);
    }

    /**
     * Creates a signature of the provided text.
     * @param text the text from which to compute the signature.
     */
    public TextSignature (String text) {

        int[] data = new int[256];

        String[] words = text.split("\\W+"); // should be changed by a customized implementation (for speed).
        int windowSize = 4;
        int head = 0;
        int tail = 1-windowSize;
        int hash = 0;
        boolean hashing = false;

        while (head < words.length) {
            hash = addWord(hash, words[head]);
            if (tail == 0) {
                hashing = true;
            }
            if (hashing) {
                data[hash%256]++;
                hash = removeWord(hash, words[tail]);
            }
            head++;
            tail++;
        }
        
        components = new CompressedIntSequence(data);
    }



    /**
     * Compares this signature with the specified signature for similarity. 
     * Returns a number in the 0..1 range that measures the similarity of this signature to another signature.
     * @param otherSig the other signature.
     * @return A float in the 0..1 range indicating the similarity to the provided signature. 0 means different, 1 means equal.
     */
    public float compareTo (TextSignature other) {
        int union_size = 0;
        int intersection_size = 0;
        int[] data1 = components.getUncompressed();
        int[] data2 = other.components.getUncompressed();
        for (int i=0; i<256; i++) {
            union_size += Math.max(data1[i], data2[i]);
            intersection_size += Math.min(data1[i], data2[i]);
        }
        return (float)intersection_size / union_size;
    }


    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TextSignature)) { return false; }
        return components.equals(((TextSignature)other).components);
    }

    @Override
    public int hashCode() {
        return components.hashCode();
    }

    @Override
    public String toString() {
        return components.toString();
    }

    // For testing purposes
    public static void main (String[] args) throws Exception {
/*
        try {
            File file1 = new File(args[0]);
            File file2 = new File(args[1]);
            String string1 = FileUtil.readFile(file1);
            String string2 = FileUtil.readFile(file2);
            TextSignature t1 = new TextSignature(string1);
            TextSignature t2 = new TextSignature(string2);
            System.out.println("Similarity: " + t1.compareTo(t2));
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
*/

        File dir = FileUtil.createTempDir("","");
        File file = new File(dir,"signature.tst");
        long max = 100000;
        for (int i=0; i<max; i++) {
            String text = TestUtils.randomText(10,500);
//            System.out.println("=================================================================");
//            System.out.println(text);
//            System.out.println("-----------------------------------------------------------------");
   
            TextSignature sig1 = new TextSignature(text);
            int[] data = sig1.components.getUncompressed();

            ObjectOutputStream outputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file, false), 16000));
            outputStream.writeObject(sig1);
            outputStream.close();

            ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file), 16000));
            TextSignature sig2 = (TextSignature)inputStream.readObject();
  
            if (!sig1.equals(sig2)) {
                System.out.println("ERROR! Sig serialization self-mismatch");
                System.exit(-1);
            }
            
            if (sig1.compareTo(sig2) != 1.0f) {
                System.out.println("ERROR! Sig serialization self-mismatch");
                System.exit(-1);
            }

//            System.out.println("SIG ("+data.length+" bytes): "+sig1.toString());
        }
        System.out.println("Ok!");
        FileUtil.deleteDir(dir);
    }

}

