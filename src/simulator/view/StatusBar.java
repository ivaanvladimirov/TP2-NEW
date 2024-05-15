package simulator.view;

import simulator.control.Controller;
import simulator.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StatusBar extends JPanel implements EcoSysObserver {
    private JLabel timeLabel;
    private JLabel animalCountLabel;
    private JLabel dimensionLabel;

    StatusBar(Controller ctrl) {
        initGUI();
        ctrl.addObserver(this);
    }

    private void initGUI() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setBorder(BorderFactory.createBevelBorder(1));

        //time label initialization and adding to the panel
        timeLabel = new JLabel("Time: ");
        this.add(timeLabel);

        //animal count label initialization and adding to the panel
        animalCountLabel = new JLabel("Total Animals: ");
        this.add(animalCountLabel);

        dimensionLabel = new JLabel("Dimension: ");
        this.add(dimensionLabel);

        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        separator.setPreferredSize(new Dimension(10, 20));
        this.add(separator);
    }

    private void updateTime(double time) {
        timeLabel.setText("Time: " + time);
    }

    private void updateAnimalCount(int count) {
        animalCountLabel.setText("Animal count: " + count);
    }

    private void updateDimensions(int width, int height, int rows, int cols) {
        dimensionLabel.setText("Dimensions:  " + width + "x" + height + " " + rows + "x" + cols);
    }

    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
        updateTime(time);
        updateAnimalCount(animals.size());
        updateDimensions(map.get_width(), map.get_height(), map.get_rows(), map.get_cols());
    }

    @Override
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
        updateTime(time);
        updateAnimalCount(animals.size());
        updateDimensions(map.get_width(), map.get_height(), map.get_rows(), map.get_cols());
    }

    @Override
    public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
        updateAnimalCount(animals.size());
    }

    @Override
    public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
    }

    @Override
    public void open(Component  parent) {

    }

    @Override
    public void onAdvanced(double currentTime, MapInfo mapInfo, List<AnimalInfo> animals, double dt) {
        updateTime(currentTime);
        updateAnimalCount(animals.size());
    }


}
