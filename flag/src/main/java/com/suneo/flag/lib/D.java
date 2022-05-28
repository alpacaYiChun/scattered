package com.suneo.flag.lib;

public class D {
	public static void main(String[] args) {
		D d = new D();
	    System.out.println(d.search(new int[] {4,5,3},4));
	}
    /**
     * @param A: an integer rotated sorted array
     * @param target: an integer to be searched
     * @return: an integer
     */
    public int search(int[] A, int target) {
        if(A.length==0) {
            return -1;
        }
        int from = 0;
        int to = A.length - 1;
        while(from!=to) {
            int mid = (from+to) >> 1;
            int midVal = A[mid];
            if(midVal==target) {
                return mid;
            }
            if(midVal>=A[0]) {
                if(midVal<target) {
                    from=mid+1;
                } else {
                    if(target<A[0]) {
                        from=mid+1;
                    } else {
                        to=mid;
                    }
                }
            } else {
                if(midVal>target) {
                    to=mid;
                } else {
                    if(target<A[0]) {
                        from=mid+1;
                    } else {
                        to=mid;
                    }
                }
            }
        }
        if(A[from]==target) {
            return from;
        }
        return -1;
        // write your code here
    }
}
