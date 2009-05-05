/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ufba.dp.j3d;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Box;
import java.io.FileNotFoundException;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Group;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Node;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Raster;
import javax.media.j3d.Shape3D;
import javax.media.j3d.View;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
//import org.ufba.raplom.j3d.stl.StlFile;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import org.j3d.renderer.java3d.loaders.STLLoader;

/**
 *
 * @author Rafael
 */
public class Java3DFacade {
    
    private BoundingSphere defaultBounds = null;
    private JPanel viewPanel = null;
    private JPanel previewPanel = null;
    
    // Java 3D global variables 
    private SimpleUniverse universe = null;
    private SimpleUniverse previewUniverse = null;
    private BranchGroup scene = null;
    private BranchGroup stl = null;
    // End of Java 3D global variables
    
    //private Solid solidBox = null;
    private HashMap<String, StlObject> openedStl = new HashMap<String, StlObject>();
    
    
    private BranchGroup previewScene = null;
    private int slice = 1;
    
    InnerModel model = new InnerModel ();

    InnerView  view1 = new InnerView (); //Visão normal
    InnerView  view2 = new InnerView (); //Fatiamento
    
    
    public Java3DFacade(JPanel view, JPanel previewPanel) {
        this.viewPanel = view;
        this.previewPanel = previewPanel;
        
        
        view1.setModel(model);
        view2.setModel(model);
        
        //view2.getViewer().getView().setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
        
        //Influencia no fatiamento
        view2.getViewer().getView().setProjectionPolicy(View.PARALLEL_PROJECTION);
        //view2.getViewer().getView().setFrontClipDistance(0.25);
        //view2.getViewer().getView().setBackClipDistance(0.252);
        
        
        
        TransformGroup vpGroup = view1.getViewingPlatform().getMultiTransformGroup().getTransformGroup(0);
        Transform3D vpTranslation = new Transform3D();
        
        Vector3f translationVector = new Vector3f(0.0f, 0.0f, 10F);
		  
		vpTranslation.setTranslation(translationVector);  
		vpGroup.setTransform(vpTranslation);  
        
        
       // view1.getViewer().getView().setFrontClipDistance(0.10);
        
        
        //Canvas3D c = createUniverse();
        //view.add(c, java.awt.BorderLayout.CENTER);
        
        //Canvas3D previewCanvas = createPreviewUniverse();
        //previewPanel.add(previewCanvas);
        
        //previewScene = createBranchGroup();
        //previewUniverse.addBranchGraph(previewScene);
        
        
        scene = createSceneGraph();
        //universe.addBranchGraph(scene);
        
        model.setScene(scene);
        
        view.add(view1.getCanvas3D(), java.awt.BorderLayout.CENTER);
        previewPanel.add(view2.getCanvas3D(), java.awt.BorderLayout.CENTER);
        
        colocarParede();
        
    }
    
    public void colocarParede()
    {
        float[] verts = {
            // Front Face
            1.0f, -1.0f,  1.0f,     1.0f,  1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f,    -1.0f, -1.0f,  1.0f
        };

        float[] normals = {
            // Front Face
            0.0f,  0.0f,  1.0f,     0.0f,  0.0f,  1.0f,
            0.0f,  0.0f,  1.0f,     0.0f,  0.0f,  1.0f
        };

        QuadArray quadArray = new QuadArray(4, QuadArray.COORDINATES |
                                              QuadArray.NORMALS);
        quadArray.setCoordinates(0, verts);
        quadArray.setNormals(0, normals);

        Appearance app = new Appearance();
        LineAttributes lineAtt =
                new LineAttributes(2,LineAttributes.PATTERN_SOLID,true);
            app.setLineAttributes(lineAtt);
            
        PolygonAttributes polyAtt =
                new PolygonAttributes(PolygonAttributes.POLYGON_FILL,
                                      PolygonAttributes.CULL_NONE,
                                      1.00f);
        
        ColoringAttributes colorAtt =
                new ColoringAttributes(0.0f,0.0f,0.0f,
                                       ColoringAttributes.NICEST);
        
        app.setPolygonAttributes(polyAtt);
        app.setColoringAttributes(colorAtt);
        
        Shape3D shape3D = new Shape3D(quadArray, app);
        shape3D.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
        shape3D.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        shape3D.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
        shape3D.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        shape3D.setCapability(Shape3D.ALLOW_BOUNDS_READ);
        shape3D.setCapability(Shape3D.ALLOW_BOUNDS_WRITE);
        
        BranchGroup bg = new BranchGroup();
        bg.addChild(shape3D);
        
        TransformGroup objTrans = new TransformGroup();
        
        Transform3D translacao = new Transform3D();
        translacao.setTranslation(new Vector3d(0.0, 0.0, 0.175));
        objTrans.addChild(bg);
        
        objTrans.setTransform(translacao);
        
        BranchGroup bg2 = new BranchGroup();
        bg2.addChild(objTrans);
        
        scene.addChild(bg2);
    }
    
    public void getScreenShotView2(){
        getScreenShot(view2.getCanvas3D(), slice++);
    }
    
    public Raster getScreenShot(Canvas3D canvas3d, int i) {

    	javax.media.j3d.GraphicsContext3D ctx = canvas3d.getGraphicsContext3D();

    	java.awt.Dimension scrDim = canvas3d.getSize();
    	
    	

    	// setting raster component
    	javax.media.j3d.Raster ras =
    	new Raster(
    	new javax.vecmath.Point3f(0.0f, 0.0f, -0.0f),
    	javax.media.j3d.Raster.RASTER_COLOR,
    	0,
    	0,
    	scrDim.width,
    	scrDim.height,
    	new javax.media.j3d.ImageComponent2D(
    	javax.media.j3d.ImageComponent.FORMAT_RGB,
    	new java.awt.image.BufferedImage(scrDim.width, scrDim.height, java.awt.image.BufferedImage.TYPE_INT_RGB)),
    	null);

    	ctx.readRaster(ras);
    	
    	BufferedImage bimage = ras.getImage().getImage();
        
    	
    	
    	ColorModel cm = bimage.getColorModel();
        
    	
    	int x = 0;
    	int y = 0;
    	for(y=0;y<scrDim.height;y++){
    		
    		for(x=0;x<scrDim.width;x++){
    			//System.out.println("RGB: "+bimage.getRGB(x, y));
                        int rgba = bimage.getRGB(x,y);
                        int red = (rgba >> 16) & 0xff;
                        int green = (rgba >> 8) & 0xff;
                        int blue = rgba & 0xff;
                        
                        
                        
                        //System.out.println("R: "+red+" G: "+green+" B: "+blue);

    		}
    		
    	}
    	
    	
    	try {
    		FileOutputStream out = new FileOutputStream("3d"+i+".jpg");
    		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
    		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bimage);
    		param.setQuality(1.0f, false);
    		encoder.setJPEGEncodeParam(param);
    		encoder.encode(bimage);
    		out.close();
    	}catch ( IOException e ){
    		System.out.println("I/O exception!");
    	}

    	return ras;
    }
    
    public Superficie getScreenShotView(){
        return getScreenShot2(view2.getCanvas3D(), slice++);
    }
    
    public Superficie getScreenShot2(Canvas3D canvas3d, int i) {

    	javax.media.j3d.GraphicsContext3D ctx = canvas3d.getGraphicsContext3D();

    	java.awt.Dimension scrDim = canvas3d.getSize();
    	
    	

    	// setting raster component
    	javax.media.j3d.Raster ras =
    	new Raster(
    	new javax.vecmath.Point3f(0.0f, 0.0f, -0.0f),
    	javax.media.j3d.Raster.RASTER_COLOR,
    	0,
    	0,
    	scrDim.width,
    	scrDim.height,
    	new javax.media.j3d.ImageComponent2D(
    	javax.media.j3d.ImageComponent.FORMAT_RGB,
    	new java.awt.image.BufferedImage(scrDim.width, scrDim.height, java.awt.image.BufferedImage.TYPE_INT_RGB)),
    	null);

    	ctx.readRaster(ras);
    	
    	BufferedImage bimage = ras.getImage().getImage();
        
    	
    	
    	ColorModel cm = bimage.getColorModel();
        
        Superficie superficie = new Superficie();
    	
    	int x = 0;
    	int y = 0;
    	for(y=0;y<scrDim.height;y++){
    		
            Linha l = new Linha();     
            superficie.addLinha(l);

            for(x=0;x<scrDim.width;x++){
                //System.out.println("RGB: "+bimage.getRGB(x, y));
                int rgba = bimage.getRGB(x,y);
                int red = (rgba >> 16) & 0xff;
                int green = (rgba >> 8) & 0xff;
                int blue = rgba & 0xff;                    

                Segmento s = l.getUltimoSegmento();
                if(red == 255 && green == 255){
                    if(s == null){
                        s = new Segmento();
                        s.setDepositar(true);
                    }

                    if(s.isDepositar()==true){
                        s.addPasso();
                    }else{
                        Segmento seg2 = new Segmento();
                        seg2.setDepositar(true);
                        l.addSegmento(seg2);
                        seg2.addPasso();
                    }

                }else{
                    if(s == null){
                        s = new Segmento();
                        s.setDepositar(false);
                    }

                    if(s.isDepositar()==false){
                        s.addPasso();
                    }else{
                        Segmento seg2 = new Segmento();
                        seg2.setDepositar(false);
                        l.addSegmento(seg2);
                        seg2.addPasso();
                    }
                }
            }
    	}
    	return superficie;
    }
    
    
    public boolean xVai = true;
    
    public ArrayList<Ponto> getScreenShot2(int z, ArrayList<Ponto> listaFinal) {
        
        Canvas3D canvas3d = view2.getCanvas3D();

	javax.media.j3d.GraphicsContext3D ctx = canvas3d.getGraphicsContext3D();
	java.awt.Dimension scrDim = canvas3d.getSize();
	
	// setting raster component
	javax.media.j3d.Raster ras =
	new Raster(
	new javax.vecmath.Point3f(0.0f, 0.0f, -0.0f),
	javax.media.j3d.Raster.RASTER_COLOR,
	0,
	0,
	scrDim.width,
	scrDim.height,
	new javax.media.j3d.ImageComponent2D(
	javax.media.j3d.ImageComponent.FORMAT_RGB,
	new java.awt.image.BufferedImage(scrDim.width, scrDim.height, java.awt.image.BufferedImage.TYPE_INT_RGB)),
	null);

	ctx.readRaster(ras);
	
	BufferedImage bimage = ras.getImage().getImage();
	
	int x = 0;
	int y = 0;
        
        boolean yVai;
        
        if(z%2==0){
            yVai = true;
        }else
        {
            yVai = false;
        }
        
        
        int comparaX = 0;
        if(!xVai) comparaX=1;

	if(yVai){
            for(y=0;y<scrDim.height;y++){
		ArrayList<Ponto> listaIntermediaria = new ArrayList<Ponto>();
		if(y%2==comparaX)
		{
                    //va
                    for(x=0;x<scrDim.width;x++)
                    {
			int rgba = bimage.getRGB(x,y);
			int red = (rgba >> 16) & 0xff;
			int green = (rgba >> 8) & 0xff;
			int blue = rgba & 0xff;
			
			if(red == 255 && green == 255 && blue == 0){
				Ponto p = new Ponto(x,y,z);
				listaIntermediaria.add(p);
			}
                    }
		}
                else
		{
                    //volte
                    for(x=scrDim.width-1;x>=0;x--)
                    {
			int rgba = bimage.getRGB(x,y);
			int red = (rgba >> 16) & 0xff;
			int green = (rgba >> 8) & 0xff;
			int blue = rgba & 0xff;
			
			if(red == 255 && green == 255 && blue == 0){
                            Ponto p = new Ponto(x,y,z);
                            listaIntermediaria.add(p);
                        }
		    }
		}
		if(listaIntermediaria.size()>0){
                    listaFinal.add(listaIntermediaria.get(0));
                    listaFinal.add(listaIntermediaria.get(listaIntermediaria.size()-1));
                }
            }
	}
	else
	{
            for(y=scrDim.height-1;y>=0;y--){
                ArrayList<Ponto> listaIntermediaria = new ArrayList<Ponto>();
		if(y%2==comparaX)
		{
                    //va
                    for(x=0;x<scrDim.width;x++)
                    {
                        int rgba = bimage.getRGB(x,y);
			int red = (rgba >> 16) & 0xff;
			int green = (rgba >> 8) & 0xff;
			int blue = rgba & 0xff;
					
			if(red == 255 && green == 255 && blue == 0){
				Ponto p = new Ponto(x,y,z);
				listaIntermediaria.add(p);
			}
		    }
		}
                else
		{
                    //volte
                    for(x=scrDim.width-1;x>=0;x--)
                    {
			int rgba = bimage.getRGB(x,y);
			int red = (rgba >> 16) & 0xff;
			int green = (rgba >> 8) & 0xff;
			int blue = rgba & 0xff;
					
			if(red == 255 && green == 255 && blue == 0){
                            Ponto p = new Ponto(x,y,z);
                            listaIntermediaria.add(p);
			}
                    }
		}
                if(listaIntermediaria.size()>0){
                    listaFinal.add(listaIntermediaria.get(0));
                    listaFinal.add(listaIntermediaria.get(listaIntermediaria.size()-1));
                }
            }
        }
        xVai = !xVai;
	return listaFinal;
    }
    
    
    private Canvas3D createUniverse() {
    	 // Get the preferred graphics configuration for the default screen
    	 GraphicsConfiguration config =
    		 SimpleUniverse.getPreferredConfiguration();

    	 // Create a Canvas3D using the preferred configuration
    	 Canvas3D c = new Canvas3D(config);

    	 // Create simple universe with view branch
    	 universe = new SimpleUniverse(c);

    	 // This will move the ViewPlatform back a bit so the
    	 // objects in the scene can be viewed.
    	 universe.getViewingPlatform().setNominalViewingTransform();

    	 // Ensure at least 5 msec per frame (i.e., < 200Hz)
    	 universe.getViewer().getView().setMinimumFrameCycleTime(5);

    	 return c;
     }
    
    private Canvas3D createPreviewUniverse() {
        
        GraphicsConfiguration config =
    		 SimpleUniverse.getPreferredConfiguration();

    	 // Create a Canvas3D using the preferred configuration
    	 Canvas3D c = new Canvas3D(config);

    	 // Create simple universe with view branch
    	 previewUniverse = new SimpleUniverse(c);

    	 // This will move the ViewPlatform back a bit so the
    	 // objects in the scene can be viewed.
    	 previewUniverse.getViewingPlatform().setNominalViewingTransform();

    	 // Ensure at least 5 msec per frame (i.e., < 200Hz)
    	 previewUniverse.getViewer().getView().setMinimumFrameCycleTime(5);

    	 return c;
    }
    
    public void loadStlFile(URL location)
    {
        STLLoader loader = new STLLoader();
        
        openedStl.clear();
        
        if(stl != null)
        {
            scene.removeChild(stl);
        }
        
        BranchGroup result = new BranchGroup();
    	stl = new BranchGroup();
        stl.setCapability(BranchGroup.ALLOW_DETACH);
        
        Scene scene2;
        
        BoundingBox bbox = null;
        
        double X = 0.0;
	double Y = 0.0;
	double Z = 0.0;
	
	double X0 = 0.0;
	double Y0 = 0.0;
	double Z0 = 0.0;
        
        Appearance app = new Appearance();
        PolygonAttributes polyAtt =
                new PolygonAttributes(PolygonAttributes.POLYGON_FILL,
                                      PolygonAttributes.CULL_NONE,
                                      5.01f);
        
        //polyAtt.setPolygonMode(PolygonAttributes.POLYGON_LINE);
        
        ColoringAttributes colorAtt =
                new ColoringAttributes(1.0f,1.0f,0.0f,
                                       ColoringAttributes.NICEST);
        
        app.setPolygonAttributes(polyAtt);
        app.setColoringAttributes(colorAtt);
        
        try 
    	{
    		scene2 = loader.load(location);
    		if (scene2 != null){
    			result = scene2.getSceneGroup();
                result.setCapability(Node.ALLOW_BOUNDS_WRITE);
                result.setCapability(Group.ALLOW_CHILDREN_WRITE);
                
                Hashtable namedObjects = scene2.getNamedObjects( );
                java.util.Enumeration enumValues = namedObjects.elements( );
                
                if( enumValues != null ) 
                {
                    while(enumValues.hasMoreElements( )) 
                    {
                    	Shape3D value = (Shape3D)enumValues.nextElement();
                        
                        
                        value.setAppearance(app);
                        
                        
                        bbox = (BoundingBox)value.getBounds();
                        
                        javax.vecmath.Point3d p0 = new javax.vecmath.Point3d();
                        javax.vecmath.Point3d p1 = new javax.vecmath.Point3d();
                        bbox.getLower(p0);
                        bbox.getUpper(p1);
                        
                        X = p1.getX();
                        Y = p1.getY();
                        Z = p1.getZ();
                        
                        X0 = p0.getX();
                        Y0 = p0.getY();
                        Z0 = p0.getZ();
                        
                        System.out.println("X: "+p1.getX());
                        System.out.println("Y: "+p1.getY());
                        System.out.println("Z: "+p1.getZ());
                        
                        System.out.println("X0: "+p0.getX());
                        System.out.println("Y0: "+p0.getY());
                        System.out.println("Z0: "+p0.getZ());
                        
                        
                        BoundingSphere bboxCamera = (BoundingSphere)result.getBounds();
                        
                        javax.vecmath.Point3d p3 = new javax.vecmath.Point3d();
                        
                        bboxCamera.getCenter(p3);
                        
                        System.out.println("Xres: "+p0.getX());
                        System.out.println("Yres: "+p0.getY());
                        System.out.println("Zres: "+p0.getZ());

                        
                        
                        System.out.println("Raio res: "+bboxCamera.getRadius());
                        
                        
                        
                        value.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE );
                        GeometryArray g = (GeometryArray)value.getGeometry();
                        g.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
                    }
                }               
    		}
    	}catch ( Exception e ){
    		System.err.println("loadSingelSTL(): Exception loading STL file from: " 
    				+ location);
                	e.printStackTrace();
    	}
        
        float difX = (float) (X-X0);
        float centroX = (float)X0 + difX/2;

        float difY = (float) (Y-Y0);
        float centroY = (float)Y0 + difY/2;

        float difZ = (float)(Z-Z0);
        float centroZ = (float)Z0 + difZ/2;
        
        
        
        TransformGroup objTrans = new TransformGroup();
        
        Transform3D translacao = new Transform3D();
        //translacao.setScale(1.0);
        translacao.setTranslation(new Vector3d(-centroX, -centroY, -centroZ+1.7));
        translacao.normalize();
        
        
        
        
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	objTrans.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
	
        
        
        
        RotateBehavior rotator = new RotateBehavior(objTrans);
        ScaleBehaviorStl scaler = new ScaleBehaviorStl(objTrans);
        TranslateBehavior translater = new TranslateBehavior(objTrans, 0.1f);
        
        rotator.setSchedulingBounds(getDefaultBounds());
        scaler.setSchedulingBounds(getDefaultBounds());
        translater.setSchedulingBounds(getDefaultBounds());
        
        
        
        objTrans.setTransform(translacao);
        objTrans.addChild(rotator);
        objTrans.addChild(scaler);
        objTrans.addChild(translater);
        
        
        objTrans.addChild(result);
        
        
        stl.addChild(objTrans);
        
        scene.addChild(stl);
        
        
        openedStl.put("teste", new StlObject(result, rotator, scaler, translater));
        
        
    }
    
    
    
    public void rotateX() {
        //opendedSolids.values().iterator().next().rotateX();
        openedStl.values().iterator().next().getRotator().rotateX();
    }
    
    public void rotateY() {
        //opendedSolids.values().iterator().next().getRotator().rotateY();
        openedStl.values().iterator().next().getRotator().rotateY();
    }
    
    public void rotateZ() {
        //opendedSolids.values().iterator().next().rotateZ();
        openedStl.values().iterator().next().getRotator().rotateZ();
    }
    
    public void rotateXBack() {
        //opendedSolids.values().iterator().next().rotateX();
        openedStl.values().iterator().next().getRotator().rotateXBack();
    }
    
    public void rotateYBack() {
        //opendedSolids.values().iterator().next().getRotator().rotateY();
        openedStl.values().iterator().next().getRotator().rotateYBack();
    }
    
    public void rotateZBack() {
        //opendedSolids.values().iterator().next().rotateZ();
        openedStl.values().iterator().next().getRotator().rotateZBack();
    }
    
    public void moveX(){
        openedStl.values().iterator().next().getTranslater().translateX();
    }
    
    public void moveY(){
        openedStl.values().iterator().next().getTranslater().translateY();
    }
    
    public void moveZ(){
        openedStl.values().iterator().next().getTranslater().translateZ();
    }
    
    
    public void moveXBack(){
        openedStl.values().iterator().next().getTranslater().translateXBack();
    }
    
    public void moveYBack(){
        openedStl.values().iterator().next().getTranslater().translateYBack();
    }
    
    public void moveZBack(){
        openedStl.values().iterator().next().getTranslater().translateZBack();
    }
    
    
    public void scaleMoore() {
        //opendedSolids.values().iterator().next().scaleMoore();
        openedStl.values().iterator().next().getScaler().scaleMoore();
    }
    
    public void scaleLess() {
        System.out.println("porreessa123");
        //opendedSolids.values().iterator().next().scaleLess();
        
        StlObject stl = openedStl.values().iterator().next();
        
        stl.getScaler().scaleLess();
    }
	
    public void centralizar() {
        System.out.println("Cajajo");
        StlObject stl = openedStl.values().iterator().next();
        //stl.getScaler().scaleLess();
        
        Enumeration children = stl.getStl().getAllChildren();
        int pontoAtual;
        double X0 = Integer.MAX_VALUE;
        double Y0 = Integer.MAX_VALUE;
        double Z0 = Integer.MAX_VALUE;

        double X = Integer.MIN_VALUE;
        double Y = Integer.MIN_VALUE;
        double Z = Integer.MIN_VALUE;
        while(children.hasMoreElements( )) 
        {
            Shape3D shape = (Shape3D)children.nextElement();
            Enumeration e = shape.getAllGeometries();
            while( e.hasMoreElements() ){
                 Object elem = e.nextElement();
                 if(elem instanceof GeometryArray){
                         GeometryArray g = (GeometryArray)elem;
                         pontoAtual=0;
                         while(pontoAtual<g.getValidVertexCount()){
                                 Point3d p1 = new Point3d();
                                 Point3d p2 = new Point3d();
                                 Point3d p3 = new Point3d();
                                 g.getCoordinate(pontoAtual, p1);
                                 pontoAtual++;
                                 g.getCoordinate(pontoAtual, p2);
                                 pontoAtual++;
                                 g.getCoordinate(pontoAtual, p3);
                                 pontoAtual++;
                                 
                                 if(p1.x < X0) X0 = p1.x;
                                 if(p1.x > X) X = p1.x;
                                 
                                 if(p1.y < Y0) Y0 = p1.y;
                                 if(p1.y > Y) Y = p1.y;
                                 
                                 if(p1.z < Z0) Z0 = p1.z;
                                 if(p1.z > Z) Z = p1.z;
                                 
                                 if(p2.x < X0) X0 = p2.x;
                                 if(p2.x > X) X = p2.x;
                                 
                                 if(p2.y < Y0) Y0 = p2.y;
                                 if(p2.y > Y) Y = p2.y;
                                 
                                 if(p2.z < Z0) Z0 = p2.z;
                                 if(p2.z > Z) Z = p2.z;
                                 
                                 if(p3.x < X0) X0 = p3.x;
                                 if(p3.x > X) X = p3.x;
                                 
                                 if(p3.y < Y0) Y0 = p3.y;
                                 if(p3.y > Y) Y = p3.y;
                                 
                                 if(p3.z < Z0) Z0 = p3.z;
                                 if(p3.z > Z) Z = p3.z;
                         }
                 }
            } 
        }
        X0 = X0*stl.getScaler().getScale();
        Y0 = Y0*stl.getScaler().getScale();
        Z0 = Z0*stl.getScaler().getScale();
        
        X = X*stl.getScaler().getScale();
        Y = Y*stl.getScaler().getScale();
        Z = Z*stl.getScaler().getScale();
        
        
        System.out.println("MIN: ("+X0+","+Y0+","+Z0+")  MAX: ("+X+","+Y+","+Z+")  -  escala: "+stl.getScaler().getScale());
        float difX = (float) (X-X0);
        float centroX = (float)X0 + difX/2;

        float difY = (float) (Y-Y0);
        float centroY = (float)Y0 + difY/2;

        float difZ = (float)(Z-Z0);
        float centroZ = (float)Z0 + difZ/2;
        
        stl.getTranslater().vaPara(-centroX, -centroY, -centroZ);
    }
    
    public BranchGroup createBranchGroup() {
    	 // Create the root of the branch graph
    	 BranchGroup root = new BranchGroup();
         
         root.setCapability(Group.ALLOW_CHILDREN_WRITE);
         root.setCapability(Group.ALLOW_CHILDREN_READ);
         root.setCapability(Group.ALLOW_CHILDREN_EXTEND); 
         
         return root;
    }
    
    public BranchGroup createSceneGraph() {
        
        BranchGroup     root = createBranchGroup();
        TransformGroup  rootTrans = createTransformGroup();
        Appearance      waApp = new Appearance();
        Box             workArea = new Box(0.50f,0.35f,0.50f,waApp);
        TransparencyAttributes transAtt = new TransparencyAttributes();
        
        transAtt.setTransparency(0.80f);
        transAtt.setTransparencyMode(TransparencyAttributes.FASTEST);
        waApp.setTransparencyAttributes(transAtt);
        
        Transform3D vpTranslation = new Transform3D();
        Vector3f translationVector = new Vector3f(0.0f, 0.0f, 0.6F);
        
        //rootTrans.addChild(workArea);
                
        root.addChild(rootTrans);
        
        return root;
    }
    
    public BranchGroup createPreviewSceneGraph() {
        // Create the root of the branch graph
    	 BranchGroup root = new BranchGroup();
         
         root.setCapability(Group.ALLOW_CHILDREN_WRITE);
         root.setCapability(Group.ALLOW_CHILDREN_READ);
         root.setCapability(Group.ALLOW_CHILDREN_EXTEND);

         root.compile(); 
         
         return root;
    }
    
    public BoundingSphere getDefaultBounds() {
        if (defaultBounds==null) {
            defaultBounds = new BoundingSphere(new Point3d(0.0f,0.0f,0.0f),100.0);
        }
        return defaultBounds;
    }
    
    public TransformGroup createTransformGroup() {
        
        TransformGroup root = new TransformGroup();
        
        root.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        root.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        
        MouseRotate rotate = new MouseRotate(root);
        rotate.setSchedulingBounds(getDefaultBounds());
        
        root.addChild(rotate);
        
        return root;
    }
    
    public Transform3D createTransform3D() {
        Transform3D t3d = new Transform3D();
        return t3d;
    }
}
