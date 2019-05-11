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

    /**
     * Получение всех функций справочника
     * @return Список функций
     */
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

    /**
     * Добавление новой функции в справочник
     * @param func - добавляемая функция
     */
    @Override
    public void addFunction(WinAPIFunction func) {
        String addFunctionQuery;
        PreparedStatement preparedStatement;
        try {
            addFunctionQuery = "INSERT INTO winapi_function(name, params, return_value, description) " +
                            "VALUES(?, ?, ?, ?);";
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
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Обновление сведений об определнной функции
     * @param func - обновляемая функция
     */
    @Override
    public void updateFunction(WinAPIFunction func) {
        String updateFunctionQuery;
        PreparedStatement preparedStatement;
        try {
            updateFunctionQuery = "UPDATE winapi_function " +
                    "SET name=?, params=?, return_value=?, description=? " +
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

    /**
     * Извлечение функции из справочника
     * @param function - извлекаемая функция
     */
    @Override
    public void removeFunction(WinAPIFunction function) {
        String removeFunctionQuery;
        PreparedStatement preparedStatement;
        try {
            removeFunctionQuery = "DELETE FROM winapi_function " +
                    "WHERE name=?;";

            try {
                Class.forName("org.postgresql.Driver").newInstance();
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            String url = ResourceBundle.getBundle("config").getString("db.url");
            String user = ResourceBundle.getBundle("config").getString("db.user");
            String password = ResourceBundle.getBundle("config").getString("db.password");

            preparedStatement = DriverManager.getConnection(url, user, password).prepareStatement(removeFunctionQuery);
            preparedStatement.setString(1, function.getName());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Сериализация полученных в результате запроса к БД данных в объекты функций
     * @param resultSet - полученные из БД данные
     * @return Сериализованный объект функции
     * @throws SQLException - ошибка доступа к БД
     */
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
