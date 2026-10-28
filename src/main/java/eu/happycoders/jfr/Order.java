package eu.happycoders.jfr;

import java.util.List;

public record Order(long id, String productId, String quantity, List<String> lines) {}
