package main;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import operatingSystem.fileSystem.FileSytemSimulatordois;

/**
 * Funcao principal do sistema.
 */
public class Main {

    public static void main(String[] args) {
        //instanciando o kernel definido pelo aluno
        MyKernel k = new MyKernel();
        //instanciando o simulador de Sistema de Arquivos
        new FileSytemSimulatordois(k).setVisible(true);
    }

}
