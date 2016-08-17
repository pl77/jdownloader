package jd.http.requests;

import java.io.IOException;
import java.net.URL;

import org.appwork.utils.net.httpconnection.HTTPConnection.RequestMethod;

import jd.http.Request;
import jd.parser.html.Form;

public class PutRequest extends PostRequest {
    public PutRequest(final Form form) throws IOException {
        super(form);
    }

    public PutRequest(final Request cloneRequest) {
        super(cloneRequest);
    }

    public PutRequest(final String url) throws IOException {
        super(url);
    }

    public PutRequest(final URL url) throws IOException {
        super(url);
    }

   

    @Override
    protected RequestMethod getRequestMethod() {
        return RequestMethod.PUT;
    }

    @Override
    protected PostRequest cloneRequestRaw() {
        return new PutRequest(this);
    }

  
}
