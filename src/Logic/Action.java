package Logic;

public abstract class Action {
	protected Block A = null;
	protected Block B = null;
	
	public Action(Block A){ this.A = new Block(A); }	
	
	public Action(Block A, Block B){ this(A); this.B = new Block(B); }	
	
	public abstract boolean isPossible();
	public abstract void Execute();
}
