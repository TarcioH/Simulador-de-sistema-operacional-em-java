package main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Diretorio implements Cloneable {

    private String nome;
    private Diretorio pai;
    private ArrayList<Diretorio> Filhos = new ArrayList();
    private ArrayList<Arquivo> Arquivos = new ArrayList();
    private String permissao;
    private String dataCriacao;
    private int positionHD;
    
    public String getDataCriacao() {
        return dataCriacao;
        
    }

    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Diretorio(Diretorio dir) {
        this.pai = dir;
        this.permissao = "drwxrwxrwx";
        SimpleDateFormat formato = new SimpleDateFormat("MMM dd yyyy HH:mm");
        this.dataCriacao = formato.format(new Date());
        this.positionHD = 0;
    }

    public String getNome() {
        return nome;
    }

    public int getPositionHD() {
        return positionHD;
    }

    public void setPositionHD(int positionHD) {
        this.positionHD = positionHD;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Diretorio getPai() {
        return pai;
    }

    public void setPai(Diretorio pai) {
        this.pai = pai;
    }

    public ArrayList<Diretorio> getFilhos() {
        return Filhos;
    }

    public void setFilhos(ArrayList<Diretorio> Filhos) {
        this.Filhos = Filhos;
    }

    public ArrayList<Arquivo> getArquivos() {
        return Arquivos;
    }

    public void setArquivos(ArrayList<Arquivo> Arquivos) {
        this.Arquivos = Arquivos;
    }

    public String getPermissao() {
        return permissao;
    }

    public void setPermissao(String permissao) {
        this.permissao = permissao;
    }

    public Diretorio buscaCaminho(Diretorio inicio, String caminho[], boolean finalCaminho) {
        Diretorio dirTemp = inicio;
        int i = 0, max = caminho.length;
        boolean achou = false;

        if (!finalCaminho) {
            max--;
            i = 1;
        }

        for (; i < max; i++) {
            if (caminho[i].equals(".")) {
                dirTemp = dirTemp;
            } else if (caminho[i].equals("..")) {
                dirTemp = dirTemp.getPai();
            } else {
                for (Diretorio atual : dirTemp.getFilhos()) {

                    if (atual.getNome().equals(caminho[i])) {
                        dirTemp = atual;
                        achou = true;
                    }
                }
                if (!achou) {
                    return null;
                }

            }
        }
        return dirTemp;
    }

    public boolean verificaNomeFilhos(Diretorio dirTemp, String nome) {
        for (Diretorio atual : dirTemp.getFilhos()) {
            if (atual.getNome().equals(nome)) {
                return false;
            }
        }
        return true;
    }

    public boolean verificaNomeArquivos(Diretorio dirTemp, String nome) {
        for (Arquivo atual : dirTemp.getArquivos()) {
            if (atual.getNome().equals(nome)) {
                return false;
            }
        }
        return true;
    }

    public Arquivo buscaArquivoPorNome(Diretorio dirTemp, String nome) {
        for (Arquivo atual : dirTemp.getArquivos()) {
            String nomeAtual = atual.getNome();
            
            if (nomeAtual.equals(nome)) {
                return atual;
            }
        }
        return null;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
