import java.net.*;
import java.nio.channels.DatagramChannel;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class Treatment {
    private static String[] helpText = {
            "info : вывести в стандартный поток вывода информацию о коллекции.",
            "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении.",
            "add {element} : добавить новый элемент в коллекцию.",
            "update id {element} : обновить значение элемента коллекции, id которого равен заданному.",
            "remove_by_id id : удалить элемент из коллекции по его id",
            "clear : очистить коллекцию.",
            "save : сохранить коллекцию в файл.",
            "execute_script file_name : считать и исполнить скрипт из указанного файла.",
            "exit : завершить программу (без сохранения в файл).",
            "head : вывести первый элемент коллекции.",
            "add_if_min {element} : добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции.",
            "remove_greater {element} : удалить из коллекции все элементы, превышающие заданный.",
            "sum_of_health : вывести сумму значений поля health для всех элементов коллекции.",
            "count_by_health health : вывести количество элементов, значение поля health которых равно заданному.",
            "filter_by_health health : вывести элементы, значение поля health которых равно заданному."
    };

    private static String[] exc = {
            "Колекция пуста.",
            "Элементов не найдено.",
            "Элемент не добавлен.",
            "Не удалено ни одного элемента."
    };

    private static String[] info = {
            "Успешно."
    };

    private static ReturnPack help () {
        ReturnPack rp = new ReturnPack(helpText, null, null);
        return rp;
    }

    private static ReturnPack head (PriorityQueue<SpaceMarine> pQ) {
        SpaceMarine[] sp = {pQ.peek()};
        ReturnPack rp;
        if (sp!=null) {
            rp = new ReturnPack(null, null, sp);
        } else {
            rp = new ReturnPack(null, exc[0], null);
        }
        return rp;
    }

    private static ReturnPack show (PriorityQueue<SpaceMarine> pQ) {
        ReturnPack rp;
        if (!pQ.isEmpty()) {
//            SpaceMarine[] sp = new SpaceMarine[pQ.size()];
//            Iterator value = pQ.iterator();
//            int i = 0;
//            while (value.hasNext()) {
//                sp[i] = (SpaceMarine) value.next();
//                i++;
//            }
            rp = new ReturnPack(null, null, pQ.stream().sorted(new NameComparator()).toArray(SpaceMarine[]::new));
        } else {
            rp = new ReturnPack(null, exc[0], null);
        }
        return rp;
    }

    private static ReturnPack clear (PriorityQueue<SpaceMarine> pQ) {
        pQ.clear();
        ReturnPack rp = new ReturnPack(info, null, null);
        return rp;
    }

    private static ReturnPack sumOfHealth (PriorityQueue<SpaceMarine> pQ) {
        Iterator value = pQ.iterator();
        int hp = 0;
        while (value.hasNext()) {
            hp += ((SpaceMarine) value.next()).getHealth();
        }
        ReturnPack rp = new ReturnPack(new String[]{Integer.toString(hp)}, null, null);
        return rp;
    }

    private static ReturnPack info (PriorityQueue<SpaceMarine> pQ, LocalDateTime tC) {
        ReturnPack rp = new ReturnPack(new String[]{
                "Тип: " + pQ.getClass().getName(),
                "Время инициализации: " + tC,
                "Количество элементов: " + pQ.size()
        }, null, null);
        return rp;
    }

    private static ReturnPack countByHealth (PriorityQueue<SpaceMarine> pQ, String arg) {
//        Iterator value = pQ.iterator();
//        int i = 0;
//        while (value.hasNext()) {
//            if (((SpaceMarine) value.next()).getHealth()==Integer.parseInt(arg)){
//                i++;
//            }
//        }
        ReturnPack rp = new ReturnPack(new String[]{Integer.toString((int) pQ.stream().filter(x -> x.getHealth() == Integer.parseInt(arg)).count())}, null, null);
        return rp;
    }

    private static ReturnPack filterByHealth (PriorityQueue<SpaceMarine> pQ, String arg) {
        ReturnPack rp;
        SpaceMarine[] sp = pQ.stream().filter(x -> x.getHealth() == Integer.parseInt(arg)).sorted(new NameComparator()).toArray(SpaceMarine[]::new);
//        Iterator value = pQ.iterator();
//        SpaceMarine[] sp = new SpaceMarine[pQ.size()];
//        int i = 0;
//        while (value.hasNext()) {
//            SpaceMarine chek = (SpaceMarine) value.next();
//            if (chek.getHealth()==Integer.parseInt(arg)){
//                sp[i] = chek;
//                i++;
//            }
//        }
        if (sp.length != 0) {
            rp = new ReturnPack(null, null, sp);
        } else {
            rp = new ReturnPack(null, exc[1], null);
        }
        return rp;
    }

    private static ReturnPack removeById (PriorityQueue<SpaceMarine> pQ, String arg, SortedSet<Integer> allId) {
        ReturnPack rp;
        Optional<SpaceMarine> sp = pQ.stream().filter(x -> x.getId() == Integer.parseInt(arg)).findFirst();
//        Iterator value = pQ.iterator();
//        SpaceMarine sp = null;
//        ReturnPack rp;
//        while (value.hasNext()) {
//            SpaceMarine chek = (SpaceMarine) value.next();
//            if (chek.getId()==Integer.parseInt(arg)){
//                sp = chek;
//                break;
//            }
//        }
        if (sp.isPresent()) {
            allId.remove(sp.get().getId());
            pQ.remove(sp.get());
            rp = new ReturnPack(info, null, null);
        } else {
            rp = new ReturnPack(null, exc[1], null);
        }
        return rp;
    }

    private static ReturnPack add (PriorityQueue<SpaceMarine> pQ, SpaceMarine sp, SortedSet<Integer> allId) {
        if (pQ.size() != 0) {
            sp.setId(allId.last() + 1);
            pQ.add(sp);
            allId.add(allId.last() + 1);
        } else {
            sp.setId(1);
            pQ.add(sp);
            allId.add(1);
        }
        ReturnPack rp = new ReturnPack(info, null, null);
        return rp;
    }

    private static ReturnPack addIfMin (PriorityQueue<SpaceMarine> pQ, SpaceMarine sp, SortedSet<Integer> allId) {
        ReturnPack rp;
        if (pQ.size() != 0) {
            sp.setId(allId.last() + 1);
            if (sp.hashCode() < pQ.peek().hashCode()) {
                pQ.add(sp);
                allId.add(allId.last() + 1);
                rp = new ReturnPack(info, null, null);
            } else {
                rp = new ReturnPack(null, exc[2], null);
            }
        } else {
            sp.setId(1);
            pQ.add(sp);
            allId.add(1);
            rp = new ReturnPack(info, null, null);
        }
        return rp;
    }

    private static ReturnPack removeGreater (PriorityQueue<SpaceMarine> pQ, SpaceMarine sp, SortedSet<Integer> allId) {
        boolean ch = false;
        ReturnPack rp;
        if (pQ.size() > 0 && allId.contains(sp.getId())) {
            sp.setId(allId.last() + 1);
            SpaceMarine[] sm = pQ.stream().filter(x -> x.hashCode() > sp.hashCode()).toArray(SpaceMarine[]::new);
            for (SpaceMarine i : sm) {
                allId.remove(i.getId());
                pQ.remove(i);
                ch = true;
            }
//            Iterator value = pQ.iterator();
//            SpaceMarine pQsp;
//            while (value.hasNext()) {
//                pQsp = (SpaceMarine) value.next();
//                if (!ch && pQsp.hashCode() > sp.hashCode()) {
//                    ch = true;
//                    allId.remove(pQsp.getId());
//                    pQ.remove(pQsp);
//                }
//                if (ch) {
//                    allId.remove(pQsp.getId());
//                    pQ.remove(pQsp);
//                }
//            }
        }
        if (ch) {
            rp = new ReturnPack(info, null, null);
        } else {
            rp = new ReturnPack(null, exc[3], null);
        }
        return rp;
    }

    private static ReturnPack update (PriorityQueue<SpaceMarine> pQ, SpaceMarine sp, SortedSet<Integer> allId) {
        ReturnPack rp;
        Optional<SpaceMarine> sm = pQ.stream().filter(x -> x.getId().equals(sp.getId())).findFirst();
        if (sm.isPresent()) {
            pQ.remove(sm.get());
            pQ.add(sp);
            rp = new ReturnPack(info, null, null);
        } else {
            rp = new ReturnPack(null, exc[1], null);
        }
//        Iterator value = pQ.iterator();
//        ReturnPack rp;
//        SpaceMarine chek = null;
//        boolean swop = false;
//        while (value.hasNext()) {
//            chek = (SpaceMarine) value.next();
//            if (chek.getId().equals(sp.getId())){
//                pQ.remove(chek);
//                pQ.add(sp);
//                swop = true;
//                break;
//            }
//        }
//        if (swop) {
//            rp = new ReturnPack(info, null, null);
//        } else {
//            rp = new ReturnPack(null, exc[1], null);
//        }
        return rp;
    }

    public static void treatment (SendPack sp,
                                  LocalDateTime timeCreate,
                                  PriorityQueue<SpaceMarine> pQueue,
                                  SortedSet<Integer> allId,
                                  DatagramSocket serverSocket,
                                  SocketAddress senderAddress) throws SocketException {
        ReturnPack rp;
        switch (sp.getComand()) {
            case help:
                rp = Treatment.help();
                break;
            case head:
                rp = Treatment.head(pQueue);
                break;
            case show:
                rp = Treatment.show(pQueue);
                break;
            case clear:
                rp = Treatment.clear(pQueue);
                break;
            case sum_of_health:
                rp = Treatment.sumOfHealth(pQueue);
                break;
            case info:
                rp = Treatment.info(pQueue, timeCreate);
                break;
            case count_by_health:
                rp = Treatment.countByHealth(pQueue, sp.getArg());
                break;
            case filter_by_health:
                rp = Treatment.filterByHealth(pQueue, sp.getArg());
                break;
            case remove_by_id:
                rp = Treatment.removeById(pQueue, sp.getArg(), allId);
                break;
            case add:
                rp = Treatment.add(pQueue, sp.getSp(), allId);
                break;
            case add_if_min:
                rp = Treatment.addIfMin(pQueue, sp.getSp(), allId);
                break;
            case remove_greater:
                rp = Treatment.removeGreater(pQueue, sp.getSp(), allId);
                break;
            case update:
                rp = Treatment.update(pQueue, sp.getSp(), allId);
                break;
            default:
                rp = null;
                break;
        }

        if (rp!=null) {
            Sending.sanding(rp, serverSocket, senderAddress);
        }
    }
}
