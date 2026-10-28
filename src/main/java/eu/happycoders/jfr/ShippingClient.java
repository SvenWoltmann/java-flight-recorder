package eu.happycoders.jfr;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;

class ShippingClient {

  private final URI serviceUri;

  ShippingClient(URI serviceUri) {
    this.serviceUri = serviceUri;
  }

  BigDecimal shippingCost(Order order) throws IOException {
    URI uri = serviceUri.resolve("/cost?order=" + order.id());
    try (InputStream in = uri.toURL().openStream()) {
      return new BigDecimal(new String(in.readAllBytes(), StandardCharsets.US_ASCII));
    }
  }
}
