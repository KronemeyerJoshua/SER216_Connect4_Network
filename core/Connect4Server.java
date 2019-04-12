package core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The type Connect 4 server.
 */
public class Connect4Server {
    /**
     * The entry point of application.
     *
     * @param args Optional Args (Could be used to setup custom ports)
     * @throws IOException Unlikely to throw an exception, but will if resources cannot be allocated
     */
    public static void main(String[] args) throws IOException {
        ServerSocket sSocket = new ServerSocket(8082);
        while (true) {
            Socket socket = sSocket.accept();
            System.out.println("Client 1 is connecting...");

            DataInputStream dIn = new DataInputStream(socket.getInputStream());
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

            Socket socket2 = sSocket.accept();
            System.out.println("Client 2 is connecting...");

            DataInputStream dIn2 = new DataInputStream(socket2.getInputStream());
            DataOutputStream dOut2 = new DataOutputStream(socket2.getOutputStream());

            System.out.println("Spinning new server thread");

            Thread t = new Connect4Client(socket, socket2, dIn, dOut, dIn2, dOut2);

            t.start();
        }
    }
}

/**
 * The type Connect 4 client.
 */
class Connect4Client extends Thread {
    private final Socket socket, socket2;
    private final DataInputStream dIn, dIn2;
    private final DataOutputStream dOut, dOut2;
    private Connect4 game;

    /**
     * Constructor
     *
     * @param socket  The socket we are on
     * @param socket2 the socket 2
     * @param dIn     DataInputStream (from Client)
     * @param dOut    DataOutputStream (to Client)
     * @param dIn2    the d in 2
     * @param dOut2   the d out 2
     * @throws IOException the io exception
     */
    Connect4Client(Socket socket, Socket socket2, DataInputStream dIn, DataOutputStream dOut, DataInputStream dIn2, DataOutputStream dOut2) throws IOException {
        game = new Connect4();
        this.dOut = dOut;
        this.dIn = dIn;
        this.socket = socket;
        this.socket2 = socket2;
        this.dIn2 = dIn2;
        this.dOut2 = dOut2;
        dOut.writeUTF(game.getCurrentPlayer());
        dOut2.writeUTF(game.getNonCurrentPlayer());
    }

    @Override
    public void run() {
        String fromClient;
        while (true) {
            try {
                // First Client
                fromClient = dIn.readUTF();
                System.out.println("Received input " + fromClient);

                if (fromClient.equals("r")) { // Reset via Networking isn't implemented yet
                    game.initBoard();
                }
                else if (fromClient.equals("q")) {
                    break; // Terminate
                } else if (!game.update(fromClient)) {
                    while (!game.update(fromClient)) {
                        dOut.writeUTF("1"); // Invalid
                        fromClient = dIn.readUTF();
                    }
                    dOut.writeUTF("0"); // Valid
                    dOut2.writeUTF(fromClient);
                } else if (game.checkWin()) {
                    System.out.println("Win!");
                    dOut.writeUTF("-2"); // Win
                    dOut2.writeUTF("-2");
                    dOut2.writeUTF(fromClient);
                } else {
                    dOut.writeUTF("0"); // Valid
                    dOut2.writeUTF(fromClient);
                    System.out.println("Sending " + fromClient + " to Client 2");
                }
                // Second Client
                fromClient = dIn2.readUTF();
                System.out.println("Received input " + fromClient);
                if (fromClient.equals("r")) {
                    game.initBoard();
                }
                else if (fromClient.equals("q")) {
                    break; // Terminate
                } else if (!game.update(fromClient)) {
                    while (!game.update(fromClient)) {
                        dOut2.writeUTF("1"); // Invalid
                        fromClient = dIn2.readUTF();
                    }
                    dOut2.writeUTF("0"); // Valid
                    dOut.writeUTF(fromClient);
                } else if (game.checkWin()) {
                    System.out.println("Win!");
                    dOut.writeUTF("-2"); // Win
                    dOut.writeUTF(fromClient);
                    dOut2.writeUTF("-2");
                } else {
                    dOut2.writeUTF("0"); // Valid
                    dOut.writeUTF(fromClient);
                    System.out.println("Sending " + fromClient + " to Client 1");
                }
            } catch (Exception e) {
                System.out.println("Client terminated connection closing");
                break;
            }
        }

        try {
            this.dIn.close();
            this.dOut.close();
            this.dIn2.close();
            this.dOut2.close();
            this.socket.close();
            this.socket2.close();
        } catch (Exception e) {
            System.out.println("Could not close server correctly... Check task manager.");
        }

    }
}
