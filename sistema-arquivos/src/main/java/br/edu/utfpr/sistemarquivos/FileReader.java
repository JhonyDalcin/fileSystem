package br.edu.utfpr.sistemarquivos;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReader {

    public void read(Path path, String[] parameters) throws IOException {
        var fileName = File.separator + parameters[1];
        var showFilepath = Path.of(path + fileName);
        if (!Files.exists(showFilepath)){
            throw new UnsupportedOperationException("This file does not exist.");
        }
        if (Files.isDirectory(showFilepath)){
            throw new UnsupportedOperationException("This command should be used with files only.");
        }
        if (showFilepath.getFileName().toString().endsWith(".txt")){
            System.out.println(Files.readAllLines(showFilepath).get(0));
        }else throw new UnsupportedOperationException("Extension not suported.");
    }
}
