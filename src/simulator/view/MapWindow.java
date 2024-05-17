package simulator.view;

import simulator.control.Controller;
import simulator.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

public class MapWindow extends JFrame implements EcoSysObserver{
    private final Controller _ctrl;
    private AbstractMapViewer _viewer;
    private final Frame _parent;

    public MapWindow(Controller ctrl, Frame parent) {
        super("[MAP VIEWER]");
        _ctrl = ctrl;
        _parent = parent;
        initGUI();
        _ctrl.addObserver(this);
    }

    private void initGUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);
        _viewer = new MapViewer(_ctrl);
        mainPanel.add(_viewer, BorderLayout.CENTER);

        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                _ctrl.removeObserver(MapWindow.this);
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            } });
        pack();
        if (_parent != null)
            setLocation(
                    _parent.getLocation().x + _parent.getWidth()/2 - getWidth()/2,
                    _parent.getLocation().y + _parent.getHeight()/2 - getHeight()/2);
        setResizable(false);
        setVisible(true);
    }
    @Override
    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
        SwingUtilities.invokeLater(() -> {
            _viewer.reset(time, map, animals);
            pack();
        });
    }


    @Override
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
        SwingUtilities.invokeLater(() -> {
            _viewer.reset(time, map, animals);
            pack();
        });
    }

    @Override
    public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {

    }

    @Override
    public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {

    }

    @Override
    public void open(Component parent) {

    }

    @Override
    public void onAdvanced(double currentTime, MapInfo mapInfo, List<AnimalInfo> animals, double dt) {
        SwingUtilities.invokeLater(() -> {
            _viewer.update(animals, currentTime);
        });
    }
}
