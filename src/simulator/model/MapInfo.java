package simulator.model;

public interface MapInfo extends JSONable, Iterable<MapInfo.RegionData> {
    record RegionData(int row, int col, RegionInfo r) {
    }
    int get_cols();

    int get_rows();

    int get_width();

    int get_height();

    int get_region_width();

    int get_region_height();
}
