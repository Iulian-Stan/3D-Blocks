package D3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

public class World extends TransformGroup
{
	private final static int FLOOR_WID = 3 * (int)Block.SIZE2;
	private final static Color3f BLUE = new Color3f(0.0f, 0.0f, 1.0f);
	private final static Color3f GREEN = new Color3f(1.0f, 1.0f, 0.0f);

	private Transform3D _trans = new Transform3D();
	private Vector3f _vector = new Vector3f();
	private Set<Character> _processedLabels = new HashSet<Character>();

	//current world blocks
	private Map<Character, Block> _blocks = new HashMap<Character, Block>();

	public World(Map<Character ,Character> beliefs)
	{
		super();

		setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		_vector.set(0.0f, 0.0f, 0.0f);
		_trans.set(_vector);
		setTransform(_trans);

		AddFloor(beliefs.keySet().size() * (int)Block.SIZE2);

		AddBlocks(beliefs);
	}

	private void AddTile(boolean isBlue, int x, int z)
	{
		FloorTile floorTile = new FloorTile((isBlue) ? BLUE : GREEN, x, z);
		addChild(floorTile);
	}

	private void AddFloor(int floorLength)
	{
		boolean isBlue;
		for(int z=-FLOOR_WID/2; z <= (FLOOR_WID/2)-1; z++) {
			isBlue = (z%2 == 0)? true : false;  
			for(int x=-floorLength/2; x <= (floorLength/2)-1; x++) {
				AddTile(isBlue, x, z);
				isBlue = !isBlue;
			}
		}
	} 

	private void AddBlock(Character label, float x, float y)
	{
		Block block = new Block(label, x, y);
		Vector3f vector = new Vector3f();
		block.StorePosition(vector);
		_blocks.put(label, block);
		addChild(block);
	}

	private void AddBlock(Character label, Character base)
	{
		Block baseBlock = _blocks.get(base);
		Block block = new Block(label, baseBlock.GetX(), baseBlock.GetY() + Block.SIZE2);
		Vector3f vector = new Vector3f();
		block.StorePosition(vector);
		_blocks.put(label, block);
		addChild(block);
	}

	private void AddBlock(Character label, Map<Character, Character> beliefs, IntegerObj column) 
	{
		if (_processedLabels.contains(label))
			return;
		Character base = beliefs.get(label);
		if (base == null)
		{
			AddBlock(label, column.value * Block.SIZE2, 1);
			_processedLabels.add(label);
			++column.value;
			return;
		}
		AddBlock(base, beliefs, column);
		_processedLabels.add(base);
		AddBlock(label, base);
		_processedLabels.add(label);
	}

	private void AddBlocks(Map<Character, Character> beliefs)
	{
		Set<Character> labels = beliefs.keySet();
		Integer columns = 0;
		IntegerObj column;
		for (Character label : labels)
			if (beliefs.get(label) == null)
				++columns;
		column = new IntegerObj(-columns / 2);
		_processedLabels.clear();
		for (Character label : labels)
			AddBlock(label, beliefs, column);
	}

	public Map<Character,Block> GetBlocks()
	{
		return _blocks;
	}

	public Block GetBlock(Character label)
	{
		return _blocks.get(label);
	}

	public Block GetBlock(float x, float y)
	{
		for (Block block : _blocks.values())
		{
			if (block.GetX() == x && block.GetY() == y)
				return block;
		}
		return null;
	}

	public void GetConfiguration(Map<Character, Character> config)
	{		
		Set<Character> labels = config.keySet();
		for (Character label : labels)
		{
			Block block = GetBlock(label);
			Block baseBlock = GetBlock(block.GetX(), block.GetY() - Block.SIZE2);
			config.put(label, baseBlock == null ? null : baseBlock.GetID());
		}
	}

	public void DisplayConfiguration(Map<Character, Character> config)
	{
		Set<Character> labels = config.keySet();
		Integer columns = 0;
		IntegerObj column;
		for (Character label : labels)
			if (config.get(label) == null)
				++columns;
		column = new IntegerObj(-columns / 2);
		_processedLabels.clear();		
		for (Character label : labels)
			SetBlock(label, config, column);
	}

	private void SetBlock(Character label, Map<Character, Character> config, IntegerObj column) 
	{
		if (_processedLabels.contains(label))
			return;		
		Block block = _blocks.get(label);
		Character base = config.get(label);
		if (base == null)
		{
			block.SetPosition(column.value * Block.SIZE2, 1);
			_processedLabels.add(label);
			++column.value;
			return;
		}
		SetBlock(base, config, column);
		_processedLabels.add(base);
		SetBlock(block, base);
		_processedLabels.add(label);
	}

	private void SetBlock(Block block, Character base)
	{
		Block baseBlock = _blocks.get(base);
		block.SetPosition(baseBlock.GetX(), baseBlock.GetY() + Block.SIZE2);
	}

	class IntegerObj {
		int value;
		IntegerObj(int val) {
			this.value = val;
		}
	}

	private void MoveBlock(Character label, float dx, float dy)
	{
		Block A = _blocks.get(label);
		A.Move(dx, dy);
	}

	public void MoveUp(Character label)
	{
		MoveBlock(label, 0, Block.SIZE2);
	}

	public void MoveDown(Character label)
	{
		MoveBlock(label, 0, -Block.SIZE2);
	}

	public void MoveLeft(Character label)
	{
		MoveBlock(label, -Block.SIZE2, 0);
	}

	public void MoveRight(Character label)
	{
		MoveBlock(label, Block.SIZE2, 0);
	}


	private void PickUp(Character A)
	{
		Block block = _blocks.get(A);
		float height = 1;
		for (Block b : _blocks.values())
			if (b.GetY() > height)
				height = b.GetY();
		height += Block.SIZE2;
		while (block.GetY() < height)
		{
			MoveUp(A);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void PutOn(Character A, Character B)
	{
		Block block = _blocks.get(A);
		float height = 1;
		if (B != null)
		{
			Block base = _blocks.get(B);
			height = base.GetY() + Block.SIZE2;
		}
		while (block.GetY() > height)
		{
			MoveDown(A);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isFreeColumn(float column)
	{
		for (Block block : _blocks.values())
			if (block.GetX() == column)
				return false;
		return true;
	}

	private void MoveOver(Character A, Character B)
	{
		Block block = _blocks.get(A);
		float column = 0;
		if (B == null)
			while(!isFreeColumn(column))
				column += Block.SIZE2;	
		else
			column = _blocks.get(B).GetX();
		while (block.GetX() != column)
		{
			if (column < block.GetX())
				MoveLeft(A);
			else
				MoveRight(A);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	public void Stack(Character A, Character B)
	{
		PickUp(A);
		MoveOver(A, B);
		PutOn(A, B);
	}
}
