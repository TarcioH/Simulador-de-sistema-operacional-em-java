
import java.util.ArrayList;
import java.util.List;

public class FileSystem {
    private String name;
    private boolean isDirectory;
    private List<FileSystem> children;
    // Outros atributos, como permissões, podem ser adicionados aqui

    // Construtor
    public FileSystem(String name, boolean isDirectory) {
        this.name = name;
        this.isDirectory = isDirectory;
        this.children = new ArrayList<>();
    }

    // Métodos para adicionar, remover e listar filhos
    public void addChild(FileSystem child) {
        children.add(child);
    }

    public void removeChild(FileSystem child) {
        children.remove(child);
    }

    public List<FileSystem> getChildren() {
        return children;
    }
}
