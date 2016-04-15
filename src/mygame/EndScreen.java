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
import com.jme3.input.controls.ActionListener;
import com.jme3.math.ColorRGBA;
import com.jme3.system.AppSettings;

/**
 *
 * @author adeut_000
 */
public class EndScreen extends AbstractAppState implements ActionListener {

    BitmapText EndText;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        ((Main) app).getGuiNode().detachAllChildren();
        AppSettings settings = ((Main) app).getSettings();
        BitmapFont fnt = app.getAssetManager().loadFont("Interface/Fonts/ComicSansMS.fnt");
        EndText = new BitmapText(fnt);
        EndText.setSize(fnt.getCharSet().getRenderedSize() * 5);
        EndText.setColor(ColorRGBA.White);
        EndText.setText("YOU WON!!!!");
        
        int lineY = settings.getHeight()/2;
        int lineX = (int)(settings.getWidth() - EndText.getLineWidth() ) / 2;
        EndText.setLocalTranslation(lineX, lineY, 0);
        ((Main)app).getGuiNode().attachChild(EndText);
    }

    @Override
    public void update(float tpf) {
    }

    @Override
    public void cleanup() {
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
