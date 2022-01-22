package com.suneo.flag.lib;

public class MuxiYuan {
	public static void main(String [] args) {
		Node root = new Node(1);
		Node n = new MuxiYuan().treeToDoublyList(root);
		System.out.println(n.val);
	}
	
	static class Node {
	    public int val;
	    public Node left;
	    public Node right;

	    public Node() {}

	    public Node(int _val) {
	        val = _val;
	    }

	    public Node(int _val,Node _left,Node _right) {
	        val = _val;
	        left = _left;
	        right = _right;
	    }
	};
public Node treeToDoublyList(Node root) {
	if(root==null) {
	        return null;
	    }
	    Node[] getch = convert(root);
	    return getch[0];
	}
	
	private Node[] convert(Node root) {
	    if(root.left==null&&root.right==null) {
	        return new Node[]{root,root};
	    }
	    Node pLeft = root;
	    Node pRight = root;
	    if(root.left!=null) {
	        Node[] aLeft=convert(root.left);
	        aLeft[1].right=root;
	        root.left=aLeft[1];
	        pLeft=aLeft[0];
	    }
	    if(root.right!=null) {
	        Node[] aRight=convert(root.right);
	        root.right=aRight[0];
	        aRight[0].left=root;
	        pRight=aRight[1];
	    }
	    return new Node[]{pLeft, pRight};
	}
}