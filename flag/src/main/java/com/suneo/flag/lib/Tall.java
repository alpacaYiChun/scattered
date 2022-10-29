package com.suneo.flag.lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tall {
    public static class K {
        int value;
        long cost;
        public K(int value, int cost) {
            this.value = value;
            this.cost = cost;
        }
    }
    public long minCost(int[] nums, int[] cost) {
        int n = nums.length;
        K[] list = new K[n];
        for(int i=0;i<n;i++) {
            list[i] = new K(nums[i], cost[i]);
        }
        Arrays.sort(list, (a, b) -> b.value - a.value);

        int top = 0;
        for(int i=1;i<n;i++) {
            if(list[i].value == list[top].value) {
                list[top].cost += list[i].cost;
            } else {
                list[++top] = list[i];
            }
        }

        int len = top + 1;
        long[] sum = new long[len];
        sum[0] = list[0].cost;
        for(int i=1;i<len;i++) {
            sum[i] = sum[i-1] + list[i].cost;
        }

        int fix = 0;
        for(int i=1;i<len;i++) {
            long left = sum[i-1];
            long right = sum[len-1]-sum[i-1];
            if(left>=right) {
                break;
            } else {
                fix = i;
            }
        }

        int rope = list[fix].value;

        long ret = 0;
        for(int i=0;i<len;i++) {
            ret += Math.abs(rope - list[i].value) * list[i].cost;
        }

        return ret;
    }
}
