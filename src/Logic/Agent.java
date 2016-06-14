package Logic;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

public class Agent implements Runnable 
{
	private boolean _life = true;
	private boolean _running = false;

	private Gui.Core _listener;
	private Map<Character, Character> _beliefs, _desires, _memory;
	private Map<State, Transition> _graph = new HashMap<State, Transition>();
	private Stack<Action> _plan = new Stack<Action>();

	public Agent (Gui.Core listener, Map<Character, Character> beliefs, Map<Character, Character> desires)
	{	
		_listener = listener;
		_beliefs = new HashMap<Character, Character>(beliefs);
		_memory = beliefs;
		_desires = desires;
	}

	public Map<Character, Character> GetBeliefs()
	{
		return _beliefs;
	}

	public Map<Character, Character> GetDesires()
	{
		return _desires;
	}

	public Map<Character, Character> GetMemory()
	{
		return _memory;
	}

	public void Stop()
	{
		_running = false;
	}

	synchronized public void Run()
	{ 
		_running = true;
		notify();
	}

	public void Finish()
	{ 
		_life = false;
	}

	@Override
	public void run() 
	{
		while(_life) 
		{ 
			try 
			{ 
				synchronized(this) 
				{ 
					if(!_running) 
						wait() ;
				}
				if (SolutionFound(_beliefs, _desires))
				{
					System.out.println("Nothing to do");
					Stop();
					continue;
				}
				if (_plan.isEmpty())
				{
					_graph.clear();
					CreatePlan();
				}
				else
				{
					_plan.peek().Execute(_listener, _beliefs);
					_plan.pop();
				}
			} 
			catch(InterruptedException e){} 
		}
	}

	public boolean SolutionFound(Map<Character, Character> belifs, Map<Character, Character> desires)
	{
		for (Character label : desires.keySet())
			if (belifs.get(label) != desires.get(label))
				return false;
		return true;
	}

	//---------------Planner-------------------
	//DFS
	private void CreatePlanDFS()
	{
		boolean back;
		State currentState = new State(_beliefs);
		_graph.put(currentState, null);
		System.out.println("Planning");

		while (!SolutionFound(currentState.GetConfig(),_desires))
		{			
			back = true;
			List<Action> intentions = currentState.GetIntentions(_desires);
			for (Action intention : intentions)
			{
				State state = new State(intention.Simulate(currentState.GetConfig()));
				if (_graph.containsKey(state))
					continue;
				_graph.put(state, new Transition(currentState, intention));
				currentState = state;
				back = false;
				break;
			}
			if (back)
				currentState = _graph.get(currentState).state;
		}
		System.out.println("Done Planning");
		while (_graph.get(currentState) != null)
		{
			_plan.add(_graph.get(currentState).action);
			currentState = _graph.get(currentState).state;
		}
	}

	//BFS
	private void CreatePlanBFS()
	{		
		Queue<State> toExplore = new LinkedList<State>();
		State currentState = new State(_beliefs);
		_graph.put(currentState, null);
		toExplore.add(currentState);
		System.out.println("Planning");

		while (!toExplore.isEmpty())
		{		
			currentState = toExplore.poll();
			List<Action> intentions = currentState.GetIntentions(_desires);
			for (Action intention : intentions)
			{
				State state = new State(intention.Simulate(currentState.GetConfig()));
				if (_graph.containsKey(state))
					continue;
				toExplore.add(state);
				_graph.put(state, new Transition(currentState, intention));	
				if (SolutionFound(state.GetConfig(),_desires))
				{
					currentState = state;
					toExplore.clear();
					break;
				}
			}
		}
		System.out.println("Done Planning");
		while (_graph.get(currentState) != null)
		{
			_plan.add(_graph.get(currentState).action);
			currentState = _graph.get(currentState).state;
		}
	}

	//Heuristic based
	private void CreatePlan()
	{		
		Queue<State> toExplore = new PriorityQueue<State>(new Comparator<State>() 
		{
			public int compare(State state1, State state2) {
			    return state2.GetBlocksOnPosition(_desires) - state1.GetBlocksOnPosition(_desires);
			  }
		});
		State currentState = new State(_beliefs);
		_graph.put(currentState, null);
		toExplore.add(currentState);
		System.out.println("Planning");

		while (!toExplore.isEmpty())
		{		
			currentState = toExplore.poll();
			List<Action> intentions = currentState.GetIntentions(_desires);
			for (Action intention : intentions)
			{
				State state = new State(intention.Simulate(currentState.GetConfig()));
				if (_graph.containsKey(state))
					continue;

				toExplore.add(state);
				_graph.put(state, new Transition(currentState, intention));	
				if (SolutionFound(state.GetConfig(),_desires))
				{
					currentState = state;
					toExplore.clear();
					break;
				}
			}
		}
		System.out.println("Done Planning");
		while (_graph.get(currentState) != null)
		{
			_plan.add(_graph.get(currentState).action);
			currentState = _graph.get(currentState).state;
		}
	}
}