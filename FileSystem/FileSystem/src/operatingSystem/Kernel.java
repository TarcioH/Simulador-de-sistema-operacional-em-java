/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingSystem;

/**
 * Classe de Interface que define as chamadas de sistema para gerenciamento do
 * sistema de arquivos.
 */
public interface Kernel {

    /**
     * Funcao para listar diretorios e arquivos.
     *
     * @param parameters Parametros recebidos no terminal .
     * @return String que é impressa no terminal.
     */
    public String ls(String parameters);

    /**
     * Funcao para criar diretorio.
     *
     * @param parameters Parametros recebidos no terminal.
     * @return String que é impressa no terminal.
     */
    public String mkdir(String parameters);

    /**
     * Funcao para navegação entre diretorios.
     *
     * @param parameters Parametros recebidos no terminal.
     * @return String que é impressa no terminal.
     */
    public String cd(String parameters);

    /**
     * Funcao para remover diretorio vazio.
     *
     * @param parameters Parametros recebidos no terminal.
     * @return String que é impressa no terminal.
     */
    public String rmdir(String parameters);

    /**
     * Funcao para copiar arquivos e diretorios.
     *
     * @param parameters Parametros recebidos no terminal.
     * @return String que é impressa no terminal.
     */
    public String cp(String parameters);

    /**
     * Funcao para mover arquivos e diretorios.
     *
     * @param parameters Parametros recebidos no terminal.
     * @return String que é impressa no terminal.
     */
    public String mv(String parameters);

    /**
     * Funcao para remover arquivos e diretorios.
     *
     * @param parameters Parametros recebidos no terminal.
     * @return String que é impressa no terminal.
     */
    public String rm(String parameters);

    /**
     * Funcao para definir permissao de arquivos e diretorios
     *
     * @param parameters Parametros recebidos no terminal.
     * @return String que é impressa no terminal.
     */
    public String chmod(String parameters);

    /**
     * Funcao para criar arquivos.
     *
     * @param parameters Parâmetros recebidos no terminal.
     * @return String que é impressa no terminal.
     */
    public String createfile(String parameters);

    /**
     * Funcao para listar o conteudo de um arquivo.
     *
     * @param parameters Parametros recebidos no terminal.
     * @return String que é impressa no terminal.
     */
    public String cat(String parameters);

    /**
     * Funcao para rodar um arquivo de lote.
     *
     * @param parameters Parametros recebidos no terminal.
     * @return String que é impressa no terminal.
     */
    public String batch(String parameters);

    /**
     * Funcao para gerar dump do Sistema de Arquivos.
     *
     * @param parameters Parametros recebidos no terminal.
     * @return String que é impressa no terminal.
     */
    public String dump(String parameters);

    /**
     * Função para indicar informacoes do simulador.
     *
     * @return String que é impressa no terminal.
     */
    public String info();

}
