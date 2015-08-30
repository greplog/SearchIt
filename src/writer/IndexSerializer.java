package writer;

import entities.Position;

import java.io.*;
import java.util.List;
import java.util.Map;

import static indexes.InMemoryIndexes.wordLock;

public class IndexSerializer implements Runnable{

    private static String fileName = "/Users/sachin.goyal/IndexDir3/";
    private String word;
    private Map<Integer, List<Position>> docToPositions;
    private Map<String, Map<Integer, List<Position>>> index;

    public IndexSerializer(String word, Map<Integer, List<Position>> docToPositions) {
        this.word = word;
        this.docToPositions = docToPositions;
    }

    @Override
    public void run() {
        File file = new File(fileName + word + ".txt");
        Object lock = wordLock.get(word);
        if (lock == null) {
            lock = new Object();
            synchronized (lock) {
                System.out.println("Start merging : " + word);
                mergeMaps(file, docToPositions, lock);
            }
        }else{
            System.out.println("Waiting for word : " + word + " to merge");
            synchronized (lock){
                System.out.println("Start merging : " + word + " got lock after waiting");
                mergeMaps(file, docToPositions, lock);
            }
        }
    }

    private void mergeMaps(File file, Map<Integer, List<Position>> docToPositions, Object lock) {
        wordLock.put(word, lock);
        Map<Integer, List<Position>> fileIndex;
        if (file.exists()) {
            System.out.println("merging maps for file : " +  file.getName());
            fileIndex = deSerializeMap(file);
            try {
                fileIndex.forEach((k, v) -> docToPositions.merge(k, v, (v1, v2) -> {
                    v1.addAll(v2);
                    return v1;
                }));
            }catch (Exception e){
                wordLock.remove(word);
                System.out.println("Exception in merging maps for file : " + file.getName());
            }
        }
        serializeMap(docToPositions, file);
        wordLock.remove(word);
    }

    public void serializeMap(Map<Integer, List<Position>> map, File file) {
        try {
            System.out.println("FileName : " + file.getName());
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream objStream = new ObjectOutputStream(fileOut);
            objStream.writeObject(map);
            objStream.close();
            fileOut.close();
        } catch (IOException i) {
            System.out.println(
                Thread.currentThread() + " " + file.getName() + " Serializing Exception");
            //            i.printStackTrace();
        }
    }

    public Map<Integer, List<Position>> deSerializeMap(File file) {
        Map<Integer, List<Position>> map = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            map = (Map<Integer, List<Position>>) ois.readObject();
            ois.close();
        } catch (ClassNotFoundException | IOException e) {
            System.out.println(
                Thread.currentThread() + " " + file.getName() + " DESerializing Exception");
            //            e.printStackTrace();
        }
        return map;
    }

//        public static void main(String[] args) {
//            System.out.println(System.currentTimeMillis() % 1000);
//            Map<Integer, List<Position>> abc =
//                deSerializeMap(new File("/Users/sachin.goyal/IndexDir2/insecure.txt"));
//            System.out.println(System.currentTimeMillis() % 1000);
//            Map<Integer, List<Position>> abc1 =
//                deSerializeMap(new File("/Users/sachin.goyal/IndexDir2/send.txt"));
//            abc.keySet().stream().filter(abc1::containsKey).forEach(System.out::println);
//            abc.forEach((k, v) -> abc1.merge(k, v, (v1, v2) -> {
//                v1.addAll(v2);
//                return v1;
//            }));
//            serializeMap(abc1, new File("/Users/sachin.goyal/IndexStore/alsoTesting.txt"));
//            Map<Integer, List<Position>> deserialized =
//                deSerializeMap(new File("/Users/sachin.goyal/IndexStore/alsoTesting.txt"));
//        }

}
