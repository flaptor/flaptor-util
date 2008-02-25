package com.flaptor.util;

/**
 * Tests for {@link DocumentParser}
 */
public class DocumentParserTest extends TestCase {

    public void setUp() {
    }

    public void tearDown() {
    }

    @TestInfo (testType = TestInfo.TestType.UNIT)
    public void testParseIncompleteDocument() {
        DocumentParser parser = new DocumentParser();

        assertEquals("did not get null output on unparseable document",null, parser.genDocument("<xml><not><closed/>"));
        assertTrue("did not get a document after unparseable document",null != parser.genDocument("<xml><element/></xml>"));
    }

}

