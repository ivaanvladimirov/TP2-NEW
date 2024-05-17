package simulator.model;

import java.util.List;

public interface RegionInfo extends JSONable {
    List<AnimalInfo> getAnimalsInfo();
}
