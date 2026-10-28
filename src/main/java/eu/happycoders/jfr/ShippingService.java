package eu.happycoders.jfr;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

/** Simulates a remote shipping-cost service that answers after 20 ms. */
class ShippingService implements AutoCloseable {

  private final HttpServer server;

  private ShippingService(HttpServer server) {
    this.server = server;
  }

  static ShippingService start() throws IOException {
    InetSocketAddress address = new InetSocketAddress(InetAddress.getLoopbackAddress(), 0);
    HttpServer server = HttpServer.create(address, 0);
    server.createContext("/cost", ShippingService::handle);
    server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
    server.start();
    return new ShippingService(server);
  }

  private static void handle(HttpExchange exchange) throws IOException {
    try {
      Thread.sleep(20);
    } catch (InterruptedException _) {
      Thread.currentThread().interrupt();
    }
    byte[] body = "4.99".getBytes(StandardCharsets.US_ASCII);
    exchange.sendResponseHeaders(200, body.length);
    try (OutputStream out = exchange.getResponseBody()) {
      out.write(body);
    }
  }

  URI uri() {
    return URI.create("http://localhost:" + server.getAddress().getPort());
  }

  @Override
  public void close() {
    server.stop(0);
  }
}
