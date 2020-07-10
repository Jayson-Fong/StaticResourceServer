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
package me.jaysonfong.staticresourceserver.exception;

import java.io.IOException;
import me.jaysonfong.staticresourceserver.core.Settings;
import org.apache.commons.httpclient.HttpStatus;

/**
 *
 * @author Jayson Fong <contact@jaysonfong.me>
 */
public class ServerIOException extends IOException {
    
    protected int statusCode = 0x1f4;
    
    public ServerIOException(String messageIdentifier) {
        super(Settings.S.getLanguageBundle().getString(messageIdentifier));
    }
    
    public ServerIOException(String messageIdentifier, Exception e) {
        super(Settings.S.getLanguageBundle().getString(messageIdentifier), e);
    }
    
    public ServerIOException(String messageIdentifier, int statusCode) {
        super(Settings.S.getLanguageBundle().getString(messageIdentifier));
        this.statusCode = statusCode;
    }
    
    public ServerIOException(String messageIdentifier, int statusCode, Exception e) {
        super(Settings.S.getLanguageBundle().getString(messageIdentifier), e);
        this.statusCode = statusCode;
    }
    
    public ServerIOException(String messageIdentifier, int statusCode, String info) {
        super(String.format("%s : %s", Settings.S.getLanguageBundle().getString(messageIdentifier), info));
    }
    
    public ServerIOException(String messageIdentifier, int statusCode, String info, Exception e) {
        super(String.format("%s : %s", Settings.S.getLanguageBundle().getString(messageIdentifier), info), e);
    }
    
    public int getStatusCode() {
        return this.statusCode;
    }
    
    public String getStatusMessage() {
        return HttpStatus.getStatusText(this.statusCode);
    }
    
    public String getCombinedStatus() {
        return String.format("%d - %s", getStatusCode(), getStatusMessage());
    }
    
}
