package eu.happycoders.jfr;

class InvoiceNumberGenerator {

  private long lastNumber;

  // reads, increments, and stores the last number in the database
  synchronized long next() throws InterruptedException {
    Thread.sleep(5); // simulated database round trip
    return ++lastNumber;
  }
}
