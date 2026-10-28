package eu.happycoders.jfr;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderProcessing {

  private static final int WORKERS = 8;

  static void main(String[] args) throws Exception {
    long seconds = args.length > 0 ? Long.parseLong(args[0]) : 60;
    Instant end = Instant.now().plus(Duration.ofSeconds(seconds));

    try (ShippingService shippingService = ShippingService.start()) {
      OrderService orderService = new OrderService(shippingService.uri());

      try (ExecutorService executor = Executors.newFixedThreadPool(WORKERS)) {
        for (int i = 0; i < WORKERS; i++) {
          OrderGenerator generator = new OrderGenerator(i);
          executor.execute(() -> orderService.processUntil(end, generator));
        }
      }

      System.out.printf("Processed %,d orders%n", orderService.processedOrders());
    }
  }
}
