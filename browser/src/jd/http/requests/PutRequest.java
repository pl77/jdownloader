package jd.http.requests;

import java.io.IOException;
import java.net.URL;

import jd.http.Request;
import jd.parser.html.Form;

import org.appwork.utils.net.httpconnection.HTTPConnection.RequestMethod;

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
    public PutRequest cloneRequest() {
        return new PutRequest(this);
    }

    @Override
    public void preRequest() throws IOException {
        super.preRequest();
        this.httpConnection.setRequestMethod(RequestMethod.PUT);
    }

}
