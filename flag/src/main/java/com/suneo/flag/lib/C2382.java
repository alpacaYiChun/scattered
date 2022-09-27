package com.suneo.flag.lib;

import java.util.*;

public class C2382 {
    public static class Seg {
        int from;
        int to;
        public Seg(int from, int to) {
            this.from = from;
            this.to = to;
        }
    }
    public long[] maximumSegmentSum(int[] nums, int[] removeQueries) {
        int n = nums.length;

        long[] sum = new long[n];
        sum[0] = nums[0];
        for(int i=1;i<n;i++) {
            sum[i]=nums[i]+sum[i-1];
        }

        long total = sum[n-1];

        TreeMap<Integer, Seg> set = new TreeMap<>(Comparator.comparingInt(e -> e));
        set.put(0, new Seg(0, n-1));
        TreeMap<Long, Integer> lengthMap = new TreeMap<>((e1, e2) -> Long.compare(e2, e1));
        lengthMap.put(total, 1);

        long[] ret = new long[removeQueries.length];

        for(int i = 0; i<removeQueries.length; i++) {
            int q = removeQueries[i];
            Map.Entry<Integer, Seg> find = set.floorEntry(q);
            int from = find.getValue().from;
            int to = find.getValue().to;
            long oldSum = sum[to] - (from>0?sum[from-1]:0);

            Seg left = null;
            Seg right = null;
            long leftSum = 0;
            long rightSum = 0;
            if(q > from) {
                left = new Seg(from, q-1);
                leftSum = sum[q-1]-(from>0?sum[from-1]:0);
            }
            if(q < to) {
                right = new Seg(q+1, to);
                rightSum = sum[to] - sum[q];
            }

            set.remove(find.getKey());
            int already = lengthMap.get(oldSum);
            lengthMap.put(oldSum, already - 1);

            if(left!=null) {
                int k = 0;
                if(lengthMap.containsKey(leftSum)) {
                    k = lengthMap.get(leftSum);
                }
                lengthMap.put(leftSum, k+1);
                set.put(left.from, left);
            }
            if(right!=null) {
                int k = 0;
                if(lengthMap.containsKey(rightSum)) {
                    k = lengthMap.get(rightSum);
                }
                lengthMap.put(rightSum, k+1);
                set.put(right.from, right);
            }

            long top = 0;
            do {
                 Map.Entry<Long, Integer> entry = lengthMap.pollFirstEntry();
                 if(entry.getValue() == 0) {
                     continue;
                 }
                 top = entry.getKey();
                 lengthMap.put(entry.getKey(), entry.getValue());
                 break;
            } while(lengthMap.size() > 0);

            ret[i] = top;
        }

        return ret;
    }
}