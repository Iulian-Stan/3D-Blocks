package D3;

import java.util.HashMap;
import java.util.Map;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class World extends TransformGroup
{
	private final static int FLOOR_LEN = 20;
	private final static int FLOOR_WID = 6;

	private final static Color3f blue = new Color3f(0.0f, 0.0f, 1.0f);
	private final static Color3f green = new Color3f(1.0f, 1.0f, 0.0f);

	private Transform3D trans = new Transform3D();
	private Vector3f vector = new Vector3f();

	private Map<Character, Block> blocks = new HashMap<Character, Block>();
	private Map<Character, Vector3f> memory = new HashMap<Character, Vector3f>();

	public World()
	{
		super();

		setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		vector.set(0.0f, 0.0f, 0.0f);
		trans.set(vector);
		setTransform(trans);

		AddFloor();

		AddBlocks();
	}

	private void AddBlocks()
	{
		Block A;
		A = addBlock('F', -4);
		A = addBlock('A', A);
		A = addBlock('C', A);

		A = addBlock('B', 0);
		A = addBlock('D', A);
		A = addBlock('G', A);
		

		A = addBlock('E', 4);
		A = addBlock('H', A);
	}

	private void AddFloor()
	{
		boolean isBlue;
		for(int z=-FLOOR_WID/2; z <= (FLOOR_WID/2)-1; z++) {
			isBlue = (z%2 == 0)? true : false;  
			for(int x=-FLOOR_LEN/2; x <= (FLOOR_LEN/2)-1; x++) {
				createTile(x, z, isBlue);
				isBlue = !isBlue;
			}
		}
	}

	private void createTile(int x, int z, boolean isBlue)
	{
		Point3f p1 = new Point3f(x, 0f, z);
		Point3f p2 = new Point3f(x+1.0f, 0f, z);
		Point3f p3 = new Point3f(x+1.0f, 0f, z+1.0f);
		Point3f p4 = new Point3f(x, 0f, z+1.0f);
		Color3f col = (isBlue) ? blue : green;
		addChild( new Floor(p4, p3, p2, p1, col) );
	} 

	private Block addBlock(Character label, float x, float y)
	{
		Block block = new Block(label, x, y);
		Vector3f vector = new Vector3f();
		block.getPosition(vector);
		blocks.put(label, block);
		memory.put(label, vector);
		addChild(block);
		return block;
	}

	private Block addBlock(Character label, float x)
	{
		return addBlock(label, x, Block.Size);
	}

	private Block addBlock(Character label, Block under)
	{
		return addBlock(label, under.getX(), under.getY() + 2 * Block.Size);
	}

	private boolean isFreeSpace(Character A)
	{
		float x = blocks.get(A).getX();
		for (Character B : blocks.keySet())
			if (A != B && x == blocks.get(B).getX())
				return false;
		return true;
	}

	private boolean Move(Character label, float dx, float dy)
	{
		Block A = blocks.get(label);		
		A.Peek(vector, dx, dy);
		if (vector.x >= -(FLOOR_LEN / 2 - 2) && vector.x <= FLOOR_LEN / 2 - 2 && 
				vector.y >= Block.Size && vector.y <= Block.Size + 2 * blocks.size())
		{
			for (Block B : blocks.values())
			{
				if (!A.equals(B) && B.isOnPosition(vector))
					return false;
			}
			A.Move(dx, dy);
			return true;
		}
		return false;
	}

	public boolean Up(Character label)
	{
		return Move(label, 0, 2 * Block.Size);
	}

	public boolean Down(Character label)
	{
		return Move(label, 0, -2 * Block.Size);
	}

	public boolean Left(Character label)
	{
		return Move(label, -2 * Block.Size, 0);
	}

	public boolean Right(Character label)
	{
		return Move(label, 2 * Block.Size, 0);
	}

	public void PickUp(Character A)
	{
		while (Up(A))
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void PutDown(Character A, Character B)
	{
		if (blocks.get(A).getX() > blocks.get(B).getX())
			while (blocks.get(A).getX() != blocks.get(B).getX() && Left(A))
			{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		else
			while (blocks.get(A).getX() != blocks.get(B).getX() && Right(A))
			{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		while (Down(A))
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}

	public void PutDown(Character A)
	{
		while (!isFreeSpace(A) && Left(A))
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		while (!isFreeSpace(A) && Right(A))
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		while (Down(A))
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}

	public Map<Character,Block> getBlocks()
	{
		return blocks;
	}

	public Block getUnder(Block A)
	{
		if (A.getY() == Block.Size)
			return null;
		for (Block B : blocks.values())
		{
			if (B.isUnder(A))
				return B;
		}
		return A;
	}

	public void Memorize()
	{
		for (Character label : blocks.keySet())
		{
			blocks.get(label).getPosition(memory.get(label));
		}
	}

	public void Restore()
	{
		for (Character label : blocks.keySet())
			blocks.get(label).setPosition(memory.get(label));
	}
}
