package eu.europa.ec.comp.elen.notice.remote;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class NoticesRequest {
    private List<String> coordinates;
    private String renderer;
    private Map<String, String> options = new HashMap<>();

    public List<String> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<String> coordinates) {
        this.coordinates = coordinates;
    }

    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "NoticesRequest{" +
                "coordinates=" + coordinates +
                ", renderer='" + renderer + '\'' +
                ", options='" + options + '\'' +
                '}';
    }
}
