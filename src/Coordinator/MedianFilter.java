package Coordinator;

import java.lang.reflect.Array;
import java.util.*;

public class MedianFilter {

    private int filterSize = 3;
    private Queue<Integer> valuesTrack;

    private MedianFilter(){
        valuesTrack = new PriorityQueue<>(filterSize);
    }
    public MedianFilter(int defaultValues){
        this();
        for(int i = 0; i < filterSize;i++){
            valuesTrack.add(defaultValues);
        }
    }

    private Comparator<Integer> comparator = new Comparator<Integer>() {

        @Override
        public int compare(Integer o1, Integer o2) {
            if( o1 > o2 ){
                return 1;
            }
            if( o1 < o2 ){
                return -1;
            }
            return 0;
        }
    };

    public int getFilteredValue(int newValue){

        valuesTrack.remove();
        valuesTrack.add(newValue);

        Integer[] sortedValuesTrack = new Integer[valuesTrack.size()];
        valuesTrack.toArray(sortedValuesTrack);
        Arrays.sort(sortedValuesTrack,comparator);//сортировка по возрастанию

        int ret = sortedValuesTrack[(sortedValuesTrack.length / 2) + 1];
        return ret;
    }
}
