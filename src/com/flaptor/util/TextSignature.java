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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class TextSignature implements Serializable {

    private static final long serialVersionUID = 1L;
    protected int[] components = null;

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

        components = new int[256];

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
                components[hash%256]++;
                hash = removeWord(hash, words[tail]);
            }
            head++;
            tail++;
        }

    }


    /**
     * Compares this signature with the specified signature for similarity. 
     * Returns a number in the 0..1 range that measures the similarity of this signature to another signature.
     * @param otherSig the other signature.
     * @return A float in the 0..1 range indicating the similarity to the provided signature. 0 means different, 1 means equal.
     */
    public float compareTo (TextSignature otherSig) {
        int union_size = 0;
        int intersection_size = 0;
        for (int i=0; i<256; i++) {
            union_size += Math.max(components[i], otherSig.components[i]);
            intersection_size += Math.min(components[i], otherSig.components[i]);
        }
        return (float)intersection_size / union_size;
    }

    /**
     * Exports this siganture into a string.
     * @return a string that represents this signature.
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        for (int i=0; i<256; i++) {
            int val = components[i];
            if (val < 0xFC) { // FC=11111100, we could use the lowest two bits as the number of bytes to read next.
                oos.writeByte(val);
            } else {
                oos.writeByte(0xFF);
                oos.writeInt(val);
            }
        }
    }

    /**
     * Imports this siganture from a string.
     * @param str a string that represents a signature.
     */
    private void readObject (ObjectInputStream ois) throws IOException, ClassNotFoundException {
        components = new int[256];
        for (int i=0; i<256; i++) {
            int val = ois.readByte();
            if (val < 0) val += 256; // convert signed to unsigned
            if (val >= 0xFC) {
                val = ois.readInt();
            } else
            components[i] = val;
        }
    }


    /**
     * @override
     * 
     * only equals if the other is TextSignature and components are the
     * same, for every component
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof TextSignature)) return false;
        TextSignature other = (TextSignature)obj;
        return java.util.Arrays.equals(components, other.components);
    }

    public int hashCode() {
        return components[0];
    }

    public String toString() {
        String sep = "";
        StringBuffer buf = new StringBuffer();
        buf.append("[");
        for (int i=0; i<256; i++) {
            buf.append(sep+components[i]);
            sep = ",";
        }
        buf.append("]");
        return buf.toString();
    }

    public static void main (String[] args) throws Exception {
        File file1 = new File(args[0]);
        File file2 = new File(args[1]);
        char[] car1 = new char[1000000];
        char[] car2 = new char[1000000];
        try {
            FileReader fr1 = new FileReader(file1);
            FileReader fr2 = new FileReader(file2);
            fr1.read(car1);
            fr2.read(car2);
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
        String string1 = new String(car1);
        String string2 = new String(car2);
        TextSignature t1 = new TextSignature(string1);
        TextSignature t2 = new TextSignature(string2);
        System.out.println("Similarity: " + t1.compareTo(t2));
    }

}

