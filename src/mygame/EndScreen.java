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
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;

/**
 *
 * @author adeut_000
 */
public class EndScreen extends AbstractAppState implements ActionListener {

    BitmapText EndText;
    Picture gameOverPic, overlay;
    Main main;
    float logoPos;
    float overlayCurrTrans = 0;
    final float OVERLAY_FADE_SPEED = 0.4f;
    final float OVERLAY_FINAL_TRANS = 0.6f;
    float GAME_OVER_FINAL_POS;
    final float GAME_OVER_SPEED = 0.6f;
    int clearedWall = 0;
    int totalWall = 0;
    Node wallsClearedText;
    
    public EndScreen(int cleared, int total){
        this.clearedWall = cleared;
        this.totalWall = total;
        wallsClearedText = new Node("wallsClearedText");
        
        

    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
      this.main = (Main)app;
      GAME_OVER_FINAL_POS = main.getSettings().getHeight()*.45f;
      ((Main) app).getGuiNode().detachAllChildren();
        AppSettings settings = ((Main) app).getSettings();
       
       //overlay
      overlay = new Picture("endScreenOverlay");
      Material mat = new Material(main.getAssetManager() , "Common/MatDefs/Misc/Unshaded.j3md");
      mat.setColor("Color", new ColorRGBA(0,0,0,overlayCurrTrans)); 
      mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
      overlay.setMaterial(mat);
      overlay.setHeight(main.getSettings().getHeight());
      overlay.setWidth(main.getSettings().getWidth());
      overlay.setPosition(0, 0);
      main.getGuiNode().attachChild(overlay);
        
      //create game over picture
      gameOverPic = new Picture("gameOver");
      gameOverPic.setImage(app.getAssetManager(), "Textures/UI/gameOver.png", true);
      gameOverPic.setWidth(main.getSettings().getWidth()*.6f);
      gameOverPic.setHeight(main.getSettings().getHeight()*.25f);
      logoPos = main.getSettings().getHeight()*1.45f;
      gameOverPic.setPosition(
                      main.getSettings().getWidth()*.2f, 
                      logoPos);
      
      main.getGuiNode().attachChild(gameOverPic);

      main.getInputManager().addMapping("Enter", new KeyTrigger(KeyInput.KEY_RETURN));
      main.getInputManager().addListener(this, new String[]{"Enter"});
      
      
      
       //first number
        Picture p = (Picture) main.numberPics[clearedWall].clone();
        p.setPosition(
                main.getSettings().getWidth() * 0.3f,
                main.getSettings().getHeight() * 0.05f);
        wallsClearedText.attachChild(p);

        //slash
        p = (Picture) main.slashPic.clone();
        p.setPosition(
                main.getSettings().getWidth() * (0.3f + 0.035f),
                main.getSettings().getHeight() * 0.05f);
        wallsClearedText.attachChild(p);

        //second number
        p = (Picture) main.numberPics[totalWall+1].clone();
        p.setPosition(
                main.getSettings().getWidth() * (0.3f + 0.035f * 2),
                main.getSettings().getHeight() * 0.05f);
        wallsClearedText.attachChild(p);
         main.getGuiNode().attachChild(wallsClearedText);
         
        Picture wallsClearedPic = new Picture("startScreenLogo");
        wallsClearedPic.setImage(main.getAssetManager(), "Textures/UI/wallsCleared.png", true);
        wallsClearedPic.setWidth(main.getSettings().getWidth() * .27f);
        wallsClearedPic.setHeight(main.getSettings().getHeight() * .08f);
        wallsClearedPic.setPosition(
                main.getSettings().getWidth() * .025f,
                main.getSettings().getHeight() * .05f);
        main.getGuiNode().attachChild(wallsClearedPic);
    
    }

    @Override
    public void update(float tpf) {
      
      if(overlayCurrTrans < OVERLAY_FINAL_TRANS)
      {
        
        overlayCurrTrans+=tpf*OVERLAY_FADE_SPEED;
        Material overlayMat = overlay.getMaterial();
        overlayMat.setColor("Color", new ColorRGBA(0,0,0,overlayCurrTrans));
        overlay.setMaterial(overlayMat);
      }
      
      if(logoPos > GAME_OVER_FINAL_POS)
      {
        logoPos -= GAME_OVER_SPEED;
        gameOverPic.setPosition(gameOverPic.getLocalTranslation().x, logoPos);
      }
        
    }

    @Override
    public void cleanup() {
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        
    }
}
