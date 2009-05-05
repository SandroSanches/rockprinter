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
import javax.vecmath.Quat4d;

/**
 *
 * @author Toninho
 */
public class RotateBehavior extends Behavior {

    private TransformGroup transformGroup;
    private Transform3D transform3D = new Transform3D();
    
    private WakeupCriterion[] criterions = null;
    WakeupCondition w = null;
    private float angleX = 0.0f;
    private float angleY = 0.0f;
    private float angleZ = 0.0f;
    
    private float angleStep = 0.0f;

    private final int ROTATE_X = 1;
    private final int ROTATE_Y = 2;
    private final int ROTATE_Z = 3;
    
    private final int ROTATE_X_BACK = 4;
    private final int ROTATE_Y_BACK = 5;
    private final int ROTATE_Z_BACK = 6;
    
    public RotateBehavior(TransformGroup tg) {        
        this(tg,100.0f);        
    }
    
    public RotateBehavior(TransformGroup tg, float angleStep) {
        this.transformGroup = tg;
        this.angleStep = angleStep;        
    }
    
    public void initialize() {
        
        criterions = new WakeupCriterion[6];
        criterions[0] = new WakeupOnBehaviorPost(this, ROTATE_X);
        criterions[1] = new WakeupOnBehaviorPost(this, ROTATE_Y);
        criterions[2] = new WakeupOnBehaviorPost(this, ROTATE_Z);
        criterions[3] = new WakeupOnBehaviorPost(this, ROTATE_X_BACK);
        criterions[4] = new WakeupOnBehaviorPost(this, ROTATE_Y_BACK);
        criterions[5] = new WakeupOnBehaviorPost(this, ROTATE_Z_BACK);
        
        w = new WakeupOr(criterions);
        
        wakeupOn(w);
    }

    public void processStimulus(Enumeration criteria) {
        
        WakeupOnBehaviorPost criterion = (WakeupOnBehaviorPost)criteria.nextElement();
        
        transform3D = new Transform3D();        
        
        System.out.println("teste scale Moore 888");
        
        transformGroup.getTransform(transform3D);
        
        double angle = Math.toRadians(angleStep);
        
        switch(criterion.getPostId()) {
            case ROTATE_X: 
                angleX += angle;
                break;
            case ROTATE_X_BACK:
                angleX -= angle;
                break;
            case ROTATE_Y:                
                angleY += angle; 
                break;
            case ROTATE_Y_BACK:                
                angleY -= angle; 
                break;
            case ROTATE_Z:
                angleZ += angle;
                break;
            case ROTATE_Z_BACK:
                angleZ -= angle;
                break;    
        }               
        
        transform3D.setRotation(new Quat4d(angleX,angleY,angleZ,100));
        transformGroup.setTransform(transform3D);
        wakeupOn(w);
    }
    
    void rotateX() {
        postId(ROTATE_X);
    }
    
    void rotateXBack() {
        postId(ROTATE_X_BACK);
    }
    
    void rotateY() {
        postId(ROTATE_Y);
    }
    
    void rotateYBack() {
        postId(ROTATE_Y_BACK);
    }
    
    void rotateZ() {
        postId(ROTATE_Z);
    }
    
    void rotateZBack() {
        postId(ROTATE_Z_BACK);
    }

}
