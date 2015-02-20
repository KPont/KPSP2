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

    String regex = "#";
    String regex2 = ",";
    String[] sp;
    String[] sp2;

    public TestClient() {
    }

    @BeforeClass
    public static void setUpClass() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EchoServer.main(null);
            }
        }).start();
    }

    @AfterClass
    public static void tearDownClass() {
        EchoServer.stopServer();
    }

    @Before
    public void setUp() {

    }

    @Test
    public void connect() throws IOException {
        EchoClient client = new EchoClient();
        client.registerEchoListener(this);
        assertFalse(client.isAlive());
        client.connect("localhost", 9090);
        assertTrue(client.isAlive());
        assertTrue(client.getSocket().getPort() == 9090);
        assertTrue(client.getSocket().isConnected());

    }

    @Test
    public void stopServer() throws IOException, InterruptedException {
        EchoClient client = new EchoClient();
        client.registerEchoListener(this);
        client.connect("localhost", 9090);
        assertTrue(client.getSocket().isConnected());

        client.stopServer();
        client.join();
        assertTrue(client.getSocket().isClosed());

    }

    @Test
    public void send() throws IOException {
        EchoClient client = new EchoClient();
        client.registerEchoListener(this);
        client.connect("localhost", 9090);
        client.send("Hello");
        try {
            client.join();
            assertEquals("Hello", sp[0]);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Test
    public void regUnRegEchoListener() throws InterruptedException {

        EchoClient client = new EchoClient();
        assertTrue(client.getListeners().isEmpty());
        client.join();
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

        sp = data.split(regex);
        sp2 = sp[1].split(regex2); //To change body of generated methods, choose Tools | Templates.
    }

    public String[] getSp() {
        return sp;
    }

    public String[] getSp2() {
        return sp2;
    }

}
