import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import thrift.WinAPIHandbookService;
import thrift.WinAPITechnology;
import thrift.WinAPITechnologyVersions;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

class WinAPIHandbookHandler implements WinAPIHandbookService.Iface {
    private static final Logger log = LogManager.getLogger(WinAPIHandbookService.class);

    @Override
    public List<WinAPITechnology> getAllTechnologies() {
        String getAllTechnologiesQuery;
        ResultSet rs;
        List<WinAPITechnology> list = new ArrayList<WinAPITechnology>();
        try {
            getAllTechnologiesQuery = "SELECT * " +
                    "FROM winapi_technologies " +
                    "INNER JOIN used_versions " +
                    "ON winapi_technologies.versions = used_versions.used_versions_id;";
            try {
                Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            String url = ResourceBundle.getBundle("config").getString("db.url");
            String user = ResourceBundle.getBundle("config").getString("db.user");
            String password = ResourceBundle.getBundle("config").getString("db.password");

            Statement stmt = DriverManager.getConnection(url, user, password).createStatement();
            rs = stmt.executeQuery(getAllTechnologiesQuery);

            while (rs.next()) {
                list.add(fromResultSetToWinAPITechnologyObject(rs));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public void addTechnology(WinAPITechnology technology) {
        String addTechnologyQuery;
        PreparedStatement preparedStatement;
        try {
            addTechnologyQuery =
                    "INSERT INTO used_versions(win16, win32, win32s, win64) " +
                    "VALUES(?, ?, ?, ?, ?) " +
                    "ON DUPLICATE " +
                    "KEY UPDATE win16=?, win32=?, win32s=?, win64=?;";

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            String url = ResourceBundle.getBundle("config").getString("db.url");
            String user = ResourceBundle.getBundle("config").getString("db.user");
            String password = ResourceBundle.getBundle("config").getString("db.password");

            preparedStatement = DriverManager.getConnection(url, user, password).prepareStatement(addTechnologyQuery);
            preparedStatement.setString(1, technology.getVersions().getVersionForWin16());
            preparedStatement.setString(2, technology.getVersions().getVersionForWin32());
            preparedStatement.setString(3, technology.getVersions().getVersionForWin32s());
            preparedStatement.setString(4, technology.getVersions().getVersionForWin64());
            preparedStatement.setString(5, technology.getVersions().getVersionForWin16());
            preparedStatement.setString(6, technology.getVersions().getVersionForWin32());
            preparedStatement.setString(7, technology.getVersions().getVersionForWin32s());
            preparedStatement.setString(8, technology.getVersions().getVersionForWin64());
            preparedStatement.executeUpdate();

            addTechnologyQuery =
                    "INSERT INTO winapi_technologies(tech_name, versions, description) " +
                    "VALUES(?, " +
                    "(SELECT used_versions_id " +
                    "FROM used_versions " +
                    "WHERE win16=? AND win32=? AND win32s=? AND win64=?), ?) " +
                    "ON DUPLICATE KEY UPDATE tech_name=?;";
            preparedStatement = DriverManager.getConnection(url, user, password).prepareStatement(addTechnologyQuery);
            preparedStatement.setString(1, technology.getName());
            preparedStatement.setString(2, technology.getVersions().getVersionForWin16());
            preparedStatement.setString(3, technology.getVersions().getVersionForWin32());
            preparedStatement.setString(4, technology.getVersions().getVersionForWin32s());
            preparedStatement.setString(5, technology.getVersions().getVersionForWin64());
            preparedStatement.setString(6, technology.getDescription());
            preparedStatement.setString(7, technology.getName());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void removeTechnology(WinAPITechnology technology) {
        String removeTechnologyQuery;
        PreparedStatement preparedStatement;
        try {
            removeTechnologyQuery = "DELETE FROM winapi_technologies " +
                    "WHERE tech_id=?;";

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            String url = ResourceBundle.getBundle("config").getString("db.url");
            String user = ResourceBundle.getBundle("config").getString("db.user");
            String password = ResourceBundle.getBundle("config").getString("db.password");

            preparedStatement = DriverManager.getConnection(url, user, password).prepareStatement(removeTechnologyQuery);
            preparedStatement.setInt(1, technology.getId());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void updateTechnology(WinAPITechnology technology) {
        String updateTechnologyQuery;
        PreparedStatement preparedStatement;
        try {
            updateTechnologyQuery = "INSERT INTO used_versions(win15, win32, win32s, win64) " +
                    "VALUES(?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE win16=?, win32=?, win32s=?, win64=?;";

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            String url = ResourceBundle.getBundle("config").getString("db.url");
            String user = ResourceBundle.getBundle("config").getString("db.user");
            String password = ResourceBundle.getBundle("config").getString("db.password");

            preparedStatement = DriverManager.getConnection(url, user, password).prepareStatement(updateTechnologyQuery);
            preparedStatement.setString(1, technology.getVersions().getVersionForWin16());
            preparedStatement.setString(2, technology.getVersions().getVersionForWin32());
            preparedStatement.setString(3, technology.getVersions().getVersionForWin32s());
            preparedStatement.setString(4, technology.getVersions().getVersionForWin64());
            preparedStatement.setString(5, technology.getVersions().getVersionForWin16());
            preparedStatement.setString(6, technology.getVersions().getVersionForWin32());
            preparedStatement.setString(7, technology.getVersions().getVersionForWin32s());
            preparedStatement.setString(8, technology.getVersions().getVersionForWin64());
            preparedStatement.executeUpdate();

            updateTechnologyQuery = "UPDATE java_technologies\n" +
                    "SET tech_name=?, versions=(SELECT used_versions_id\n" +
                    "FROM used_versions\n" +
                    "WHERE java_4=? AND java_5=? AND java_6=? AND java_7=? AND java_8=?),\n" +
                    "description=?\n" +
                    "WHERE tech_id=?;";
            preparedStatement = DriverManager.getConnection(url, user, password).prepareStatement(updateTechnologyQuery);
            preparedStatement.setString(1, technology.getName());
            preparedStatement.setString(2, technology.getVersions().getVersionForWin16());
            preparedStatement.setString(3, technology.getVersions().getVersionForWin32());
            preparedStatement.setString(4, technology.getVersions().getVersionForWin32s());
            preparedStatement.setString(5, technology.getVersions().getVersionForWin64());
            preparedStatement.setString(7, technology.getDescription());
            preparedStatement.setInt(8, technology.getId());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private WinAPITechnology fromResultSetToWinAPITechnologyObject(ResultSet rs) throws SQLException {
        WinAPITechnology technology = new WinAPITechnology();
        technology.setId(rs.getInt("tech_id"));
        technology.setName(rs.getString("tech_name"));
        technology.setVersions(new WinAPITechnologyVersions());
        technology.getVersions().setVersionForWin16(rs.getString("win16"));
        technology.getVersions().setVersionForWin32(rs.getString("win32));
        technology.getVersions().setVersionForWin32s(rs.getString("win32s"));
        technology.getVersions().setVersionForWin64(rs.getString("win64"));
        technology.setDescription(rs.getString("description"));

        return technology;
    }
}
