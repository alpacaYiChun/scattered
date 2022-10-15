package com.suneo.flag.lib;

public class C2381 {
	public static void main(String[] args) {
		System.out.println(-29%26);
		
		String c = "xuwdbdqik";
		int[][] op = new int[][] {{4,8,0},{4,4,0},{2,4,0},{2,4,0},{6,7,1},{2,2,1},{0,2,1},{8,8,0},{1,3,1}};
		
		C2381 cls = new C2381();
		System.out.println(cls.shiftingLetters(c, op));
	}
	
	private static class S {
        int from;
        int to;
        int accu;
        
        S left;
        S right;
        
        public S(int from, int to) {
            this.from = from;
            this.to = to;
        }
        
        public void add(int a, int b, int v) {
            if(a>to || b<from) {
                return;
            }
            
            if(a<=from && b>=to) {
                accu+=v;
                return;
            }
            
            if(left==null) {
                int mid = (from+to) >>1;
                left = new S(from, mid);
                right = new S(mid+1, to);
            }
            
            left.add(a,b,v);
            right.add(a,b,v);
        }
        
        public static int get(int index, S root) {
            if(root==null) {
                return 0;
            }
            if(index>root.to||index<root.from) {
                return 0;
            }
            return root.accu+get(index, root.left)+get(index,root.right);
        }
    }
    public String shiftingLetters(String s, int[][] shifts) {
        char[] ar = s.toCharArray();
        int n = ar.length;
        S root = new S(0, n-1);
        for(int[] e : shifts) {
            int v = e[2]==0?-1:1;
            root.add(e[0],e[1],v);
        }
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<n;i++) {
            int a = S.get(i,root);
            int oldIndex = ar[i]-'a';
            int newIndex = (oldIndex+a)%26;
            if(newIndex<0) {
            	newIndex+=26;
            }
            char p = (char)('a'+newIndex);
            sb.append(p);
        }
        return sb.toString();
    }
}