package eu.happycoders.jfr;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class OrderGenerator {

  private static final List<String> ITEMS = createItems();

  private final Random random;
  private final int worker;
  private long sequence;

  OrderGenerator(int worker) {
    this.random = new Random(worker);
    this.worker = worker;
  }

  Order next() {
    long id = ++sequence * 100 + worker;
    String productId = "P-" + random.nextInt(OrderService.PRODUCT_COUNT);
    String quantity = randomQuantity();

    int lineCount = 50 + random.nextInt(100);
    List<String> lines = new ArrayList<>(lineCount);
    for (int i = 0; i < lineCount; i++) {
      lines.add(ITEMS.get(random.nextInt(ITEMS.size())));
    }

    return new Order(id, productId, quantity, lines);
  }

  // every fifth quantity comes from a form field with a unit, e.g. "3 pcs"
  private String randomQuantity() {
    int quantity = 1 + random.nextInt(9);
    return random.nextInt(5) == 0 ? quantity + " pcs" : Integer.toString(quantity);
  }

  private static List<String> createItems() {
    Random random = new Random(0);
    List<String> items = new ArrayList<>();
    for (int i = 0; i < 1_000; i++) {
      items.add(
          "Item %05d  %3d x %7.2f EUR"
              .formatted(random.nextInt(100_000), 1 + random.nextInt(9), random.nextDouble(100)));
    }
    return List.copyOf(items);
  }
}
