package com.suneo.flag.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		
		public MatrixState(int m, int n, int x, int y, IRepository repository) {
			this.m = m;
			this.n = n;
			this.x = x;
			this.y = y;
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

		@Override
		public String toString() {
			return x+","+y;
		}
	}

	public static class MatrixRepository implements IRepository {
		private int rows;
		private int cols;
		private Map<Integer, IState> stateCache = new HashMap<>();
		private List<IAction> actionList = List.of(new MatrixAction(0), new MatrixAction(1), new MatrixAction(2), new MatrixAction(3));

		public MatrixRepository(int rows, int cols) {
			this.rows = rows;
			this.cols = cols;
		}

		@Override
		public IState getStateById(int id) {
			if (stateCache.containsKey(id)) {
				return stateCache.get(id);
			}
			int x = id / cols;
			int y = id % cols;

			IState create = new MatrixState(rows, cols, x, y, this);
			stateCache.put(id, create);

			return create;
		}

		@Override
		public IAction getActionById(int id) {
			return actionList.get(id);
		}
	}

	public static class MatrixEnv implements IEnvironment {
		private int[][] map;

		public MatrixEnv(int[][] map) {
			this.map = map;
		}

		public int[] getDim() {
			return new int[]{map.length, map[0].length};
		}

		@Override
		public double reward(IState state, IAction action) {
			IState next = state.transit(action);
			MatrixState nextMatrixState = (MatrixState) next;
			int x = nextMatrixState.x;
			int y = nextMatrixState.y;

			return map[x][y] - 1;
		}

		@Override
		public boolean ternmial(IState state) {
			MatrixState matrixState = (MatrixState) state;
			int x = matrixState.x;
			int y = matrixState.y;

			return map[x][y] == 100 || map[x][y] == -100;
		}
		
	}

	public static void main(String[] args) {
		int[][] map = new int[][] {
				{0, 1,  0,     0, -1},
				{0, -1, 1,     1,  -100},
				{0, -1, 100,  -1, -1},
				{0, -1, 0,     0,  0 },
				{1,  1, 1,     1,  1}};

		IEnvironment env = new MatrixEnv(map);
		IRepository repository = new MatrixRepository(map.length, map[0].length);

		IState init = new MatrixState(map.length, map[0].length, 0, 0, repository);

		int epoch = 20;
		double step = 1.0 / epoch;
		double explore = 1.0;

		for(int i=0; i<epoch; i++) {
			BasicQ basicQ = new BasicQ(4, map[0].length * map.length, explore, init, env);
			basicQ.qLearn();

			basicQ.say();

			explore -= step;
		}

	}
}
