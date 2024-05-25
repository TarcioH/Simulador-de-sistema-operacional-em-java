package hardware;

/**
 *
 * @author douglas
 */
public class HardDisk {

    private boolean hardDisk[];
    private int numeroDeBits;

    public HardDisk(int tamanhoDaMemoriaSecundaria) {
        this.numeroDeBits = tamanhoDaMemoriaSecundaria * 8 * 1024 * 1024;
        this.hardDisk = new boolean[this.numeroDeBits];
    }

    public void inicializarMemoriaSecundaria() {
        for (int i = 0; i < numeroDeBits; i++) {
            this.hardDisk[i] = false;
        }
    }

    public void setBitDaPosicao(boolean bit, int posicao) {
        this.hardDisk[posicao] = bit;
    }

    public boolean getBitDaPosicao(int i) {
        return hardDisk[i];
    }

}
