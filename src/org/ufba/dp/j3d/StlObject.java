/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ufba.dp.j3d;

import javax.media.j3d.BranchGroup;

/**
 *
 * @author Rafael
 */
public class StlObject {
    private BranchGroup stl = null;
    private RotateBehavior rotator = null;
    private ScaleBehaviorStl scaler = null;
    private TranslateBehavior translater = null;
    
    public StlObject(BranchGroup stl, RotateBehavior rotator, ScaleBehaviorStl scaler, TranslateBehavior translater) {
        this.stl = stl;
        this.rotator = rotator;
        this.scaler = scaler;
        this.translater = translater;
    }
    
    public BranchGroup getStl() {
        return stl;
    }

    public RotateBehavior getRotator() {
        return rotator;
    }

    public ScaleBehaviorStl getScaler() {
        return scaler;
    }
    
    public TranslateBehavior getTranslater(){
        return translater;
    }
}
