package com.suneo.flag.lib;

import java.util.*;
import java.util.stream.Collectors;

public class C2454 {
    public int[] secondGreaterElement(int[] nums) {
        int n = nums.length;
        int[] first = new int[n];
        Stack<Integer> stack = new Stack<>();
        for(int i=n-1;i>=0;i--) {
            int val = nums[i];
            while(!stack.isEmpty() && nums[stack.peek()] <= val) {
                stack.pop();
            }
            if(!stack.isEmpty()) {
                first[i] = stack.peek();
            } else {
                first[i] = -1;
            }
            stack.push(i);
        }
        HashMap<Integer, List<Integer>> index = new HashMap<>();
        for(int i=0;i<n;i++) {
            if(!index.containsKey(nums[i])) {
                index.put(nums[i], new ArrayList<>());
            }
            index.get(nums[i]).add(i);
        }
        var sorted = index.entrySet()
                .stream()
                .sorted((x,y) -> y.getKey() - x.getKey())
                .collect(Collectors.toList());
        int[] second = new int[n];
        TreeSet<Integer> treeSet = new TreeSet<>();
        for(var e : sorted) {
            for(Integer i : e.getValue()) {
                int firstGreater = first[i];
                if(firstGreater == -1) {
                    second[i] = -1;
                } else {
                    var p = treeSet.higher(firstGreater);
                    if(p!=null) {
                        second[i] = p;
                    } else {
                        second[i] = -1;
                    }
                }
            }
            for(Integer used : e.getValue()) {
                treeSet.add(used);
            }
        }

        for(int i=0;i<n;i++) {
            if(second[i]!=-1) {
                second[i] = nums[second[i]];
            }
        }

        return second;
    }
}
