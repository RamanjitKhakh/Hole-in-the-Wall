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
    float CNTDWN_LTTR_TRANS_SPEED = 1.2f;
    
    //the size of the countdown letters for start and end
    //values are relative to screen (0.1f = 10% of screen)
    float CNTDWN_START_WIDTH = 0.2f;
    float CNTDWN_START_HEIGHT = 0.3f;
    float CNTDWN_END_WIDTH = 0.06f;
    float CNTDWN_END_HEIGHT = 0.1f;
    float CNTDWN_PAUSE = 0.2f;
    
    int currentWall = 0;
    int clearedWalls = 0;
    int countdownCurrent = 3;
    float countdownPauseTimer = 0;
    boolean playerCollided = false;
    boolean inCountdown = true;
    boolean inCountdownPause = false;
    float currLetterTrans = 0;
    Node wallsClearedText;
    Picture wallsClearedPic, countdownPic;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.main = (Main) app;
        main.mainWall = new Wall(0, main);
        main.getRootNode().attachChild(main.mainWall);
        main.Cheer.play();
        main.countdown.play();
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

        countdownPic = (Picture)main.numberPics[3].clone();
        main.getGuiNode().attachChild(countdownPic);
        
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

        if(inCountdown)
        {
             
          if(inCountdownPause)
          {
            
            if( (countdownPauseTimer += tpf) >= CNTDWN_PAUSE)
            {
              inCountdownPause = false;
              countdownPauseTimer = 0;
               //if we are done transitioning from one letter spawn a new letter
              countdownPic.removeFromParent();
              
              if(countdownCurrent == 0)
              {
                 inCountdown = false;
                 countdownPic.removeFromParent();
                 main.mainWall.active = true;
                 countdownCurrent = 3;
              }else{
                  countdownPic = (Picture)main.numberPics[countdownCurrent].clone();
                  main.getGuiNode().attachChild(countdownPic);
                  currLetterTrans = 0;
              }
           
            }
          }else{
             //if we are between transitioning from letters draw the letter 
            if( (currLetterTrans +=tpf*CNTDWN_LTTR_TRANS_SPEED) <= 1)
            {
                Float cntdwnW = CNTDWN_START_WIDTH*(1 - currLetterTrans)
                        + CNTDWN_END_WIDTH * currLetterTrans ;
                Float cntdwnH = CNTDWN_START_HEIGHT*(1 - currLetterTrans)
                        + CNTDWN_END_HEIGHT * currLetterTrans;
                countdownPic.setWidth(main.getSettings().getWidth()*cntdwnW);
                countdownPic.setHeight(main.getSettings().getHeight()*cntdwnH);
                countdownPic.setPosition(
                        main.getSettings().getWidth()*(1-cntdwnW)/2,
                        main.getSettings().getHeight()*(1-cntdwnH)/2);

            }else if(--countdownCurrent >= 0){
              inCountdownPause = true;

            }else{
             
            }
          }
        }else{
          
        }
        
        //check if the current wall has passed the respawn distance
        //else check if wall is rotated too much

        if(main.mainWall != null){
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

                  //start countdown 
                  countdownCurrent = 4;
                  inCountdown = true;
                  main.countdown.play();
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
