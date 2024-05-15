package simulator.factories;

import org.json.JSONObject;

import java.util.List;

/**
 * Factory class for creating instances of a specific type.
 *
 * @param <T> Type of object to create
 */
public interface Factory<T> {
    List<JSONObject> get_info();

    T createInstance(JSONObject info) throws IllegalArgumentException;
}