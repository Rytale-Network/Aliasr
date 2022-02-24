package com.github.tobi406.aliasr;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
public class AliasrCommand implements SimpleCommand {

    // @Override
    // public void execute(@NonNull CommandSource source, @NonNull String[] args) {
    //     if (source.hasPermission("aliasr.reload")) AliasrPlugin.getInstance().reload();
    //     source.sendMessage(TextComponent.of(
    //             (source.hasPermission("aliasr.reload") ? "Reloaded " : "Running ")).append(
    //             TextComponent.of("Aliasr").color(NamedTextColor.AQUA)).append(
    //             TextComponent.of(" version ")).append(
    //             TextComponent.of("1.0-BETA").color(NamedTextColor.AQUA))
    //     );
    // }

    @Override
    public void execute(final Invocation invocation)
    {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (args.length > 0 && args[0].equalsIgnoreCase("reload"))
        {
            AliasrPlugin.getInstance().reload();
            source.sendMessage(Component.text("Reloaded Aliasr version 1.1-BETA").color(NamedTextColor.AQUA));
        }
    }

    @Override
    public boolean hasPermission(final Invocation invocation)
    {
        return invocation.source().hasPermission("aliasr.reload");
    }

    @Override
    public List<String> suggest(final Invocation invocation)
    {
        ArrayList<String> suggestions = new ArrayList<String>();
        suggestions.add("reload");
        return suggestions;
    }
}
