package map.gdsc_2023;

import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class FSHazard {
    String description, image;
    GeoPoint location;
    Map<String, Object> tags = new HashMap<>();
    Map<String, Object> severity = new HashMap<>();

    public FSHazard() {}

    public FSHazard(
            String description,
            String image,
            GeoPoint location,
            Map<String, Object> tags,
            Map<String, Object> severity) {
        this.description = description;
        this.image = image;
        this.location = location;
        this.tags = tags;
        this.severity = severity;
    }

    public String getDescription() {
        return this.description;
    }

    public String getImage() {
        return this.image;
    }

    public GeoPoint getLocation() {
        return this.location;
    }

    public Map<String, Object> getTags() {
        return this.tags;
    }

    public Map<String, Object> getSeverity() {
        return this.severity;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public void setTags(Map<String, Object> tags) {
        this.tags = tags;
    }

    public void setSeverity(Map<String, Object> severity) {
        this.severity = severity;
    }
}
