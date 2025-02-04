package br.edu.utfpr.sistemarquivos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.stream.Stream;

public enum Command {

    LIST() {
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("LIST") || commands[0].startsWith("list");
        }

        @Override
        Path execute(Path path) throws IOException {
            System.out.println("Content of: " + path);
            try(var stream = Files.list(path)){
                stream.forEach(p -> System.out.println(p.getFileName()));
            }
            return path;
        }
    },
    SHOW() {
        private String[] parameters = new String[]{};

        @Override
        void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("SHOW") || commands[0].startsWith("show");
        }

        @Override
        Path execute(Path path) throws IOException {
            if (parameters.length <= 1){
                throw new UnsupportedOperationException("Invalid argument");
            }
            var fileReader = new FileReader();
            fileReader.read(path, parameters);
            return path;
        }
    },
    BACK() {
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("BACK") || commands[0].startsWith("back");
        }

        @Override
        Path execute(Path path) {
            if (path.getFileName().toString().equals("hd")){
                throw new UnsupportedOperationException("Error: Back not allowed, the current directory is ROOT.");
            }
            else path = path.getParent();
            return path;
        }
    },
    OPEN() {
        private String[] parameters = new String[]{};

        @Override
        void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("OPEN") || commands[0].startsWith("open");
        }

        @Override
        Path execute(Path path) {
            if (parameters.length <= 1){
                throw new UnsupportedOperationException("Invalid argument");
            }
            var fileName = File.separator + parameters[1];
            path = Path.of(path + fileName);
            if (!Files.exists(path)){
                throw new UnsupportedOperationException("Directory " + fileName + " does not exist.");
            }
            if (!Files.isDirectory(path)){
                throw new UnsupportedOperationException("Directory " + fileName + " is not a directory.");
            }
            return path;
        }
    },
    DETAIL() {
        private String[] parameters = new String[]{};

        @Override
        void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("DETAIL") || commands[0].startsWith("detail");
        }

        @Override
        Path execute(Path path) throws IOException {
            if (parameters.length <= 1){
                throw new UnsupportedOperationException("Invalid argument");
            }
            var fileName = File.separator + parameters[1];
            var detailFilepath = Path.of(path + fileName);
            if (!Files.exists(detailFilepath)){
                throw new UnsupportedOperationException("File or Directory " + fileName + " does not exist.");
            }
            BasicFileAttributeView view = Files.getFileAttributeView(detailFilepath, BasicFileAttributeView.class);
            BasicFileAttributes attributes = view.readAttributes();
            System.out.println("Is directory: " + attributes.isDirectory() + "\n" +
                                "Size: " + attributes.size() + "\n" +
                                "Created on: " + attributes.creationTime() + "\n" +
                                "Last access time: " + attributes.lastAccessTime());
            return path;
        }
    },
    EXIT() {
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("EXIT") || commands[0].startsWith("exit");
        }

        @Override
        Path execute(Path path) {
            System.out.print("Saindo...");
            return path;
        }

        @Override
        boolean shouldStop() {
            return true;
        }
    };

    abstract Path execute(Path path) throws IOException;

    abstract boolean accept(String command);

    void setParameters(String[] parameters) {
    }

    boolean shouldStop() {
        return false;
    }

    public static Command parseCommand(String commandToParse) {

        if (commandToParse.isBlank()) {
            throw new UnsupportedOperationException("Type something...");
        }

        final var possibleCommands = values();

        for (Command possibleCommand : possibleCommands) {
            if (possibleCommand.accept(commandToParse)) {
                possibleCommand.setParameters(commandToParse.split(" "));
                return possibleCommand;
            }
        }

        throw new UnsupportedOperationException("Can't parse command [%s]".formatted(commandToParse));
    }
}
