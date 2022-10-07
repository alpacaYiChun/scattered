package com.suneo.flag.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class C2430 {
    public int deleteString(String s) {
        char[] ar = s.toCharArray();
        int n = ar.length;
        int[][] mist = new int[2][n];
        int flag = 0;
        HashMap<Integer, List<Integer>> possible = new HashMap<Integer, List<Integer>>();

        for (int i = n - 2; i >= 0; i--) {
            for (int j = n - 1; j >= i + 1; j--) {
                if (ar[i] != ar[j]) {
                    mist[flag][j] = 0;
                    continue;
                } else {
                    int left = 0;
                    if (j < n - 1) {
                        left = mist[(flag + 1) % 2][j + 1];
                    }
                    mist[flag][j] = left + 1;
                }
                int dist = j - i;
                if (mist[flag][j] >= dist && j + dist <= n) {
                    if (!possible.containsKey(i)) {
                        possible.put(i, new ArrayList<>());
                    }
                    possible.get(i).add(j);
                }
            }
            flag = (flag + 1) % 2;
        }

        int[] op = new int[n];
        op[n - 1] = 1;
        for (int i = n - 2; i >= 0; i--) {
            int max = 0;
            if (possible.containsKey(i)) {
                for (Integer next : possible.get(i)) {
                    if (op[next] > max) {
                        max = op[next];
                    }
                }
            }
            op[i] = max + 1;
        }

        return op[0];
    }
}
