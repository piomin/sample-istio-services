package pl.piomin.services.callme.event;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Name("ProcessingEvent")
@Category("Custom Events")
@Label("Processing Time")
public class ProcessingEvent extends Event {
    @Label("Event ID")
    private Integer id;

    public ProcessingEvent(Integer id) {
        this.id = id;
    }

    @Label("Event ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
