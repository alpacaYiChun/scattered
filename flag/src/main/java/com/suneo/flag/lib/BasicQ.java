package com.suneo.flag.lib;

import java.util.List;

public class BasicQ {
	public static interface IAction {
		int getId();
	}
	
	public static interface IState {
		int getId();
		IState transit(IAction action);
		List<IAction> getAllPossibleActions();
	}
	
	public static interface IEnvironment {
		double reward(IState state, IAction action);
		boolean ternmial(IState state);
	}
	
	public static interface IRepository {
		IState getStateById(int id);
		IAction getActionById(int id);
	}
		
	private int n_actions;
	
	private double[][] qTable;
	private double rate;
	private double explore = 1.0;
	
	private IState start;
	private IEnvironment env;
	
	public BasicQ(int n_actions, int n_states, IState start, IEnvironment env) {
		this.n_actions = n_actions;
		this.qTable = new double[n_states][n_actions];
		this.start = start;
		this.env = env;
	}
	
	
	
	public void qLearn() {
		IState now = start;
		
		int round = 0;
				
		while(round < 100 && !env.ternmial(now)) {
			double random = Math.random();
			List<IAction> possible = now.getAllPossibleActions();
			IAction selected = null;
			if(random < explore) {
				int size = possible.size() - 1;
				int index = (int)(Math.random() * size);
			    selected = possible.get(index);
			} else {
				int maxIndex = -1;
				for(int j=0;j<n_actions;j++) {
					if(j == -1 && qTable[now.getId()][maxIndex] < qTable[now.getId()][j]) {
						maxIndex = j;
					}
				}
				selected = possible.get(maxIndex);
			}
			
			double reward = env.reward(now, selected);
			
			update(now, selected, reward);
			
			now = now.transit(selected);
			
			++round;
		}
	}
	
	private void update(IState from, IAction taken, double reward) {
		IState resultState = from.transit(taken);
		int resultStateId = resultState.getId();
		double max = Double.MIN_VALUE;
		for (int j = 0; j < n_actions; j++) {
			double tmp = qTable[resultStateId][j];
			if (tmp > max) {
				max = tmp;
			}
		}
		
		double newValue = reward + max;
		double decided = newValue * rate + qTable[from.getId()][taken.getId()] * (1 - rate);
		
		qTable[from.getId()][taken.getId()] = decided;
	}
}
