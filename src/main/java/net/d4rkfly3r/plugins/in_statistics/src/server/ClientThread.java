package net.d4rkfly3r.plugins.in_statistics.src.server;

import net.d4rkfly3r.plugins.in_statistics.src.INStatistics;
import net.d4rkfly3r.plugins.in_statistics.src.server.routes.Route;
import net.d4rkfly3r.plugins.in_statistics.src.server.routes.RouteService;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ClientThread implements Runnable {
    private final Socket client;
    private final RouteService router;

    public ClientThread(@Nonnull Socket client, @Nonnull RouteService router) {
        this.client = client;
        this.router = router;
    }

    @Override
    public void run() {
        try {
            HTTPHeaderParser headers = new HTTPHeaderParser(client.getInputStream());
            headers.parseRequest();
            if (!router.routeAvailable(headers)) {
                error404(client);
            } else if (router.isRouteSpecial(headers)) {
                Route route = router.getSpecialRoute(headers);
                List<String> excess = router.getExcess(headers);
                route.call(headers, client, excess);
            } else {
                Route route = router.getRoute(headers);
                List<String> excess = router.getExcess(headers);
                if (route != null) {
                    PrintWriter printWriter = new PrintWriter(client.getOutputStream());
                    printWriter.println("HTTP/1.1 200 OK");
                    printWriter.println("Date: " + new Date().toString());
                    printWriter.println("Server: " + INStatistics.SERVER_NAME);
                    printWriter.println("Accept-Ranges: bytes");
                    printWriter.println("Content-Type: " + router.getContentType());
                    printWriter.println("Set-Cookie: pyid=" + UUID.randomUUID());
                    printWriter.println();
                    printWriter.flush();
                    route.call(headers, client, excess);
                } else {
                    error404(client);
                }
            }
            client.close();
        } catch (IOException e) {
            try {
                client.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public void error404(Socket client) throws IOException {
        PrintWriter printWriter = new PrintWriter(client.getOutputStream());
        printWriter.println("HTTP/1.1 404 Not Found");
        printWriter.println("Date: " + new Date().toString());
        printWriter.println("Server: " + INStatistics.SERVER_NAME);
        printWriter.println("Accept-Ranges: bytes");
        printWriter.println("Content-Type: text/html");
        printWriter.println();
        printWriter.println("<html>");
        printWriter.println("<Title>404 File Not Found</Title>");
        printWriter.println("<body style='background-color: #2A3132;'>");
        printWriter.println("<p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p>");
        printWriter.println("<div align='center'><center>");
        printWriter.println("<div style='width: 60%;padding: 7px;background-color: #763626;'>");
        printWriter.println("<p align='center'><font color='#FFFFFF' size='6'><strong>404 File Not Found</strong></font></p>");
        printWriter.println("<p><font color='#FFFFFF' size='4'>The Web Server cannot find the requested file or script.  Please check the URL to be sure that it is correct.</font></p>");
        printWriter.println("</div>");
        printWriter.println("</center></div>");
        printWriter.println("</html>");
        printWriter.flush();
    }

    public void stop() {
    }
}
