import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

public class Main {
    private static final String[] exc = {
            "Файл не найден.",
            "Ошибка структуры файла.",
            "Ошибка ID элементов."
    };
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner in = new Scanner(System.in);
        String FILE_PASS = in.nextLine();
        Scanner scan = null;
        LocalDateTime time_create;
        PriorityQueue<SpaceMarine> pQueue;
        SortedSet<Integer> all_id = new TreeSet<>();
        try {
//            scan = new Scanner(new File("E:/JP/M5/src/main/java/kavo.json"));
            scan = new Scanner(new File(FILE_PASS));
        } catch (FileNotFoundException e) {
            System.out.println(exc[0]);
            System.exit(0);
        }

        String jsstr = "";
        while (scan.hasNextLine()) {
            jsstr = jsstr + scan.nextLine();
        }
        scan.close();
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(ZonedDateTime.class, new TypeAdapter<ZonedDateTime>() {
                    @Override
                    public void write(JsonWriter out, ZonedDateTime value) throws IOException {
                        out.value(value.toString());
                    }

                    @Override
                    public ZonedDateTime read(JsonReader in) throws IOException {
                        return ZonedDateTime.parse(in.nextString());
                    }
                })
                .enableComplexMapKeySerialization()
                .create();
        pQueue = new PriorityQueue<SpaceMarine>(new SpaceMarineComparator());
        time_create = LocalDateTime.now();

        try {
            if (jsstr.length() != 0) {
                for (SpaceMarine obj : gson.fromJson(jsstr, SpaceMarine[].class)) {
                    pQueue.add(obj);
                    all_id.add(obj.getId());
                }
            }
        } catch (JsonSyntaxException e) {
            System.err.println(exc[1]);
            System.exit(0);
        }
        if (pQueue.size() != all_id.size()) {
            System.err.println(exc[2]);
            System.exit(0);
        }
//        DatagramSocket serverSocket = new DatagramSocket(27913);
//        byte[] receivingDataBuffer = new byte[2048];
//        byte[] sendingDataBuffer = new byte[2048];
//        DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
//        serverSocket.setSoTimeout(20000);
        Receiving.receiving(pQueue, gson, time_create, all_id, FILE_PASS);
    }
}
