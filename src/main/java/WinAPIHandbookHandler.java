import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import thrift.WinAPIHandbookService;
import thrift.WinAPIFunction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

class WinAPIHandbookHandler implements WinAPIHandbookService.Iface {
    private static final Logger log = LogManager.getLogger(WinAPIHandbookService.class);

    @Override
    public List<WinAPIFunction> getAllFunctions() {
        String getAllFuctionsQuery;
        ResultSet result;
        List<WinAPIFunction> functions = new ArrayList<WinAPIFunction>();
        try {
            getAllFuctionsQuery = "SELECT * " +
                    "FROM winapi_functions;";
            try {
                Class.forName("org.postgres.Driver").newInstance();
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage());
            }

            String url = ResourceBundle.getBundle("config").getString("db.url");
            String user = ResourceBundle.getBundle("config").getString("db.user");
            String password = ResourceBundle.getBundle("config").getString("db.password");

            Statement stmt = DriverManager.getConnection(url, user, password).createStatement();
            result = stmt.executeQuery(getAllFuctionsQuery);

            while (result.next()) {
                functions.add(fromResultSetToWinAPIFunctionObject(result));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return functions;
        return null;
    }

    @Override
    public void addFunction(WinAPIFunction func) {
        String addFunctionQuery;
        PreparedStatement preparedStatement;
        try {
            addFunctionQuery =
                    "INSERT INTO winapi_functions " +
                    "VALUES(?, ?, ?, ?) ;";

            try {
                Class.forName("com.postgres.jdbc.Driver").newInstance();
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            String url = ResourceBundle.getBundle("config").getString("db.url");
            String user = ResourceBundle.getBundle("config").getString("db.user");
            String password = ResourceBundle.getBundle("config").getString("db.password");

            preparedStatement = DriverManager.getConnection(url, user, password).prepareStatement(addFunctionQuery);
            preparedStatement.executeUpdate();

            addFunctionQuery =
                    "INSERT INTO winapi_technologies(tech_name, versions, description) " +
                    "VALUES(?, " +
                    "(SELECT used_versions_id " +
                    "FROM used_versions " +
                    "WHERE win16=? AND win32=? AND win32s=? AND win64=?), ?) " +
                    "ON DUPLICATE KEY UPDATE tech_name=?;";
            preparedStatement = DriverManager.getConnection(url, user, password).prepareStatement(addFunctionQuery);
            preparedStatement.setString(1, func.getName());
            preparedStatement.setString(2, func.getDescription());
            preparedStatement.setString(3, func.getParams());
            preparedStatement.setString(4, func.getReturnValue());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void removeFunction(WinAPIFunction function) {
        String removeFunctionQuery;
        PreparedStatement preparedStatement;
        try {
            removeFunctionQuery = "DELETE FROM winapi_functions " +
                    "WHERE tech_id=?;";

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            String url = ResourceBundle.getBundle("config").getString("db.url");
            String user = ResourceBundle.getBundle("config").getString("db.user");
            String password = ResourceBundle.getBundle("config").getString("db.password");

            preparedStatement = DriverManager.getConnection(url, user, password).prepareStatement(removeFunctionQuery);
            preparedStatement.setInt(1, function.getId());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void updateFunction(WinAPIFunction func) {
        String updateFunctionQuery;
        PreparedStatement preparedStatement;
        try {
            updateFunctionQuery = "INSERT INTO used_versions(win16, win32, win32s, win64) " +
                    "VALUES(?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE win16=?, win32=?, win32s=?, win64=?;";

            try {
                Class.forName("com.postgres.jdbc.Driver").newInstance();
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            String url = ResourceBundle.getBundle("config").getString("db.url");
            String user = ResourceBundle.getBundle("config").getString("db.user");
            String password = ResourceBundle.getBundle("config").getString("db.password");

            preparedStatement = DriverManager.getConnection(url, user, password).prepareStatement(updateFunctionQuery);

            preparedStatement.executeUpdate();

            updateFunctionQuery = "UPDATE winapi_technologies " +
                    "SET tech_name=?, versions=(SELECT used_versions_id " +
                    "FROM used_versions " +
                    "WHERE win16=? AND win32=? AND win32s=? AND win64=?), " +
                    "description=? " +
                    "WHERE tech_id=?;";
            preparedStatement = DriverManager.getConnection(url, user, password).prepareStatement(updateFunctionQuery);
            preparedStatement.setString(1, func.getName());
            preparedStatement.setString(2, func.getDescription());
            preparedStatement.setString(3, func.getParams());
            preparedStatement.setString(4, func.getReturnValue());
            preparedStatement.setInt(5, func.getId());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private WinAPIFunction fromResultSetToWinAPIFunctionObject(ResultSet resultSet) throws SQLException {
        WinAPIFunction function = new WinAPIFunction();
        function.setId(resultSet.getInt("id"));
        function.setName(resultSet.getString("name"));
        function.setDescription(resultSet.getString("description"));
        function.setDescription(resultSet.getString("params"));
        function.setDescription(resultSet.getString("return_value"));
        return function;
    }
}
