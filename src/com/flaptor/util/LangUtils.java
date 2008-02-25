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


/**
 * Utility class for language identifying (a wrapper for nutch's language identifiers)
 *  
 * @author Martin Massera
 */
public class LangUtils {

    // Language identifier
    private static NgramJLanguageIdentifier ngramjIdentifier;


    // Initialize NgramJLanguageIdentifier
    // It is done this way to catch the exception
    static {
        try {
            ngramjIdentifier = new NgramJLanguageIdentifier();
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String identify(String text) {
        return ngramjIdentifier.identify(text);
    } 

    public static boolean isEnglish(String text) {
        return "en".equals(identify(text));
    }

    public static void main(String[] args) throws Exception {
        for (String file: args) {
            java.io.FileInputStream fis = new java.io.FileInputStream(file);
            String input = IOUtil.readAll(fis);
            Execute.close(fis);
            System.out.println(file + ":" + LangUtils.identify(input));
        }
    }
}
