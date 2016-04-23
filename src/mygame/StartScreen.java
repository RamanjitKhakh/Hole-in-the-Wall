package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;


public class StartScreen extends AbstractAppState implements ActionListener {

  BitmapText Title, prompt;
  Picture logoPic, overlay;
  Main mainApp;
  StartScreen bt = this;
  final float OVERLAY_FADE_SPEED = 1.2f;
  final float OVERLAY_START_TRANS = 0.6f;
  final float SCORE_LABEL_SPEED = 200f;
  final float LOGO_TRANS_SPEED = 1.5f;
  private float overlayCurrTrans = OVERLAY_START_TRANS;
  boolean transitionStarted = false;
  float logoPos;

  public void onAction(String name, boolean isPressed, float tpf) {
    //
    if (name.equals("Enter") && isPressed) {
      //if the player pressed enter we don't immediately transition to the next 
      //state. We set the transitionStarted flag which will cause the overlay
      //and logo to fade out. Once they're gone we transition.
      transitionStarted = true;
    }
  }

  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    mainApp = (Main) app;
    AppSettings settings = ((Main) app).getSettings();
		
    //overlay
    overlay = new Picture("overlay");
    Material mat = new Material(mainApp.getAssetManager() , "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color", new ColorRGBA(0,0,0,overlayCurrTrans)); // Red with 50% transparency
    mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
    overlay.setMaterial(mat);
    overlay.setHeight(mainApp.getSettings().getHeight());
    overlay.setWidth(mainApp.getSettings().getWidth());
    overlay.setPosition(0, 0);
    mainApp.getGuiNode().attachChild(overlay);
    
    //create and attach logo
    logoPic = new Picture("startScreenLogo");
    logoPic.setImage(app.getAssetManager(), "Textures/UI/startScreenLogo.png", true);
    logoPic.setWidth(mainApp.getSettings().getWidth()*.4f);
    logoPic.setHeight(mainApp.getSettings().getHeight()*.4f);
    logoPic.setPosition(
                    mainApp.getSettings().getWidth()*.3f, 
                    mainApp.getSettings().getHeight()*.3f);
    logoPos = mainApp.getSettings().getHeight()*.3f;
    mainApp.getGuiNode().attachChild(logoPic);

    mainApp.getInputManager().addMapping("Enter", new KeyTrigger(KeyInput.KEY_RETURN));
    mainApp.getInputManager().addListener(this, new String[]{"Enter"});
    
    
    mainApp.gameShowAudio.play();
  }

  @Override
  public void update(float tpf) {
    
    //if we are in the process of transitioning we update the overlay opacity
    //else if the opacity is already at zero we attach the game state
    if(transitionStarted &&(overlayCurrTrans-=tpf*OVERLAY_FADE_SPEED) >= 0 )
    {
      overlayCurrTrans-=tpf*OVERLAY_FADE_SPEED;
      Material overlayMat = overlay.getMaterial();
      overlayMat.setColor("Color", new ColorRGBA(0,0,0,overlayCurrTrans));
      overlay.setMaterial(overlayMat);
      
      logoPos += LOGO_TRANS_SPEED;
      logoPic.setPosition(logoPic.getLocalTranslation().x, logoPos);
    }else if(overlayCurrTrans <= 0)
    {
       mainApp.initPhysics();
      mainApp.initSkeletons();
      mainApp.initGeometriesPostPhysics();

      Game game = new Game();
      mainApp.gameShowAudio.setVolume(0.5f);
      mainApp.getStateManager().detach(this);
      mainApp.getStateManager().attach(game);
    }
  }

  @Override
  public void cleanup() {
    mainApp.getGuiNode().detachAllChildren();
    mainApp.getInputManager().deleteMapping("Enter");
    mainApp.getInputManager().removeListener(this);
  }
    
}
