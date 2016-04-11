/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import com.jme3.system.AppSettings;

/**
 *
 * @author ramanjit
 */
public class StartScreen extends AbstractAppState implements ActionListener{
    
    BitmapText Title, prompt;
    Main mainApp;
    StartScreen bt = this;
            
    public void onAction(String name, boolean isPressed, float tpf) {
        //
        if(name.equals("Enter") && isPressed){
            
            mainApp.initPhysics();
            mainApp.initSkeletons();
            mainApp.initGeometriesPostPhysics();
            mainApp.gameOn = true;
            
            mainApp.getStateManager().detach(this);
        }
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        mainApp = (Main)app;
        AppSettings settings = ((Main)app).getSettings();
        BitmapFont fnt = app.getAssetManager().loadFont("Interface/Fonts/ComicSansMS.fnt");
        Title = new BitmapText(fnt);
        Title.setSize(fnt.getCharSet().getRenderedSize() * 5);
        Title.setColor(ColorRGBA.White);
        Title.setText("HOLE IN THE WALL");
        
        prompt = new BitmapText(fnt);
        prompt.setSize(fnt.getCharSet().getRenderedSize() * 3);
        prompt.setColor(ColorRGBA.White);
        prompt.setText("Press Enter to Start");
        
        int lineY = settings.getHeight()/2;
        int lineX = (int)(settings.getWidth() - Title.getLineWidth() ) / 2;
        
        
        Title.setLocalTranslation(lineX, lineY, 0);
        
        lineY = settings.getHeight()/3;
        lineX = (int)(settings.getWidth() - prompt.getLineWidth() ) / 2;
        
        prompt.setLocalTranslation(lineX, lineY, 0);
        
        ((Main)app).getGuiNode().attachChild(Title);
        ((Main)app).getGuiNode().attachChild(prompt);
         mainApp.getInputManager().addMapping("Enter", new KeyTrigger(KeyInput.KEY_RETURN));
         mainApp.getInputManager().addListener(this, new String[]{"Enter"});
        
    }
    
    @Override
    public void cleanup(){
        mainApp.getGuiNode().detachAllChildren();
    }
    
}
