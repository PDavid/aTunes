package net.sourceforge.atunes.plugins.webinterface;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.http.HttpServerConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

public class WebServer {

	private static Thread listener;
	
	public static void start(int port, String pubResourceDir, String templateResourceDir, String baseDir) throws Exception {
        listener = new HttpListener(port, pubResourceDir, templateResourceDir, baseDir);
        listener.setDaemon(false);
        listener.start();
	}
	
	public static void stop() {
		listener.interrupt();
		listener = null;
	}

	private static class HttpListener extends Thread {
		
        private ServerSocket serversocket;
        
        private HttpParams parameters;
        
        private HttpService httpService;
		
		HttpListener(int port, String pubResourceDir, String templateResourceDir, String baseDir) throws IOException {
			super();
			this.serversocket = new ServerSocket(port);
			
            // Set up request handlers
            HttpRequestHandlerRegistry registry = new HttpRequestHandlerRegistry();
            registry.register("*", new StaticHttpHandler(pubResourceDir));
            registry.register("/velocity", new VelocityHttpHandler(templateResourceDir, baseDir));
            registry.register("/images", new ImagesHttpHandler());

            this.parameters = new BasicHttpParams();
            this.parameters
            .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
            .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
            .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
            .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
            .setParameter(CoreProtocolPNames.ORIGIN_SERVER, "HttpComponents/1.1");

            // Set up the HTTP protocol processor
            BasicHttpProcessor httpproc = new BasicHttpProcessor();
            httpproc.addInterceptor(new ResponseDate());
            httpproc.addInterceptor(new ResponseServer());
            httpproc.addInterceptor(new ResponseContent());
            httpproc.addInterceptor(new ResponseConnControl());

            // Set up the HTTP service
            this.httpService = new HttpService(httpproc, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory());
            this.httpService.setHandlerResolver(registry);
		}
		
		@Override
		public void run() {
            while (!Thread.interrupted()) {
                try {
                    // Set up HTTP connection
                    Socket socket = this.serversocket.accept();
                    DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
                    conn.bind(socket, this.parameters);

                    // Start worker thread
                    Thread t = new WorkerThread(this.httpService, conn);
                    t.start();
                } catch (InterruptedIOException ex) {
                    break;
                } catch (IOException e) {
                    System.err.println("I/O error initialising connection thread: " 
                            + e.getMessage());
                    break;
                }
            }
		}
	}
	
    static class WorkerThread extends Thread {

        private final HttpService httpservice;
        private final HttpServerConnection conn;
        
        public WorkerThread(final HttpService httpservice, final HttpServerConnection conn) {
            super();
            this.httpservice = httpservice;
            this.conn = conn;
        }
        
        public void run() {
            HttpContext context = new BasicHttpContext(null);
            try {
                while (!Thread.interrupted() && this.conn.isOpen()) {
                    this.httpservice.handleRequest(this.conn, context);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    this.conn.shutdown();
                } catch (IOException ignore) {}
            }
        }
    }
}
