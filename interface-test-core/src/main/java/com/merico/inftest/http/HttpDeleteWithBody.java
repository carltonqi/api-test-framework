package com.merico.inftest.http;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

public class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase{
    public static final String METHOD_NAME="DELETE";
    @Override
    public String getMethod() {
        return METHOD_NAME;
    }

    public HttpDeleteWithBody(final String url){
        super();
        setURI(URI.create(url));
    }

    public HttpDeleteWithBody(){
        super();
    }

    public HttpDeleteWithBody(final URI uri){
        super();
        setURI(uri);

    }

}
