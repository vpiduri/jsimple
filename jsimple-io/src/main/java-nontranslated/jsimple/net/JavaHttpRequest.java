package jsimple.net;

import jsimple.io.JSimpleOutputStreamOnJavaStream;
import jsimple.io.JavaIOUtils;
import jsimple.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Java platform implementation of platform independent HttpRequest.  This implementation is basically a wrapper around
 * the request part of HttpURLConnection.
 *
 * @author Bret Johnson
 * @since 10/6/12 12:58 AM
 */
public class JavaHttpRequest extends HttpRequest {
    private HttpURLConnection httpUrlConnection;
    private OutputStream bodyStream;

    public JavaHttpRequest(String url) {
        // System.getProperty("http.keepAlive") defaults to true.  We don't change it here as the doc is a bit unclear
        // exactly when that property is checked for HttpURLConnection.  The app can set it to something else if they
        // want different behavior.
        //System.setProperty("http.keepAlive", keepAlive ? "true" : "false");

        try {
            httpUrlConnection = (HttpURLConnection) new URL(url).openConnection();
        } catch (java.io.IOException e) {
            throw new jsimple.io.IOException(e);
        }
    }

    @Override public void setMethod(String method) {
        try {
            httpUrlConnection.setRequestMethod(method);
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public void setTimeout(int timeoutInMillis) {
        httpUrlConnection.setConnectTimeout(timeoutInMillis);
        httpUrlConnection.setReadTimeout(timeoutInMillis);
    }

    @Override public void setHeader(String name, String value) {
        httpUrlConnection.setRequestProperty(name, value);
    }

    @Override public String getHeader(String name) {
        return httpUrlConnection.getRequestProperty(name);
    }

    @Override public OutputStream createRequestBodyStream() {
        if (bodyStream == null) {
            try {
                httpUrlConnection.setDoOutput(true);
                bodyStream = new JSimpleOutputStreamOnJavaStream(httpUrlConnection.getOutputStream());
            } catch (java.io.IOException e) {
                throw JavaIOUtils.jSimpleExceptionFromJavaIOException(e);
            }
        }

        return bodyStream;
    }

    @Override public HttpResponse send() {
        // TODO: The doc seems to say that, for example, for a 404 error the connect call will throw a FileNotFoundException, and the caller can use getErrorStream to read the bod.  Test that & change to catch such exceptions here, so caller gets a valid response object
        // Scribe called getErrorStream when response code not in: return getCode() >= 200 && getCode() < 400;

        try {
            httpUrlConnection.connect();
            return new JavaHttpResponse(httpUrlConnection);
        } catch (java.io.IOException e) {
            throw JavaIOUtils.jSimpleExceptionFromJavaIOException(e);
        }
    }

    public static class JavaHttpRequestFactory implements HttpRequestFactory {
        @Override public HttpRequest createHttpRequest(String url) {
            return new JavaHttpRequest(url);
        }
    };
}