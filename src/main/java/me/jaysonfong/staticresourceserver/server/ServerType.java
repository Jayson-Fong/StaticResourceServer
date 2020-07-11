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
import me.jaysonfong.staticresourceserver.httphandler.BaseHandler;
import me.jaysonfong.staticresourceserver.httphandler.ProtectedAssetHandler;
import me.jaysonfong.staticresourceserver.httphandler.StaticAssetHandler;
import me.jaysonfong.staticresourceserver.utils.Language;

/**
 *
 * @author Jayson Fong <contact@jaysonfong.me>
 */
public enum ServerType {
    
    StaticAssetServer(new StaticAssetHandler()),
    ProtectedAssetServer(new ProtectedAssetHandler());
    
    protected HttpHandler handler;
    
    ServerType(HttpHandler httpHandler) {
        this.handler = httpHandler;
    }
    
    public HttpHandler getHandler(Server conf) {
        if (handler instanceof BaseHandler) {
            BaseHandler rHandler = ((BaseHandler) handler).newInstance();
            rHandler.setServerConf(conf);
        }
        try {
            return this.handler.getClass().getConstructor().newInstance();
        } catch (Exception e) {
            System.out.println(Language.getLocaleMessage("e_handler_create"));
        }
        return handler;
    }
    
}