import java.io.*;
public class Serialize {
    public static byte[] serialize(ReturnPack rp)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(rp);
            oos.close();
            byte[] obj = baos.toByteArray();
            baos.close();
            return obj;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}