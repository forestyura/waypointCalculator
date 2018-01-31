package domains;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import domains.Fields.Field;
import domains.Points.Point;
import logginig.Logger;
import sqlutils.DBHelper;

public class Fields extends PersistentContainer<Field>{

	public final static String TABLE_NAME = "FIELDS";
	private static List<Field> entityList = new ArrayList<>();
	
	public static Logger logger = Logger.getLogger(Fields.class);
	
	public Fields() throws SQLException{
		instance = this;
		Fields.loadAll();
	}
	
	public static class Field extends PersistentObject implements ToolTipRecord {

		public String name = "";

		@XmlElement(name = "point")
		@XmlElementWrapper
		public List<Point> points = new ArrayList<>();

		private Field load(ResultSet rs) throws SQLException {
			this.id = rs.getInt("ID");
			this.name = rs.getString("NAME");
			this.points = Points.getPoints(this.id);
			return this;
		}

		@Override
		public void save() throws SQLException {
			int idx = entityList.indexOf(this);
			if (idx == -1) {
				this.id = DBHelper.getNextSequence(Fields.TABLE_NAME);
				entityList.add(this);
			} else {
				this.id = entityList.get(idx).id;
				entityList.set(idx, this);
			}

			this.persist();

			for (Point point : this.points) {
				point.fieldId = this.id;
				point.seq = this.points.size();
				point.save();
				point.persist();
			}
		}

		@Override
		public void persist() throws SQLException {
			ResultSet rs = DBHelper.executeQuery("SELECT SUM(1) AS EXIST FROM " + TABLE_NAME + " WHERE ID = ?",
					new Object[] { this.id });
			rs.next();

			if (rs.getBoolean("EXIST")) {
				DBHelper.executeUpdate(String.format("UPDATE %s SET NAME = ? WHERE ID = ?", TABLE_NAME),
						new Object[] { this.name, this.id });// update
			} else {
				DBHelper.executeUpdate(String.format("INSERT INTO %s (ID, NAME) VALUES (?, ?)", TABLE_NAME),
						new Object[] { this.id, this.name });// insert
			}
		}

		@Override
		public void delete() throws SQLException {
			DBHelper.executeUpdate("DELETE FROM FIELDS WHERE ID = ?", new Object[] { this.id });
		}	
		
		@Override
		public void loadAll() throws SQLException{
			Fields.loadAll();
		}

		@Override
		public void dispose() {
			for (PersistentObject p : Points.getPoints(this.id)) {
				points.remove(p);
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Field other = (Field) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
		
//		@Override
//		public PersistentContainer<?> getInstance() {
//			return Fields.instance;
//		}
		
		@Override
		public String validate() throws SQLException {
			if ("".equals(this.name))
				return "Name cannot be empty";

			if (points.size() < 3)
				return "Field should contain at least 3 points";

			if (this.id == 0) {
				ResultSet rs = DBHelper.executeQuery("SELECT SUM(1) AS EXIST FROM " + TABLE_NAME + " WHERE NAME = ?",
						new Object[] { this.name });
				rs.next();
				if (rs.getBoolean("EXIST"))
					return "Entity with this name already exeists";
			}
			return "";
		}	

		@Override
		public String toString() {
			return name;
		}
	}
	
	public static List<Field> getEntities() {
		return entityList;
	}
	
	public static void loadAll() throws SQLException {
		logger.info("Loading Fields...");
		ResultSet rs = DBHelper.executeQuery(String.format("SELECT ID, NAME FROM %s", TABLE_NAME), null);

		entityList.clear();
		while (rs.next()) {
			entityList.add(new Field().load(rs));
		}
		logger.info(String.format("\tLoaded %d fields", entityList.size()));
		notifyListeners();
	}
}
