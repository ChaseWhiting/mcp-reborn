package net.minecraft.server;

import net.minecraft.client.main.GroovyScriptLoader;
import net.minecraft.groovy.GroovyCommand;

public class Scripts {

    public Scripts() {}

    public void loadAll(boolean hide) {
        GroovyScriptLoader.reloadGroovyScripts(GroovyCommand.PATH);
    }
}
