package com.suneo.flag.lib;

import java.util.ArrayList;
import java.util.List;

public class BasicQ {
	public static interface IAction {
		int getId();
	}
	
	public static interface IState {
		int getId();
	}
	
	public static interface IEnvironment {
		double reward(IState state, IAction action);
		boolean ternmial(IState state);
		IState transit(IState state, IAction action);
		List<IAction> getAllPossibleActions(IState state);
		void takeAction(IState state, IAction action);
	}
	
	public static interface IRepository {
		IState getStateById(int id);
		IAction getActionById(int id);
	}
			
	private double[][] qTable;
	private double rate;
	private double explore;
	
	public BasicQ(int n_actions, int n_states, double rate, double explore) {
		this.qTable = new double[n_states][n_actions];
		this.explore = explore;
		this.rate = rate;
	}
	
	
	public void setExplore(double explore) {
		this.explore = explore;
	}
	
	public void qLearn(IEnvironment env, IState start) {
		IState now = start;
		
		List<IState> trace = new ArrayList<>();
		trace.add(start);
		
		int round = 0;
				
		while(round < 100 && !env.ternmial(now)) {
			double random = Math.random();
			List<IAction> possible = env.getAllPossibleActions(now);
			IAction selected = null;
			if(random < explore) {
				int size = possible.size() - 1;
				int index = (int)(Math.random() * size);
			    selected = possible.get(index);
			} else {
				int maxIndex = -1;
				IAction maxAction = null;
				for(IAction action : possible) {
					int id = action.getId();
					if(maxIndex == -1 || qTable[now.getId()][maxIndex] < qTable[now.getId()][id]) {
						maxIndex = id;
						maxAction = action;
					}
				}
				selected = maxAction;
			}
			
			double reward = env.reward(now, selected);
			
			update(env, now, selected, reward);
			
			env.takeAction(now, selected);

			now = env.transit(now, selected);

			trace.add(now);
			
			++round;
		}
		
		StringBuilder sb = new StringBuilder();
		for(IState state : trace) {
			sb.append(state.toString());
			sb.append("-->");
		}

		System.out.println(sb.toString());
	}
	
	private void update(IEnvironment env, IState from, IAction taken, double reward) {		
		IState resultState = env.transit(from, taken);
		int resultStateId = resultState.getId();
		List<IAction> nextActions = env.getAllPossibleActions(resultState);
		double max = Double.MIN_VALUE;
		for (IAction action : nextActions) {
			int id = action.getId();
			double tmp = qTable[resultStateId][id];
			if (tmp > max) {
				max = tmp;
			}
		}
		
		double newValue = reward + max;
		double decided = newValue * rate + qTable[from.getId()][taken.getId()] * (1 - rate);
		
		qTable[from.getId()][taken.getId()] = decided;
	}
	
	public void say() {
		for(int i=0;i<qTable.length;i++) {
			for(int j=0;j<qTable[0].length;j++) {
				System.out.print(qTable[i][j]+"  ");
			}
			System.out.println();
		}
	}
}
