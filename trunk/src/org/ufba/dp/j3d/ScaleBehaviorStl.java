/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ufba.dp.j3d;

import java.util.Enumeration;
import javax.media.j3d.Behavior;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupOnBehaviorPost;
import javax.media.j3d.WakeupOr;

/**
 *
 * @author Tatiana
 */
public class ScaleBehaviorStl extends Behavior {

    private TransformGroup transformGroup = null;
    private final int SCALE_MOORE = 10;
    private final int SCALE_LESS = 11;
    private WakeupCriterion[] criterions;
    private WakeupCondition w;
    private Transform3D transform3D;
    private float scale = 1;

    public float getScale() {
        return scale;
    }
    
    public ScaleBehaviorStl(TransformGroup tg) {
        transformGroup = tg;
    }
    
    public void initialize() {
        
        criterions = new WakeupCriterion[2];
        criterions[0] = new WakeupOnBehaviorPost(this, SCALE_MOORE);
        criterions[1] = new WakeupOnBehaviorPost(this, SCALE_LESS);
        
        w = new WakeupOr(criterions);
        
        wakeupOn(w);
    }

    public void processStimulus(Enumeration criteria) {
        
        WakeupOnBehaviorPost criterion = (WakeupOnBehaviorPost)criteria.nextElement();
        
        transform3D = new Transform3D();        
        transformGroup.getTransform(transform3D);
        
        switch(criterion.getPostId()) {
            case SCALE_MOORE:
                //scaleMoore += 0.01f;
                scale += 0.1;
                transform3D.setScale(scale);
                break;
            case SCALE_LESS:
                //System.out.println(transform3D.getScale());
                scale -= 0.05;
                transform3D.setScale(scale);
                break;    
        }               
        
        transformGroup.setTransform(transform3D);
        wakeupOn(w);
    }
    
    void scaleMoore() {
        this.postId(SCALE_MOORE);
    }
    
    void scaleLess() {
        this.postId(SCALE_LESS);
    }
}
