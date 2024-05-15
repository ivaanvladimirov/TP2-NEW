package simulator.factories;

import org.json.JSONObject;
import simulator.model.DefaultRegion;
import simulator.model.Region;

/**
 * Builder for the DefaultRegion class object.
 */
public class DefaultRegionBuilder extends Builder<Region> {
    public DefaultRegionBuilder() {
        super("default", "Infinite food supply");
    }

    @Override
    protected Region create_instance(JSONObject data) throws IllegalArgumentException {
        return new DefaultRegion();
    }

}
