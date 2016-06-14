package Gui;

import java.util.Map;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import D3.World;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

@SuppressWarnings("serial")
public class WorldPanel extends Canvas3D
{
	private SimpleUniverse _universe;
	private BranchGroup _scene;
	private World _world;

	public WorldPanel(Map<Character, Character> beliefs)
	{
		super(SimpleUniverse.getPreferredConfiguration());
		_universe = new SimpleUniverse(this);
		_world = new World(beliefs);
		_scene = CreateSceneGraph(_world);

		//universe.getViewingPlatform().setNominalViewingTransform();

		InitUserPosition();        
		OrbitControls(this);

		_universe.addBranchGraph(_scene);
	}

	private BranchGroup CreateSceneGraph(World world) {
		// Create the root of the branch graph
		BranchGroup objRoot = new BranchGroup();

		TransformGroup objScale = new TransformGroup();
		Transform3D scaleTrans = new Transform3D();
		scaleTrans.set(1 / 5f); // scale down by 3.5x
		objScale.setTransform(scaleTrans);
		objRoot.addChild(objScale);

		// Create a TransformGroup and initialize it to the
		// identity. Enable the TRANSFORM_WRITE capability so that
		// the mouse behaviors code can modify it at runtime. Add it to the
		// root of the subgraph.
		TransformGroup objTrans = new TransformGroup();
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		objScale.addChild(objTrans);

		objTrans.addChild(world);

		BoundingSphere bounds = new BoundingSphere(new Point3d(), 100.0);

		Background bg = new Background(new Color3f(0.17f, 0.65f, 0.92f));
		bg.setApplicationBounds(bounds);
		objTrans.addChild(bg);

		// set up the mouse rotation behavior
		MouseRotate mr = new MouseRotate();
		mr.setTransformGroup(objTrans);
		mr.setSchedulingBounds(bounds);
		mr.setFactor(0.007);
		objTrans.addChild(mr);

		// set up the mouse wheel zooom behavior
		MouseWheelZoom mwz = new MouseWheelZoom();
		mwz.setTransformGroup(objTrans);
		mwz.setSchedulingBounds(bounds);
		mwz.setFactor(3);
		objTrans.addChild(mwz);

		// Set up the ambient light
		Color3f ambientColor = new Color3f(0.1f, 0.1f, 0.1f);
		AmbientLight ambientLightNode = new AmbientLight(ambientColor);
		ambientLightNode.setInfluencingBounds(bounds);
		objRoot.addChild(ambientLightNode);

		// Set up the directional lights
		Color3f light1Color = new Color3f(1.0f, 1.0f, 1.0f);
		Vector3f light1Direction = new Vector3f(0.0f, -0.2f, -1.0f);

		DirectionalLight light1 = new DirectionalLight(light1Color,
				light1Direction);
		light1.setInfluencingBounds(bounds);
		objRoot.addChild(light1);

		return objRoot;
	}

	private void OrbitControls(Canvas3D c)	
	{
		//OrbitBehaviour allows the user to rotate around the scene, and to zoom in and out.
		OrbitBehavior orbit = new OrbitBehavior(c, OrbitBehavior.REVERSE_ALL);
		BoundingSphere bounds =	new BoundingSphere(new Point3d(0, 0, 0), 100);
		orbit.setSchedulingBounds(bounds);

		ViewingPlatform vp = _universe.getViewingPlatform();
		vp.setViewPlatformBehavior(orbit);	    
	}

	private void InitUserPosition()
	{
		//Set the user's initial viewpoint using lookAt()
		ViewingPlatform vp = _universe.getViewingPlatform();
		TransformGroup steerTG = vp.getViewPlatformTransform();

		Transform3D t3d = new Transform3D( );
		steerTG.getTransform( t3d );

		// args are: viewer posn, where lokking, up direction
		t3d.lookAt( new Point3d(0,5,10), new Point3d(0,0,-5), new Vector3d(0,1,0));
		t3d.invert();

		steerTG.setTransform(t3d);
	}

	public void Up(Character label)
	{
		_world.MoveUp(label);
	}

	public void Down(Character label)
	{
		_world.MoveDown(label);
	}

	public void Left(Character label)
	{
		_world.MoveLeft(label);
	}

	public void Right(Character label)
	{
		_world.MoveRight(label);
	}

	public void Move(String labels)
	{
		if (labels.length() < 2)
			_world.Stack(labels.charAt(0), null);
		else
			_world.Stack(labels.charAt(0), labels.charAt(1));
	}
	
	public void GetConfiguration(Map<Character, Character> config)
	{
		_world.GetConfiguration(config);
	}
	
	public void DispalyConfiguration(Map<Character, Character> config) 
	{
		_world.DisplayConfiguration(config);
	}
}
