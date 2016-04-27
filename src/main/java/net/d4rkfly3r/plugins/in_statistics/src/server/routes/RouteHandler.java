package net.d4rkfly3r.plugins.in_statistics.src.server.routes;

import net.d4rkfly3r.plugins.in_statistics.src.server.HTTPHeaderParser;
import net.d4rkfly3r.plugins.in_statistics.src.util.TriConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.Socket;
import java.util.List;

public class RouteHandler extends Route {
    public RouteHandler(@Nonnull String key, @Nullable TriConsumer<HTTPHeaderParser, Socket, List<String>> consumer) {
        super(key, consumer);
    }
}
