package com.suneo.flag.lib;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

public class C2385 {
	 public class TreeNode {
		      int val;
		      TreeNode left;
		      TreeNode right;
		      TreeNode() {}
		      TreeNode(int val) { this.val = val; }
		      TreeNode(int val, TreeNode left, TreeNode right) {
		          this.val = val;
		          this.left = left;
		          this.right = right;
		      }
    }
	public class Beam {
		TreeNode node;
		int step;
		
		public Beam(TreeNode node, int step) {
			this.node = node;
			this.step = step;
		}
	}
	public int amountOfTime(TreeNode root, int start) {
        Stack<TreeNode> s = new Stack<>();
        s.push(root);
        reach(s, start);
        LinkedList<Beam> queue = new LinkedList<>();
        HashSet<Integer> already = new HashSet<>();
        int init = 0;
        while(!s.isEmpty()) {
        	TreeNode top = s.pop();
        	queue.add(new Beam(top, init));
        	already.add(top.val);
        	++init;
        }
        int ret = -1;
        while(!queue.isEmpty()) {
        	Beam fetch = queue.poll();
        	if(fetch.step>ret) {
        		ret=fetch.step;
        	}
        	if(fetch.node.left!=null&&!already.contains(fetch.node.left.val)) {
        		queue.add(new Beam(fetch.node.left, fetch.step+1));
        	}
        	if(fetch.node.right!=null&&!already.contains(fetch.node.right.val)) {
        		queue.add(new Beam(fetch.node.right, fetch.step+1));
        	}
        }
        
        return ret;
    }
	
	public boolean reach(Stack<TreeNode> s, int target) {
		TreeNode top = s.peek();
		if(top.val==target) {
			return true;
		}
		if(top.left!=null) {
			s.push(top.left);
			boolean leftSide = reach(s,target);
			if(leftSide) {
				return true;
			}
			s.pop();
		}
		if(top.right!=null) {
			s.push(top.right);
			boolean rightSide = reach(s,target);
			if(rightSide) {
				return true;
			}
			s.pop();
		}
		
		return false;
	}
}