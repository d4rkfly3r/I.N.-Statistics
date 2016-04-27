package net.d4rkfly3r.plugins.in_statistics.src;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.nio.file.Path;

/**
 * Created by Joshua Freedman on 4/26/2016.
 * Project: SpongeForge
 */
@Plugin(id = "in-statistics", name = "I.N. | Statistics", authors = "Joshua Freedman (d4rkfly3r)", url = "https://d4rkfly3r.net")
public class INStatistics {

    public final Cause pluginCause = Cause.builder().named("plugin", this).build();

    @Inject
    private Logger logger;

    @Inject
    private Game game;

    @Inject
    private EventManager eventManager;

    @Inject
    @ConfigDir(sharedRoot = true)
    private Path configDirectory;

    @Inject
    private PluginContainer container;

    @Listener
    public void onGameIntitializationEvent(GameInitializationEvent event) {

    }

    @Listener
    public void onGamePreIntitializationEvent(GamePreInitializationEvent event) {
        logger.info("Beginning " + container.getName() + " Initialization");
        logger.info("Version: " + container.getVersion().orElse("Unknown"));
        // TODO Read config (esp. database details if needed)
    }
}
