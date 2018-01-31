package gui.panel.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import domains.DataChangeListener;
import domains.PersistentObject;
import domains.Points.Point;

@SuppressWarnings("serial")
public class PointDialog extends EntityDialog<Point> {

	public PointDialog(DataChangeListener dataChangeListener) {
		super(dataChangeListener);
	}

	private JTextField txtLat;
	private JTextField txtLon;

	@Override
	public void composeEntityElements(JPanel panel, PersistentObject entity) {
		Point point = (Point) entity;
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		JLabel lblId = new JLabel("Id");
		lblId.setVisible(point.id != 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 10, 0, 0);
		gbc.weightx = 1;
		panel.add(lblId, gbc);

		JTextField txtId = new JTextField();
		txtId.setVisible(point.id != 0);
		txtId.setText(String.valueOf(point.id));
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 3;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 0, 0, 10);
		gbc.weightx = 1;
		txtId.setEditable(false);
		panel.add(txtId, gbc);

		JLabel lblLat = new JLabel("Latitude");
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(0, 10, 0, 0);
		gbc.weightx = 1;
		panel.add(lblLat, gbc);

		txtLat = new JTextField(String.valueOf(point.lat), 6);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 3;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.insets = new Insets(5, 0, 0, 10);
		gbc.weightx = 1;
		panel.add(txtLat, gbc);

		JLabel lblLon = new JLabel("Longitude");
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.insets = new Insets(0, 10, 0, 0);
		gbc.weightx = 1;
		panel.add(lblLon, gbc);

		txtLon = new JTextField(String.valueOf(point.lon));
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 3;
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.insets = new Insets(5, 0, 5, 10);
		gbc.weightx = 1;
		panel.add(txtLon, gbc);

	}

	@Override
	public PersistentObject collectEntity(PersistentObject entity) {
		Point point = (Point) entity;
		point.lat = Double.valueOf(txtLat.getText());
		point.lon = Double.valueOf(txtLon.getText());
		return point;
	}
}
