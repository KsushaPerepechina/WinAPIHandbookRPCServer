import org.junit.*;
import thrift.WinAPIFunction;
import thrift.WinAPIHandbookService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class WinAPIHandbookHandlerTest {
    private static WinAPIFunction function;
    private static WinAPIHandbookHandler handler;

    @BeforeClass
    public static void init(){
        function = new WinAPIFunction();
        handler = new WinAPIHandbookHandler();
        function.setName("some name");
        function.setParams("some params");
        function.setReturnValue("some return value");
        function.setDescription("some description");
    }

    @Before
    public void start() throws SQLException {
        handler.addFunction(function);
    }

    @After
    public void close() throws SQLException {
        handler.removeFunction(function);
    }

    @Test
    public void getAllFunctions() {
        WinAPIFunction function1 = new WinAPIFunction();

        function1.setName("some name 1");
        function1.setParams("some params 1");
        function1.setReturnValue("some return value 1");
        function1.setDescription("some description 1");

        handler.addFunction(function1);

        List<WinAPIFunction> actual = handler.getAllFunctions();

        List<WinAPIFunction> expected = new ArrayList<>();
        actual.add(function);
        actual.add(function1);

        Assert.assertTrue(containsAll(actual,expected));

        handler.removeFunction(function1);
    }

    @Test
    public void addFunction() {
        Assert.assertTrue(containsOne(handler.getAllFunctions(), function));
    }

    @Test
    public void updateFunction() {
        function.setDescription("updated description");
        handler.updateFunction(function);
        Assert.assertFalse(containsOne(handler.getAllFunctions(), function));
    }

    @Test
    public void removeFunction() {
        Assert.assertTrue(containsOne(handler.getAllFunctions(), function));
        handler.removeFunction(function);
        Assert.assertFalse(containsOne(handler.getAllFunctions(), function));
    }

    private static boolean containsAll(List<WinAPIFunction> list, List<WinAPIFunction> sublist){
        int elementsNumber = 0;
        for (WinAPIFunction function: list) {
            for (WinAPIFunction function1: sublist) {
                if(function.equals(function1)) elementsNumber++;
            }
        }
        return elementsNumber == sublist.size();
    }

    private static boolean containsOne(List<WinAPIFunction> list, WinAPIFunction function){
        for (WinAPIFunction func: list) {
            if (function.getName().equals(func.getName())
            && function.getParams().equals(func.getParams())
            && function.getReturnValue().equals(func.getReturnValue())
            && function.getDescription().equals(func.getDescription())) return true;
        }
        return false;
    }
}