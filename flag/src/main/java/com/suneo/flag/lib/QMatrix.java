package com.suneo.flag.lib;

import java.util.ArrayList;
import java.util.List;

import com.suneo.flag.lib.BasicQ.IAction;
import com.suneo.flag.lib.BasicQ.IEnvironment;
import com.suneo.flag.lib.BasicQ.IRepository;
import com.suneo.flag.lib.BasicQ.IState;

public class QMatrix {
	public static class MatrixAction implements IAction {
		public static int[][] dirs = new int[][]{{0,1},{1,0},{0,-1},{-1,0}};
		
		private int id;
		
		public MatrixAction(int id) {
			this.id = id;
		}
		
		@Override
		public int getId() {
			return this.id;
		}
		
	}
	
	public static class MatrixState implements IState {
		private int x;
		private int y;
		private int m;
		private int n;
		
		private IRepository repository;
		
		public MatrixState(int x, int y, int m, int n, IRepository repository) {
			this.x = x;
			this.y = y;
			this.m = m;
			this.n = n;
			this.repository = repository;
		}
		
		@Override
		public int getId() {
			return x*n + y;
		}

		@Override
		public IState transit(IAction action) {
			int aid = action.getId();
			int xx = x+MatrixAction.dirs[aid][0];
			int yy = y+MatrixAction.dirs[aid][1];
			int xid = xx*n+yy;
			
			return repository.getStateById(xid);
		}

		@Override
		public List<IAction> getAllPossibleActions() {
			List<IAction> ret = new ArrayList<>();
			for(int d=0;d<4;d++) {
				int xx = x+MatrixAction.dirs[d][0];
				int yy = y+MatrixAction.dirs[d][1];
				if(xx<0||yy<0||xx>=m||yy>=n) {
					continue;
				}
				ret.add(repository.getActionById(d));
			}
			return ret;
		}
		
	}
	
	public static class MatrixEnv implements IEnvironment {

		@Override
		public double reward(IState state, IAction action) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean ternmial(IState state) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
}
