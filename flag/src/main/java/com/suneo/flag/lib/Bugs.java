package com.suneo.flag.lib;

import java.math.BigInteger;

public class Bugs {
	public static void main(String[] args) {
		BigInteger big = new BigInteger("27651812046361280818524266832");
		var result = big.mod(new BigInteger("1000000007"));
		System.out.println(result);
		
		System.out.println(c(4,2));
		System.out.println(c(10,5));
		System.out.println(c(99,44));
	}
	
	private static int BIG = 1000000007;
	
	public static int c(int m, int n) {
        long ret = 1;
        for(int i=1;i<=n;i++) {
            ret= ret *(m-i+1);
            ret=ret/i;
            ret=ret%BIG;
        }
        System.out.println(ret);
        return (int)ret;
    }
}