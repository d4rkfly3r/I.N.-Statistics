package net.d4rkfly3r.plugins.in_statistics.src.server;

import net.d4rkfly3r.plugins.in_statistics.src.INStatistics;
import net.d4rkfly3r.plugins.in_statistics.src.server.routes.DefaultRoutes;
import net.d4rkfly3r.plugins.in_statistics.src.server.routes.Routes;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

public class INStatisticsServer extends Thread implements Runnable {

    private boolean running;

    private ThreadPoolExecutor threadPoolExecutor;
    private HashMap<SocketAddress, ClientThread> clientThreads;

    public INStatisticsServer() {
        super("IN Statistics Server");
        setRunning(true);
        clientThreads = new HashMap<>(INStatistics.DEFAULT_CORE_THREADS);
        ThreadFactory threadFactory = r -> new Thread(r, "ClientThread");
        threadPoolExecutor = new ThreadPoolExecutor(INStatistics.DEFAULT_CORE_THREADS, INStatistics.MAX_CORE_THREADS, INStatistics.CORE_THREAD_KEEP_ALIVE, INStatistics.CORE_THREAD_KEEP_ALIVE_TIME_UNIT, new ArrayBlockingQueue<>(INStatistics.CORE_THREAD_BACKLOG), threadFactory);

    }

    @Override
    public void run() {
        while (isRunning()) {
            try (ServerSocket serverSocket = new ServerSocket(INStatistics.CORE_PORT, INStatistics.CORE_BACKLOG, InetAddress.getByAddress(INStatistics.CORE_BIND_IP))) {
                threadPoolExecutor.allowCoreThreadTimeOut(true);
                while (!serverSocket.isClosed()) {
                    try {
                        Socket client = serverSocket.accept();
                        ClientThread clientThread = new ClientThread(client, DefaultRoutes.getInstance());
                        clientThreads.put(client.getRemoteSocketAddress(), clientThread);
                        threadPoolExecutor.execute(clientThread);
                    } catch (Exception general) {
                        general.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
