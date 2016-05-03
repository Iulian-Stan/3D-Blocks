package D3;

import javax.media.j3d.*;
import javax.vecmath.*;


public class Floor extends Shape3D 
{
	private static final int NUM_VERTS = 4;
	private QuadArray plane;

	public Floor(Point3f p1, Point3f p2, Point3f p3, Point3f p4, Color3f col) 
	{
		plane = new QuadArray(NUM_VERTS, GeometryArray.COORDINATES | GeometryArray.COLOR_3 );
		createGeometry(p1, p2, p3, p4, col);
		setGeometry(plane);
		createAppearance();
	}    

	private void createGeometry(Point3f p1, Point3f p2, Point3f p3, Point3f p4, Color3f col)
	{
		// clockwise point specification
		plane.setCoordinate(0, p1);
		plane.setCoordinate(1, p2);
		plane.setCoordinate(2, p3);
		plane.setCoordinate(3, p4);
		Color3f cols[] = new Color3f[NUM_VERTS];
		for (int i=0; i < NUM_VERTS; i++)
			cols[i] = col;
		plane.setColors(0, cols);
	}


	private void createAppearance()
	{
		Appearance app = new Appearance();

		RenderingAttributes ra = new RenderingAttributes();
		ra.setDepthBufferEnable(true);
		app.setRenderingAttributes(ra);

		PolygonAttributes pa = new PolygonAttributes();
		pa.setCullFace(PolygonAttributes.CULL_NONE);   
		// no culling since the user can see the ColouredPlane from both sides
		app.setPolygonAttributes(pa);

		app.setMaterial( new Material() );
		setAppearance(app);
	}
}
