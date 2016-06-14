package Logic;

import java.util.Map;

public abstract class Action {
	protected Character A = null;
	protected Character B = null;
	
	public Action(Character A){ this.A = A; }	
	
	public Action(Character A, Character B){ this(A); this.B = B; }	
	
	public abstract boolean isPossible();
	public abstract void Execute(Gui.Core listener, Map<Character, Character> config);
	public abstract Map<Character, Character> Simulate(Map<Character, Character> config);
}