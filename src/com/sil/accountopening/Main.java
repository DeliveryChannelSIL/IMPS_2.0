package com.sil.accountopening;

import java.io.IOException;
import java.net.URI;
import java.util.Scanner;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import com.sil.filters.CORSFilter;
import com.sil.prop.ConfigurationLoader;
public class Main {
	public static String uri = null;
	public static String BASE_URI = null;
	static {
		uri = ConfigurationLoader.getParameters(false).getProperty("CBS_URI");
		System.out.println("uri::>>>" + uri);
		BASE_URI = uri;
	}
	public static HttpServer startServer() {
		ResourceConfig rc = new ResourceConfig();
		rc.packages("com.sil.ws");
		rc.register(CORSFilter.class);
		return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
	}

	public static void main(String[] args) throws IOException {
		final HttpServer server = startServer();
		System.out.println(String.format("Jersey app started with WADL available at "
				+ "%sapplication.wadl\n Enter \"stop\" without quotes and Hit enter to stop it...", BASE_URI));
		/*try (Scanner sc = new Scanner(System.in)) {
			// HBUtil.getSessionFactory().openSession();
			while (!sc.nextLine().equals("stop")) {
			}
			server.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
}
