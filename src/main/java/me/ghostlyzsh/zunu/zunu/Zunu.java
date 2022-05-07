package me.ghostlyzsh.zunu.zunu;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;

public final class Zunu extends JavaPlugin {
    private static final HashMap<String, Interpreter> interpreters = new HashMap<>();
    static HashMap<String, Boolean> hadError = new HashMap<>();
    static HashMap<String, Boolean> hadRuntimeError = new HashMap<>();

    static HashMap<String, byte[]> sources = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        String[] pathnames;

        // Get zunu directory, if it doesn't exist, then create it.
        File f = new File("plugins/Zunu");
        if(!Files.exists(Paths.get("plugins/Zunu"))) {
            f.mkdir();
        }

        // List all pathnames. This is used to get the files next.
        pathnames = f.list();
        // Get all filenames ending in .zn
        List<String> filenames = new ArrayList<>();
        assert pathnames != null;
        try {
            for(String p : pathnames) {
                if(p.endsWith(".zn")) {
                    filenames.add(p);
                }
            }
        } catch(NullPointerException e) {
            this.getLogger().log(Level.SEVERE, "Something went wrong in loading files.");
            this.getPluginLoader().disablePlugin(this);
        }
        // Convert each filename to a path, this is used to get the content of files
        List<Path> filepaths = new ArrayList<>();
        for (String fn: filenames) {
            filepaths.add(Paths.get("plugins/Zunu/" + fn));
        }

        // Read all files and convert its contents into byte arrays
        List<byte[]> contentStrings = new ArrayList<>();
        for (Path path: filepaths) {
            try {
                contentStrings.add(Files.readAllBytes(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Add file contents to a sources map and run the files
        for(int i = 0; i < contentStrings.size(); i++) {
            sources.put(filenames.get(i), contentStrings.get(i));
            runFile(contentStrings.get(i), filenames.get(i));
        }
    }

    private static void runFile(byte[] bytes, String name) {
        // Each file defaults to having no errors
        hadError.put(name, false);
        hadRuntimeError.put(name, false);

        run(new String(bytes, Charset.defaultCharset()), name);
    }

    private static void run(String source, String name) {
        // pass the source through the lexer/scanner
        Scanner scanner = new Scanner(source, name);
        List<Token> tokens = scanner.scanTokens();

        // handle lexing errors
        if(hadError.get(name)) return;

        // parse the list of tokens
        Parser parser = new Parser(tokens, name);
        List<Stmt> statements = parser.parse();

        // more error handling
        if(hadError.get(name)) return;
        if(hadRuntimeError.get(name)) return;

        // interpret the file from the AST generated
        interpreters.put(name, new Interpreter());
        interpreters.get(name).interpreter(statements, name);
    }

    static void error(int line, int start, String message, String name) {
        report(line, start, "", message, name);
    }

    private static void report(int line, int start, String where, String message, String name) {
        // first find the start and end of the line
        int end = start;
        int realStart = start;
        while(Zunu.sources.get(name)[realStart] != '\n' && realStart > -1) {
            realStart--;
        }
        while(Zunu.sources.get(name)[end] != '\n' && end < Zunu.sources.get(name).length) {
            end++;
        }
        // take a substring of the line
        String lineStr = new String(Arrays.copyOfRange(Zunu.sources.get(name), realStart, end)).trim();

        getPlugin(Zunu.class).getServer().getLogger().log(Level.SEVERE, "\u001b[31m" + "Error" + where + ": " + message + "\u001b[34m\n" +
                "--> " + name + " : line " + line + "\n" +
                "\t|\n" +
                line + "\t|\t\u001b[0m" + lineStr + "\n" +
                "\u001b[34m\t|\u001b[0m");
        hadError.put(name, true);
    }

    static void error(Token token, String message, String name) {
        if(token.type == TokenType.EOF) {
            report(token.line, token.start, " at end", message, name);
        } else {
            report(token.line, token.start, " at '" + token.lexeme + "'", message, name);
        }
    }

    static void runtimeError(RuntimeError error, String name) {
        getPlugin(Zunu.class).getServer().getLogger().log(Level.SEVERE, "\u001b[31m" +
                "[line " + error.token.line + "] " + error.getMessage() + "\u001b[0m");
        hadRuntimeError.put(name, true);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
