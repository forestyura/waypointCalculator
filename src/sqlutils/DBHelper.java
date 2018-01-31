package sqlutils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import calculator.App;
import logginig.Logger;
import tools.Config;
import tools.ExportImport;

public class DBHelper {
	private static Connection connection = null;
	private static Logger logger = Logger.getLogger(DBHelper.class);

	private final static String SQL_DRIVER = "org.sqlite.JDBC";
	private final static String SQL_DB_JDBC = "jdbc:sqlite:";
	private final static String SQL_DB_NAME = "database.db";

	private static boolean isDbInitailized() {
		Connection c = DBHelper.getConnection();
		try {
			c.createStatement().executeUpdate("SELECT COUNT(1) FROM DB_INFO");
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	public static ResultSet executeQuery(String sqlText, Object[] parameters) {
		PreparedStatement stmt = null;
		Connection c = DBHelper.getConnection();
		ResultSet res = null;
		try {
			stmt = c.prepareStatement(sqlText);
			for (int i = 0; parameters != null && i < parameters.length; i++) {
				stmt.setObject(i + 1, parameters[i]);
			}
			res = stmt.executeQuery();

		} catch (SQLException e) {
			if (stmt == null) {
				logger.info("Database is not openned");
			}
			logger.info("Error while executing statement: \n" + sqlText);
			e.printStackTrace();

		}
		return res;
	}

	public static int executeUpdate(String sqlText, Object[] parameters) throws SQLException {
		PreparedStatement stmt = null;
		Connection c = DBHelper.getConnection();
		int res = 0;
		if (c == null)
			return 0;
		try {
			stmt = c.prepareStatement(sqlText);
			if (parameters != null) {
				for (int i = 0; i < parameters.length; i++) {
					stmt.setObject(i + 1, parameters[i]);
				}
			}
			res = stmt.executeUpdate();
		} catch (SQLException e) {
			if (stmt == null) {
				logger.info("Database is not openned");
			}
			logger.info("Error while executing statement: \n" + sqlText);
			logger.info(e);
			throw e;
		}
		return res;
	}

	public static int executePlainUpdate(String sqlText) {
		Statement stmt = null;
		Connection c = DBHelper.getConnection();
		int res = 0;
		if (c == null)
			return 0;
		try {
			stmt = c.createStatement();
			res = stmt.executeUpdate(sqlText);
		} catch (SQLException e) {
			if (stmt == null) {
				logger.info("Database is not openned");
			}
			logger.info("Error while executing statement: \n" + sqlText);
			e.printStackTrace();

		}

		return res;
	}

	private static Connection getConnection() {
		if (connection == null) {
			try {
				Class.forName(SQL_DRIVER);
				connection = DriverManager.getConnection(SQL_DB_JDBC + SQL_DB_NAME);

			} catch (ClassNotFoundException e) {
				logger.info("Place SQLite driver to ../lib folder");
				e.printStackTrace();
			} catch (SQLException e) {
				if (connection == null) {
					logger.info("SQL exception occured while creating connection: %s");
				}
				e.printStackTrace();
			}
		}
		return connection;
	}

	public static String getSqlText(String path) throws IOException {
		final String SCRIPT_DIR = App.config.getString("resource.dir.scripts", Config.APP_SCRIPT_DIR);

		InputStream inputStream = DBHelper.class.getClassLoader().getResourceAsStream(SCRIPT_DIR + "/" + path);

		StringBuilder sb = new StringBuilder();
		try(BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))){
			br.lines().map(s -> s + "\n").forEach(sb::append);
		} catch (IOException e){
			throw new FileNotFoundException(String.format("Cannot find file %s at path %s", path, SCRIPT_DIR));
		}

		return sb.toString();
	}

	public static int getCurrentSequence(String tableName) throws SQLException {
		if(tableName == null) logger.info("Empty table name");
		int res = -1;
		Connection conn = getConnection();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(
					String.format("SELECT VAL as SEQ FROM DB_INFO WHERE NAME = '%s_SEQUENCE'", tableName));
			res = rs.getInt("SEQ");
			rs.close();
		} catch (SQLException e) {
			logger.info("Cannot get sequence for table " + tableName);
			throw e;
		}

		return res;
	}

	public static int getNextSequence(String tableName) throws SQLException {
		int res = getCurrentSequence(tableName);
		if (res == -1)
			throw new SQLException("Impossible sequence value -1");
		res++;
		Connection conn = getConnection();
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(String.format("UPDATE DB_INFO SET VAL = %d WHERE NAME = '%s_SEQUENCE'", res, tableName));
		} catch (SQLException e) {
			logger.debug("Cannot get sequence for table " + tableName);
			throw e;
		}

		return res;
	}

	public static void checkDB() throws SQLException, IOException {
		logger.info("Checking database");

		logger.info("Attempting to connect: " + SQL_DB_JDBC + SQL_DB_NAME);
		if (new File(SQL_DB_NAME).exists()) {
			logger.info("Database exists. Connecting...");
		} else {
			logger.info("Database does not exists. Creating...");
		}		
		if (DBHelper.getConnection() == null) {
			throw new SQLException("Failed to create connection");
		}else{
			logger.info("Connected to database");
		}

		logger.info("Validating schema");
		if (!isDbInitailized()) {
			logger.info("Database is not initialized. Running core script...");
			String coreScript = getSqlText("create-schema.sql");
			
			BufferedReader br = new BufferedReader(new StringReader(coreScript));
			String line = null;
			while( (line=br.readLine()) != null )			{
				logger.info(line);
			}
			
			if(0 < executePlainUpdate(getSqlText("create-schema.sql"))){
				logger.info("Schema created");
			}

			logger.info("Importing preset");
			String filePath = App.config.getString("resource.dir.export", Config.APP_EXPORT_DIR) + "/initial.xml";

			try(InputStream is = DBHelper.class.getClassLoader().getResourceAsStream(filePath)) {
				ExportImport.importXML(is);
			} catch (IOException | JAXBException e) {
				logger.info("Failed to import preset " + filePath);
				logger.info(e);
			}
		}
	}
}