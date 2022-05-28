package com.suneo.flag.lib;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

public class CB {
	public static void main(String[] args) {
		CB cb = new CB();
		//System.out.println(cb.findFirstBadVersion(2147483647));
		
		//cb.search(new int[] {4,4,4,5,1,4}, 5);
		
	    System.out.println(cb.findMin(new int[] {3,1,2}));
	}
    /**
     * @param n: An integer
     * @return: An integer which is the first bad version.
     */
	
    public int findMin(int[] nums) {
        if(nums[0]<nums[nums.length-1]) {
            return nums[0];
        }
        // write your code here
        int from = 0;
        int to = nums.length - 1;
        int min = -1;
        while(from!=to) {
            int mid=(from+to) >> 1;
            int midVal = nums[mid];
            if(midVal<nums[0]) {
                to=mid;
                min = mid;
            } else {
                from=mid+1;
            }
            System.out.println(from+"fuck"+to);
        }
        if(nums[from]<nums[0]) {
            min=from;
        }
        return nums[min];
    }
	
    public boolean search(int[] A, int target) {
        if(A.length==0) {
            return false;
        }
        // write your code here
        int from = 0;
        int to = A.length-1;
        int maxIndex = -1;
        while(from!=to) {
            int mid = (from+to) >> 1;
            if(A[mid]>A[0]) {
                maxIndex=mid;
                from=mid+1;
            } else if(A[mid]<A[0]) {
                to=mid;
            } else {
                maxIndex=mid;
                from=mid+1;
            }
            System.out.println(from+","+to);
        }
        if(A[from]>=A[0]) {
            maxIndex=from;
        }

if(target==A[0]){
    return true;
}

System.out.println(maxIndex);
        if(maxIndex==A.length-1) {
            return find(A,0,A.length-1,target);
        }

        if(target>A[0]) {
            return find(A,0,maxIndex,target);
        }
        return find(A,maxIndex+1,A.length-1,target);

         
   }

   private boolean find(int[] A, int from, int to, int t) {
       int x=from;
       int y=to;
       while(x!=y) {
           int mid=(x+y)>>1;
           if(A[mid]==t) {
               return true;
           }
           if(A[mid]<t) {
               from=mid+1;
           }
           else {
               to=mid;
           }
       }
       if(A[x]==t) {
           return true;
       }
       return false;
   }
	
    public int findFirstBadVersion(int n) {
        long from = 1;
        long to = n;
        long ret = -1;
        while(from!=to) {
            long mid=(from+to) >> 1;
            if(bad((int)mid)) {
                ret=mid;
                to=mid;
            } else {
                from=mid+1;
            }
        }
        if(ret!=from) {
            if(bad((int)from)) {
                ret=from;
            }
        }
        return (int)ret;
        // write your code here
    }
    
    public boolean bad(int t) {
    	return t>=2147483647;
    }
    

    public List<List<String>> solveNQueens(int n) {
        // write your code here
        Stack<Integer> s = new Stack<>();
        HashSet<Integer> col = new HashSet<Integer>();
        HashSet<Integer> add = new HashSet<>();
        HashSet<Integer> sub = new HashSet<>();
        List<List<Integer>> list = new ArrayList<>();
        doit(n,0,s,col,add,sub,list);
        List<List<String>> ret = new ArrayList<>();
        for(List<Integer> l:list) {
            List<String> now = new ArrayList<>();
            for(Integer r:l) {
                now.add(m(n,r));
            }
            ret.add(now);
        }
        return ret;
    }

    private String m(int n, int p) {
        char[] t = new char[n];
        for(int i=0;i<n;i++) {
            t[i]='.';
        }
        t[p]='Q';
        return new String(t);
    }

    private void doit(int n, int row, Stack<Integer> s, HashSet<Integer> col, HashSet<Integer> add, HashSet<Integer> sub, List<List<Integer>> collect) {
        if(row==n) {
            List<Integer> list = new ArrayList<>();
            for(int i=0;i<s.size();i++) {
                list.add(s.get(i));
            }
            collect.add(list);
            return;
        }
        for(int i=0;i<n;i++) {
            int colNow = i;
            int addNow = row+i;
            int subNow = row-i;
            if(col.contains(col) || add.contains(addNow) || sub.contains(subNow)) {
                continue;
            } 
            s.push(i);
            col.add(i);
            add.add(addNow);
            sub.add(subNow);
            doit(n,row+1,s,col,add,sub,collect);
            sub.remove(subNow);
            add.remove(addNow);
            col.remove(i);
            s.pop();
        }
    }

}
