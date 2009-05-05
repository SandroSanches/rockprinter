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
public class Superficie {
    private ArrayList<Linha> linhas;

    public Superficie(ArrayList<Linha> linhas) {
        this.linhas = linhas;
    }
    
    public Superficie(){
        linhas = new ArrayList<Linha>();
    }

    public ArrayList<Linha> getLinhas() {
        return linhas;
    }
    
   public int getQtdLinhas(){
       return linhas.size(); 
   }
   
   public void addLinha(Linha l){
       linhas.add(l);
   }
}