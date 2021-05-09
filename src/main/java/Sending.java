import java.io.IOException;
import java.net.*;

public class Sending {
    public static void sanding (ReturnPack rp, DatagramSocket serverSocket, SocketAddress address) throws SocketException {
        byte[] data = Serialize.serialize(rp);
        try
        {
            DatagramPacket dp = new DatagramPacket(data, data.length, address);
            serverSocket.send(dp);
        }
        catch(IOException e)
        {
            System.err.println("IOException senderissa " + e);
        }
    }
}
