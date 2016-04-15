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
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
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

    BitmapText stateInfoText;
    public static Material gold, magenta;
    Geometry geomSphere, geomBox, geomJoint, heightBoxGeo;
    MocapPlayer player;
    Mocap mocap;
    Node ayyLmaoNode;
    Skeleton[] skeletons;
    Skeleton player1;
    Spatial wallModel;
    BulletAppState bullet;
    RigidBodyControl wall, phyJoint;
    HingeJoint joint;
    boolean mocapPlayer = true;// change to false for kinect
    boolean gameOn = false;
    StartScreen s;
    Wall mainWall;
    int level = 0;
    
    public static void main(String[] args) {
        Main app = new Main();
        initAppScreen(app);
        app.start();
    }

    public AppSettings getSettings(){
        return this.settings;
    }
    
    @Override
    public void simpleInitApp() {
        s = new StartScreen();
        stateManager.attach(s);
        
				if(mocapPlayer){
						player = new MocapPlayer("assets/etc/Test3.serial");
				}else{
						mocap = new Mocap();
				}
				initGui();
				initMaterials();
				initLightandShadow();
				initGeometries();
				initCam();
				// call these 3 methods to start
				//initPhysics();
				//initSkeletons();
				//initGeometriesPostPhysics();
        
    }

    @Override
    public void simpleUpdate(float tpf) {
        if(gameOn){
           
        }
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

   @Override 
   public void destroy(){
       System.out.println("exiting");
       super.destroy();
       try{
           
           player1.removeFromParent();
       }catch(Exception e){
           
       }
       
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
  
        //create the state info break in the top right
        BitmapFont bmf = this.getAssetManager().loadFont("Interface/Fonts/ArialBlack.fnt");
        stateInfoText = new BitmapText(bmf);
        stateInfoText.setSize(bmf.getCharSet().getRenderedSize() * 1f);
        stateInfoText.setColor(ColorRGBA.White);
        stateInfoText.setText("");
        stateInfoText.setLocalTranslation(10f, this.settings.getHeight() - 10, 0f);
        this.getGuiNode().attachChild(stateInfoText);
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

    public void initGeometries() {
   
        // Ground
        Box box = new Box(8f, 2f, 8f);
        geomBox = new Geometry("ground", box);
        geomBox.setMaterial(magenta);
        geomBox.setLocalTranslation(0, -4f, 0);
        rootNode.attachChild(geomBox);

        // define shadow behavior
        //geomSphere.setShadowMode(ShadowMode.CastAndReceive);
        geomBox.setShadowMode(ShadowMode.Receive);
        
        //skeleton height test box
        Box heightBox = new Box(1f,1f,1f);
        heightBoxGeo = new Geometry("heightBox", heightBox);
        heightBoxGeo.setMaterial(gold);
        heightBoxGeo.setLocalTranslation(0, 0.9f, 0);
        //rootNode.attachChild(heightBoxGeo);
    }
    
    public void initGeometriesPostPhysics()
    {
        //mainWall = new Wall(4, this);
       //rootNode.attachChild(mainWall);
    }

    public void initCam() {
        flyCam.setEnabled(true);
				flyCam.setMoveSpeed(10f);
        cam.setLocation(new Vector3f(0f, 2f, 8f));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
				
    }

    public void initSkeletons() {
        
        player1 = new Skeleton(this);
        player1.rotate(0, 3.14f, 0);
        player1.setLocalTranslation( 0.2f, -0.7f, 0);
        
        rootNode.attachChild(player1);
    }
    
    public void initPhysics(){
        bullet = new BulletAppState();
       
        stateManager.attach(bullet);
        bullet.setDebugEnabled(true);
         
        RigidBodyControl ground = new RigidBodyControl(0.0f);
        geomBox.addControl(ground);
        bullet.getPhysicsSpace().add(ground);
       
        bullet.setDebugEnabled(true);
        
    }
}

