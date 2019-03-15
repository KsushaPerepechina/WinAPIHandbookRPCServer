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
        String getAllFunctionsQuery;
        ResultSet result;
        List<WinAPIFunction> functions = new ArrayList<>();
        try {
            getAllFunctionsQuery = "SELECT * " +
                    "FROM winapi_function;";
            try {
                Class.forName("org.postgresql.Driver").newInstance();
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage());
            }

            String url = ResourceBundle.getBundle("config").getString("db.url");
            String user = ResourceBundle.getBundle("config").getString("db.user");
            String password = ResourceBundle.getBundle("config").getString("db.password");

            Statement stmt = DriverManager.getConnection(url, user, password).createStatement();
            result = stmt.executeQuery(getAllFunctionsQuery);

            while (result.next()) {
                functions.add(fromResultSetToWinAPIFunctionObject(result));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return functions;
    }

    @Override
    public void addFunction(WinAPIFunction func) {
        String addFunctionQuery;
        PreparedStatement preparedStatement;
        try {
            addFunctionQuery = "INSERT INTO winapi_function(name, params, return_values, description) " +
                            "VALUES(?, ?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE name=?;";
            try {
                Class.forName("org.postgresql.Driver").newInstance();
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            String url = ResourceBundle.getBundle("config").getString("db.url");
            String user = ResourceBundle.getBundle("config").getString("db.user");
            String password = ResourceBundle.getBundle("config").getString("db.password");

            preparedStatement = DriverManager.getConnection(url, user, password).prepareStatement(addFunctionQuery);
            preparedStatement.setString(1, func.getName());
            preparedStatement.setString(2, func.getParams());
            preparedStatement.setString(3, func.getReturnValue());
            preparedStatement.setString(4, func.getDescription());
            preparedStatement.setString(5, func.getName());
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
            removeFunctionQuery = "DELETE FROM winapi_function " +
                    "WHERE id=?;";

            try {
                Class.forName("org.postgresql.Driver").newInstance();
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
            updateFunctionQuery = "UPDATE winapi_function " +
                    "SET name=?, params=?, return_values=?, description=? " +
                    "WHERE id=?;";
            try {
                Class.forName("org.postgresql.Driver").newInstance();
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            String url = ResourceBundle.getBundle("config").getString("db.url");
            String user = ResourceBundle.getBundle("config").getString("db.user");
            String password = ResourceBundle.getBundle("config").getString("db.password");

            preparedStatement = DriverManager.getConnection(url, user, password).prepareStatement(updateFunctionQuery);
            preparedStatement.setString(1, func.getName());
            preparedStatement.setString(2, func.getParams());
            preparedStatement.setString(3, func.getReturnValue());
            preparedStatement.setString(4, func.getDescription());
            preparedStatement.setInt(5,func.getId());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private WinAPIFunction fromResultSetToWinAPIFunctionObject(ResultSet resultSet) throws SQLException {
        WinAPIFunction function = new WinAPIFunction();
        function.setId(resultSet.getInt("id"));
        function.setName(resultSet.getString("name"));
        function.setParams(resultSet.getString("params"));
        function.setReturnValue(resultSet.getString("return_value"));
        function.setDescription(resultSet.getString("description"));
        return function;
    }
}
