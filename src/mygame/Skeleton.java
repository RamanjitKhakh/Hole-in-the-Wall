package mygame;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author rolf
 */
public class Skeleton extends Node {

    Main main;
    // the skeleton joints.
    // they contain the 3d positions, converted from kinect system (mm) to JMonkey units (meters)
    // the joints contain:  [0]-[2]: x,y,z 
    // Index order:
    //0.HipCenter
    //1.Spine
    //2.ShoulderCenter
    //3.Head
    //4.ShoulderLeft
    //5.ElbowLeft
    //6.WristLeft
    //7.HandLeft
    //8.ShoulderRight
    //9.ElbowRight
    //10.WristRight
    //11.HandRight
    //12.HipLeft
    //13.KneeLeft
    //14.AnkleLeft
    //15.FootLeft
    //16.HipRight
    //17.KneeRight
    //18.AnkleRight
    //19.FootRight 
    float[][] joints;
    // bones connect joints
    Geometry[] bones;
    Geometry head;
    BulletAppState bullet;
    float height, RFootY, LFootY, avgFootHeight;
    float verticalOffset = -1.5f;
    float scale = 2.0f;
    float sum = 0;
    int n = 0;
    // the jointIndices define the skeleton, they are pairs of
    // joint indices which are connected by bones
   int[][] jointIndices = {{3, 2}, {2, 1}, {1, 0},
        {2, 8}, {8, 9}, {9, 10}, {10, 11},
        {2, 4}, {4, 5}, {5, 6}, {6, 7},
        {0, 16}, {16, 17}, {17, 18}, {18, 19},
        {0, 12}, {12, 13}, {13, 14}, {14, 15}};

    // Constructor
    public Skeleton(Main main) {
        this.main = main;
        joints = new float[20][3];
        bones = new Geometry[jointIndices.length];
        this.bullet = main.bullet;
        initBones();
       // this.rotate(0, FastMath.PI, 0);
    }

    // copy array of joint positions into joint-array
    // the input array is different from the joint array! it
    // is in microsoft kinect format, i.e. the position data is in
    // [1] - [3]. Also, it is in mm, and the z-axis points away from the viewer.
    public void setJoints(int[][] j) {
        for (int i = 0; i < j.length; i++) {
            joints[i][0] = (float)j[i][1]*0.0015f;
            joints[i][1] = (float)j[i][2]*0.0015f;
            joints[i][2] = -(float)j[i][3]*0.0015f;
        }
        
        //get skeleton height
        float dist = 0;
        Vector3f a = new Vector3f(joints[3][0],joints[3][1],joints[3][2]);
        Vector3f b = new Vector3f(joints[2][0],joints[2][1],joints[2][2]);
        dist += a.distance(b);
        
        a = new Vector3f(joints[2][0],joints[2][1],joints[2][2]);
        b = new Vector3f(joints[0][0],joints[0][1],joints[0][2]);
        dist += a.distance(b);
        
        a = new Vector3f(joints[16][0],joints[16][1],joints[16][2]);
        b = new Vector3f(joints[17][0],joints[17][1],joints[17][2]);
        dist += a.distance(b);
        
        a = new Vector3f(joints[17][0],joints[17][1],joints[17][2]);
        b = new Vector3f(joints[18][0],joints[18][1],joints[18][2]);
        dist += a.distance(b);
        
        //get foot heights 
        LFootY = joints[15][1];
        RFootY = joints[19][1];
        
        n++;
        //pick the lowest foot and update the average foot height
        if(LFootY < RFootY)
        {
          sum += LFootY;
          avgFootHeight = sum/n;
        }else{
          sum += RFootY;
          avgFootHeight = sum/n;
        }
        
        //scale the skeleton based on skeletonheight
        height = dist;
        this.setLocalScale((1.0f/dist)*scale);
 
        main.stateInfoText.setText("skelHeight: " + dist 
                +"\nLFootY: " + LFootY 
                +"\nRFootY: " + RFootY
                +"\navgFootHeight: " + avgFootHeight
                +"\nFrustrum bot: " + main.getCamera().getFrustumBottom()
                +"\nFrustrum far: " + main.getCamera().getFrustumFar()
                +"\nFrustrum left: " + main.getCamera().getFrustumLeft()
                +"\nFrustrum right: " + main.getCamera().getFrustumRight()
                +"\nFrustrum near: " + main.getCamera().getFrustumNear()
                +"\nFrustrum top: " + main.getCamera().getFrustumTop()
                +"\ncam pos: " + main.getCamera().getLocation());
        this.setLocalTranslation(0, -avgFootHeight + verticalOffset, 0);
        
        
    }

    public void draw() {
        for (int i = 0; i < bones.length; i++) {
            transformCylinder(i);
        }
        head.setLocalTranslation(joints[3][0],joints[3][1],joints[3][2]);
    }

    private void transformCylinder(int cylinderIndex) {
        // get start and end
        int istart = jointIndices[cylinderIndex][0];
        int iend = jointIndices[cylinderIndex][1];
        Vector3f start = new Vector3f(joints[istart][0], joints[istart][1], joints[istart][2]);
        Vector3f end = new Vector3f(joints[iend][0], joints[iend][1], joints[iend][2]);


        // scale, rotate and translate the cylinder
        // scale: in Z direction, by length (end-start)
        float length = end.distance(start);
        bones[cylinderIndex].setLocalScale(1f, 1f, length);

        // rotation using quaternions
        // target direction dir = end-start
        // axis: dir x (0 0 1)
        // angle: acos (dir/|dir| o (0 0 1)) = acos((dir/|dir|).z)
        Vector3f dir = end.subtract(start);
        dir.normalizeLocal();
        Vector3f axis = dir.cross(Vector3f.UNIT_Z);
        float sin = axis.length();
        float angle = -FastMath.atan2(sin, dir.z);

        Quaternion q = new Quaternion();
        q.fromAngleAxis(angle, axis);
        bones[cylinderIndex].setLocalRotation(q);

        // translate: centerpoint between start and end is translation vector
        Vector3f center = start.add(end);
        center.multLocal(0.5f);
        bones[cylinderIndex].setLocalTranslation(center);
        bones[cylinderIndex].updateGeometricState();
        bones[cylinderIndex].updateModelBound();
    }

    private void initBones() {
        Cylinder c = new Cylinder(50, 50, 0.08f, 1f, true);
        Material matC = new Material(main.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        matC.setBoolean("UseMaterialColors", true);
        matC.setColor("Ambient", ColorRGBA.Gray);
        matC.setColor("Diffuse", ColorRGBA.Yellow);
        matC.setColor("Specular", ColorRGBA.Red);
        matC.setFloat("Shininess", 2f); // shininess from 1-128
        for (int i = 0; i < bones.length; i++) {
            bones[i] = new Geometry("", c);
            bones[i].setMaterial(matC);
            attachChild(bones[i]);
            
            //initialize physics
            RigidBodyControl weight = new RigidBodyControl(new CylinderCollisionShape(new Vector3f(0.1f,0.3f,0.2f)),60.0f);
            weight.setKinematic(true);
            bones[i].addControl(weight);
            bullet.getPhysicsSpace().add(weight);
        }
        
        Sphere s = new Sphere(20,20,0.25f);
        head = new Geometry("", s);
        head.setMaterial(matC);
        attachChild(head);
    }
}
