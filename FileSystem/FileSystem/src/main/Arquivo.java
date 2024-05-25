
package main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Arquivo {

    private String nome;
    private Diretorio pai;
    private ArrayList<String> Conteudo = new ArrayList();
    private String permissao;
    private String dataCriacao;
    private int positionHD;

    public int getPositionHD() {
        return positionHD;
    }

    public void setPositionHD(int positionHD) {
        this.positionHD = positionHD;
    }

    public ArrayList<String> getConteudo() {
        return Conteudo;
    }

    public void setConteudo(ArrayList<String> Conteudo) {
        this.Conteudo = Conteudo;
    }

    public Arquivo(Diretorio dir) {
        this.pai = dir;
        this.permissao = "-rwxrwxrwx";
        SimpleDateFormat formato = new SimpleDateFormat("MMM dd yyyy HH:mm");
        this.dataCriacao = formato.format(new Date());
    }

    public String getNome() {
        return nome;
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

    public String getPermissao() {
        return permissao;
    }

    public void setPermissao(String permissao) {
        this.permissao = permissao;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
