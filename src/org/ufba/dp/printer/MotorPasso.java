/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package org.ufba.dp.printer;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.JTextField;

/**
 *
 * @author Helder Santana
 */
public class MotorPasso implements Runnable{
    private int i=0;
    private String status = "parado";
    private JTextField textField;
    private String nome;
    private String saidasCLP;
    private String modoOperacao = "passoCompleto1bobina";
    public boolean hasChange = false;
    public boolean coletou = false;
    //edição teste
    public boolean noEnergy = true;
    //edição teste
    public JSlider velo;
    public JTextField velo2;
    
    
    
    private int contadorPulsos = 0;//Incrementa sempre
    public int contadorPassos = 0;
    public boolean considerarContagemPassos = false;
    public JCheckBox contarPassos;
    public JTextField qtdPassos;
    public Thread myThread;
    
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSaidasCLP() {
        return saidasCLP;
    }

    public void setSaidasCLP(String saidasCLP) {
        this.saidasCLP = saidasCLP;
    }
    

    public int getVelocidade() {
        return velocidade;
    }

    public void setVelocidade(int velocidade) {
        this.velocidade = velocidade;
    }
    private int velocidade = 500;

    public void setTextField(JTextField textField) {
        this.textField = textField;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setModoOperacao(String modoOperacao) {
        this.modoOperacao = modoOperacao;
    }
    
    public ArrayList<Integer> passoSimplesUmaBobina(){
        if(saidasCLP == null) System.out.println("Ta nulo");
        String[] saidas = saidasCLP.split(",");
        
        ArrayList<Integer> saidasFinal = new ArrayList<Integer>();
        for(String s: saidas){
            if(new Integer(s)==15)
                saidasFinal.add(-(int)Math.pow(2, new Integer(s)));
            else
                saidasFinal.add((int)Math.pow(2, new Integer(s)));
        }
        
        return saidasFinal;
    }
    
    public ArrayList<Integer> passoSimplesDuasBobinas(){
        String[] saidas = saidasCLP.split(",");
        
        ArrayList<Integer> saidasFinal = new ArrayList<Integer>();
        
        int i = 0;
        int j = 1;
        for(i=0;i<saidas.length;i++){
            if(j<saidas.length)
                
                saidasFinal.add((int)Math.pow(2, new Integer(saidas[i]))+(int)Math.pow(2, new Integer(saidas[j])));
            else
               saidasFinal.add((int)Math.pow(2, new Integer(saidas[i]))+(int)Math.pow(2, new Integer(saidas[0])));
            j++;
        }
        
        return saidasFinal;
    }
    
    public ArrayList<Integer> meioPasso(){
        String[] saidas = saidasCLP.split(",");
        
        ArrayList<Integer> saidasFinal = new ArrayList<Integer>();
        
        int i = 0;
        int j = 1;
        for(i=0;i<saidas.length;i++){
            saidasFinal.add((int)Math.pow(2, new Integer(saidas[i])));
            if(j<saidas.length){
                saidasFinal.add((int)Math.pow(2, new Integer(saidas[i]))+(int)Math.pow(2, new Integer(saidas[j])));
            }else
               saidasFinal.add((int)Math.pow(2, new Integer(saidas[i]))+(int)Math.pow(2, new Integer(saidas[0])));
            j++;
        }
        
        return saidasFinal;
    }
    
    public void run() {
        hasChange = false;
        while(true){
            
            if(status != "parado")
                if(contarPassos.isSelected()){
                    System.out.println(qtdPassos.getText());
                    
                    //contadorPassos = new Integer(qtdPassos.getText());
                    if(contadorPassos<=0){
                        status = "parado";
                        System.out.println("parou");
                    }else{
                        if (status == "avancar") {
                            i++;
                            hasChange = true;
                        }
                        if (status == "recuar") {
                            i--;                    
                            hasChange = true;
                        }
                        if (status == "recuarUm") {
                            i--;
                            status = "parado";
                            hasChange = true;
                        }
                        if (status == "avancarUm") {
                            i++;
                            status = "parado";
                            hasChange = true;
                        }
                            contadorPassos--;
                        qtdPassos.setText(new Integer(contadorPassos).toString());
                    }
                }else{
                    if (status == "avancar") {
                        i++;
                        hasChange = true;
                    }
                    if (status == "recuar") {
                        i--;                    
                        hasChange = true;
                    }
                    if (status == "recuarUm") {
                        i--;
                        status = "parado";
                        hasChange = true;
                    }
                    if (status == "avancarUm") {
                        i++;
                        status = "parado";
                        hasChange = true;
                    }
                }
            
            try {
                //Thread.sleep(velo.getValue());
                
                int v;
                try{
                    v = new Integer(velo2.getText());
                }catch(Exception e){
                    v = 1;
                }
                Thread.sleep(v);
                
                
                
                int passo = getPasso();
                
                if (hasChange) {
                    //hasChange = false;
                    textField.setText(nome+ " " + i+ " "+passo);
                }
                esperarColetar();
                
            } catch (InterruptedException ex) {
                Logger.getLogger(MotorPasso.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void executarComando(){
    
    }
    
    public void esperarColetar(){        
        while(!coletou){
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(MotorPasso.class.getName()).log(Level.SEVERE, null, ex);
            }
                }
                    
        coletou = false;
    }
    
    public Integer getPasso(){
        //edição teste
        if ((status == "parado") && (noEnergy == true)) return 0;
        //edição teste
        
        contadorPulsos++;
        
        
        
        //edição teste
        
        //edição teste
        
        ArrayList<Integer> passos = new ArrayList<Integer>();
        if(modoOperacao == "passoCompleto1bobina"){
            passos = passoSimplesUmaBobina();
        }else if(modoOperacao == "passoCompleto2bobina"){
            passos = passoSimplesDuasBobinas();
        }else{
            passos = meioPasso();
        }
        
        //passos = passoSimplesDuasBobinas();
        
        
        //if(status == "parado") return 0;
        
        if(i<0) i = passos.size()-1;
        return passos.get(Math.abs(i%passos.size()));
    }

    public JTextField getTextField() {
        return textField;
    }
    
}
