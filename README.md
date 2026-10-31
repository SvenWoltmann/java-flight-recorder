# Java Flight Recorder

[![Build](https://github.com/SvenWoltmann/java-flight-recorder/actions/workflows/build.yml/badge.svg)](https://github.com/SvenWoltmann/java-flight-recorder/actions/workflows/build.yml)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=SvenWoltmann_java-flight-recorder&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=SvenWoltmann_java-flight-recorder)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=SvenWoltmann_java-flight-recorder&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=SvenWoltmann_java-flight-recorder)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=SvenWoltmann_java-flight-recorder&metric=security_rating)](https://sonarcloud.io/dashboard?id=SvenWoltmann_java-flight-recorder)

The example program of the HappyCoders.eu article "Java Flight Recorder (JFR)" ([English](https://www.happycoders.eu/java/java-flight-recorder/), [German](https://www.happycoders.eu/de/java/java-flight-recorder/)).

Eight worker threads process randomly generated orders for a given number of seconds. A few spots in the code are written clumsily on purpose, the way they turn up in grown code – the article records the program with the Java Flight Recorder and finds each of them with `jfr view` and `jfr print`.

## Running the example

The example uses Java 25 language features and the JFR annotation `@Contextual`, so it needs Java 25 or newer. Build once with `mvn compile`, then record a run of 60 seconds from the repository root:

```
java -XX:StartFlightRecording:filename=orders.jfr,settings=profile \
     -cp target/classes eu.happycoders.jfr.OrderProcessing 60
```

and open the recording, for example with:

```
jfr view hot-methods orders.jfr
```

The article shows many more ways to record and read: `jcmd`, `JFR.view`, `report-on-exit`, the disk and memory ring buffers, method timing, and `-XX:+DebugNonSafepoints` for exact line numbers in the samples.

## Classes

* `OrderProcessing` – the main class; its only argument is the running time in seconds (default 60)
* `OrderService` – processes one order: parses the quantity, checks the product against a list of blocked products, asks the shipping service for the shipping cost, gets an invoice number, formats the receipt, and keeps it in a cache
* `OrderGenerator` – creates random orders with 50 to 149 lines; on average every fifth quantity comes with a unit, such as `"3 pcs"`
* `ShippingService` – an HTTP server in the same process that simulates a remote shipping-cost service answering after 20 ms
* `ShippingClient` – asks the shipping service with `URL.openStream()`
* `InvoiceNumberGenerator` – a `synchronized` generator whose database round trip is simulated with a sleep of 5 ms
* `OrderEvent` – the custom JFR event `eu.happycoders.Order`, one per order, with the order ID as `@Contextual` field
* `MonitoredOrderProcessing` – runs the order processing and prints every order that takes longer than 80 ms, read live with a `RecordingStream`

## Results

`results/outputs-jdk27.txt` holds the outputs of the recording the article analyzes – `jfr summary` and the `jfr view` tables – recorded on JDK 27 on an Apple M5 Pro.
