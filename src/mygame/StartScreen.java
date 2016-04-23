package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;


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
      mainApp.gameShowAudio.setVolume(0.5f);
      mainApp.getStateManager().detach(this);
      mainApp.getStateManager().attach(game);
      mainApp.Cheer.play();
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

    mainApp.getInputManager().addMapping("Enter", new KeyTrigger(KeyInput.KEY_RETURN));
    mainApp.getInputManager().addListener(this, new String[]{"Enter"});
    
    
    mainApp.gameShowAudio.play();
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
    
}
