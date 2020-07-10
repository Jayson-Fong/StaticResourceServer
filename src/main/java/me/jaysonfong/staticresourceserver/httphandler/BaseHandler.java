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
package me.jaysonfong.staticresourceserver.httphandler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import me.jaysonfong.staticresourceserver.core.Settings;
import me.jaysonfong.staticresourceserver.exception.ServerIOException;

/**
 *
 * @author Jayson Fong <contact@jaysonfong.me>
 */
public abstract class BaseHandler implements HttpHandler {
    
    public abstract void handleRequest(HttpExchange httpExchange) throws IOException;
    
    @Override
    public final void handle(HttpExchange httpExchange) {
        try {
            handleRequest(httpExchange);
        } catch (ServerIOException e) {
            handleError(httpExchange, e);
        } catch (IOException e) {
            handleError(httpExchange, e);
        }
        
    }
    
    protected void handleError(HttpExchange httpExchange, IOException e) {
        String response = this.getLocaleMessage("e_handler");
        try {
            sendStringResponse(httpExchange, response, 0x1f4);
        } catch (IOException sendException) {
            System.out.println(this.getLocaleMessage("e_handler_double_exception"));
        }
    }
    
    protected void handleError(HttpExchange httpExchange, ServerIOException e) {
        try {
            sendStringResponse(httpExchange, e.getCombinedStatus(), e.getStatusCode());
        } catch (IOException sendException) {
            System.out.println(this.getLocaleMessage("e_handler_double_exception"));
        }
    }
    
    protected void sendStringResponse(HttpExchange httpExchange, String response, int statusCode) throws IOException {
        byte[] responseBytes = response.getBytes();
        sendResponse(httpExchange, responseBytes, statusCode);
    }
    
    protected void sendFileResponse(HttpExchange httpExchange, Path filePath, int statusCode) throws IOException {
        byte[] responseBytes = Files.readAllBytes(filePath);
        sendResponse(httpExchange, responseBytes, statusCode);
    }
    
    protected void sendResponse(HttpExchange httpExchange, byte[] responseBytes, int statusCode) throws IOException {
        httpExchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream outputStream = httpExchange.getResponseBody()) {
            outputStream.write(responseBytes);
            outputStream.flush();
        }
    }
    
    protected String getLocaleMessage(String messageIdentifier) {
        return Settings.S.getLanguageBundle().getString(messageIdentifier);
    }
        
}