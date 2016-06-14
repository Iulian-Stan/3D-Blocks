package Logic;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class State
{
	private Map<Character, Character> _config;
	private List<Action> _intentions = new LinkedList<Action>();

	public State(Map<Character, Character> config)
	{
		_config = new HashMap<Character, Character>(config);			
	}

	private boolean OnPosition(Character label, Map<Character, Character> finalConfig)
	{
		return finalConfig.containsKey(label) &&
				finalConfig.get(label) == _config.get(label) &&
				(finalConfig.get(label) == null || OnPosition(finalConfig.get(label), finalConfig)) ||
				!finalConfig.containsKey(label) &&
				(finalConfig.get(label) == null || OnPosition(finalConfig.get(label), finalConfig));
	}

	private void GenerateIntentions(Map<Character, Character> finalConfig) 
	{
		Set<Character> labels = _config.keySet();
		for (Character firstLabel : labels)
		{
			if (OnPosition(firstLabel, finalConfig))
				continue;
			for (Character secondLabel : labels)
			{
				Action intension = new MoveAction(firstLabel, secondLabel);
				if (intension.isPossible())
				{
					_intentions.add(intension);
				}
			}
			Action intension = new MoveAction(firstLabel, null);
			if (intension.isPossible())
			{
				_intentions.add(intension);
			}
		}
	}

	public Map<Character, Character> GetConfig()
	{
		return _config;
	}

	public List<Action> GetIntentions(Map<Character, Character> finalConfig)
	{
		if (_intentions.isEmpty())
			GenerateIntentions(finalConfig);
		return _intentions;
	}

	@Override
	public String toString()
	{
		String hash = "";
		for (Character label : _config.keySet())
		{
			hash += "" + label + (_config.get(label) == null ? " " : _config.get(label));
		}
		return hash;
	}

	@Override
	public int hashCode()
	{			
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!State.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		final State other = (State) obj;
		return this.hashCode() == other.hashCode();
	}

	//---------------Preidcates------------------

	public boolean ON(Character A, Character B)
	//is true if A is on B
	{
		return _config.get(A) == B;
	}

	public boolean FREE(Character A)
	//is true if no block is on A
	{
		for (Character B : _config.keySet())
			if (ON(B, A))
				return false;
		return true;
	}

	//---------------Action---------------------

	class MoveAction extends Action
	{
		public MoveAction(Character A, Character B) {super(A,B);}

		@Override
		public boolean isPossible() 
		{
			return A != B && FREE(A) && (B == null || FREE(B));
		}

		@Override
		public void Execute(Gui.Core listener, Map<Character, Character> config) 
		{
			config.put(A, B);
			listener.actionPerformed(new ActionEvent(B == null ? A.toString() : A.toString() + B.toString(), 0, "move"));	 
		}

		@Override
		public String toString() 
		{
			return "Move " + A + " on " + (B == null ? '_' : B);
		}

		@Override
		public Map<Character, Character> Simulate(Map<Character, Character> config) 
		{
			Map<Character, Character> newConfig = new HashMap<Character, Character>(config);
			newConfig.put(A, B);	
			return newConfig;
		}
	}
	
	public int GetBlocksOnPosition(Map<Character, Character> finalConfig)
	{
		int nr = 0;
		for (Character label : _config.keySet())
		{
			if (OnPosition(label, finalConfig))
				++nr;
		}
		return nr;
	}
}
