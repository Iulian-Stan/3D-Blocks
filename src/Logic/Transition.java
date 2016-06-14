package Logic;

public class Transition 
{
	public State state;
	public Action action;
	
	public Transition(State state, Action action)
	{
		this.state = state;
		this.action = action;
	}
}