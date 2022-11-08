package robot;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final Random random = new Random();
    private static final int THREADS_COUNT = 1000;
    private static final char COUNT_LETTER = 'R';
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    public static List<Thread> list = new ArrayList<>();
    public static List<Thread> list2 = Collections.synchronizedList(list);

    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < THREADS_COUNT; i++) {
            list2.add(new Thread(() -> {
                countSizeToFreqOfPath(generateRoute("RLRFR", 100));
            }));
        }

        for (Thread thread : list2) {
            thread.start();
            thread.join();
        }

        logFinalInfo();
    }

    private static void logFinalInfo() {
        var sorted = getSortedEntriesFromMapFreq();
        var firstEntry = sorted.pop();

        System.out.printf("Самое частое количество повторений %d (встретилось %d раз)\n", firstEntry.getKey(), firstEntry.getValue());
        System.out.println("Другие размеры:");

        for (var current : sorted) {
            System.out.printf(" - %d (%d раз)\n", current.getKey(), current.getValue());
        }
    }

    private static LinkedList<Map.Entry<Integer, Integer>> getSortedEntriesFromMapFreq() {
        return sizeToFreq
                .entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private static void countSizeToFreqOfPath(String path) {
        int count = getCountLettersCount(path);

        synchronized (sizeToFreq) {
            sizeToFreq.merge(count, 1, Integer::sum);
        }
    }

    private static int getCountLettersCount(String path) {
        return (int) path.chars().filter(e -> e == COUNT_LETTER).count();
    }

    public static String generateRoute(String letters, int length) {
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}
