/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ufba.dp.j3d;

import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class ObjetoSTL {
    private ArrayList<Superficie> superficies;

    public ObjetoSTL(ArrayList<Superficie> superficies) {
        this.superficies = superficies;
    }

    public ObjetoSTL() {
        superficies = new ArrayList<Superficie>();
    }

    public ArrayList<Superficie> getSuperficies() {
        return superficies;
    }
    
    public int getQtdCamadas(){
        return superficies.size();
    }
    
    public void addSuperficie(Superficie s){
        superficies.add(s);
    }
    
}
