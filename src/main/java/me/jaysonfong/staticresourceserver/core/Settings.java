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
package me.jaysonfong.staticresourceserver.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import me.jaysonfong.staticresourceserver.exception.ArgumentException;
import me.jaysonfong.staticresourceserver.exception.FileNotFoundException;

/**
 *
 * @author Jayson Fong <contact@jaysonfong.me>
 */
public class Settings {
    
    public static Settings S;
    
    protected Locale locale;
    protected ResourceBundle languageBundle;
    
    protected Path documentRoot;
    protected int serverPort;
    
    protected Properties properties;
    protected List<String> propertiesKeys;
    
    protected final String propertiesFile = "config.properties";
    
    private Settings() {}
    
    public static final boolean setInstance() {
        S = new Settings();
        return S.reload();
    }
    
    public final Locale getLocale() {
        return this.locale;
    }
    
    public final ResourceBundle getLanguageBundle() {
        return this.languageBundle;
    }
    
    public final Path getDocumentRoot() {
        return this.documentRoot;
    }
    
    public final int getServerPort() {
        return this.serverPort;
    }
    
    public boolean reload() {
        boolean success = true;
        try {
            this.parseProperties();
            this.setRunItems();
        } catch (IOException e) {
            success = false;
        }
        return success;
    }
    
    protected void setRunItems() throws ArgumentException {
        this.setLanguage();
        this.setHandlerItems();
    }
    
    protected void setHandlerItems() throws ArgumentException {
        if (!propertiesKeys.contains("documentroot"))
            throw new ArgumentException("e_arg_documentroot_not_found");
        documentRoot = Paths.get(propertyFetch("documentroot"));
        serverPort = (int) propertyFetch("server_port", 8080);
    }
    
    protected void setLanguage() {
        if (properties.contains("language")) {
            if (properties.contains("language_country")) {
                if (properties.contains("language_variant")) {
                    this.locale = new Locale(
                            propertyFetch("language"),
                            propertyFetch("language_country"),
                            propertyFetch("language_variant")
                    );
                } else {
                    this.locale = new Locale(
                            propertyFetch("language"),
                            propertyFetch("language_country")
                    );
                }
            } else {
                this.locale = new Locale(propertyFetch("language"));
            }
        } else {
            this.locale = Locale.US;
        }
        this.languageBundle = ResourceBundle.getBundle("messages", this.locale);
    }
    
    protected void parseProperties() throws ArgumentException {
        properties = new Properties();
        try (InputStream propertiesStream = getClass().getClassLoader().getResourceAsStream(propertiesFile)) {
            if (propertiesStream != null) {
                properties.load(propertiesStream);
                this.setPropertiesKeys();
            } else {
                throw new FileNotFoundException(propertiesFile);
            }
        } catch (IOException e) {
            throw new ArgumentException("e_properties_parse", e);
        }
    }
    
    protected void setPropertiesKeys() {
        this.propertiesKeys = new ArrayList();
        for (Enumeration<Object> e = this.properties.elements(); e.hasMoreElements();)
            propertiesKeys.add(e.nextElement().toString());
    }
    
    protected String propertyFetch(String key) {
        return properties.getProperty(key);
    }
    
    protected Object propertyFetch(String key, Object fallback) {
        return properties.getProperty(key, (String) fallback);
    }
    
}
