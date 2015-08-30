package retrievel;


import entities.Position;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static indexes.InMemoryIndexes.*;

public class FetchDocuments {

    private static String fileName = "/Users/sachin.goyal/IndexDir3/";

    public static void main(String[] args) {
        List<File> files = allFiles(new File("/Users/sachin.goyal/Desktop/enron_mail_20150507.tgz.xz/maildir/"), new ArrayList<>());
        for(File file : files){
            if(file.isFile()){
                incrementDocCount(file.getPath());
            }
        }
        System.out.println(new Date() + " System is Ready to be queried");
        queryDocuments();
    }

    public static List<File> allFiles(File folder, List<File> filesList){
        if(folder.isFile()){
            filesList.add(folder);
            return filesList;
        }
        for(File file : folder.listFiles()){
            if(file.isFile()){
                filesList.add(file);
            }else{
                allFiles(file, filesList);
            }
        }
        return filesList;
    }

    public static void incrementDocCount(String fileName) {
        numOfDocuments++;
        documentToId.put(fileName, numOfDocuments);
        idToDocument.put(numOfDocuments, fileName);
    }


    private static void queryDocuments(){
        FetchDocuments fetchDocuments = new FetchDocuments();
        Scanner scanner = new Scanner(System.in);
        String input;
        while((input = scanner.nextLine()) != null){
            Set<Integer> docIds = fetchDocuments.fetchDocuments(input);
            System.out.println(docIds.size());
            for(Integer doc : docIds) {
                System.out.println(String.valueOf(idToDocument.get(doc)) + "  " + doc);
            }
        }
    }


    public Set<Integer> fetchDocuments(String searchQuery){
        System.out.println(System.currentTimeMillis() % 1000);
        Set<Integer> resultSet = new HashSet<>();
        String[] tokens = searchQuery.split("\\s");
        Map<Integer, List<Position>> map = null;
        for(String token : tokens){
            String prunedStr = pruning(token);
            if(prunedStr.length() >= 0) {
                File file = new File(fileName + prunedStr + ".txt");
                if(file.exists()) map = deSerializeMap(file);
                if(map != null) resultSet.addAll(map.keySet());
            }
        }
        System.out.println(System.currentTimeMillis() % 1000);
        System.out.println(String.valueOf(resultSet));
        return resultSet;
    }

    public static String pruning(String str) {
        str = str.toLowerCase();
        Pattern p = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(str);

        return m.find() ?  "" : str;
    }


    public static Map<Integer, List<Position>> deSerializeMap(File file) {
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


    public Set<Integer> getDocuments(String searchQuery, String param){
        System.out.println(new Date());
        Set<Integer> resultSet = new HashSet<>();
        String[] tokens = searchQuery.split("\\s");
        boolean firstToken = true;
        for(String token : tokens) {
            if(token.length() > 0 && preIndex.containsKey(preIndexKey(token))) {
                Map<String, Set<Integer>> map = preIndex.get(preIndexKey(token));
                if (firstToken) {
                    resultSet.addAll(map.getOrDefault(token, new HashSet<>()));
                    firstToken = false;
                }
                else
                    unionOrIntersection(param, resultSet, token, map);
            }
        }
        System.out.println(new Date());
        return resultSet;
    }

    private void unionOrIntersection(String param, Set<Integer> resultSet, String token,
        Map<String, Set<Integer>> map) {
        if(param == null || param.equals(""))
            resultSet.addAll(map.getOrDefault(token, new HashSet<>()));
        else
            resultSet.retainAll(map.getOrDefault(token, new HashSet<>()));
    }


    private Integer preIndexKey(String str){
        return str.charAt(0) % 131;
    }

}
