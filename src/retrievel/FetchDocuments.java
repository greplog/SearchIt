package retrievel;


import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static indexes.InMemoryIndexes.preIndex;

public class FetchDocuments {

    public Set<Integer> getDocuments(String searchQuery){
        System.out.println(new Date());
        Set<Integer> resultSet = new HashSet<>();
        String[] tokens = searchQuery.split("\\s");
        for(String token : tokens) {
            if(preIndex.containsKey(preIndexKey(token))) {
                Map<String, Set<Integer>> map = preIndex.get(preIndexKey(token));
                resultSet.addAll(map.getOrDefault(token, new HashSet<>()));
            }
        }
        System.out.println(new Date());
        return resultSet;
    }

    private Integer preIndexKey(String str){
        return str.charAt(0) % 131;
    }

}
