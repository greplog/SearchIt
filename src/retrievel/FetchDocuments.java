package retrievel;


import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static indexes.InMemoryIndexes.preIndex;

public class FetchDocuments {

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
