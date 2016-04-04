package mygame;

/**
 * This is a template that provides a start setup for many projects. It provides
 * initialization of all major components: initAppScreen(app); initGui();
 * initMaterials(); initLightandShadow(); initGeometries(); initCam();
 */
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.HingeJoint;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResults;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.util.TangentBinormalGenerator;
import java.awt.Dimension;
import java.awt.Toolkit;
import poormocap.Mocap;
import poormocapplayer.MocapPlayer;

public class Main extends SimpleApplication {

    public static Material gold, magenta;
    Geometry geomSphere, geomBox, geomJoint;
    MocapPlayer player;
    Mocap mocap;
    Node ayyLmaoNode;
    Skeleton[] skeletons;
    Skeleton player1;
    Spatial wallModel;
    BulletAppState bullet;
    RigidBodyControl wall, phyJoint;
    HingeJoint joint;
    
    public static void main(String[] args) {
        Main app = new Main();
        initAppScreen(app);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        player = new MocapPlayer("assets/etc/Test3.serial");
        //mocap = new Mocap();
        initGui();
        initMaterials();
        initLightandShadow();
        initGeometries();
        initCam();
        initPhysics();
        initSkeletons();
        
    }

    @Override
    public void simpleUpdate(float tpf) {
        int[][] joints = player.getJoints();
        //int[][] joints = mocap.getJoints();
        if (joints != null) {
           
            player1.setJoints(player.getJoints());
            player1.draw();
        }
        
        CollisionResults result = new CollisionResults();
        Vector3f current = phyJoint.getPhysicsLocation();
        current.z -= tpf;
        phyJoint.setPhysicsLocation(current);
       
    }

    // -------------------------------------------------------------------------
    // Initialization Methods
    // -------------------------------------------------------------------------
    private static void initAppScreen(SimpleApplication app) {
        AppSettings aps = new AppSettings(true);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        screen.width *= 0.75;
        screen.height *= 0.75;
        aps.setResolution(screen.width, screen.height);
        app.setSettings(aps);
        app.setShowSettings(false);
    }

    private void initMaterials() {
        gold = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        gold.setBoolean("UseMaterialColors", true);
        gold.setColor("Ambient", ColorRGBA.Red);
        gold.setColor("Diffuse", ColorRGBA.Green);
        gold.setColor("Specular", ColorRGBA.Gray);
        gold.setFloat("Shininess", 4f); // shininess from 1-128

        magenta = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        magenta.setBoolean("UseMaterialColors", true);
        magenta.setColor("Ambient", ColorRGBA.Gray);
        magenta.setColor("Diffuse", ColorRGBA.Blue);
        magenta.setColor("Specular", ColorRGBA.Red);
        magenta.setFloat("Shininess", 2f); // shininess from 1-128
    }

    private void initGui() {
        setDisplayFps(true);
        setDisplayStatView(false);
    }

    private void initLightandShadow() {
        // Light1: white, directional
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.7f, -1.3f, -0.9f)).normalizeLocal());
        sun.setColor(ColorRGBA.Gray);
        rootNode.addLight(sun);

        // Light 2: Ambient, gray
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.7f, 0.7f, 0.7f, 1.0f));
        rootNode.addLight(ambient);

        // SHADOW
        // the second parameter is the resolution. Experiment with it! (Must be a power of 2)
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 512, 1);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);
    }

    private void initGeometries() {
				
        //hole wall	#1	
        //wallModel = getAssetManager().loadModel("Models/wall2/wall2.j3o");

        //hole wall #3
        //wallModel = getAssetManager().loadModel("Models/wall3/wall3.j3o");

        //hole wall #4
        wallModel = getAssetManager().loadModel("Models/wall4/wall4.j3o");

        TangentBinormalGenerator.generate(wallModel);
        ayyLmaoNode = new Node();
        ayyLmaoNode.attachChild(wallModel);
        ayyLmaoNode.setMaterial(gold);
        wallModel.setLocalTranslation(0.0f, 0f, 5f);
        ayyLmaoNode.attachChild(wallModel);
        ayyLmaoNode.updateModelBound();
        rootNode.attachChild(ayyLmaoNode);
						
	//joint sphere
        Sphere jointSphere = new Sphere(32, 32, 0.1f);
        geomJoint = new Geometry("joint", jointSphere);
        geomJoint.setMaterial(gold);
        geomJoint.setLocalTranslation(0, 3, 5f);
        rootNode.attachChild(geomJoint);
        // Materials must be initialized first
        
        // Ground
        Box box = new Box(8f, 2f, 8f);
        geomBox = new Geometry("ground", box);
        geomBox.setMaterial(magenta);
        geomBox.setLocalTranslation(0, -4f, 0);
        rootNode.attachChild(geomBox);

        // define shadow behavior
        //geomSphere.setShadowMode(ShadowMode.CastAndReceive);
        geomBox.setShadowMode(ShadowMode.Receive);
    }

    private void initCam() {
        flyCam.setEnabled(true);
	flyCam.setMoveSpeed(10f);
        cam.setLocation(new Vector3f(0f, -0.1f, -8f));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
				
    }

    private void initSkeletons() {
        
        player1 = new Skeleton(this);
        player1.setLocalTranslation( -0.2f, -0.7f, 0);
        rootNode.attachChild(player1);
    }
    
    private void initPhysics(){
        bullet = new BulletAppState();
       
        stateManager.attach(bullet);
        bullet.setDebugEnabled(true);
         
        RigidBodyControl ground = new RigidBodyControl(0.0f);
        geomBox.addControl(ground);
        bullet.getPhysicsSpace().add(ground);
        
        
        wall = new RigidBodyControl(CollisionShapeFactory.createMeshShape(wallModel), 1.0f);
        wallModel.addControl(wall);
        bullet.getPhysicsSpace().add(wall);
        
        
        phyJoint = new RigidBodyControl(0.0f);
        geomJoint.addControl(phyJoint);
        bullet.getPhysicsSpace().add(phyJoint);
        
        // connect small and large sphere by a HingeJoint
        joint = new HingeJoint(
                phyJoint,
                wall,
                new Vector3f(0f, 0f, 0f), // pivot point local to A
                new Vector3f(0f, 3f, 0f), // pivot point local to B 
                Vector3f.UNIT_X, // DoF Axis of A (x axis)
                Vector3f.UNIT_X);        // DoF Axis of B (x axis)
        
        bullet.getPhysicsSpace().add(joint);
        bullet.setDebugEnabled(true);
        
    }
}

