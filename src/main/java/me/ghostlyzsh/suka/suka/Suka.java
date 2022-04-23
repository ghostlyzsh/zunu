package me.ghostlyzsh.suka.suka;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Suka extends JavaPlugin {
    static HashMap<String, Boolean> hadError = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        String[] pathnames;

        File f = new File("plugins/Suka");

        pathnames = f.list();
        List<String> filenames = new ArrayList<>();
        assert pathnames != null;
        try {
            for(String p : pathnames) {
                if(p.endsWith(".su")) {
                    filenames.add(p);
                }
            }
        } catch(NullPointerException e) {
            System.out.println("Something went wrong in loading files.");
            this.getPluginLoader().disablePlugin(this);
        }
        List<Path> filepaths = new ArrayList<>();
        for (String fn: filenames) {
            filepaths.add(Path.of("plugins/Suka/" + fn));
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
        }
    }

    private static void runFile(byte[] bytes, String name) {
        hadError.put(name, false);
        run(new String(bytes, Charset.defaultCharset()), name);
    }

    private static void run(String source, String name) {
        Scanner scanner = new Scanner(source, name);
        List<Token> tokens = scanner.scanTokens();

        // for now print tokens
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    static void error(int line, String message, String name) {
        report(line, "", message, name);
    }

    private static void report(int line, String where, String message, String name) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError.put(name, true);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
