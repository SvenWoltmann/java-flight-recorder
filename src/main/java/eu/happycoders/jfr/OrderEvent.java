package eu.happycoders.jfr;

import jdk.jfr.Contextual;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Name("eu.happycoders.Order")
@Label("Order")
class OrderEvent extends Event {

  @Label("Order ID")
  @Contextual
  long orderId;
}
