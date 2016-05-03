package D3;

import java.awt.Font;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Text2D;

public class Label extends TransformGroup 
{
	private final char ID;
	private static Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
	
	public Label (Character letter)
	{
		super();
		ID = letter;
		Text2D label = new Text2D(letter.toString(), green, "SansSerif", 512, Font.BOLD );
	
		Transform3D trans = new Transform3D();
		Vector3f vector = new Vector3f(-0.7f, -1.15f, 1.1f);
		trans.set(vector);
		setTransform(trans);
		
		addChild(label);
	}

	public char getID()
	{
		return ID;
	}
}
