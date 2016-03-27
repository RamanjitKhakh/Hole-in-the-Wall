package mygame;

/**
 * This is a template that provides a start setup for many projects. It provides
 * initialization of all major components: initAppScreen(app); initGui();
 * initMaterials(); initLightandShadow(); initGeometries(); initCam();
 */
import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResults;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
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
    Geometry geomSphere, geomBox;
    MocapPlayer player;
    Mocap mocap;
		Node ayyLmaoNode;
    Skeleton[] skeletons;
    Skeleton player1;
		Spatial wallModel;
    
    public static void main(String[] args) {
        Main app = new Main();
        initAppScreen(app);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //player = new MocapPlayer("assets/etc/Test3.serial");
        mocap = new Mocap();
        initGui();
        initMaterials();
        initLightandShadow();
        initGeometries();
        initCam();
        initSkeletons();
    }

    @Override
    public void simpleUpdate(float tpf) {
       // int[][] joints = player.getJoints();
        int[][] joints = mocap.getJoints();
        if (joints != null) {
           /* for (Skeleton skeleton : skeletons) {
                skeleton.setJoints(mocap.getJoints());
                skeleton.draw();
            }*/
            player1.setJoints(mocap.getJoints());
            player1.draw();
        }
        
//        CollisionResults res = new CollisionResults();
//            for(int i =0; i < earth.terrian.size(); i++){
//              Rocks r = earth.terrian.get(i);
//              BoundingVolume hitbox = r.internal.getWorldBound();
//              oto.otoNode.collideWith(hitbox, res );
//              if( (res.size() > 0) ){
//                  
//                  new SingleBurstParticleEmitter(main ,oto.otoNode, r.getLocalTranslation());
//                  res.clear();
//                  hit++;
//                  hitCounter.setText("Total Amount Hit " + hit); 
//                 
//              }
//            }
        CollisionResults result = new CollisionResults();
        //ayyLmaoNode
        BoundingVolume hitbox = wallModel.getWorldBound();//ayyLmaoNode.getChild(0).getWorldBound();
        for(Geometry m : player1.bones){
            m.collideWith(hitbox, result);
            if(result.size() > 0 ){
                result.clear();
                //System.out.println("hit!!!");
                new SingleBurstParticleEmitter((SimpleApplication)this, m.getParent(), m.getLocalTranslation());
            }
        }

        ayyLmaoNode.move(0, 0, -tpf);
        if( ayyLmaoNode.getLocalTranslation().z <= -13)
            ayyLmaoNode.setLocalTranslation(0.0f, 0.0f, 5f);

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
				wallModel = getAssetManager().loadModel("Models/wall3/wall3.j3o");
						
				TangentBinormalGenerator.generate(wallModel);
				ayyLmaoNode = new Node();
				ayyLmaoNode.attachChild(wallModel);
				ayyLmaoNode.setMaterial(gold);
				wallModel.setLocalTranslation(0.0f, -0.5f, 5f);
				//wallModel.rotate(0,FastMath.HALF_PI, 0);
				wallModel.scale(1.05f);
				ayyLmaoNode.attachChild(wallModel);
                                ayyLmaoNode.updateModelBound();
                                
				rootNode.attachChild(ayyLmaoNode);
						
						
        // Materials must be initialized first
        // Large Sphere
        Sphere sphereLarge = new Sphere(32, 32, 1.5f);
        geomSphere = new Geometry("Shiny", sphereLarge);
        geomSphere.setMaterial(gold);
        geomSphere.setLocalTranslation(0, 2f, 0);
        //rootNode.attachChild(geomSphere);

        // Ground
        Box box = new Box(8f, 2f, 8f);
        geomBox = new Geometry("ground", box);
        geomBox.setMaterial(magenta);
        geomBox.setLocalTranslation(0, -4f, 0);
        rootNode.attachChild(geomBox);

        // define shadow behavior
        geomSphere.setShadowMode(ShadowMode.CastAndReceive);
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
}
