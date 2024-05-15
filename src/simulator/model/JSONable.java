package simulator.model;

import org.json.JSONObject;

public interface JSONable {
    default JSONObject as_JSON() {
        return new JSONObject();
    }
}
