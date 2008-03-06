package com.flaptor.util.remote;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

import com.flaptor.util.Execute;
import com.flaptor.util.IOUtil;
import com.flaptor.util.Pair;
import com.flaptor.util.TestCase;
import com.flaptor.util.TestInfo;

/**
 * Tests for {@link XmlrpcClient} calls and proxy Also tests for
 * {@link XmlrpcSerialization}
 * 
 * @author Martin Massera
 * 
 */
public class XmlrpcClientTest extends TestCase {
    private XmlrpcServer server;
    private XmlrpcClient client;
    static final int port = 50002;
    private URL url;

    public void setUp() throws MalformedURLException {
        server = new XmlrpcServer(port);
        server.addHandler(null, new ServiceImpl());
        server.start();
        url = new URL("http://localhost:" + port);
        client = new XmlrpcClient(url);
    }

    protected void tearDown() throws Exception {
        Execute.stop(server);
    }

     @TestInfo(testType = TestInfo.TestType.UNIT, requiresPort = { port })
    public void testSimple() throws XmlRpcException, IOException {
        assertEquals(1, new XmlRpcClient(url).execute("simpleMethod", new Vector()));
        assertEquals(client.execute(null, "simpleMethod", new Object[] {}), 1);
        Object o = XmlrpcClient.proxy(null, Service.class, client);
        assertTrue(o instanceof Service);
        Service s = (Service) o;
        assertEquals(s.simpleMethod(), 1);
    }

    @TestInfo(testType = TestInfo.TestType.UNIT, requiresPort = { port })
    @SuppressWarnings("unchecked")
    public void testSerialization() throws XmlRpcException, IOException, ClassNotFoundException {
        try {
            server.addHandler("context", new ComplexServiceImpl());
            Pair<String, String> pair = (Pair<String, String>) client.execute("context", "method", new Object[] { new Pair<Integer, Integer>(1, 1) });
            fail();
        } catch (Throwable t) {
        }

        server.addHandler("context2", XmlrpcSerialization.handler(new ComplexServiceImpl()));
        byte[] pairByteArray = (byte[]) client.execute("context2", "method", new Object[] { new Pair<Integer, Integer>(1, 1) });
        Pair<String, String> pair = (Pair<String, String>) IOUtil.deserialize(pairByteArray);
        assertEquals(pair.first(), pair.last());
        assertEquals(pair.first(), "1");

        ComplexService cs = (ComplexService) XmlrpcClient.proxy("context2", ComplexService.class, client);
        Pair<String, String> ps = cs.method(new Pair<Integer, Integer>(2, 3));
        assertEquals(ps, new Pair<String, String>("2", "3"));
    }

    @TestInfo(testType = TestInfo.TestType.UNIT, requiresPort = { port })
    public void testUnsupportedOperation() throws XmlRpcException {
        server.addHandler("list", new ArrayList());
        assertEquals(client.execute("list", "size", new Object[] {}), 0);
        try {
            client.execute("list", "lalalala", new Object[] {});
            fail();
        } catch (NoSuchRpcMethodException e) {
        }
    }

    @TestInfo(testType = TestInfo.TestType.UNIT, requiresPort = { port })
    public void testException() throws XmlRpcException {
        filterOutput("Array");
        server.addHandler("serviceException", XmlrpcSerialization.handler(new ServiceImplException()));
        try {
            client.execute("serviceException", "simpleMethod", new Object[] {});
            fail();
        } catch (RemoteHostCodeException e) {
            assertTrue(e.getMessage().contains("ArrayIndexOutOfBounds"));
        }
        try {
            ((Service) XmlrpcClient.proxy("serviceException", Service.class, client)).simpleMethod();
            fail();
        } catch (RemoteHostCodeException e) {
            assertTrue(e.getMessage().contains("ArrayIndexOutOfBounds"));
        }
        unfilterOutput();
    }

    @TestInfo(testType = TestInfo.TestType.UNIT)
    public void testConnectionException() throws XmlRpcException, MalformedURLException {
        filterOutput("Avoiding obscuring");
        URL url2 = new URL("http://localhost:12345");
        XmlrpcClient client2 = new XmlrpcClient(url2);
        try {
            client2.execute("pepe", "pepep", new Object[] {});
            fail();
        } catch (RpcConnectionException e) {
        }
        unfilterOutput();
    }

    static public interface Service {
        int simpleMethod();
    }

    static public interface ComplexService {
        Pair<String, String> method(Pair<Integer, Integer> pairInt);
    }

    static public class ServiceImpl implements Service {
        public int simpleMethod() {
            return 1;
        }
    }

    static public class ServiceImplException implements Service {
        public int simpleMethod() {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    static public class ComplexServiceImpl implements ComplexService {
        public Pair<String, String> method(Pair<Integer, Integer> pairInt) {
            return new Pair<String, String>(pairInt.first().toString(), pairInt.last().toString());
        }
    }
}
