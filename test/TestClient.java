import echoclient.EchoClient;
import echoclient.EchoListener;
import echoserver.ClientHandler;
import echoserver.EchoServer;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Lars Mortensen
 */
public class TestClient implements EchoListener {

    private String regex = "#";
    private String regex2 = ",";
    private String[] sp;
    private String[] sp2;
    private String msg;
    private EchoClient client;

    public TestClient() {
    }

    @BeforeClass
    public static void setUpClass() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EchoServer.runServer();
            }
        }).start();
    }

    @AfterClass
    public static void tearDownClass() {
        EchoServer.stopServer();
    }

    @Before
    public void setUp() {
        client = new EchoClient();
    }

//    @Test
//    public void stopServerProtocol() throws IOException, InterruptedException {
//        //      EchoClient client = new EchoClient();
//        client.registerEchoListener(this);
//        client.connect("localhost", 9090);
////        client.send("CONNECT#Per");
//        assertTrue(client.getSocket().isConnected());
//        
//        client.send("##STOP##");
//        client.join();
//        assertTrue(client.getSocket().isConnected());
//    }
    
    @Test
    public void Online() throws IOException, InterruptedException {
        client = new EchoClient();
        client.registerEchoListener(this);
        client.connect("localhost", 9090);
        
        assertTrue(client.getSocket().isConnected());
        
        client.send("CONNECT#Per");
        client.sleep(1000);
        
        assertEquals("ONLINE", sp[0]);
        assertEquals("Per", sp2[0]);
        
        EchoClient client2 = new EchoClient();
        client2.registerEchoListener(this);
        client2.connect("localhost", 9090);
        
        assertTrue(client2.getSocket().isConnected());
        
        client2.send("CONNECT#Lasse");
        client2.sleep(1000);
        
        assertEquals("ONLINE", sp[0]);
        assertEquals("Per", sp2[0]);
        assertEquals("Lasse", sp2[1]);
        
        EchoClient client3 = new EchoClient();
        client3.registerEchoListener(this);
        client3.connect("localhost", 9090);
        assertTrue(client3.getSocket().isConnected());
        client3.send("CONNECT#Rasmus");
        client3.sleep(1000);
        
        assertEquals("ONLINE", sp[0]);
        assertEquals("Per", sp2[0]);
        assertEquals("Lasse", sp2[1]);
        assertEquals("Rasmus", sp2[2]);
        
        client3.send("CLOSE#Rasmus");
        client3.sleep(1000);
        
        assertEquals("ONLINE", sp[0]);
        assertEquals("Per", sp2[0]);
        assertEquals("Lasse", sp2[1]);
        assertNotSame("Rasmus", sp2[2]);
        
        client2.send("CLOSE#");
        client2.sleep(1000);
        
        assertEquals("ONLINE", sp[0]);
        assertEquals("Per", sp2[0]);
        assertNotSame("Lasse", sp2[1]);
        
        client.send("CLOSE#");
    }

    @Test
    public void send() throws IOException, InterruptedException {
        client = new EchoClient();
        client.registerEchoListener(this);
        client.connect("localhost", 9090);
        client.send("CONNECT#Per");
        client.send("SEND#*#Hello");
        client.sleep(1000);
        
        assertEquals("MESSAGE", sp[0]);
        assertEquals("Per", sp[1]);
        assertEquals("Hello", sp[2]);
        
        EchoClient client2 = new EchoClient();
        client2.registerEchoListener(this);
        client2.connect("localhost", 9090);
        client2.send("CONNECT#Lasse");
        client2.send("SEND#Per#Hello Per");
        client2.sleep(1000);
        
        assertEquals("MESSAGE", sp[0]);
        assertEquals("Lasse", sp[1]);
        assertEquals("Hello Per", sp[2]);
        
        client.send("CLOSE#");
        client2.send("CLOSE#");
    }
//

    @Test
    public void regUnRegEchoListener() throws InterruptedException {

        client = new EchoClient();
        
        assertTrue(client.getListeners().isEmpty());
        
        client.registerEchoListener(this);
        assertTrue(client.getListeners().size() == 1);
        
        client.registerEchoListener(this);
        assertTrue(client.getListeners().size() == 2);
        
        client.unRegisterEchoListener(this);
        assertTrue(client.getListeners().size() == 1);
        
        client.unRegisterEchoListener(this);
        assertTrue(client.getListeners().isEmpty());
    }

    @Override
    public void messageArrived(String data) {

        msg = data;
        sp = data.split(regex);
        sp2 = sp[1].split(regex2); //To change body of generated methods, choose Tools | Templates.
    }

}
