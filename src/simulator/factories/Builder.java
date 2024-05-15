package simulator.factories;

import org.json.JSONObject;

/**
 * Abstract class for creating instances of a specific type.
 *
 * @param <T> Type of object to create
 */
public abstract class Builder<T> {
    private final String _type_tag;
    private final String _desc;

    public Builder(String type_tag, String desc) {
        if (type_tag == null || desc == null || type_tag.isBlank() ||
                desc.isBlank())
            throw new IllegalArgumentException("Invalid type/desc");

        _type_tag = type_tag;
        _desc = desc;
    }

    /**
     * Retrieves the type tag associated with this builder.
     *
     * @return Type tag
     */
    public String get_type_tag() {
        return _type_tag;
    }

    /**
     * Retrieves information about this builder.
     *
     * @return JSON object containing builder information
     */
    public JSONObject get_info() {
        JSONObject info = new JSONObject();
        info.put("type", _type_tag);
        info.put("desc", _desc);
        JSONObject data = new JSONObject();
        fill_in_data(data);
        info.put("data", data);
        return info;
    }

    /**
     * Fills in additional data into the provided JSON object.
     *
     * @param o JSON object to fill in data
     */
    protected void fill_in_data(JSONObject o) {
    }

    @Override
    public String toString() {
        return _desc;
    }

    /**
     * Creates an instance based on the provided JSON data.
     *
     * @param data JSON object containing data for the instance creation
     * @return Instance of type T
     * @throws IllegalArgumentException If the provided JSON data is invalid
     */
    protected abstract T create_instance(JSONObject data) throws IllegalArgumentException;
}