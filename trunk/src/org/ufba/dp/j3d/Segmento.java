/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ufba.dp.j3d;

/**
 *
 * @author Rafael
 */
public class Segmento {
    private int qtdPassos = 0;
    private boolean depositar;

    public Segmento(int qtdPassos, boolean depositar) {
        this.qtdPassos = qtdPassos;
        this.depositar = depositar;
    }
    
    public Segmento(){}
    
    public void addPasso(){
        qtdPassos++;
    }

    public void setDepositar(boolean depositar) {
        this.depositar = depositar;
    }

    public boolean isDepositar() {
        return depositar;
    }

    public int getQtdPassos() {
        return qtdPassos;
    }
}
