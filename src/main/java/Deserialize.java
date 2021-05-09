import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class Deserialize {
    public static SendPack deserialize(byte[] obj)
    {
        try
        {
            ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(obj));
            SendPack sp = (SendPack) iStream.readObject();
            iStream.close();
            return sp;
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
