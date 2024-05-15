package simulator.view;

import simulator.control.Controller;
import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.MapInfo;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Timer;

public class MapViewer extends AbstractMapViewer{

	private int _width;
	private int _height;

	private int _rows;
	private int _cols;

	int _region_width;
	int _region_height;

	Animal.State _currState;
	private int i = 0;

	volatile private Collection<AnimalInfo> _objs;
	volatile private Double _time;

	private static class SpeciesInfo {
		private Integer _count;
		private Color _color;

		SpeciesInfo(Color color) {
			_count = 0;
			_color = color;
		}
	}

	Map<String, SpeciesInfo> _kindsInfo = new HashMap<>();

	private Font _font = new Font("Arial", Font.BOLD, 12);

	private boolean _showHelp;

	public MapViewer(Controller ctrl) {
		initGUI();
	}

	private void initGUI() {

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) {
					case 'H':
					case 'h':
						_showHelp = !_showHelp;
						repaint();
						break;
					case 'S':
					case 's':
						Animal.State[] values = Animal.State.values();

						if(i < values.length){
							_currState = values[i];
							i++;
						}
						else{
							_currState = null;
							i = 0;
						}

						repaint();
					default:
				}
			}

		});

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				requestFocus();
			}
		});

		_currState = null;
		_showHelp = true;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D gr = (Graphics2D) g;
		gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g.setFont(_font);

		gr.setBackground(Color.WHITE);
		gr.clearRect(0, 0, _width, _height);

		if (_objs != null)
			drawObjects(gr, _objs, _time);

		if (_showHelp) {
			gr.setColor(Color.BLACK);
			gr.drawString("h: toggle help, s: change state", 10, 10);
		}
	}

	private boolean visible(AnimalInfo a) {
		return a.get_state() == _currState || _currState == null;
	}

	private void drawObjects(Graphics2D g, Collection<AnimalInfo> animals, Double time) {
		for (int i = 0; i < _rows; i++) {
			for (int j = 0; j < _cols; j++) {
				g.setColor(Color.LIGHT_GRAY);
				g.drawRect(j * _region_width, i * _region_height, _region_width, _region_height);
			}
		}
		// Draw the animals
		for (AnimalInfo a : animals) {

			// If not visible, skip iteration
			if (!visible(a))
				continue;

			// Species information of 'a'
			SpeciesInfo esp_info = _kindsInfo.get(a.get_genetic_code());
			// Add an entry to the map if esp_info is null
			if (esp_info == null) {
				Color col = ViewUtils.get_color(a.get_genetic_code());
				esp_info = new SpeciesInfo(col);
				_kindsInfo.put(a.get_genetic_code(), esp_info);
			}

			// Increment the species counter
			esp_info._count++;
			g.setColor(esp_info._color);
			g.fillOval((int) a.get_position().getX(), (int) a.get_position().getY(), (int) a.get_age() + 3, (int) a.get_age() + 3);
		}

		// Draw the visible state label, if not null
		if (_currState != null) {
			g.setColor(Color.BLACK);
			drawStringWithRect(g, 10, 560, "State: " + _currState);
		}

		// Draw the time label
		// Use String.format("%.3f", time) to write only 3 decimals
		if (time != null) {
		g.setColor(Color.BLACK);
		drawStringWithRect(g, 10, 540, "Time: " + String.format("%.3f", time));
		}


		// Draw the information of all species
		// At the end of each iteration, set the species counter to 0 to reset it
		int i = 0;
		for (Map.Entry<String, SpeciesInfo> entry : _kindsInfo.entrySet()) {
			SpeciesInfo info = entry.getValue();
			g.setColor(info._color);
			drawStringWithRect(g, 10, 500 + i * 20, entry.getKey() + ": " + info._count);
			info._count = 0;
			i++;
		}
	}

	void drawStringWithRect(Graphics2D g, int x, int y, String s) {
		Rectangle2D rect = g.getFontMetrics().getStringBounds(s, g);
		g.drawString(s, x, y);
		g.drawRect(x - 1, y - (int) rect.getHeight(), (int) rect.getWidth() + 1, (int) rect.getHeight() + 5);
	}

	@Override
	public void update(List<AnimalInfo> objs, Double time) {
		_objs = objs;
		_time = time;
		repaint();
	}

	@Override
	public void reset(double time, MapInfo map, List<AnimalInfo> animals) {
		this._cols = map.get_cols();
		this._rows = map.get_rows();
		this._width = map.get_width();
		this._height = map.get_height();
		this._region_width = _width / _cols;
		this._region_height = _height / _rows;

		setPreferredSize(new Dimension(map.get_width(), map.get_height()));

		update(animals, time);
	}

}
