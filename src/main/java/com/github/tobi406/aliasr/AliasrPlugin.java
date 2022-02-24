package com.github.tobi406.aliasr;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;

import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Plugin(
        id = "aliasr",
        name = "Aliasr",
        version = "1.1-BETA",
        description = "Simple Alias manager using RegEx",
        authors = { "Tobi406", "pjv99" }
)

public class AliasrPlugin 
{
    private static AliasrPlugin instance;

    private CommandManager commandManager;
    private Logger logger;
    private Toml config;

    private Path folder;

    List<String> registeredCommands = new ArrayList<>();

    @Inject
    public AliasrPlugin(CommandManager commandManager, Logger logger, @DataDirectory final Path folder)
    {
        this.commandManager = commandManager;
        this.logger = logger;
        this.folder = folder;

        this.config = this.loadConfig(folder);

        this.registerCommands();

        instance = this;
    }

    public static AliasrPlugin getInstance()
    {
        return instance;
    }

    private Toml loadConfig(Path path)
    {
        File folder =  path.toFile();
        File file = new File(folder, "config.toml");

        if (!file.getParentFile().exists())
        {
            file.getParentFile().mkdirs();
        }

        if (!file.exists())
        {
            try (InputStream input = getClass().getResourceAsStream("/" + file.getName()))
            {
                if (input != null)
                {
                    Files.copy(input, file.toPath());
                }
                else
                {
                    file.createNewFile();
                }
            }
            catch (IOException exception)
            {
                exception.printStackTrace();
                return null;
            }
        }

        return new Toml().read(file);
    }

    public void reload()
    {
        this.config = this.loadConfig(this.folder);

        this.unregisterCommands();
        this.registerCommands();
    }

    public void registerCommands() {
        // Aliasr Reload command
        CommandMeta aliasrMeta = this.commandManager.metaBuilder("aliasr").build();
        this.commandManager.register(aliasrMeta, new AliasrCommand());
        this.logger.info("Registered plugin command \"aliasr\"");

        // Aliased commands
        List<HashMap<String, String>> aliases = this.config.getList("aliases");

        aliases.forEach(hashMap ->
        {
            CommandMeta meta = this.commandManager.metaBuilder(hashMap.get("name")).build();

            this.commandManager.register(meta, new SimpleCommand()
            {
                private String args = hashMap.get("args");
                private String command = hashMap.get("command");
                private String commandArgs = hashMap.get("commandArgs");

                @Override
                public void execute(final Invocation invocation)
                {
                    CommandSource source = invocation.source();
                    String[] args = invocation.arguments();
                    String joinedArgs = String.join(" ", args);

                    AliasrPlugin.getInstance().commandManager.executeAsync(source, this.command + (args.length > 0 ? (" " + joinedArgs.replaceAll(this.args, this.commandArgs)) : ""));
                }

                @Override
                public boolean hasPermission(final Invocation invocation)
                {
                    // This can always be true due to the fact that this passes off permission responsibility to the aliased command being ran
                    return true;
                }
            });
        });
    }

    public void unregisterCommands()
    {
        this.registeredCommands.forEach(this.commandManager::unregister);
    }
}
