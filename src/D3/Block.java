package D3;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Box;

public class Block extends TransformGroup 
{
	public static final float Size = 1.0f;
	
	private static Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
	private static Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
	private static Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
	
	private static Material material = new Material(red, black, red, white, 64);
	private static Appearance appearance = new Appearance();

	static {appearance.setMaterial(material);}
	
	private static Transform3D trans = new Transform3D();
	private Vector3f vector = new Vector3f();

	private Label label;
	private Box box;
	
	public Block(Character letter, float x, float y)
	{
		super();
		
		setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		
		box = new Box(Size, Size, Size, appearance);
		label = new Label(letter);
		
		addChild(box);
		addChild(label);
	
		setPosition(x, y);
	}
	
	public void setPosition(float x, float y)
	{
		vector.set(x,y,0);
		trans.set(vector);
		setTransform(trans);
	}
	
	public void setPosition(Vector3f vect)
	{
		vector.set(vect.x, vect.y, 0);
		trans.set(vector);
		setTransform(trans);
	}
	
	public void getPosition(Vector3f vect)
	{
		vect.set(vector.x, vector.y, 0);
	}
	
	public void Peek(Vector3f vect, float dx, float dy)
	{
		vect.set(vector.x + dx, vector.y + dy, 0);
	}
	
	public void Move(float dx, float dy)
	{
		vector.set(vector.x + dx, vector.y + dy ,0);
		trans.set(vector);
		setTransform(trans);
	}
	
	public float getX()
	{
		return vector.getX();
	}
	
	public float getY()
	{
		return vector.getY();
	}
	
	public Character getID()
	{
		return label.getID();
	}
	
	public boolean isOnPosition(float x, float y)
	{
		return vector.x == x && vector.y == y;
	}
	
	public boolean isOnPosition(Vector3f vect)
	{
		return isOnPosition(vect.x, vect.y);
	}
	
	public boolean isUnder(Block block)
	{
		return isOnPosition(block.getX(), block.getY() - 2 * Block.Size);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}

		Block block = (Block) obj;
		return label.equals(block.label);
	}
}
