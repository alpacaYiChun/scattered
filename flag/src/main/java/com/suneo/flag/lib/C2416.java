package com.suneo.flag.lib;

public class C2416 {
	public static class Node {
		int ends;
		int total;
		Node[] children;
		
		public Node() {
			this.ends = 0;
			this.total = 0;
			children = new Node[26];
		}
		
		public Node expand(char c) {
			int idx = c-'a';
			if(children[idx]==null) {
				children[idx] = new Node();
			}
			return children[idx];
		}
		
		public void end() {
			this.ends++;
		}
		
		public static void go(Node root, String str) {
			char[] ar = str.toCharArray();
			Node now = root;
			for(int i=0;i<ar.length;i++) {
				now = now.expand(ar[i]);
			}
			now.end();
		}
		
		public static void totals(Node root) {
			if(root==null) {
				return;
			}
			
			int s = root.ends;
			for(int i=0;i<26;i++) {
				if(root.children[i]!=null) {
					totals(root.children[i]);
					s+=root.children[i].total;
				}
			}
			
			root.total = s;
		}
		
		public static int sum(Node root, String str) {
			char[] ar = str.toCharArray();
			Node now = root;
			int ret = 0;
			for(int i=0;i<ar.length;i++) {
				now = now.expand(ar[i]);
				ret+=now.total;
			}
			return ret;
		}
	}
	public int[] sumPrefixScores(String[] words) {
        Node root = new Node();
        for(String word : words) {
        	Node.go(root, word);
        }
        
        Node.totals(root);
        
        int[] ret = new int[words.length];
        
        for(int i=0;i<words.length;i++) {
        	ret[i] = Node.sum(root, words[i]);
        }
        
        return ret;
    }
}