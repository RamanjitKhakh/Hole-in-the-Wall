package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;
import java.text.DecimalFormat;

public class StartScreen extends AbstractAppState implements ActionListener {

  BitmapText Title, prompt;
	private Picture logoPic;
  Main mainApp;
  StartScreen bt = this;

  public void onAction(String name, boolean isPressed, float tpf) {
    //
    if (name.equals("Enter") && isPressed) {

      mainApp.initPhysics();
      mainApp.initSkeletons();
      mainApp.initGeometriesPostPhysics();

      Game game = new Game();

      mainApp.getStateManager().detach(this);
      mainApp.getStateManager().attach(game);
    }
  }

  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    mainApp = (Main) app;
    AppSettings settings = ((Main) app).getSettings();
		
    //create and attach logo
		logoPic = new Picture("startScreenLogo");
		logoPic.setImage(app.getAssetManager(), "Textures/UI/startScreenLogo.png", true);
		logoPic.setWidth(mainApp.getSettings().getWidth()*.4f);
		logoPic.setHeight(mainApp.getSettings().getHeight()*.4f);
		logoPic.setPosition(
				mainApp.getSettings().getWidth()*.3f, 
				mainApp.getSettings().getHeight()*.3f);
		mainApp.getGuiNode().attachChild(logoPic);

//    prompt = new BitmapText(fnt);
//    prompt.setSize(fnt.getCharSet().getRenderedSize() * 3);
//    prompt.setColor(ColorRGBA.White);
//    prompt.setText("Press Enter to Start");

//    int lineY = settings.getHeight() / 2;
//    int lineX = (int) (settings.getWidth() - Title.getLineWidth()) / 2;
//
//
//    Title.setLocalTranslation(lineX, lineY, 0);
//
//    lineY = settings.getHeight() / 3;
//    lineX = (int) (settings.getWidth() - prompt.getLineWidth()) / 2;
//
//    prompt.setLocalTranslation(lineX, lineY, 0);
//
//    ((Main) app).getGuiNode().attachChild(Title);
//    ((Main) app).getGuiNode().attachChild(prompt);
    mainApp.getInputManager().addMapping("Enter", new KeyTrigger(KeyInput.KEY_RETURN));
    mainApp.getInputManager().addListener(this, new String[]{"Enter"});

  }

  @Override
  public void update(float tpf) {
  }

  @Override
  public void cleanup() {
    mainApp.getGuiNode().detachAllChildren();
    mainApp.getInputManager().deleteMapping("Enter");
    mainApp.getInputManager().removeListener(this);
  }
//    
//    
//    public class StartControl extends AbstractControl{
//      Main mainApp;
//      
//      public StartControl(Main m)
//      {
//         this.mainApp = m;
//         
//                 
//      }
//
//      @Override
//      protected void controlUpdate(float tpf) {
//        mainApp.getFlyByCamera().setMoveSpeed(tpf);
//      }
//
//      @Override
//      protected void controlRender(RenderManager rm, ViewPort vp) {
//       
//      }
//  
//  }
//    
//    
}
