package main;

import static binary.Binario.binaryStringToInt;
import static binary.Binario.intToBinaryString;
import hardware.HardDisk;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.tools.jlink.plugin.Plugin;
import operatingSystem.Kernel;

/**
 * Kernel desenvolvido pelo aluno. Outras classes criadas pelo aluno podem ser
 * utilizadas, como por exemplo: - Arvores; - Filas; - Pilhas; - etc...
 *
 * 
 */
public final class MyKernel implements Kernel {

    Diretorio raiz = new Diretorio(null);
    Diretorio dirAtual = new Diretorio(raiz);
    HardDisk HD = new HardDisk(4);
    int positionHD, positionDirAtual;

    public MyKernel() {
        raiz.setNome("Nome");
        raiz.setPai(raiz);
        dirAtual = raiz;

        positionHD = 0;
        positionDirAtual = 0;
        HD.inicializarMemoriaSecundaria();
        salvaDiretorioNoHD("Nome", "drwxrwxrwx", 0);
    }

    public int verificaOrigem(String Caminho[], boolean finalCaminho) {
        int dirTemp;

        if (Caminho == null) {
            return -1;
        } else {
            switch (Caminho[0]) {
                case "":
                    dirTemp = 0;
                    break;
                case "..":
                    String binario = retornaBinario(positionDirAtual + 880, (positionDirAtual + 880 + 16));
                    dirTemp = binaryStringToInt(binario);
                    break;
                default:
                    dirTemp = positionDirAtual;
                    break;
            }
        }
        if (dirTemp >= 0) {
            dirTemp = percorreCaminho(dirTemp, Caminho, finalCaminho);
        }
        return dirTemp;
    }

    public int percorreCaminho(int dirTemp, String[] Caminho, boolean finalCaminho) {
        int diretorio = dirTemp, limite, i = 0, positionFilhos, limiteDiretorio;
        boolean nomeEncontrado = false;
        String nome;

        if (finalCaminho) {
            limite = Caminho.length;
        } else {
            limite = Caminho.length - 1;
        }

        if (Caminho.length == 1) {
            return diretorio;
        }

        while (i < limite) {

            if (Caminho[i].equals("..")) {
                String binario = retornaBinario(positionDirAtual + 880, (positionDirAtual + 880 + 16));
                diretorio = binaryStringToInt(binario);
            } else if (Caminho[i].equals(".")) {
                diretorio = diretorio;
            } else if (Caminho[i].equals("")) {
                diretorio = 0;
            } else {
                limiteDiretorio = diretorio + 2496;
                diretorio += 896;
                while (diretorio < (limiteDiretorio) && !nomeEncontrado) {

                    String binario = retornaBinario(diretorio, (diretorio + 16));
                    positionFilhos = binaryStringToInt(binario);

                    nome = retornaString(positionFilhos, positionFilhos + (80 * 8));
                    if (Caminho[i].equals(nome)) {
                        diretorio = binaryStringToInt(binario);
                        nomeEncontrado = true;
                        dirTemp = diretorio;
                    } else {
                        diretorio += 16;
                        dirTemp = -1;
                    }
                }
                nomeEncontrado = false;
            }

            i++;
        }

        return dirTemp;
    }

    public boolean verificaNome(String nome) {

        if (nome.contains(".")) {
            return false;
        } else if (nome.trim().equals("")) {
            return false;
        } else if ((Character) nome.charAt(0) == '-') {
            return false;
        }
        return true;
    }

    public boolean seExisteNome(String nomeProcurado, int posicao) {
        int diretorio = posicao + 896, limiteDiretorio, position;
        boolean encontrado = false;
        String nome;

        limiteDiretorio = diretorio + 2496;

        while (diretorio < (limiteDiretorio) && !encontrado) {

            String binario = retornaBinario(diretorio, (diretorio + 16));
            position = binaryStringToInt(binario);

            nome = retornaString(position, position + (80 * 8));
            if (nomeProcurado.equals(nome)) {
                return false;
            } else {
                diretorio += 16;
            }
        }

        return true;
    }

    public int retornaPosicaoPorNome(String nomeProcurado, int posicao) {
        int diretorio = posicao, limiteDiretorio, position, posicaoEncontrada = -1;
        boolean encontrado = false;
        String nome;

        limiteDiretorio = diretorio + 3200;
        if (nomeProcurado.equals("..")) {
            String binario = retornaBinario(positionDirAtual + 880, (positionDirAtual + 880 + 16));
            return binaryStringToInt(binario);
        } else if (nomeProcurado.equals(".")) {
            return diretorio;
        } else if (nomeProcurado.equals("")) {
            return 0;
        } else {
            while (diretorio < (limiteDiretorio) && !encontrado) {

                String binario = retornaBinario(diretorio, (diretorio + 16));
                position = binaryStringToInt(binario);

                nome = retornaString(position, position + (80 * 8));
                if (nomeProcurado.equals(nome)) {
                    return position;
                } else {
                    diretorio += 16;
                }
            }
        }

        return posicaoEncontrada;
    }

    public int retornaPosicaoFilho(int filho, int pai) {
        int diretorio = pai + 896, limiteDiretorio, position, posicaoEncontrada = -1;
        boolean encontrado = false;

        limiteDiretorio = diretorio + 2496;

        while (diretorio < (limiteDiretorio) && !encontrado) {

            String binario = retornaBinario(diretorio, (diretorio + 16));
            position = binaryStringToInt(binario);

            if (position == filho) {
                return diretorio;
            } else {
                diretorio += 16;
            }
        }

        return posicaoEncontrada;
    }

    public String ls(String parameters) {
    String result = "";
    System.out.println("Chamada de Sistema: ls");
    System.out.println("\tParâmetros: " + parameters);

    String[] instrucao = parameters.split(" ");
    boolean showDetails = false;

    if (instrucao.length > 0 && instrucao[0].equals("-l")) {
        showDetails = true;
    }

    result = listarConteudo(showDetails, instrucao);

    return result;
}

private String listarConteudo(boolean showDetails, String[] instrucao) {
    String result = "";
    int posicaoHDInicio = positionDirAtual + 896;
    String binario;
    int posicao;

    if (instrucao.length == 1) {
        do {
            binario = retornaBinario(posicaoHDInicio, posicaoHDInicio + 16);
            posicao = binaryStringToInt(binario);

            if (posicao > 0) {
                String conteudo = remontarDocs(showDetails, posicao);
                result += showDetails ? conteudo + "\n" : conteudo + " ";
            }

            posicaoHDInicio += 16;
        } while (posicao > 0);

        posicaoHDInicio = positionDirAtual + 2496;

        do {
            binario = retornaBinario(posicaoHDInicio, posicaoHDInicio + 16);
            posicao = binaryStringToInt(binario);

            if (posicao > 0) {
                String conteudo = remontarDocs(showDetails, posicao);
                result += showDetails ? conteudo + "\n" : conteudo + " ";
            }

            posicaoHDInicio += 16;
        } while (posicao > 0);
    } else {
        String[] caminho = instrucao[1].split("/");
        int dirTemp = verificaOrigem(caminho, false);
        int posicaoPorNome = retornaPosicaoPorNome(caminho[caminho.length - 1], dirTemp + 896);

        if (dirTemp >= 0 && posicaoPorNome >= 0) {
            posicaoPorNome += 896;

            do {
                binario = retornaBinario(posicaoPorNome, posicaoPorNome + 16);
                posicao = binaryStringToInt(binario);

                if (posicao > 0) {
                    String conteudo = remontarDocs(showDetails, posicao);
                    result += showDetails ? conteudo + "\n" : conteudo + " ";
                }

                posicaoPorNome += 16;
            } while (posicao > 0);
        } else {
            result = "Não foi possível encontrar o diretório";
        }
    }

    return result;
}


    public String mkdir(String parameters) {
    String result = "";
    System.out.println("Chamada de Sistema: mkdir");
    System.out.println("\tParâmetros: " + parameters);

    String[] caminho = parameters.split("/");
    String nome = caminho[caminho.length - 1];

    int dirTemporario = verificaOrigem(caminho, false);

    if (verificaNome(nome)) {
        result = criarDiretorio(nome, dirTemporario);
    } else {
        result = "Nome informado é inválido";
    }

    return result;
}
//lipeza e clareza de cd
private String criarDiretorio(String nome, int dirTemporario) {
    if (dirTemporario >= 0) {
        if (seExisteNome(nome, dirTemporario)) {
            salvaDiretorioNoHD(nome, "drwxrwxrwx", dirTemporario);
            return "";
        } else {
            return "Nome informado já existe";
        }
    } else {
        return "Erro no caminho informado!";
    }
}


    public String cd(String parameters) {
    String result = "";
    String currentDir = "";
    System.out.println("Chamada de Sistema: cd");
    System.out.println("\tParametros: " + parameters);

    // Divida o caminho em partes usando a barra "/"
    String[] caminho = parameters.split("/");

    // Verifica a origem do diretório
    int dirTemp = verificaOrigem(caminho, false);

    if (dirTemp >= 0) {
        // Verifica se o diretório de destino existe
        int posicaoPorNome = retornaPosicaoPorNome(caminho[caminho.length - 1], dirTemp + 896);

        if (posicaoPorNome >= 0) {
            String dirAtual = remontarDocs(false, posicaoPorNome);

            if (!dirAtual.contains(".txt")) {
                // Atualiza a posição do diretório atual
                positionDirAtual = posicaoPorNome;

                // Atualiza o diretório atual
                if (positionDirAtual == 0) {
                    currentDir = "/";
                } else {
                    currentDir = dirAtual;
                }
            } else {
                result = "Você não pode entrar em um arquivo.";
                currentDir = "/";
            }
        } else {
            result = "Diretório não encontrado!";
        }
    } else {
        result = "Diretório inválido!";
    }

    // Atualize o diretório atual na parte gráfica
    operatingSystem.fileSystem.FileSytemSimulator.currentDir = currentDir;

    return result;
}


    public String rmdir(String parameters) {
    // Variável 'result' deverá conter o que será impresso na tela após o comando do usuário.
    String result = "";
    System.out.println("Chamada de Sistema: rmdir");
    System.out.println("\tParâmetros: " + parameters);

    // Início da implementação do aluno
    String[] caminho = parameters.split("/");
    String nome = caminho[caminho.length - 1];

    int dirTemp = verificaOrigem(caminho, false);
    int posicaoPorNome = retornaPosicaoPorNome(caminho[caminho.length - 1], dirTemp + 896);

    if (dirTemp >= 0 && posicaoPorNome >= 0) {
        if (verificaHDVazio(posicaoPorNome + 896, 3200)) {
            // Se o diretório estiver vazio, pode ser removido.
            limpaHD(posicaoPorNome, posicaoPorNome + 4095);
        } else {
            result = "diretório possui conteúdo e não pode ser removido.";
        }
    } else {
        result = "diretório não encontrado.";
    }

    // Fim da implementação do aluno
    return result;
}
public String cp(String parameters) {
    String result = "";  // O resultado contém a saída após a execução do comando pelo usuário
    System.out.println("Chamada de Sistema: cp");
    System.out.println("\tParâmetros: " + parameters);

    String[] comando = parameters.split(" ");

    if (comando.length == 2) {
        String caminhoOrigem = comando[0];
        String caminhoDestino = comando[1];

        String nomeOrigem = getNomeFromCaminho(caminhoOrigem);
        String nomeDestino = getNomeFromCaminho(caminhoDestino);

        int dirOrigem = verificaOrigem(caminhoOrigem.split("/"), false);
        int dirDestino = verificaOrigem(caminhoDestino.split("/"), false);

        if (dirOrigem >= 0 && dirDestino >= 0) {
            int posicaoOrigem = retornaPosicaoPorNome(nomeOrigem, dirOrigem + 896);
            int posicaoDestino = retornaPosicaoPorNome(nomeDestino, dirDestino + 896);
            int posicaoFinal = posicaoDestino + 1600;

            if (nomeOrigem.contains(".txt")) {
                if (seExisteNome(nomeDestino, posicaoDestino)) {
                    if (verificaHDVazio(posicaoDestino + 896, posicaoFinal)) {
                        if (nomeDestino.contains(".txt") && nomeOrigem.contains(".txt") && !nomeDestino.equals(nomeOrigem)) {
                            int copia = copiaBloco(posicaoOrigem);
                            positionHD += 4096;
                            addFilho(copia, posicaoDestino, true);

                            String binarioNomeDestino = retornaBinario(nomeDestino);
                            Boolean[] bitsBinarioNomeDestino = desconverteBinario(binarioNomeDestino);
                            armazenaNoHD(bitsBinarioNomeDestino, copia);
                        } else {
                            int copia = copiaBloco(posicaoOrigem);
                            positionHD += 4096;
                            addFilho(copia, posicaoDestino, true);
                        }
                    } else {
                        result = "Impossível criar pasta (Armazenamento Cheio)";
                    }
                }
            } else {
                if (seExisteNome(nomeDestino, posicaoDestino)) {
                    if (verificaHDVazio(posicaoDestino + 896, posicaoFinal)) {
                        addFilho(posicaoOrigem, posicaoDestino, true);
                        int posicaoLimpa = retornaPosicaoFilho(posicaoOrigem, dirOrigem);
                        int max = posicaoLimpa + 16;
                        limpaHD(posicaoLimpa, max);
                    } else {
                        result = "Impossível criar pasta (Armazenamento Cheio)";
                    }
                }
            }
        } else {
            result = "Diretório de origem ou destino não encontrado.";
        }
    } else if (comando.length == 3 && comando[0].contains("-R")) {
        String caminhoOrigem = comando[1];
        String caminhoDestino = comando[2];

        int dirOrigem = verificaOrigem(caminhoOrigem.split("/"), false);
        int dirDestino = verificaOrigem(caminhoDestino.split("/"), false);

        if (dirOrigem >= 0 && dirDestino >= 0) {
            int posicaoOrigem = retornaPosicaoPorNome(getNomeFromCaminho(caminhoOrigem), dirOrigem + 896);
            int posicaoDestino = retornaPosicaoPorNome(getNomeFromCaminho(caminhoDestino), dirDestino + 896);
            int posicaoFinal = posicaoDestino + 1600;

            if (seExisteNome(getNomeFromCaminho(caminhoOrigem), posicaoDestino)) {
                if (posicaoDestino >= 0 && posicaoOrigem >= 0) {
                    if (verificaHDVazio(posicaoDestino + 896, posicaoFinal)) {
                        int copia = copiaBloco(posicaoOrigem);
                        positionHD += 4096;
                        addFilho(copia, posicaoDestino, true);
                    } else {
                        result = "Impossível copiar pasta (Armazenamento Cheio)";
                    }
                } else {
                    result = "Diretório informado não encontrado";
                }
            } else {
                result = "Não foi possível encontrar o objeto";
            }
        } else {
            result = "Comando incorreto";
        }
    } else {
        result = "Comando incorreto";
    }
    
    return result;
}

private String getNomeFromCaminho(String caminho) {
    String[] partesCaminho = caminho.split("/");
    return partesCaminho[partesCaminho.length - 1];
}

 public String mv(String parameters) {
    // Variável result conterá a saída do comando do usuário.
    String result = "";
    System.out.println("Chamada de Sistema: mv");
    System.out.println("\tParâmetros: " + parameters);

    // Divide os parâmetros em partes.
    String[] comando = parameters.split(" ");
    String binario;
    Boolean[] bitsBinario;

    if (comando.length == 2) {
        String[] caminho1 = comando[0].split("/");
        String[] caminho2 = comando[1].split("/");
        String nome = caminho1[caminho1.length - 1];
        String novoNome = caminho2[caminho2.length - 1];

        // Verifica o diretório de origem e destino.
        int dirOrigem = verificaOrigem(caminho1, false);
        int origemPorNome = retornaPosicaoPorNome(caminho1[caminho1.length - 1], dirOrigem + 896);

        int dirDestino = verificaOrigem(caminho2, false);
        int destinoPorNome = retornaPosicaoPorNome(caminho2[caminho2.length - 1], dirDestino + 896);
        int positionInicio = destinoPorNome + 896, positionFinal = positionInicio + 1600;

        if (nome.contains(".txt")) {
            if (seExisteNome(nome, destinoPorNome)) {
                if (verificaHDVazio(positionInicio, positionFinal)) {
                    if (novoNome.contains(".txt") && nome.contains(".txt")) {
                        if (!novoNome.equals(nome)) {
                            // Renomeia o arquivo no HD.
                            binario = retornaBinario(novoNome);
                            bitsBinario = desconverteBinario(binario);
                            armazenaNoHD(bitsBinario, origemPorNome);
                        }
                    } else {
                        // Move o arquivo para o destino.
                        addFilho(origemPorNome, destinoPorNome, true);
                        int posicaoLimpa = retornaPosicaoFilho(origemPorNome, dirOrigem);
                        int max = posicaoLimpa + 16;
                        limpaHD(posicaoLimpa, max);
                    }
                } else {
                    result = "impossível criar pasta (Armazenamento Cheio)";
                }
            }
        } else {
            if (seExisteNome(nome, destinoPorNome)) {
                if (verificaHDVazio(positionInicio, positionFinal)) {
                    // Move o arquivo para o destino.
                    addFilho(origemPorNome, destinoPorNome, true);
                    int posicaoLimpa = retornaPosicaoFilho(origemPorNome, dirOrigem);
                    int max = posicaoLimpa + 16;
                    limpaHD(posicaoLimpa, max);
                } else {
                    result = "impossível criar pasta (Armazenamento Cheio)";
                }
            }
        }
    } else {
        result = "comando incorreto";
    }

    return result;
}


    public String rm(String parameters) {
    // Variável result conterá a saída do comando do usuário.
    String result = "";
    System.out.println("Chamada de Sistema: rm");
    System.out.println("\tParâmetros: " + parameters);

    // Divide os parâmetros em partes.
    String[] comando = parameters.split(" ");
    if (comando.length == 1) {
        // Caso o comando seja simples (sem recursão).
        String[] caminho1 = comando[0].split("/");
        String nome = caminho1[caminho1.length - 1];

        int dirTemp = verificaOrigem(caminho1, false);
        int posicaoPorNome = retornaPosicaoPorNome(caminho1[caminho1.length - 1], dirTemp + 896);
        if (dirTemp >= 0 && posicaoPorNome >= 0) {
            // Limpa o HD para excluir o objeto.
            limpaHD(posicaoPorNome, posicaoPorNome + 4095);
        } else {
            result = "objeto para exclusão não encontrado";
        }
    } else if (comando.length == 2 && comando[0].contains("-R")) {
        // Caso o comando seja com recursão.
        String[] caminho2 = comando[1].split("/");
        String nome = caminho2[caminho2.length - 1];

        int dirTemp = verificaOrigem(caminho2, false);
        int posicaoPorNome = retornaPosicaoPorNome(caminho2[caminho2.length - 1], dirTemp + 896);
        if (!nome.contains(".txt")) {
            if (dirTemp >= 0 && posicaoPorNome >= 0) {
                // Limpa o HD para excluir o diretório com recursão.
                limpaHD(posicaoPorNome, posicaoPorNome + 4095);
            } else {
                result = "diretório não encontrado";
            }
        } else {
            result = "impossível excluir arquivo com esse comando";
        }
    }
    return result;
}


    public String chmod(String parameters) {
        // variavel result deverah conter o que vai ser impresso na tela apos comando do
        // usuário
        String result = "";
        System.out.println("Chamada de Sistema: chmod");
        System.out.println("\tParametros: " + parameters);

        // inicio da implementacao do aluno
        String[] comando = parameters.split(" "), caminho;
        String newPermission, nome, binario;
        Boolean[] bitsBinario;
        int posicaoPermissao;

        if (comando.length == 3 && comando[0].contains("-R")) {

            newPermission = converteCHMOD(comando[1].split(""));
            caminho = comando[2].split("/");

            int dirTemp = verificaOrigem(caminho, false);
            int posicaoPorNome = retornaPosicaoPorNome(caminho[caminho.length - 1], dirTemp + 896);
            if (posicaoPorNome >= 0) {

                binario = retornaBinario(newPermission);
                bitsBinario = desconverteBinario(binario);
                alteraPermissaoFilhos(posicaoPorNome, bitsBinario);
            }

        } else if (comando.length == 2) {
            caminho = comando[1].split("/");
            int dirTemp = verificaOrigem(caminho, false);
            int posicaoPorNome = retornaPosicaoPorNome(caminho[caminho.length - 1], dirTemp + 896);
            if (posicaoPorNome >= 0) {
                newPermission = converteCHMOD(comando[0].split(""));
                posicaoPermissao = posicaoPorNome + (81 * 8);

                binario = retornaBinario(newPermission);
                bitsBinario = desconverteBinario(binario);
                armazenaNoHD(bitsBinario, posicaoPermissao);
            } else {
                result = "objeto nao encontrado";
            }

        } else {
            result = "comando incorreto";
        }
        return result;
    }

    public void alteraPermissaoFilhos(int origem, Boolean[] bitsBinario) {
        int posicaoFilho, posicaoPermissao, i = 1;
        String nome, binario;
        armazenaNoHD(bitsBinario, origem + 81 * 8);

        int posicaoInicio = origem + 896, posicaoMax = origem + 896 + 16;

        String posicao = retornaBinario(posicaoInicio, posicaoMax);
        posicaoFilho = binaryStringToInt(posicao);

        while (posicaoFilho != 0) {
            posicaoPermissao = posicaoFilho + 81 * 8;
            nome = retornaString(posicaoFilho, posicaoFilho + 81 * 8);

            if (nome.contains(".txt")) {
                armazenaNoHD(bitsBinario, posicaoPermissao);
            } else {
                alteraPermissaoFilhos(posicaoFilho, bitsBinario);
            }

            armazenaNoHD(bitsBinario, posicaoPermissao);

            posicaoInicio += 16;
            posicaoMax += 16;
            posicaoFilho = binaryStringToInt(retornaBinario(posicaoInicio, posicaoMax));
        }
        posicaoInicio = origem + 896 + 1600;
        posicaoMax = posicaoInicio + 16;
        posicao = retornaBinario(posicaoInicio, posicaoMax);
        posicaoFilho = binaryStringToInt(posicao);

        while (posicaoFilho != 0) {
            posicaoPermissao = posicaoFilho + 81 * 8;
            nome = retornaString(posicaoFilho, posicaoFilho + 81 * 8);

            if (nome.contains(".txt")) {
                armazenaNoHD(bitsBinario, posicaoPermissao);
            } else {
                alteraPermissaoFilhos(posicaoFilho, bitsBinario);
            }

            armazenaNoHD(bitsBinario, posicaoPermissao);

            posicaoInicio += 16;
            posicaoMax += 16;
            posicaoFilho = binaryStringToInt(retornaBinario(posicaoInicio, posicaoMax));
        }
    }

    public String converteCHMOD(String[] chmod) {
        String permissao = "";
        if (chmod.length == 3) {
            for (String position : chmod) {
                if (position.equals("0")) {
                    permissao = permissao + "---";
                } else if (position.equals("1")) {
                    permissao = permissao + "--x";
                } else if (position.equals("2")) {
                    permissao = permissao + "-w-";
                } else if (position.equals("3")) {
                    permissao = permissao + "-wx";
                } else if (position.equals("4")) {
                    permissao = permissao + "r--";
                } else if (position.equals("5")) {
                    permissao = permissao + "r-x";
                } else if (position.equals("6")) {
                    permissao = permissao + "rw-";
                } else if (position.equals("7")) {
                    permissao = permissao + "rwx";
                }
            }

        }

        return permissao;
    }

    public String desconverteCHMOD(String permissao) {
        String CHMOD = "", aux;
        int i = 1;
        while (i < 9) {
            aux = permissao.substring(i, i + 3);
            if (aux.equals("rwx")) {
                CHMOD += "7";
            } else if (aux.equals("rw-")) {
                CHMOD += "6";
            } else if (aux.equals("r-x")) {
                CHMOD += "5";
            } else if (aux.equals("r--")) {
                CHMOD += "4";
            } else if (aux.equals("-wx")) {
                CHMOD += "3";
            } else if (aux.equals("-w-")) {
                CHMOD += "2";
            } else if (aux.equals("--x")) {
                CHMOD += "1";
            } else if (aux.equals("---")) {
                CHMOD += "0";
            }
            i = i + 3;
        }
        // -d rwx rwx rwx

        return CHMOD;
    }

public String createfile(String parameters) {
    // Variável 'result' conterá a saída após a execução do comando pelo usuário
    String result = "";
    System.out.println("Chamada de Sistema: createfile");
    System.out.println("\tParâmetros: " + parameters);

    // Início da implementação do aluno
    String[] comando = parameters.split(".txt ");
    
    // Verifica se o comando contém o nome do arquivo e seu conteúdo
    if (comando.length > 1) {
        String[] caminho = comando[0].split("/");
        String nome = caminho[caminho.length - 1];
        String conteudo = comando[1];
        
        // Verifica se o nome do arquivo é válido
        if (verificaNome(nome)) {
            nome = nome + ".txt";
            int dirTemporario = verificaOrigem(caminho, false);
            
            if (dirTemporario >= 0) {
                // Verifica se o nome do arquivo já existe no diretório
                if (seExisteNome(nome, dirTemporario)) {
                    salvaArquivoNoHD(nome, dirTemporario, conteudo);
                } else {
                    result = "Nome informado já existe";
                }
            } else {
                result = "Erro no caminho informado!";
            }
        } else {
            result = "Nome informado é inválido";
        }
    } else {
        result = "Erro no comando inserido";
    }

    // Fim da implementação do aluno
    return result;
}


   public String cat(String parameters) {
    String result = "";
    System.out.println("Chamada de Sistema: cat");
    System.out.println("\tParametros: " + parameters);

    // Divide o caminho em partes usando a barra "/"
    String[] caminho = parameters.split("/");
    String nome = caminho[caminho.length - 1];

    // Verifica a origem do diretório
    int dirOrigem = verificaOrigem(caminho, false);

    if (dirOrigem >= 0) {
        // Verifica se o arquivo com o nome especificado existe no diretório
        int position = retornaPosicaoPorNome(nome, dirOrigem);

        if (position >= 0) {
            // Calcula a posição do conteúdo do arquivo e obtém o conteúdo
            int positionConteudo = position + 880;
            String conteudo = retornaString(positionConteudo, positionConteudo + 3216);
            result = conteudo;
        } else {
            result = "Arquivo não encontrado.";
        }
    } else {
        result = "Diretório inválido.";
    }

    return result;
}
    public String batch(String parameters) {
    String result = "";
    System.out.println("Chamada de Sistema: batch");
    System.out.println("\tParametros: " + parameters);

    String caminho = parameters;
    List<String> config = FileManager.stringReader(caminho);

    for (String commandLine : config) {
        String[] parts = commandLine.split(" ", 2); // Divide a linha em comando e parâmetros
        String comando = parts[0];
        String parametros = (parts.length > 1) ? parts[1] : "";

        switch (comando) {
            case "ls":
                ls(parametros);
                break;
            case "mkdir":
                mkdir(parametros);
                break;
            case "cd":
                cd(parametros);
                break;
            case "rmdir":
                rmdir(parametros);
                break;
            case "cp":
                cp(parametros);
                break;
            case "mv":
                mv(parametros);
                break;
            case "rm":
                rm(parametros);
                break;
            case "chmod":
                chmod(parametros);
                break;
            case "createfile":
                createfile(parametros);
                break;
            case "cat":
                cat(parametros);
                break;
            case "batch":
                batch(parametros);
                break;
            case "dump":
                dump(parametros);
                break;
            default:
                result = "Comando não reconhecido: " + comando;
        }
    }

    return (result.isEmpty()) ? "Comandos Executados." : result;
}


public String dump(String parameters) {
    String result = "";
    System.out.println("Chamada de Sistema: dump");
    System.out.println("\tParametros: " + parameters);

    int rootDirectory = 0;
    String caminho = parameters;

    if (caminho != null) {
        FileManager.writer(caminho, "");
        visitaTodosOsFilhos(rootDirectory, caminho);
        result = "Arquivo de dump gerado com sucesso.";
    } else {
        result = "Caminho inválido";
    }

    return result;
}


    public void visitaTodosOsFilhos(int dirOrigem, String caminho) {
        String comando, permissao, conteudo = "", nome = "";
        int filho, irmao = 0, i = 1;
        String binario;

        binario = retornaBinario(dirOrigem + 896, (dirOrigem + 896 + 16));
        filho = binaryStringToInt(binario);

        if (filho > 0) {
            nome = retornaString(filho, filho + 80 * 8);

            if (!nome.contains(".txt")) {
                comando = "mkdir " + nome;
                FileManager.writerAppend(caminho, comando + "\n");

                comando = "cd " + nome;
                FileManager.writerAppend(caminho, comando + "\n");
                visitaTodosOsFilhos(filho, caminho);
            } else {
                conteudo = retornaString(filho + 880, filho + 4096);
                comando = "createfile " + nome + " " + conteudo;
                FileManager.writerAppend(caminho, comando + "\n");
            }

            permissao = retornaString(filho + 80 * 8, filho + 80 * 8 + 10 * 8);

            if (nome.contains(".txt")) {
                if (!permissao.equals("-rwxrwxrwx")) {
                    permissao = desconverteCHMOD(permissao);
                    comando = "chmod " + permissao + " " + nome;
                    FileManager.writerAppend(caminho, comando + "\n");
                }
            } else {
                if (!permissao.equals("drwxrwxrwx")) {
                    permissao = desconverteCHMOD(permissao);
                    comando = "chmod " + permissao + " " + nome;
                    FileManager.writerAppend(caminho, comando + "\n");
                }
            }
            if (!nome.contains(".txt")) {
                binario = retornaBinario(dirOrigem + 896 + 16, (dirOrigem + 896 + 16 + 16 * i));
                irmao = binaryStringToInt(binario);
            }

            if (irmao > 0) {
                while (irmao > 0) {
                    i++;
                    comando = "mkdir " + retornaString(irmao, irmao + 80 * 8);
                    FileManager.writerAppend(caminho, comando + "\n");
                    visitaTodosOsFilhos(irmao, caminho);
                    binario = "";
                    binario = retornaBinario(dirOrigem + 896 + 16 * (i), (dirOrigem + 896 + 16 + 16 * i));
                    irmao = binaryStringToInt(binario);
                }

            }
            if (!nome.contains(".txt")) {
                visitaTodosOsFilhos(dirOrigem + 1600, caminho);

            }
        } else {
            if (!nome.contains(".txt")) {
                comando = "cd ..";
                FileManager.writerAppend(caminho, comando + "\n");
            }
        }      
    }

    public String info() {
        String result = "";
        System.out.println("Chamada de Sistema: info");
        System.out.println("\tParametros: sem parametros");
        String name = "Tárcio Rodrigues";
        String registration = "2018.110.200.28";
        String version = "2.4";

        result += "Nome do Aluno:        " + name;
        result += "\nMatricula do Aluno:   " + registration;
        result += "\nVersao do Kernel:     " + version;

        return result;
    }

    public void salvaArquivoNoHD(String nome, int pai, String conteudo) {

        String binario = "";
        int positionAux, positionAuxMax, position = positionHD;
        Boolean[] bitsBinario;

        binario = retornaBinario(nome);
        bitsBinario = desconverteBinario(binario);
        armazenaNoHD(bitsBinario, positionHD);
        positionHD = positionHD + (80 * 8);

        binario = retornaBinario("-rwxrwxrwx");
        bitsBinario = desconverteBinario(binario);
        armazenaNoHD(bitsBinario, positionHD);
        positionHD = positionHD + (10 * 8);

        SimpleDateFormat formato = new SimpleDateFormat("MMM dd yyyy HH:mm");
        String data = formato.format(new Date());
        binario = retornaBinario(data);
        bitsBinario = desconverteBinario(binario);
        armazenaNoHD(bitsBinario, positionHD);
        positionHD = positionHD + (20 * 8);

        if (position > 65536) {

            binario = intToBinaryString(position, 24);
            bitsBinario = desconverteBinario(binario);
            positionAux = pai + 2504;
            positionAuxMax = positionAux + 1600;
            while (!verificaHDVazio(positionAux, 16) && (positionAux < positionAuxMax)) {
                positionAux += 16;
            }

            if ((positionAux < positionAuxMax)) {
                armazenaNoHD(bitsBinario, positionAux);
            }

            int posicaoAux2 = positionHD;

            binario = retornaBinario(conteudo);
            bitsBinario = desconverteBinario(binario);
            armazenaNoHD(bitsBinario, posicaoAux2);

            positionHD = positionHD + (402 * 8);

        } else {
            binario = intToBinaryString(position, 16);
            bitsBinario = desconverteBinario(binario);
            positionAux = pai + 2496;
            positionAuxMax = positionAux + 1600;
            while (!verificaHDVazio(positionAux, 16) && (positionAux < positionAuxMax)) {
                positionAux += 16;
            }

            if ((positionAux < positionAuxMax)) {
                armazenaNoHD(bitsBinario, positionAux);
            }

            int posicaoAux2 = positionHD;

            binario = retornaBinario(conteudo);
            bitsBinario = desconverteBinario(binario);
            armazenaNoHD(bitsBinario, posicaoAux2);

            positionHD = positionHD + (402 * 8);
        }

    }

    public void salvaDiretorioNoHD(String nome, String permisao, int pai) {
        String binario = "";
        int positionAux, positionAuxMax, position = positionHD;
        Boolean[] bitsBinario;

        binario = retornaBinario(nome);
        bitsBinario = desconverteBinario(binario);
        armazenaNoHD(bitsBinario, positionHD);
        positionHD = positionHD + (80 * 8);

        binario = retornaBinario("drwxrwxrwx");
        bitsBinario = desconverteBinario(binario);
        armazenaNoHD(bitsBinario, positionHD);
        positionHD = positionHD + (10 * 8);

        SimpleDateFormat formato = new SimpleDateFormat("MMM dd yyyy HH:mm");
        String data = formato.format(new Date());
        binario = retornaBinario(data);
        bitsBinario = desconverteBinario(binario);
        armazenaNoHD(bitsBinario, positionHD);
        positionHD = positionHD + (20 * 8);

        binario = intToBinaryString(pai, 16);
        bitsBinario = desconverteBinario(binario);
        armazenaNoHD(bitsBinario, positionHD);
        positionHD = positionHD + (2 * 8);

        binario = intToBinaryString(position, 16);
        bitsBinario = desconverteBinario(binario);
        positionAux = pai + 896;
        positionAuxMax = pai + 2496;

        while (!verificaHDVazio(positionAux, 16) && (positionAux < positionAuxMax)) {
            positionAux += 16;
        }

        if ((positionAux < positionAuxMax)) {
            armazenaNoHD(bitsBinario, positionAux);
        }

        positionHD = positionHD + (400 * 8);
    }

    public Boolean verificaHDVazio(int caminho, int posicaoMax) {
        String binario = "";
        int posicaoHDInicio = caminho, posicaoHDMax = caminho + posicaoMax, posicao = 0;
        for (int i = posicaoHDInicio; i < posicaoHDMax; i++) {
            if (HD.getBitDaPosicao(i)) {
                binario += "1";
            } else {
                binario += "0";
            }

            if (binario.length() == 16) {
                posicao = binaryStringToInt(binario);
                binario = "";

                if (posicao > 0) {
                    return false;
                }
            }
        }
        if (posicao > 0) {
            return false;
        } else {
            return true;
        }
    }

    public String retornaBinario(String parametro) {
        char teste;
        int j = 0, i;
        String binario = "";

        while (j < parametro.length()) {

            teste = parametro.charAt(j);
            i = teste;

            binario += intToBinaryString(i, 8);
            j++;
        }

        return binario;
    }

    public Boolean[] desconverteBinario(String Binario) {
        Boolean[] binario = new Boolean[Binario.length()];
        String[] aux = Binario.split("");
        int j;
        for (int i = 0; i < Binario.length(); i++) {
            j = Integer.parseInt(aux[i]);
            if (j > 0) {
                binario[i] = true;
            } else {
                binario[i] = false;
            }
        }
        return binario;
    }

    public void armazenaNoHD(Boolean[] bitsBinario, int inicio) {
        int i = 0, j = inicio;

        while (i < bitsBinario.length) {
            HD.setBitDaPosicao(bitsBinario[i], j);
            j++;
            i++;
        }
    }

    public void limpaHD(int posicaoInicio, int posicaoFinal) {
        int i = posicaoInicio, j = posicaoInicio;
        String bin = "";

        while (i < posicaoFinal) {
            HD.setBitDaPosicao(false, j);
            j++;
            i++;
        }
    }

    public String remontarDocs(Boolean description, int position) {
        String nome, permissao, data, retorno = "";
        int i = position, posicaoHDMax;

        posicaoHDMax = (position) + (80 * 8);
        nome = retornaString(i, posicaoHDMax);

        i = posicaoHDMax;
        posicaoHDMax += (10 * 8);
        permissao = retornaString(i, posicaoHDMax);

        i = posicaoHDMax;
        posicaoHDMax += (20 * 8);
        data = retornaString(i, posicaoHDMax);

        if (description) {
            retorno = permissao + " " + data + " " + nome;
        } else {
            retorno += nome;
        }

        return retorno;
    }

    public String retornaString(int posicao, int posicaoMax) {
        String string = "", binario = "";
        int posicaoHDMax, i, aux = 0;
        char[] numeroASC = new char[200];

        posicaoHDMax = posicaoMax;

        for (i = posicao; i < posicaoHDMax; i++) {
            if (HD.getBitDaPosicao(i)) {
                binario += "1";
            } else {
                binario += "0";
            }
            if (binario.length() == 8) {
                if (binaryStringToInt(binario) > 0) {
                    numeroASC[aux] = (char) binaryStringToInt(binario);
                    string += numeroASC[aux];
                    aux++;
                }
                binario = "";
            }
        }

        return string;
    }

    public String retornaBinario(int posicao, int posicaoMax) {
        String binario = "";
        int posicaoHDMax, i, aux = 0;

        posicaoHDMax = posicaoMax;

        for (i = posicao; i < posicaoHDMax; i++) {
            if (HD.getBitDaPosicao(i)) {
                binario += "1";
            } else {
                binario += "0";
            }
        }

        return binario;
    }

    public void addFilho(int filho, int pai, boolean mudaPai) {
        String binario;
        Boolean bitsBinario[];
        int positionAux, positionAuxMax;

        binario = intToBinaryString(filho, 16);
        bitsBinario = desconverteBinario(binario);
        positionAux = pai + 896;
        positionAuxMax = pai + 2496;

        while (!verificaHDVazio(positionAux, 16) && (positionAux < positionAuxMax)) {
            positionAux += 16;
        }

        if ((positionAux < positionAuxMax)) {
            armazenaNoHD(bitsBinario, positionAux);
        }
        if (mudaPai) {
            binario = intToBinaryString(pai, 16);
            bitsBinario = desconverteBinario(binario);
            armazenaNoHD(bitsBinario, filho + 880);
        }
    }

    public int copiaBloco(int origem) {
        int j = origem, i, count = 0;
        int pos = positionHD;
        int posMax = positionHD + 4096;
        for (i = positionHD; i < posMax; i++) {
            count++;
            HD.setBitDaPosicao(HD.getBitDaPosicao(j), i);
            j++;
        }
        return positionHD;
    }
}
