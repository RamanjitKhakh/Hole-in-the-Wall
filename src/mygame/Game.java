package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import java.awt.Font;

public class Game extends AbstractAppState implements ActionListener {

    Main main;
    static int NUM_WALLS = 6;
    static float NUM_SPACING = 0.035f;
    int currentWall = 0;
    int clearedWalls = 0;
    boolean playerCollided = false;
    Node wallsClearedText;
    Picture wallsClearedPic;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.main = (Main) app;
        main.mainWall = new Wall(main.level, main);
        main.getRootNode().attachChild(main.mainWall);
        main.Cheer.play();
        initGUI();
        initCam();


    }

    public void initCam() {
        Camera cam = main.getCamera();
        float aspect = (float) main.getSettings().getWidth() / main.getSettings().getHeight();
        float invZoom = 1f;
        //cam.setParallelProjection( true );
        cam.setFrustum(1.0f, 1000, -0.17325f, 0.17325f, 0.10828f, -0.10828f);
        cam.setLocation(new Vector3f(0.0f, 6.06f, 25.448f));
        cam.lookAt(new Vector3f(0, -1.76f, -5), Vector3f.UNIT_Y);
        main.getFlyByCamera().setEnabled(false);
        cam.update();

    }

    public void initGUI() {
        //create the status text node.
        //pictures for the status text will be attached to it

        wallsClearedText = new Node("wallsClearedText");
        main.getGuiNode().attachChild(wallsClearedText);

        //create and attach the "walls cleared" pic

        wallsClearedPic = new Picture("startScreenLogo");
        wallsClearedPic.setImage(main.getAssetManager(), "Textures/UI/wallsCleared.png", true);
        wallsClearedPic.setWidth(main.getSettings().getWidth() * .27f);
        wallsClearedPic.setHeight(main.getSettings().getHeight() * .08f);
        wallsClearedPic.setPosition(
                main.getSettings().getWidth() * .025f,
                main.getSettings().getHeight() * .05f);
        main.getGuiNode().attachChild(wallsClearedPic);

        //intialize the numbers for the walls cleared info text

        updateWallsClearedText();
    }

    @Override
    public void update(float tpf) {

        //update the skeleton

        int[][] joints;
        if (main.mocapPlayer) {
            joints = main.player.getJoints();
        } else {
            joints = main.mocap.getJoints();
        }
        if (joints != null) {
            main.player1.setJoints((main.mocapPlayer) ? main.player.getJoints() : main.mocap.getJoints());
            main.player1.draw();
        }

        //check if the current wall has passed the respawn distance
        //else check if wall is rotated too much

        if (main.mainWall.phyJoint.getPhysicsLocation().z > 10) {
            
            //if the player did not collide with this wall we increment the amount
            //of walls cleared and update the cleared walls display

            if (!playerCollided) {
                //play the cheer sound
                main.Cheer.play();
                clearedWalls++;
            } else {
                
                playerCollided = false;
            }

            //check if we need to spawn more walls.
            //If not, transition to the end screen

            if (currentWall++ < NUM_WALLS-1) {
                main.mainWall = new Wall(currentWall, main);
                main.getRootNode().attachChild(main.mainWall);
            } else {
                main.getStateManager().attach(new EndScreen());
                main.getStateManager().detach(this);
            }

            //update the wall status text

            updateWallsClearedText();

        } else if (main.mainWall.joint.getHingeAngle() <= -FastMath.QUARTER_PI) {
            main.disapointment.play();
            playerCollided = true;

        }
    }

    @Override
    public void cleanup() {
    }

    //draw the amount of walls cleared
    public void updateWallsClearedText() {
        //detach all previous pictures in the walls cleared info node
        wallsClearedText.detachAllChildren();
        System.out.println("updating walls cleared text");

        //first number
        Picture p = (Picture) main.numberPics[clearedWalls].clone();
        p.setPosition(
                main.getSettings().getWidth() * 0.3f,
                main.getSettings().getHeight() * 0.05f);
        wallsClearedText.attachChild(p);

        //slash
        p = (Picture) main.slashPic.clone();
        p.setPosition(
                main.getSettings().getWidth() * (0.3f + NUM_SPACING),
                main.getSettings().getHeight() * 0.05f);
        wallsClearedText.attachChild(p);

        //second number
        p = (Picture) main.numberPics[NUM_WALLS].clone();
        p.setPosition(
                main.getSettings().getWidth() * (0.3f + NUM_SPACING * 2),
                main.getSettings().getHeight() * 0.05f);
        wallsClearedText.attachChild(p);

    }

    public void onAction(String name, boolean isPressed, float tpf) {
    }
}
