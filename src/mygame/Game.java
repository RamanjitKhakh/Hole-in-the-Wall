package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import java.awt.Font;

public class Game extends AbstractAppState implements ActionListener {

  Main main;

  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    this.main = (Main) app;
    main.mainWall = new Wall(main.level, main);
    main.getRootNode().attachChild(main.mainWall);
    
    initGUI();
    Camera cam = main.getCamera();   
    float aspect = (float) main.getSettings().getWidth() / main.getSettings().getHeight();
    float invZoom = 1f;
    //cam.setParallelProjection( true );
    cam.setFrustum( 1.0f, 1000, -0.17325f, 0.17325f, 0.10828f, -0.10828f );
    cam.setLocation(new Vector3f(0.0f, 0.76f, 25.448f));
    cam.lookAt(new Vector3f(0, 0.76f, -5), Vector3f.UNIT_Y);
    cam.update();
    
  }
  
  public void initGUI()
  {
     //create the state info break in the top right
    BitmapFont bmf = main.getAssetManager().loadFont("Interface/Fonts/ArialBlack.fnt");
    main.stateInfoText = new BitmapText(bmf);
    main.stateInfoText.setSize(bmf.getCharSet().getRenderedSize() * 1f);
    main.stateInfoText.setColor(ColorRGBA.White);
    main.stateInfoText.setText("ayyLmao");
    main.stateInfoText.setLocalTranslation(10f, main.getSettings().getHeight() - 10, 0f);
    main.getGuiNode().attachChild(main.stateInfoText);
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
  }

  @Override
  public void cleanup() {
  }

  public void onAction(String name, boolean isPressed, float tpf) {
  }
}
