package me.ghostlyzsh.zunu.zunu;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public final class Zunu extends JavaPlugin {
    private static final Interpreter interpreter = new Interpreter();
    static HashMap<String, Boolean> hadError = new HashMap<>();
    static HashMap<String, Boolean> hadRuntimeError = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        String[] pathnames;

        File f = new File("plugins/Suka");
        if(!Files.exists(Paths.get("plugins/Suka"))) {
            f.mkdir();
        }

        pathnames = f.list();
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
        List<Path> filepaths = new ArrayList<>();
        for (String fn: filenames) {
            filepaths.add(Paths.get("plugins/Suka/" + fn));
        }

        List<byte[]> contentStrings = new ArrayList<>();
        for (Path path: filepaths) {
            try {
                contentStrings.add(Files.readAllBytes(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for(int i = 0; i < contentStrings.size(); i++) {
            runFile(contentStrings.get(i), filenames.get(i));
            System.out.println("interpreting");
        }
    }

    private static void runFile(byte[] bytes, String name) {
        hadError.put(name, false);
        hadRuntimeError.put(name, false);
        run(new String(bytes, Charset.defaultCharset()), name);
    }

    private static void run(String source, String name) {
        Scanner scanner = new Scanner(source, name);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens, name);
        List<Stmt> statements = parser.parse();

        if(hadError.get(name)) return;
        if(hadRuntimeError.get(name)) return;

        interpreter.interpreter(statements, name);
    }

    static void error(int line, String message, String name) {
        report(line, "", message, name);
    }

    private static void report(int line, String where, String message, String name) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError.put(name, true);
    }

    static void error(Token token, String message, String name) {
        if(token.type == TokenType.EOF) {
            report(token.line, " at end", message, name);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message, name);
        }
    }

    static void runtimeError(RuntimeError error, String name) {
        System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
        hadRuntimeError.put(name, true);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
