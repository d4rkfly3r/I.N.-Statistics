package net.d4rkfly3r.plugins.in_statistics.src;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.d4rkfly3r.plugins.in_statistics.src.server.INStatisticsServer;
import net.d4rkfly3r.plugins.in_statistics.src.server.routes.DefaultRoutes;
import net.d4rkfly3r.plugins.in_statistics.src.server.routes.Route;
import net.d4rkfly3r.plugins.in_statistics.src.server.routes.RouteHandler;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Plugin(id = "in-statistics", name = "I.N. | Statistics", authors = "Joshua Freedman (d4rkfly3r)", url = "https://d4rkfly3r.net")
public class INStatistics {

    public static final int CORE_PORT = 2048;
    public static final byte[] CORE_BIND_IP = {0, 0, 0, 0};
    public static final int CORE_BACKLOG = 50;
    public static final int DEFAULT_CORE_THREADS = 10;
    public static final int MAX_CORE_THREADS = 20;
    public static final int CORE_THREAD_BACKLOG = MAX_CORE_THREADS * 2;
    public static final long CORE_THREAD_KEEP_ALIVE = 5;
    public static final TimeUnit CORE_THREAD_KEEP_ALIVE_TIME_UNIT = TimeUnit.MINUTES;
    public static final String SERVER_NAME = "I.N. | Statistics | Test";


    public final Cause pluginCause = Cause.builder().named("plugin", this).build();


    final INStatisticsServer server = new INStatisticsServer();
    @Inject
    private Logger logger;
    @Inject
    private Game game;
    @Inject
    private EventManager eventManager;
    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path defaultConfig;
    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;
    @Inject
    @ConfigDir(sharedRoot = false)
    private Path privateConfigDir;
    @Inject
    private PluginContainer container;

    @Listener
    public void onGameIntitializationEvent(GameInitializationEvent event) {
        game.getServiceManager().setProvider(this, StatisticService.class, new DefaultStatisticService());
    }

    @Listener
    public void onGamePreIntitializationEvent(GamePreInitializationEvent event) {
        logger.info("Beginning " + container.getName() + " Initialization");
        logger.info("Version: " + container.getVersion().orElse("Unknown"));
        // TODO Read config (esp. database details if needed)
    }

    @Listener
    public void gameStartingServerEvent(GameStartingServerEvent event) {
        DefaultRoutes.getInstance().setRootConsumer((httpHeaderParser, socket, strings) -> {
            System.out.println("Homepage...");
            // TODO Homepage
            try {
                socket.getOutputStream().write(new FileResponse("index.html").getData());
                socket.getOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        Route detailsRoute = new RouteHandler("details", (httpHeaderParser, socket, strings) -> {
            try {
                String jsonData = new Gson().toJson(strings);
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                printWriter.println(jsonData);
                printWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        DefaultRoutes.getInstance().addChild(detailsRoute);
        server.start();
    }

    public class FileResponse {
        private byte[] data;

        public FileResponse(@Nonnull String filename) throws IOException {
            try (InputStream inputStream = INStatistics.class.getResourceAsStream("/assets/" + filename)) {
                byte[] buffer = new byte[4096];
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, read);
                }
                data = byteArrayOutputStream.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Nonnull
        public byte[] getData() {
            return data;
        }
    }
}
