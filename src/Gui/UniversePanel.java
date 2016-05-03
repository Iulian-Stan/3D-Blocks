package Gui;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

import D3.Block;
import D3.World;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

@SuppressWarnings("serial")
public class UniversePanel extends Canvas3D
{
	private SimpleUniverse universe;
	private BranchGroup scene;
	private World world;

	public UniversePanel()
	{
		super(SimpleUniverse.getPreferredConfiguration());
		universe = new SimpleUniverse(this);
		world = new World();
		scene = createSceneGraph(world);

		//universe.getViewingPlatform().setNominalViewingTransform();

		initUserPosition();        
		orbitControls(this);

		universe.addBranchGraph(scene);
	}

	private BranchGroup createSceneGraph(World world) {
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

	private void orbitControls(Canvas3D c)
	/* OrbitBehaviour allows the user to rotate around the scene, and to
	     zoom in and out.
	 */
	{
		OrbitBehavior orbit = 
				new OrbitBehavior(c, OrbitBehavior.REVERSE_ALL);
		BoundingSphere bounds =
				new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		orbit.setSchedulingBounds(bounds);

		ViewingPlatform vp = universe.getViewingPlatform();
		vp.setViewPlatformBehavior(orbit);	    
	}

	private void initUserPosition()
	/* Set the user's initial viewpoint using lookAt()  */
	{
		ViewingPlatform vp = universe.getViewingPlatform();
		TransformGroup steerTG = vp.getViewPlatformTransform();

		Transform3D t3d = new Transform3D( );
		steerTG.getTransform( t3d );

		// args are: viewer posn, where lokking, up direction
		t3d.lookAt( new Point3d(0,5,10), new Point3d(0,0,-5), 
				new Vector3d(0,1,0));
		t3d.invert();

		steerTG.setTransform(t3d);
	}

	public void Up(Character label)
	{
		world.Up(label);
	}

	public void Down(Character label)
	{
		world.Down(label);
	}

	public void Left(Character label)
	{
		world.Left(label);
	}

	public void Right(Character label)
	{
		world.Right(label);
	}

	public void PickUp(String cmd)
	{
		world.PickUp(cmd.charAt(0));
	}

	public void PutDown(String cmd)
	{
		if (cmd.charAt(1) == ' ')
			world.PutDown(cmd.charAt(0));
		else
			world.PutDown(cmd.charAt(0), cmd.charAt(1));
	}

	public void getBeliefs(HashSet<Logic.Block> beliefs, Logic.Block inHand)
	{
		beliefs.clear();
		inHand = null;
		Map<Character,Block> blocks = world.getBlocks();

		for (Entry<Character,Block> entry : blocks.entrySet())
			beliefs.add(new Logic.Block(entry.getKey()));
		for (Logic.Block block : beliefs)
		{
			Block A = blocks.get(block.getID());
			Block B = world.getUnder(A);
			if (B != null)
			{
				if (!A.equals(B))
				{
					for (Logic.Block under : beliefs)
						if (under.getID() == B.getID())
						{
							block.PutOn(under);
							break;
						}
				}
				else
					inHand = block;
			}
		}
		beliefs.remove(inHand);
	}

	public Set<Character> getLabels()
	{
		return world.getBlocks().keySet();
	}
	
	public void Memorize() 
	{
		world.Memorize();
	}
	
	public void Restore() 
	{
		world.Restore();
	}

}
