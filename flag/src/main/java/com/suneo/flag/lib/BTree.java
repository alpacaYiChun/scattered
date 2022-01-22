package com.suneo.flag.lib;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;

public class BTree {
	private static final int MAX_DEGREE = 40;
    
	public static void main(String[] args) {
		StorageHelper helper = new InMemoryStorageHelper();
		long initID = helper.getNextId();
		Node init = new Node(initID, -1, new int[0], null, helper, true);
		helper.storeNode(init);
		helper.setRoot(initID);
		
		BTree btree = new BTree(helper);
		
		for(int i=0,j=3000;i<j;i++,j--) {
			//System.out.println(i+","+j);
			btree.insert(i);
			btree.insert(j);
			//btree.show();
		}
		
		System.out.println("Total:" + helper.getSize());
	}
	
	private StorageHelper helper;
	
	public BTree(StorageHelper helper) {
		this.helper = helper;
	}
	
	public void show() {
		Node root = helper.getRoot();
		LinkedList<Node> queue = new LinkedList<>();
		queue.add(root);
		while(!queue.isEmpty()) {
			Node fetch = queue.poll();
			System.out.println(fetch.toString());
			if(!fetch.isLeaf) {
				for(Long c:fetch.children) {
					queue.add(helper.getNode(c));
				}
			}
		}
		
		System.out.println();
		System.out.println();
	}
	
	public synchronized void insert(int key) {
		Node root = helper.getRoot();
		Node now = root;
		while(!now.isLeaf) {
			int rs = now.searchKeyIndex(key);
			if(rs != -1 && now.keys[rs] == key) {
				// do not duplicate
				return;
			}
			now = helper.getNode(now.children[rs + 1]);
		}
		
		int finRs = now.searchKeyIndex(key);
		if(finRs != -1 && now.keys[finRs]==key) {
			return;
		}
		
		now.insertKey(key, -1, -1, -1);
		//System.out.println(now.toString());
		now.split();
	}
	
	public synchronized boolean search(int key) {
		Node root = helper.getRoot();
		Node now = root;
		do {
			int rs = now.searchKeyIndex(key);
			if(rs != -1&&now.keys[rs]==key) {
				return true;
			}
			if(now.isLeaf) {
				return false;
			}
			now = helper.getNode(now.children[rs + 1]);
		} while(true);
	}
	
	private static interface StorageHelper {
		public Node getNode(long id);
		public void storeNode(Node node);
		public long getNextId();
		public void reclaim(long id);
		public void setRoot(long id);
		public Node getRoot();
		public int getSize();
	}
	
	private static class InMemoryStorageHelper implements StorageHelper {
		private HashMap<Long, Node> store = new HashMap<>();
		private  AtomicLong idGen = new AtomicLong(0);
		private long rootID;
		
		@Override
		public Node getNode(long id) {
			return store.get(id);
		}

		@Override
		public void storeNode(Node node) {
            store.put(node.id, node);
		}

		@Override
		public long getNextId() {
			return idGen.incrementAndGet();
		}

		@Override
		public void reclaim(long id) {
            store.remove(id);
		}

		@Override
		public void setRoot(long id) {
			this.rootID = id;
		}

		@Override
		public Node getRoot() {
			return store.get(rootID);
		}
		
		@Override
		public int getSize() {
			return store.size();
		}
	}
	
	private static class Node {
		long id;
		long parent;
		int[] keys;
		long[] children;
		
		boolean isLeaf;
		
		StorageHelper helper;
		
		public Node(long id, long parent, int[] keys,
				long[] children, StorageHelper helper, boolean isLeaf) {
			this.id = id;
			this.parent = parent;
			this.keys = keys;
			this.children = children;
			this.helper = helper;
			this.isLeaf = isLeaf;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("id=").append(id)
			.append(" parent=").append(parent)
			.append(" children:");
			if(!isLeaf) {
				for(Long c:children) {
					sb.append(c).append(",");
				}
			} else {
				sb.append("Leaf");
			}
			
			sb.append(" Keys:");
			for(Integer key:keys) {
				sb.append(key).append(",");
			}
			
			return sb.toString();
		}
		
		public int searchKeyIndex(int key) {
			int from = 0;
			int to = keys.length - 1;
			int ret = -1;
			while(from<to) {
				int mid = (from + to) >> 1;
				if(keys[mid] <= key) {
					ret = mid;
					from = mid+1;
				} else {
					to = mid;
				}
			}
			
			if(from==to && keys[from]<=key) {
				ret = from;
			}
			
			return ret;
		}
		
		
		private void split() {
			
			// original: key 0~n-1, children 0~n
			// left: key 0~k-1, children 0~k
			// right: key k+1~n-1, children k+1~n
			// up: key k, replace original with left and right in parent children
			int len = keys.length;
			if(len > MAX_DEGREE) {
				//System.out.println("Now Splitting " + id);
				int k = (len-1) >> 1;
				
				int[] leftKeys = Arrays.copyOfRange(keys, 0, k);
				int[] rightKeys = Arrays.copyOfRange(keys, k+1, len);
				
				long[] leftChildren = null;
				long[] rightChildren = null;
				if(!isLeaf) {
				    leftChildren = Arrays.copyOfRange(children, 0, k+1);
					rightChildren = Arrays.copyOfRange(children, k+1, len+1);					
				}
				
				int midKey = keys[k];
				
				long leftID = helper.getNextId();
				long rightID = helper.getNextId();
								
				Node left = null;
				Node right = null;
				
				Node rootNode = null;
				
				Node parentNode = null;
				
				//System.out.println("ID="+id+","+midKey);
				
				if (parent != -1) {
					left = new Node(leftID, parent, leftKeys, leftChildren, helper, isLeaf);
					right = new Node(rightID, parent, rightKeys, rightChildren, helper, isLeaf);
					parentNode = helper.getNode(parent);
					parentNode.insertKey(midKey, this.id, leftID, rightID);
					helper.storeNode(parentNode);
					//System.out.println("Fin "+id);
				} else {
					long newRoot = helper.getNextId();
					int[] newRootKeys = new int[1];
					newRootKeys[0] = midKey;
					long[] newRootChildren = new long[2];
					newRootChildren[0] = leftID;
					newRootChildren[1] = rightID;
					rootNode = new Node(newRoot, -1, newRootKeys, newRootChildren, helper, false);
					helper.storeNode(rootNode);
					helper.setRoot(newRoot);
					
					left = new Node(leftID, newRoot, leftKeys, leftChildren, helper, isLeaf);
					right = new Node(rightID, newRoot, rightKeys, rightChildren, helper, isLeaf);
				}
				
				helper.storeNode(left);
				helper.storeNode(right);
				
				//System.out.println(left.toString());
				//System.out.println(right.toString());
				
				if(!isLeaf) {
					for(Long lc:leftChildren) {
						Node l = helper.getNode(lc);
						l.parent = leftID;
						helper.storeNode(l);
					}
					for(Long rc:rightChildren) {
						Node r = helper.getNode(rc);
						r.parent = rightID;
						helper.storeNode(r);
					}
				}
					
				if(parent!=-1) {
					parentNode.split();
				}
				
				helper.reclaim(this.id);
			}
		}
		
		private void insertKey(int key, long originalChild,
				long leftChild, long rightChild) {
			int[] newKeys = new int[keys.length + 1];
			int i=0;
			while(i<keys.length && keys[i]<key) {
				newKeys[i] = keys[i];
				++i;
			}
			newKeys[i] = key;
			++i;
			while(i<=keys.length) {
				newKeys[i] = keys[i-1];
				++i;
			}
			this.keys = newKeys;
			//System.out.println("FFFID="+id);
			if(!isLeaf) {
				long[] newChildren = new long[children.length + 1];
				i=0;
				//System.out.println("Original:"+originalChild);
				while(children[i] != originalChild) {
					newChildren[i] = children[i];
					++i;
				}
				//System.out.println("L:"+leftChild+",R:"+rightChild);
				newChildren[i] = leftChild;
				newChildren[i+1] = rightChild;
				i+=2;
				while(i<=children.length) {
					newChildren[i] = children[i-1];
					++i;
				}
				this.children = newChildren;
			}
		}
	}
}
