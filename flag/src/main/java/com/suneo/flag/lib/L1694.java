package com.suneo.flag.lib;

public class L1694 {
    public int killMonster(int[] atk1, int[] atk2) {
        // Write your code here
        int n = atk1.length;
        int[][] op = new int[n][n];
        for(int i=0;i<n;i++) {
            int c = atk1[i];
            if(i<n-1) {
                c+=atk2[i+1];
            }
            if(i>0) {
                c+=atk2[i-1];
            }
            op[i][0] = c;
        }
        for(int j=1;j<n;j++) {
            for(int i=0;i+j<n;i++) {
                int min = Integer.MAX_VALUE;
                for(int k=0;k<=j;k++) {
                    // last at i+k
                    // front k-1
                    // front start i
                    // rear j-k-1
                    // rear start i+k+1
                    int last = i+k;
                    int front = 0;
                    if(k>0) {
                        front = op[i][k-1];
                    }
                    int rear = 0;
                    if(k<j) {
                        rear = op[i+k+1][j-k-1];
                    }
                    int middle = atk1[last];
                    if(i>0) {
                        middle+=atk2[i-1];
                    }
                    if(i+j<n-1) {
                        middle+=atk2[i+j+1];
                    }

                    int attempt = front+rear+middle;
                    if(attempt<min) {
                        min = attempt;
                    }
                }
                op[i][j]=min;
            }
        }
        return op[0][n-1];
    }
}
