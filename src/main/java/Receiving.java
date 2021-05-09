import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.time.LocalDateTime;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

public class Receiving {
    public static void receiving (PriorityQueue<SpaceMarine> pQueue, Gson g, LocalDateTime time_create, SortedSet<Integer> all_id, String FILE_PASS) throws IOException, InterruptedException {
        DatagramSocket serverSocket = new DatagramSocket(27914);
        DatagramChannel chan = DatagramChannel.open();
        chan.configureBlocking(false);
        chan.bind(new InetSocketAddress(27913));
        ByteBuffer buffer = ByteBuffer.allocate(2048);

        boolean god = true;
        while (god) {
            int i = 0;
            while (true) {
//                System.out.println("Waiting for a client to connect...");
//                serverSocket.receive(inputPacket);
//                InetAddress senderAddress = inputPacket.getAddress();
//                int senderPort = inputPacket.getPort();
                SocketAddress from = chan.receive(buffer);
                if (from!=null) {
                    SocketAddress from1 = new SocketAddress() {};
                    byte[] bytes1 = new byte[0];
                    while (from1!=null) {
                        buffer.flip();
                        int limits = buffer.limit();
                        byte bytes[] = new byte[limits];
                        buffer.get(bytes, 0, limits);
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
                        outputStream.write(bytes);
                        outputStream.write(bytes1);
                        bytes1 = outputStream.toByteArray();
                        buffer.clear();
                        from1 = chan.receive(buffer);
                    }
                    i = 0;
                    Treatment.treatment(Deserialize.deserialize(bytes1), time_create, pQueue, all_id, serverSocket, from);
                    buffer.clear();
                } else {
                    if (!chan.isConnected()) {
                        i++;
                        if (i>50) {
                            break;
                        }
                    }
                }
                TimeUnit.MILLISECONDS.sleep(500);
            }

            boolean gose = true;
            while (gose) {
                System.out.print("save/exit/next: ");
                Scanner in = new Scanner(System.in);
                switch (in.nextLine()) {
                    case ("save"):
                        try {
                            PrintWriter pw = new PrintWriter(FILE_PASS);
                            pw.write(g.toJson(pQueue));
                            pw.close();
                        } catch (FileNotFoundException f) {
                            System.out.println("Файл не найден");
                        }
                        break;
                    case ("exit"):
                        gose = false;
                        god = false;
                        break;
                    case ("next"):
                        gose = false;
                        break;
                    default:
                        System.out.println("Неизвесная команда.");
                        break;
                }
            }
        }
        serverSocket.close();
    }
}
