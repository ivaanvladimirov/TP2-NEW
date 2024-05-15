package simulator.model;

import java.awt.*;
import java.util.List;

public interface EcoSysObserver {
    void onRegister(double time, MapInfo map, List<AnimalInfo> animals);
    void onReset(double time, MapInfo map, List<AnimalInfo> animals);
    void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a);
    void onRegionSet(int row, int col, MapInfo map, RegionInfo r);
    void open(Component parent);
    void onAdvanced(double currentTime, MapInfo mapInfo, List<AnimalInfo> animals, double dt);

}
