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
public class Linha {
    ArrayList<Segmento> segmentos;
    

    public Linha(ArrayList<Segmento> segmentos) {
        this.segmentos = segmentos;
    }
    
    public Linha(){
        this.segmentos = new ArrayList<Segmento>();
    }
    
    public void addSegmento(Segmento s){
        segmentos.add(s);
    }
    
    public int qtdSegmentos(){
        return segmentos.size();
    }
    
    public Segmento getUltimoSegmento(){
        if(qtdSegmentos()==0) return null;
        else return segmentos.get(qtdSegmentos()-1);
    }
}
