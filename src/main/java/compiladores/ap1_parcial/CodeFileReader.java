package compiladores.ap1_parcial;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.io.FileReader;

public class CodeFileReader {
    private File codeFile;
    private Reader codeFileReader;
    private int lastCharIndex;
    private boolean EoFReached;
    
    public CodeFileReader(String fileName) { 
        this.codeFile = Paths.get(fileName).toFile();
        this.lastCharIndex = 0;
        this.EoFReached = false;
        try { 
            this.codeFileReader = new BufferedReader(new FileReader(codeFile));     
        }
        catch(IOException e) { 
            System.out.println("Erro na abertura do leitor de arquivo!");
        }
    }
    
    public int readToBuffer(char[] buffer, int bufferSize) throws IOException { 
        return codeFileReader.read(buffer, 0, bufferSize);
    }
    
    public void updateLastCharIndex(int bytesRead) { 
        lastCharIndex += bytesRead;
    }
    
    public List<String> readAllLines() throws IOException { 
        return Files.readAllLines(codeFile.toPath());
    }
    
    public File getCodeFile() {
        return codeFile;
    }

    public void setCodeFile(File codeFile) {
        this.codeFile = codeFile;
    }

    public Reader getCodeFileReader() {
        return codeFileReader;
    }

    public void setCodeFileReader(Reader codeFileReader) {
        this.codeFileReader = codeFileReader;
    }

    public int getLastCharIndex() {
        return lastCharIndex;
    }

    public void setLastCharIndex(int lastCharIndex) {
        this.lastCharIndex = lastCharIndex;
    }

    public boolean isEoFReached() {
        return EoFReached;
    }

    public void setEoFReached(boolean EoFReached) {
        this.EoFReached = EoFReached;
    }
}