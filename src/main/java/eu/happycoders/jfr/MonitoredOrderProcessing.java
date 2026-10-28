package eu.happycoders.jfr;

import java.time.Duration;
import jdk.jfr.consumer.RecordingStream;

public class MonitoredOrderProcessing {

  static void main(String[] args) throws Exception {
    try (RecordingStream stream = new RecordingStream()) {
      stream.enable("eu.happycoders.Order").withThreshold(Duration.ofMillis(80));
      stream.onEvent(
          "eu.happycoders.Order",
          event ->
              System.out.printf(
                  "Slow order %d: %d ms%n",
                  event.getLong("orderId"), event.getDuration().toMillis()));
      stream.startAsync();

      OrderProcessing.main(args);
    }
  }
}
