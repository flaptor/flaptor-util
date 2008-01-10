package com.flaptor.util;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeMap;
import java.util.Vector;



public class TrieTreeTest extends TestCase {

    Random rnd = null;

    public void setUp() throws IOException {
        rnd = new Random(new Date().getTime());
    }

    public void tearDown() {
    }

    private String randomString () {
        int min = 5;
        int max = 20;
        StringBuffer buf = new StringBuffer();
        int len = min + rnd.nextInt(max-min+1);
        for (int i=0; i<len; i++) {
            char c = (char) ('a' + rnd.nextInt('z'-'a'));
            buf.append(c);
        }
        return buf.toString();
    }

    private void check (String[] keys, String[] negatives, boolean checkDuplicates, boolean hasDuplicates) {
        boolean exceptionThrown = false;
        try {
            TrieTree<String> tt = new TrieTree<String>();
            for (String key : keys) {
                if (checkDuplicates) {
                    tt.add(key,key);
                } else {
                    tt.put(key,key);
                }
            }
            boolean found;
            for (String key : keys) {
                found = tt.hasKey(key);
                assertTrue("Couldn't find a string that should be present", found);
                String val = (String)tt.get(key);
                assertTrue("Wrong value returned", key.equals(val));
            }
            for (String neg : negatives) {
                found = tt.hasKey(neg);
                assertFalse("Found a string that should not be present", found);
            }
        } catch (Exception e) {
            assertTrue("Wrong exception type", e instanceof TrieTree.DuplicateKeyException);
            exceptionThrown = true;
        }
        if (checkDuplicates && hasDuplicates) {
            assertTrue("No exception thrown", exceptionThrown);
        }
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testEmpty () {
        check (new String[] {}, new String[] {"empty"}, false, false);
    }
    
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testOne () {
        check (new String[] {"one"}, new String[] {"n","no","o","on","ones"}, false, false);
        check (new String[] {"one"}, new String[] {"n","no","o","on","ones"}, true, false);
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testTwoSeparate () {
        check(new String[] {"one","two"}, new String[] {"n","no","o","on","ones","t","tw","twos"}, false, false);
        check(new String[] {"one","two"}, new String[] {"n","no","o","on","ones","t","tw","twos"}, true, false);
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testTwoSameRoot () {
        check(new String[] {"abc","axy"}, new String[] {"not","a","ab","abcd","ax","axyz"}, false, false);
        check(new String[] {"abc","axy"}, new String[] {"not","a","ab","abcd","ax","axyz"}, true, false);
        check(new String[] {"abc","aby"}, new String[] {"not","a","ab","abcd","ax","axyz"}, false, false);
        check(new String[] {"abc","aby"}, new String[] {"not","a","ab","abcd","ax","axyz"}, true, false);
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testTwoIncluded () {
        check(new String[] {"abc","a"}, new String[] {"not","ab","abcd"}, false, false);
        check(new String[] {"abc","a"}, new String[] {"not","ab","abcd"}, true, false);
        check(new String[] {"abc","ab"}, new String[] {"not","a","abcd"}, false, false);
        check(new String[] {"abc","ab"}, new String[] {"not","a","abcd"}, true, false);
        check(new String[] {"a","abc"}, new String[] {"not","ab","abcd"}, false, false);
        check(new String[] {"a","abc"}, new String[] {"not","ab","abcd"}, true, false);
        check(new String[] {"ab","abc"}, new String[] {"not","a","abcd"}, false, false);
        check(new String[] {"ab","abc"}, new String[] {"not","a","abcd"}, true, false);
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testDuplicate () {
        check (new String[] {"abc","abc"}, new String[] {""}, false, true);
        check (new String[] {"abc","abc"}, new String[] {""}, true, true);
    }

    private void checkPartial (String[] keys, String search, int[] results) {
        TrieTree<Integer> tt = new TrieTree<Integer>();
        for (int i=0; i<keys.length; i++) {
            tt.add(keys[i], new Integer(i+1));
        }
        Vector<TrieTree<Integer>.PartialMatch<Integer>> res = tt.getPartialMatches(search);
        assertTrue("Number of results ("+res.size()+") doesn't match expected number ("+results.length+")", res.size() == results.length);
        for (int i=0; i<res.size(); i++) {
            int val = res.elementAt(i).getValue().intValue();
            assertTrue("Returned value ("+val+") doesn't match expected value ("+results[i]+")", val == results[i]);
            int pos = res.elementAt(i).getPosition();
            int keypos = keys[val-1].length()-1;
            assertTrue("Returned position ("+pos+") doesn't match expected position ("+keypos+")", pos == keypos);
        }
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testPartial () {
        checkPartial(new String[] {"ab"}, "xyz", new int[] {});
        checkPartial(new String[] {"ab"}, "ab", new int[] {1});
        checkPartial(new String[] {"ab"}, "abc", new int[] {1});
        checkPartial(new String[] {"ab","abcd"}, "abc", new int[] {1});
        checkPartial(new String[] {"ab","abcd"}, "abcde", new int[] {1,2});
        checkPartial(new String[] {"ab","cd"}, "abc", new int[] {1});
        checkPartial(new String[] {"ab","cd"}, "cde", new int[] {2});
        checkPartial(new String[] {"ab","cd"}, "xyz", new int[] {});
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testRandom (int tests, int maxTreeSize) {
        for (int i = 0; i < tests; i++) {
            int treeSize = 1+rnd.nextInt(maxTreeSize);
            TreeMap<String,Integer> map = new TreeMap<String,Integer>();
            String notAdded = randomString();
            TrieTree<Integer> tt = new TrieTree<Integer>();
            for (int j = 1; j < treeSize; j++) {
                String key;
                do {
                    key = randomString();
                } while (notAdded.equals(key));
                Integer val = new Integer(j);
                tt.put(key, val);
                map.put(key, val);
            }
            boolean found;
            Iterator<String> it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                Integer treeval = tt.get(key);
                Integer mapval = map.get(key);
                assertTrue("Wrong value returned, expected ["+mapval+"], got ["+treeval+"]", mapval.equals(treeval));
            }
            found = tt.hasKey(notAdded);
            assertFalse("Found a string that should not be present", found);
        }
    }
}

