package com.flaptor.util;

public class QueueTest extends TestCase
{

    public void setUp(){
    }

    public void tearDown(){
    }

    /**
        Test that the reported size is consistent with the objects added.
    */
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testSize(){
        Queue<String> q= new Queue<String>();
        assertEquals("Bad size reported", 0, q.size());
        q.enqueueNoBlock("1");
        assertEquals("Bad size reported", 1, q.size());
        q.enqueueNoBlock("2");
        assertEquals("Bad size reported", 2, q.size());

        q.dequeueNoBlock();
        assertEquals("Bad size reported", 1, q.size());
        q.dequeueNoBlock();
        assertEquals("Bad size reported", 0, q.size());
        q.dequeueNoBlock();
        assertEquals("Bad size reported", 0, q.size());
    }    
    
    /**
        Tests that the queue works fifo.
    */
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testOrder(){
        Queue<String> q= new Queue<String>();
        q.enqueueNoBlock("1");
        q.enqueueNoBlock("2");
        q.enqueueNoBlock("3");
        assertTrue("not in FIFO order",((String)q.dequeueNoBlock()).equals("1"));
        assertTrue("not in FIFO order",((String)q.dequeueNoBlock()).equals("2"));
        assertTrue("not in FIFO order",((String)q.dequeueNoBlock()).equals("3"));
    }

    /**
        Tests that the queue handles the maxSize correctly
    */
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testLimit(){
        Queue<String> q= new Queue<String>(2);
        assertTrue("Problem handling limits", q.enqueueNoBlock("1"));
        assertTrue("Problem handling limits", q.enqueueNoBlock("2"));
        assertFalse("Problem handling limits", q.enqueueNoBlock("3"));
        
        assertTrue("Problem handling limits", ((String)q.dequeueNoBlock()).equals("1"));
        assertTrue("Problem handling limits", ((String)q.dequeueNoBlock()).equals("2"));
        assertTrue("Problem handling limits", q.dequeueNoBlock() == null);
    }

    /**
        Tests that a blocked reader doesn't block new adds.
    */
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testBloquedReader(){
        Queue<String> q= new Queue<String>();
        BlockedReader br= new BlockedReader(q);
        br.start();
        try{
            Thread.sleep(300);
        }catch (InterruptedException e){ /*Ignore*/}
        assertTrue(br.running);
        assertFalse(br.free);
        q.enqueueNoBlock("1");
        try{
            Thread.sleep(300);
        }catch(InterruptedException e){/*ignore*/}
        assertTrue(br.free);
        
    }
    
    /**
        Tests that a blocked writer get freed by a reader.
    */
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testBloquedWriter(){
        Queue<String> q= new Queue<String>(1);
        assertTrue(q.enqueueNoBlock("1"));
        //The next enqueue should get blocked.
        BlockedWriter bw= new BlockedWriter(q);
        bw.start();
        try{
            Thread.sleep(300);
        }catch (InterruptedException e){ /*Ignore*/}
        assertTrue(bw.running);
        assertFalse(bw.free);
        q.dequeueNoBlock();
        try{
            Thread.sleep(300);
        }catch(InterruptedException e){/*ignore*/}
        assertTrue(bw.free);
        
    }

    /**
      Tests that a blocked writer waits at least the 
      specified amount when no new data is available.
     */
    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testWaitWriter(){
        Queue<String> q= new Queue<String>(1);
        assertTrue(q.enqueueNoBlock("1"));
        //The next enqueue should get blocked.
        WaitWriter ww1= new WaitWriter(q, 600);
        WaitWriter ww2= new WaitWriter(q, 600);
        ww1.start();
        ww2.start();
        try{
            Thread.sleep(200);
        }catch (InterruptedException e){ /*Ignore*/}
        assertTrue(ww1.running);
        assertFalse(ww1.free);
        assertTrue(ww2.running);
        assertFalse(ww2.free);
        //The next dequeue should wake only one of the 2 threads.
        assertTrue(q.dequeueNoBlock() != null);
        try{
            Thread.sleep(100);
        }catch(InterruptedException e){/*ignore*/}
        if (ww1.free){
            assertFalse(ww2.free);
            //Check that the other thread waits at least the specified
            //time.
            try{Thread.sleep(600);}catch(InterruptedException e){}
            assertTrue(ww2.free);
            assertTrue(ww2.elapsed >= 600);
        }else{
            assertTrue(ww2.free);
            //Check that the other thread waits at least the specified
            //time.
            try{Thread.sleep(600);}catch(InterruptedException e){}
            assertTrue(ww1.free);
            assertTrue(ww1.elapsed >= 600);
        }
    }



    //Private classes.
    class WaitWriter extends Thread{
        public long elapsed=0;
        public boolean free=false;
        public boolean running=false;
        private long timeout;
        Queue<String> q;
        public WaitWriter(Queue<String> queue, long tout){
            q= queue;
            timeout=tout;
        }
        public void run(){
            long start= System.currentTimeMillis();
            running=true;
            q.enqueueBlock("2",timeout);
            elapsed= System.currentTimeMillis()-start;
            free=true;
        }
    }

    class BlockedReader extends Thread{
        public boolean free=false;
        public boolean running=false;
        Queue<String> q;
        public BlockedReader(Queue<String> queue){
            q= queue;
        }
        public void run(){
            running=true;
            q.dequeueBlock();
            free=true;
        }
    }
    class BlockedWriter extends Thread{
        public boolean free=false;
        public boolean running=false;
        Queue<String> q;
        public BlockedWriter(Queue<String> queue){
            q= queue;
        }
        public void run(){
            running=true;
            q.enqueueBlock("2");
            free=true;
        }
    }
}
