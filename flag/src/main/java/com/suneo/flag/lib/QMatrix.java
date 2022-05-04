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
		private int n;
		
		public MatrixState(int x, int y, int n) {
			this.x = x;
			this.y = y;
			this.n = n;
		}
		
		@Override
		public int getId() {
			return x*n + y;
		}

		@Override
		public String toString() {
			return x+","+y;
		}
	}

	public static class MatrixRepository implements IRepository {
		private int cols;
		
		private Map<Integer, IState> stateCache = new HashMap<>();
		private List<IAction> actionList = List.of(new MatrixAction(0), new MatrixAction(1), new MatrixAction(2), new MatrixAction(3));

		public MatrixRepository(int cols) {
			this.cols = cols;
		}

		@Override
		public IState getStateById(int id) {
			if (stateCache.containsKey(id)) {
				return stateCache.get(id);
			}
			int x = id / cols;
			int y = id % cols;

			IState create = new MatrixState(x, y, cols);
			stateCache.put(id, create);

			return create;
		}

		@Override
		public IAction getActionById(int id) {
			return actionList.get(id);
		}
	}

	public static class MatrixEnv implements IEnvironment {
		private MatrixRepository repository;
		
		private int[][] map;

		public MatrixEnv(int[][] map) {
			this.map = map;
			this.repository = new MatrixRepository(map[0].length);
		}
				
		@Override
		public double reward(IState state, IAction action) {
			IState next = transit(state, action);
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
			
			if (map[x][y] == 100 || map[x][y] == -100) {
				return true;
			}
			
			if (getAllPossibleActions(state).size() == 0) {
				return true;
			}
			
			return false;
		}

		@Override
		public IState transit(IState state, IAction action) {
			int aid = action.getId();
			MatrixState stat = (MatrixState)state;
			int xx = stat.x+MatrixAction.dirs[aid][0];
			int yy = stat.y+MatrixAction.dirs[aid][1];
			int xid = xx*map[0].length+yy;
			
			return repository.getStateById(xid);
		}

		@Override
		public List<IAction> getAllPossibleActions(IState state) {
			List<IAction> ret = new ArrayList<>();
			MatrixState stat = (MatrixState)state;
			for(int d=0;d<4;d++) {
				int xx = stat.x+MatrixAction.dirs[d][0];
				int yy = stat.y+MatrixAction.dirs[d][1];
				if(xx<0||yy<0||xx>=map.length||yy>=map[0].length) {
					continue;
				}
				ret.add(repository.getActionById(d));
			}
			return ret;
		}
		
		@Override
		public void takeAction(IState state, IAction action) {

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

		IState init = new MatrixState(0, 0, map[0].length);

		int epoch = 100;
		double step = 1.0 / epoch;
		double explore = 1.0;

		BasicQ basicQ = new BasicQ(4, map[0].length * map.length, 0.7, explore);
		
		for(int i=0; i<epoch; i++) {
			basicQ.setExplore(explore);
			
			basicQ.qLearn(env, init);

			explore -= step;
		}
		
		basicQ.say();
	}
}
