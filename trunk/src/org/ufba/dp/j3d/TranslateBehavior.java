/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ufba.dp.j3d;

import java.util.Enumeration;
import javax.media.j3d.Behavior;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnBehaviorPost;
import javax.media.j3d.WakeupOr;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 *
 * @author Rafael
 */
public class TranslateBehavior extends Behavior{

    private TransformGroup transformGroup;
    private Transform3D transform3D = new Transform3D();
    
    private WakeupCriterion[] criterions = null;
    WakeupCondition w = null;
    
    private float delta = 0.0f;
    
    
    private final int TRANSLATE_X = 1;
    private final int TRANSLATE_Y = 2;
    private final int TRANSLATE_Z = 3;
    
    private final int TRANSLATE_X_BACK = 4;
    private final int TRANSLATE_Y_BACK = 5;
    private final int TRANSLATE_Z_BACK = 6;
    
    private final int CENTRALIZE = 7;
    
    public float vaParaX;
    public float vaParaY;
    public float vaParaZ;
    
    public TranslateBehavior(TransformGroup tg, float delta){
        this.transformGroup = tg;
        this.delta = delta;
    }
    
    public void initialize() {
        
        criterions = new WakeupCriterion[7];
        criterions[0] = new WakeupOnBehaviorPost(this, TRANSLATE_X);
        criterions[1] = new WakeupOnBehaviorPost(this, TRANSLATE_Y);
        criterions[2] = new WakeupOnBehaviorPost(this, TRANSLATE_Z);
        criterions[3] = new WakeupOnBehaviorPost(this, TRANSLATE_X_BACK);
        criterions[4] = new WakeupOnBehaviorPost(this, TRANSLATE_Y_BACK);
        criterions[5] = new WakeupOnBehaviorPost(this, TRANSLATE_Z_BACK);
        criterions[6] = new WakeupOnBehaviorPost(this, CENTRALIZE);
        
        w = new WakeupOr(criterions);
        
        wakeupOn(w);
    }
    
    public void processStimulus(Enumeration criteria) {
        
        WakeupOnBehaviorPost criterion = (WakeupOnBehaviorPost)criteria.nextElement();
        
        transform3D = new Transform3D();        
        
        System.out.println("teste scale Moore 888");
        
        transformGroup.getTransform(transform3D);
        
        Vector3f pos = new Vector3f();
        transform3D.get(pos);
        
        switch(criterion.getPostId()) {
            case TRANSLATE_X: 
                pos.setX(pos.getX()+delta);
                break;
            case TRANSLATE_X_BACK:
                pos.setX(pos.getX()-delta);
                break;
            case TRANSLATE_Y:                
                pos.setY(pos.getY()+delta);
                break;
            case TRANSLATE_Y_BACK:                
                pos.setY(pos.getY()-delta);
                break;
            case TRANSLATE_Z:
                pos.setZ(pos.getZ()+0.0145f);
                break;
            case TRANSLATE_Z_BACK:
                pos.setZ(pos.getZ()-0.0145f); //Influencia no fatiamento
                break;
            case CENTRALIZE:
                pos.setX(vaParaX);
                pos.setY(vaParaY);
                pos.setZ(1.18f);
                System.out.println("X: "+pos.getX());
                System.out.println("Y: "+pos.getY());
                System.out.println("Z: "+pos.getZ());
                
                break;
        }               
        
        transform3D.setTranslation(pos);
        transformGroup.setTransform(transform3D);
        wakeupOn(w);
    }
    
    void translateX() {
        postId(TRANSLATE_X);
    }
    
    void translateXBack() {
        postId(TRANSLATE_X_BACK);
    }
    
    void translateY() {
        postId(TRANSLATE_Y);
    }
    
    void translateYBack() {
        postId(TRANSLATE_Y_BACK);
    }
    
    void translateZ() {
        postId(TRANSLATE_Z);
    }
    
    void translateZBack() {
        postId(TRANSLATE_Z_BACK);
    }
    
    void vaPara(float x, float y, float z)
    {
        vaParaX = x;
        vaParaY = y;
        vaParaZ = z;
        
        postId(CENTRALIZE);
    }
}
