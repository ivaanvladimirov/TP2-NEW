package simulator.view;

import org.json.JSONObject;
import org.json.JSONTokener;
import simulator.control.Controller;
import simulator.launcher.Main;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ControlPanel extends JPanel {
    private final Controller _ctrl;
    private ChangeRegionsDialog _changeRegionsDialog;
    private JToolBar _toolBar;
    private JFileChooser _fc;
    private boolean _stopped = true;
    private JButton _quitButton;
    private JButton _loadButton;
    private JButton _mapButton;
    private JButton _regionsButton;
    private JButton _runButton;
    private JButton _stopButton;
    private final JSpinner _stepsSpinner = new JSpinner(new SpinnerNumberModel(10000, 1, 100000000, 1));
    private JTextField _deltaTimeField;
    private MapViewer _mapViewer;

    ControlPanel(Controller ctrl) {
        _ctrl = ctrl;
        initGUI();
    }

    private void initGUI() {
        setLayout(new BorderLayout());
        _toolBar = new JToolBar();
        add(_toolBar, BorderLayout.PAGE_START);

        // Load Button
        _loadButton = new JButton();
        _loadButton.setToolTipText("Load File");
        _loadButton.setIcon(new ImageIcon("resources/icons/open.png"));
        _loadButton.addActionListener((e) -> loadFile());
        _toolBar.add(_loadButton);

        // Map Button
        _mapButton = new JButton();
        _mapButton.setToolTipText("Open Map Viewer");
        _mapButton.setIcon(new ImageIcon("resources/icons/viewer.png"));
        _mapButton.addActionListener((e) -> openMapViewer());
        _toolBar.addSeparator();
        _toolBar.add(_mapButton);

        // Regions Button
        _regionsButton = new JButton();
        _regionsButton.setToolTipText("Change Regions");
        _regionsButton.setIcon(new ImageIcon("resources/icons/regions.png"));
        _regionsButton.addActionListener((e) -> openRegionsDialog());
        _toolBar.add(_regionsButton);

        // Run Button
        _runButton = new JButton();
        _runButton.setToolTipText("Run Simulation");
        _runButton.setIcon(new ImageIcon("resources/icons/run.png"));
        _runButton.addActionListener((e) -> {
            try {
                runSimulation();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });
        _toolBar.addSeparator();
        _toolBar.add(_runButton);

        // Stop Button
        _stopButton = new JButton();
        _stopButton.setToolTipText("Stop Simulation");
        _stopButton.setIcon(new ImageIcon("resources/icons/stop.png"));
        _stopButton.addActionListener((e) -> stopSimulation());
        _toolBar.add(_stopButton);
        _toolBar.addSeparator();

        // Steps Spinner
        _toolBar.add(new JLabel("Steps:"));
        _toolBar.addSeparator();

        Dimension fixedSize2 = new Dimension(70, 25);
        _stepsSpinner.setPreferredSize(fixedSize2);
        _stepsSpinner.setMaximumSize(fixedSize2);
        _stepsSpinner.setMinimumSize(fixedSize2);
        _toolBar.add(_stepsSpinner);

        _toolBar.addSeparator();


        // Delta Time Field
        _deltaTimeField = new JTextField();
        Dimension fixedSize = new Dimension(70, 25);
        _deltaTimeField.setPreferredSize(fixedSize);
        _deltaTimeField.setMaximumSize(fixedSize);
        _deltaTimeField.setMinimumSize(fixedSize);

        _deltaTimeField.setToolTipText("Delta Time");
        _deltaTimeField.setText(String.valueOf(Main._dt));
        _toolBar.add(new JLabel("Delta-Time:"));
        _toolBar.addSeparator();
        _toolBar.add(_deltaTimeField);

        // Quit Button
        _toolBar.add(Box.createGlue());
        _toolBar.addSeparator();
        _quitButton = new JButton();
        _quitButton.setToolTipText("Quit");
        _quitButton.setIcon(new ImageIcon("resources/icons/exit.png"));
        _quitButton.addActionListener((e) -> ViewUtils.quit(this));
        _toolBar.add(_quitButton);

        // Initialize file chooser
        _fc = new JFileChooser();
        _fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));

        // Initialize ChangeRegionsDialog
        _changeRegionsDialog = new ChangeRegionsDialog(_ctrl);
    }

    private void loadFile() {
        int returnVal = _fc.showOpenDialog(ViewUtils.getWindow(this));
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = _fc.getSelectedFile();
            try {
                InputStream is = new FileInputStream(file);

                JSONObject jsonFile = new JSONObject(new JSONTokener(is));
                is.close();

                int cols = jsonFile.getInt("cols");
                int rows = jsonFile.getInt("rows");
                int width = jsonFile.getInt("width");
                int height = jsonFile.getInt("height");

                _ctrl.reset(cols, rows, width, height);
                _ctrl.load_data(jsonFile);

            } catch (Exception ex) {
                ViewUtils.showErrorMsg("Error loading file: " + ex.getMessage());
            }
        }
    }

    private void openMapViewer() {
        MapWindow mapWindow = new MapWindow(_ctrl, ViewUtils.getWindow(this));
    }
    private void openRegionsDialog() {
        _changeRegionsDialog.open(ViewUtils.getWindow(this));
    }


    private void runSimulation() throws InterruptedException {
        _stopped = false;
        _runButton.setEnabled(false);
        _loadButton.setEnabled(false);
        _mapButton.setEnabled(false);
        _regionsButton.setEnabled(false);
        _quitButton.setEnabled(false);
        _stopButton.setEnabled(true);
        double dt = Double.parseDouble(_deltaTimeField.getText());
        int steps = (int) _stepsSpinner.getValue();
        run_sim(steps, dt);
    }

    private void stopSimulation() {
        _stopped = true;
    }

    private void run_sim(int n, double dt) throws InterruptedException {

        if (n > 0 && !_stopped) {
            try {
                _ctrl.advance(dt);
                SwingUtilities.invokeLater(() -> {
                    try {
                        run_sim(n - 1, dt);
                        Thread.sleep(20);

                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception e) {
                ViewUtils.showErrorMsg("Error advancing simulation: " + e.getMessage());
                enableButtons();
                _stopped = true;

            }

        }
        else {
            enableButtons();
            _stopped = true;
        }
    }

    private void enableButtons() {
        _runButton.setEnabled(true);
        _loadButton.setEnabled(true);
        _mapButton.setEnabled(true);
        _regionsButton.setEnabled(true);
        _quitButton.setEnabled(true);
        _stopped = true;
    }
}
