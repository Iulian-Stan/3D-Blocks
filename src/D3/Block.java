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
	private static final float SIZE = 1f;
	public static final float SIZE2 = SIZE * 2;
	
	private static final Color3f RED = new Color3f(1, 0, 0);
	private static final Color3f BLACK = new Color3f(0, 0, 0);
	private static final Color3f WHITE = new Color3f(1, 1, 1);
	
	private static Material material = new Material(RED, BLACK, RED, WHITE, 64);
	private static Appearance appearance = new Appearance();

	static {appearance.setMaterial(material);}
	
	private static Transform3D _trans = new Transform3D();
	private Vector3f _vector = new Vector3f();
	private Label _label;
	private Box _box;
	
	public Block(Character letter, float x, float y)
	{
		super();
		
		setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		
		_box = new Box(SIZE, SIZE, SIZE, appearance);
		_label = new Label(letter);
		
		addChild(_box);
		addChild(_label);
	
		SetPosition(x, y);
	}
	
	public void SetPosition(float x, float y)
	{
		_vector.set(x, y, 0);
		_trans.set(_vector);
		setTransform(_trans);
	}
	
	public void SetPosition(Vector3f vect)
	{
		_vector.set(vect.x, vect.y, 0);
		_trans.set(_vector);
		setTransform(_trans);
	}
	
	//store block position in a vector
	public void StorePosition(Vector3f vect)
	{
		vect.set(_vector.x, _vector.y, 0);
	}

	public void Move(float dx, float dy)
	{
		_vector.set(_vector.x + dx, _vector.y + dy, 0);
		_trans.set(_vector);
		setTransform(_trans);
	}
	
	public float GetX()
	{
		return _vector.getX();
	}
	
	public float GetY()
	{
		return _vector.getY();
	}
	
	public Character GetID()
	{
		return _label.GetID();
	}
}
