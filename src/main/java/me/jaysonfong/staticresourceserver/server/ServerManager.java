/*
 * The MIT License
 *
 * Copyright 2020 Jayson Fong <contact@jaysonfong.me>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package me.jaysonfong.staticresourceserver.server;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import me.jaysonfong.staticresourceserver.server.parser.ServerParser;
import me.jaysonfong.staticresourceserver.utils.Language;

/**
 *
 * @author Jayson Fong <contact@jaysonfong.me>
 */
public class ServerManager implements Runnable {
    
    public static ServerManager S;
    
    protected List<HttpServer> httpServers;
    
    protected ServerParser parser;
    
    protected ServerManager() {
        httpServers = new ArrayList();
    }
    
    public static final ServerManager setInstance() {
        S = new ServerManager();
        return S;
    }
    
    public ServerManager setParser(ServerParser parser) {
        this.parser = parser;
        return this;
    }
    
    public void createServers(Server[] servers) {
        for (Server server : servers) configureServer(server);
    }
    
    @Override
    public void run() {
        if (parser != null) parser.setServers(httpServers);
        httpServers.forEach(httpServer -> {
            httpServer.start();
        });
    }
    
    public void stop() {
        this.stopServers(0b0);
    }
    
    public void stopServers(int delay) {
        httpServers.forEach(httpServer -> {
            httpServer.stop(delay);
        });
    }
    
    protected void configureServer(Server conf) {
        int port = conf.getPort();
        try {
            HttpServer server = HttpServer.create(
                new InetSocketAddress(port), 0
            );
            server.setExecutor(getExecutor(conf.getThreads()));
            this.setContext(server, conf);
            httpServers.add(server);
        } catch (IOException e) {
            System.out.println(Language.getLocaleMessage("e_server_start", port));
        }
    }
    
    protected ExecutorService getExecutor(int threads) {
        return Executors.newFixedThreadPool(threads);
    }
    
    protected void setContext(HttpServer server, Server conf) {
        for (String path : conf.getPaths()) 
            server.createContext(path, this.getHandler(conf));
    }
    
    protected HttpHandler getHandler(Server conf) {
        return conf.type.getHandler(conf);
    }
    
}
