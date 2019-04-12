package ui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Helper for clients to connect/send moves to Connect4Server
 *
 * @author Joshua Kronemeyer
 * @version 1.0
 * @date 4 /09/2019
 */
class Connect4Client {
    private final DataInputStream dIn;
    private final DataOutputStream dOut;
    private int lastQuery;

    /**
     * Connect to client
     *
     * @throws IOException When it cannot connect to server
     */
    Connect4Client() throws IOException {
        InetAddress ip = InetAddress.getByName("localhost");
        Socket s = new Socket(ip, 8082);

        dIn = new DataInputStream(s.getInputStream());
        dOut = new DataOutputStream(s.getOutputStream());

        System.out.println("Connected: " + s.toString());
    }

    /**
     * Gets initial.
     *
     * @return the initial
     * @throws IOException the io exception
     */
    String getInitial() throws IOException {
        return dIn.readUTF();
    }

    /**
     * Send move int.
     *
     * @param s Client move
     * @return Client move validity/win
     * @throws IOException Unable to connect to server
     */
    int sendMove(String s) throws IOException {
        dOut.writeUTF(s);
        s = dIn.readUTF();
        lastQuery = Integer.parseInt(s);
        return Integer.parseInt(s);
    }

    /**
     * The last query from the server
     *
     * @return The last query from the server
     */
    int getLastQuery() {
        return lastQuery;
    }

    /**
     * Gets player move.
     *
     * @return the player move
     * @throws IOException the io exception
     */
    int getPlayerMove() throws IOException {
        String s = dIn.readUTF();
        return Integer.parseInt(s);
    }
}