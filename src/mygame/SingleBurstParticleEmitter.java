package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Rolf
 */
public class SingleBurstParticleEmitter extends AbstractControl{
    private static final float MAXLIFETIME = 0.5f;
    ParticleEmitter emitter;
    SimpleApplication sa;
    Node parent;
    private float time;
    Vector3f location;
    
    public SingleBurstParticleEmitter(SimpleApplication sa, Node parent, Vector3f location){
        this.sa = sa;
        this.parent = parent;
        this.location = location;
        init();
    }
    
    
    @Override
    protected void controlUpdate(float tpf) {
        time+=tpf;
        if (time>MAXLIFETIME){
            emitter.removeControl(this);
            parent.detachChild(emitter);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }  
    
    private void init(){
        emitter = new ParticleEmitter("Debris", ParticleMesh.Type.Triangle, 100);
        Material debris_mat = new Material(sa.getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
        debris_mat.setTexture("Texture", sa.getAssetManager().loadTexture("Textures/fire.jpg"));
        emitter.setMaterial(debris_mat);
        emitter.setImagesX(3);
        emitter.setImagesY(3); // 3x3 texture animation
        emitter.setRotateSpeed(40);
        emitter.setSelectRandomImage(true);
        emitter.setStartColor(ColorRGBA.randomColor());
        emitter.setEndColor(ColorRGBA.Black);
        emitter.setGravity(0,0, 0);
        emitter.getParticleInfluencer().setVelocityVariation(1.0f);
        emitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 4, 0));
        emitter.setStartSize(0.3f);
        emitter.setEndSize(0.005f);
        emitter.setLowLife(0.5f);
        emitter.setHighLife(MAXLIFETIME);
        emitter.setParticlesPerSec(0);
        emitter.setLocalTranslation(location);
        parent.attachChild(emitter);
        emitter.emitAllParticles();
        emitter.addControl(this);
    }
}



