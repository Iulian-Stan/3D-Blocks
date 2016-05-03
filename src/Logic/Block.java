package Logic;

public class Block {
	private final char ID;
	private Block _block;

	public Block(char id, Block block)
	{
		ID = id;
		_block = block;
	}

	public Block(char id)
	{
		this(id, null);
	}

	public Block(Block block)
	{
		this(block.ID, block._block);
	}

	public boolean OnTable()
	{
		return _block == null;
	}

	public Block On()
	{
		return _block;
	}

	public void PutOnTable()
	{
		_block = null;
	}

	public void PutOn(Block block)
	{
		_block = block;
	}
	
	public char getID()
	{
		return ID;
	}

	@Override
	public String toString()
	{
		return "" + ID;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}

		Block block = (Block) obj;
		return ID == block.ID && (_block != null && _block.equals(block._block) ||
				_block == null	&& block._block == null);
	}
	
	@Override
	public int hashCode() {
		return ID - 'A' << 4 | (OnTable() ? 0 : On().ID - 'A');
	}
}
