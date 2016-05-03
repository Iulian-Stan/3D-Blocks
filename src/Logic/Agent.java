package Logic;

import java.awt.event.ActionEvent;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Agent implements Runnable 
{
	private boolean _life = true;
	private boolean _running = false;

	private Gui.Core _listener;

	private HashSet<Block> beliefs = new HashSet<Block>();
	private HashSet<Block> desires = new HashSet<Block>();

	private Deque<Action> plan = new LinkedList<Action>();

	private Block _inHand = null;

	public Agent (Gui.Core listener)
	{	
		_listener = listener;

		_listener.getBeliefs(beliefs, _inHand);


		Block A = new Block('D');
		desires.add(A);
		A = new Block('C',A);
		desires.add(A);
		A = new Block('B',A);
		desires.add(A);
		A = new Block('A',A);
		desires.add(A);
		A = new Block('H');
		desires.add(A);
		A = new Block('G',A);
		desires.add(A);
		A = new Block('F',A);
		desires.add(A);
		A = new Block('E',A);
		desires.add(A);
	}

	public void InitBeliefs()
	{
		_listener.getBeliefs(beliefs, _inHand);
	}

	public void InitDesires()
	{
		_listener.getBeliefs(desires, _inHand);
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
	public void run() {
		while(_life) 
		{ 
			try 
			{ 
				synchronized(this) 
				{ 
					if(!_running) 
						wait() ;
				}
				if (!Succeded())
					if (plan.isEmpty())
						Plan();
					else
					{
						plan.peek().Execute();
						plan.poll();
					}
				else
					Stop();
			} 
			catch(InterruptedException e){} 
		}
	}

	private void CopySet(HashSet<Block> source, HashSet<Block> destination, Block aux)
	{
		destination.clear();
		for (Block block : source)
		{
			destination.add(new Block(block));
		}	
		if (aux == null)
			_inHand = null;
		else
			_inHand = new Block(aux);
	}

	//---------------Planifier-------------------

	private boolean Succeded()
	{
		for (Block block : desires)
			if (!beliefs.contains(block))
				return false;
		return true;
	}

	private void Plan()
	{
		State initialState = new State(beliefs, null, null, null),state,newState;
		HashSet<Block> oldBeliefs = new HashSet<Block>();
		CopySet(beliefs, oldBeliefs, _inHand);
		HashSet<State> explored = new HashSet<State>();
		Deque<State> toExplore = new LinkedList<State>();
		toExplore.addLast(initialState);
		boolean solutionFound = false;
		while (!toExplore.isEmpty() && !solutionFound)
		{
			state = toExplore.removeFirst();
			CopySet(state._beliefs, beliefs, state._aux);
			for (Action action : state.GenerateActions())
			{
				CopySet(state._beliefs, beliefs, state._aux);
				action.Execute();
				newState = new State(beliefs, _inHand, action, state);
				if (!explored.contains(newState))
				{
					toExplore.addLast(newState);
				}
				if (newState.Contains(desires))
				{
					solutionFound = true;
					break;
				}
			}
			explored.add(state);
			System.out.println(explored.size());
		}
		if (solutionFound)
		{
			state = toExplore.removeLast();
			while (!state.equals(initialState))
			{
				plan.addFirst(state._action);
				state = state._previousState;
			}
		}
		CopySet(oldBeliefs, beliefs, null);
		if (plan.isEmpty())
			System.out.println("No solution !");
		for (Action a : plan)
			System.out.println(a.toString());
	}

	//---------------Preidcates------------------

	private boolean ON(Block A, Block B)
	//is true if A is on B
	{
		return B.equals(A.On());
	}

	private boolean ONTABLE(Block A)
	//is true if A is on the  table
	{
		return A.OnTable();
	}

	private boolean CLEAR(Block A)
	//is true if no block is on A
	{
		for (Block B : beliefs)
			if (ON(B, A))
				return false;
		return true;
	}

	private boolean HOLD(Block A)
	//is true if the robot holds A
	{
		return A.equals(_inHand);
	}

	private boolean ARMEMPTY()
	//is true if the robot's arm is empty
	{
		return _inHand == null;
	}

	//---------------Actions---------------------

	class UNSTACK extends Action
	{
		public UNSTACK(Block A, Block B){super(A,B);}

		@Override
		public boolean isPossible() {
			return ARMEMPTY() && CLEAR(A) && ON(A,B);
		}

		@Override
		public void Execute() {
			_inHand = A;
			beliefs.remove(A);
			if (!plan.isEmpty())
				_listener.actionPerformed(new ActionEvent(A.toString(), 0, "pick"));
		}

		@Override
		public String toString() {
			return "UNSTACK ( " + A.toString() + ", " + B.toString() + " )"; 
		}
	}

	class STACK extends Action
	{
		public STACK(Block A, Block B) {super(A,B);}

		@Override
		public boolean isPossible() {
			return HOLD(A) && CLEAR(B);
		}

		@Override
		public void Execute() {
			_inHand = null;
			A.PutOn(B);		
			beliefs.add(A);
			if (!plan.isEmpty())
				_listener.actionPerformed(
						new ActionEvent(A.toString() + B.toString(), 0, "put"));
		}

		@Override
		public String toString() {
			return "STACK ( " + A.toString() + ", " + B.toString() + " )";
		}
	}

	class PICKUP extends Action
	{

		public PICKUP(Block A) {
			super(A);
		}

		@Override
		public boolean isPossible() {
			return ARMEMPTY() && CLEAR(A) && ONTABLE(A);
		}

		@Override
		public void Execute() {
			_inHand = A;	
			beliefs.remove(A);
			if (!plan.isEmpty())
				_listener.actionPerformed(new ActionEvent(A.toString(), 0, "pick"));
		}

		@Override
		public String toString() {
			return "PICKUP ( " + A.toString() + " )";
		}
	}

	class PUTDOWN extends Action
	{
		public PUTDOWN(Block A) {
			super(A);
		}

		@Override
		public boolean isPossible() {
			return HOLD(A);
		}

		@Override
		public void Execute() {
			_inHand = null;
			A.PutOnTable();
			beliefs.add(A);
			if (!plan.isEmpty())
				_listener.actionPerformed(
						new ActionEvent(A.toString()+ " ", 0, "put"));
		}

		@Override
		public String toString() {
			return "PUTDOWN ( " + A.toString() + " )";
		}
	}

	class State 
	{		
		private State _previousState;
		private HashSet<Block> _beliefs = new HashSet<Block>();
		private Action _action;
		private Block _aux;

		public State(HashSet<Block> beliefs, Block aux, Action action, State previous)
		{
			for (Block block : beliefs)
				_beliefs.add(new Block(block));
			_aux = aux;
			_action = action;
			_previousState = previous;
		}

		public boolean Contains(HashSet<Block> beliefs)
		{
			for (Block block : beliefs)
				if (!_beliefs.contains(block))
					return false;
			return true;
		}
		
		private boolean OnPosition(Block block)
		{
			return desires.contains(block) &&
					(block.OnTable() || OnPosition(block.On()));
		}

		private void Options(List<Action> intentions)
		{
			Action option;
			if (_aux == null )
			{
				for (Block A : _beliefs)
				{
					if(OnPosition(A))
						continue;
					option = new PICKUP(A);
					Filter(intentions, option);
					if (!A.OnTable())
					{
						option = new UNSTACK(A,A.On());
						Filter(intentions, option);
					}
				}
			}
			else
			{
				option = new PUTDOWN(_aux);
				_aux.PutOnTable();
				if (OnPosition(_aux))
				{
					intentions.add(option);
					return;
				}
				Filter(intentions, option);
				for (Block B : _beliefs)
				{
					option = new STACK(_aux, B);
					_aux.PutOn(B);
					if (OnPosition(_aux))
					{
						intentions.clear();
						intentions.add(option);
						return;
					}
					Filter(intentions, option);
				}
			}
		}


		private void Filter(List<Action> intentions, Action action)
		{
			if (action.isPossible())
				intentions.add(action);
		}

		public List<Action> GenerateActions()
		{
			List<Action> intentions = new LinkedList<Action>();
			Options(intentions);
			return intentions;
		}

		@Override
		public String toString() {
			String s = "";
			for (Block block : _beliefs)
				if (block.OnTable())
					s = s + " OnTable ( " + block.toString() + " )";
				else
					s = s + " On ( " + block.toString() + ", " + block.On().toString() + " )";
			return s;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj == null || obj.getClass() != this.getClass()) {
				return false;
			}

			State state = (State) obj;

			return Contains(state._beliefs);
		}

		@Override
		public int hashCode()
		{
			int code = 0;
			for (Block block : _beliefs)
			{
				code |= (block.OnTable() ? 0 : block.On().getID() - 'A')  << (block.getID() - 'A');
			}
			return code;
		}
	}
}