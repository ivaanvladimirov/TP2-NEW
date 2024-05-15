package simulator.factories;

import org.json.JSONObject;

import java.util.*;

/**
 * Factory class for creating instances of a specific type.
 *
 * @param <T> Type of object to create
 */
public class BuilderBasedFactory<T> implements Factory<T> {
    private final Map<String, Builder<T>> _builders;
    private final List<JSONObject> _builders_info;

    public BuilderBasedFactory() {
        _builders = new HashMap<>();
        _builders_info = new LinkedList<JSONObject>();
    }

    /**
     * Creates a new factory with the provided builders.
     *
     * @param builders
     */
    public BuilderBasedFactory(List<Builder<T>> builders) {
        this();
        for (Builder<T> b : builders) {
            add_builder(b);
        }
    }

    /**
     * Adds a new builder to the factory.
     *
     * @param b
     */
    public void add_builder(Builder<T> b) {
        // add an entry "b.getTag() |-> b" to _builders
        _builders.put(b.get_type_tag(), b);

        // add b.get_info() to _builders_info
        _builders_info.add(b.get_info());
    }

    /**
     * Creates an instance of the specified type using the provided info.
     *
     * @param info JSON object containing the type and data for the instance creation
     * @return Instance of type T
     * @throws IllegalArgumentException
     */
    @Override
    public T createInstance(JSONObject info) throws IllegalArgumentException {
        if (info == null)
            throw new IllegalArgumentException("'info' cannot be null");

        // Get the type from the info JSONObject
        String type = info.getString("type");

        // Check if a builder exists for this type
        if (_builders.containsKey(type)) {
            Builder<T> builder = _builders.get(type);

            // Get the data from the info JSONObject, or create a new JSONObject if it doesn't exist
            JSONObject data = info.has("data") ? info.getJSONObject("data") : new JSONObject();

            // Create an instance using the builder and return it
            T instance = builder.create_instance(data);
            if (instance != null) {
                return instance;
            }
        }

        // If no builder is found or the result is null, throw an exception
        throw new IllegalArgumentException("Unrecognized 'info':" + info);
    }

    /**
     * Retrieves information about the builders in this factory.
     *
     * @return List of JSON objects containing builder information
     */
    @Override
    public List<JSONObject> get_info() {
        return Collections.unmodifiableList(_builders_info);
    }

}
