package mygame;

/**
 * This is a template that provides a start setup for many projects. It provides
 * initialization of all major components: initAppScreen(app); initGui();
 * initMaterials(); initLightandShadow(); initGeometries(); initCam();
 */
import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import java.awt.Dimension;
import java.awt.Toolkit;
import poormocap.Mocap;
import poormocapplayer.MocapPlayer;

public class Main extends SimpleApplication {

    public static Material gold, magenta;
    Geometry geomSphere, geomBox;
    MocapPlayer player;
    Mocap mocap;
    Skeleton[] skeletons;
    Skeleton player1;
    
    public static void main(String[] args) {
        Main app = new Main();
        initAppScreen(app);
        app.start();
    }

    @Override
    public void simpleInitApp() {
       // player = new MocapPlayer("assets/etc/Test3.serial");
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
        //int[][] joints = player.getJoints();
        int[][] joints = mocap.getJoints();
        if (joints != null) {
            /*for (Skeleton skeleton : skeletons) {
                skeleton.setJoints(mocap.getJoints());
                skeleton.draw();
            }*/
            player1.setJoints(mocap.getJoints());
            player1.draw();
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
        cam.setLocation(new Vector3f(1f, 3f, 10f));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }

    private void initSkeletons() {
        /*skeletons = new Skeleton[1];
        for (int i = 0; i < skeletons.length; i++) {
            int indexX = i%4;
            int indexZ = i/4;
            skeletons[i] = new Skeleton(this);
            skeletons[i].setLocalTranslation(indexX * 2f - 3, -1, indexZ*2f - 3);
            rootNode.attachChild(skeletons[i]);
        }*/
        player1 = new Skeleton(this);
        player1.setLocalTranslation( 0, 0, 0);
        rootNode.attachChild(player1);
    }
}
