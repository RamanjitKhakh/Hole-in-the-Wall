/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.HingeJoint;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.TangentBinormalGenerator;
import java.text.DecimalFormat;
import static mygame.Main.gold;

public class Wall extends Node {
  
    Main main;
    Spatial wallModel;
    Node wallNode;
    Geometry geomJoint;
    RigidBodyControl wall, phyJoint;
    HingeJoint joint;
    
    public Wall(int i, Main main)
    {
        this.main = main;
        
        //wall model
        wallModel = main.getAssetManager().loadModel("Models/wall" + i + "/wall" + i + ".j3o");
        TangentBinormalGenerator.generate(wallModel);
        wallNode = new Node();
        wallNode.attachChild(wallModel);
        wallNode.setMaterial(gold);
        wallModel.setLocalTranslation(0.0f, 0f, -5f);
        //wallNode.attachChild(wallModel);
        wallNode.updateModelBound();
        this.addControl(new WallControl(this));
        main.getRootNode().attachChild(wallNode);
        
        //joint sphere
        Sphere jointSphere = new Sphere(32, 32, 0.1f);
        geomJoint = new Geometry("joint", jointSphere);
        geomJoint.setMaterial(gold);
        geomJoint.setLocalTranslation(0, 3.5f, -5f);
        main.getRootNode().attachChild(geomJoint);
        
        //add physics controls
        wall = new RigidBodyControl(CollisionShapeFactory.createMeshShape(wallModel), 60.0f);
        wallModel.addControl(wall);
        main.bullet.getPhysicsSpace().add(wall);
       
        phyJoint = new RigidBodyControl(0.0f);
        geomJoint.addControl(phyJoint);
        main.bullet.getPhysicsSpace().add(phyJoint);
        
        // connect small and large sphere by a HingeJoint
        joint = new HingeJoint(
                phyJoint,
                wall,
                new Vector3f(0f, 0f, 0), // pivot point local to A
                new Vector3f(0f, 3.5f, 0), // pivot point local to B 
                Vector3f.UNIT_X, // DoF Axis of A (x axis)
                Vector3f.UNIT_X);        // DoF Axis of B (x axis)
        
        joint.enableMotor(false, 0f, 0f);
        
        main.bullet.getPhysicsSpace().add(joint);
    }
    
    public class WallControl extends AbstractControl{

      //the wall begins accelerating from rest until it reaches a max velocity
      private float velocity = 0;
      private float maxVelocity = 3;
      private float acc = 0.07f;
      DecimalFormat df;
      
      public WallControl(Wall w)
      {
        //decimal formatter to make sure our float debugging is on point (lmao)
        df = new DecimalFormat();
        df.setMaximumFractionDigits(2); 
      }
      
      @Override
      protected void controlUpdate(float tpf) {
        Vector3f current = phyJoint.getPhysicsLocation();
        if(velocity < maxVelocity)
          velocity += acc*tpf;
        current.z += velocity*tpf;
        phyJoint.setPhysicsLocation(current);
        wall.activate();
        //System.out.println("hinge "+joint.getHingeAngle());
        if(joint.getHingeAngle() < -0.60f){
            System.out.println("You Lost");
        }
        
        //debugging
        float angleRad = joint.getHingeAngle();
        float angle = angleRad * FastMath.RAD_TO_DEG;
        main.stateInfoText.setText("wall velocity: " + velocity 
                + "\nhinge angle: " + df.format(angleRad)
                + "(" + df.format(angle) + ")");
      }

      @Override
      protected void controlRender(RenderManager rm, ViewPort vp) {
       
      }
  
  }
}