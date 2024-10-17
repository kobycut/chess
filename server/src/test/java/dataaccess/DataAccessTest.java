package dataaccess;

import org.junit.jupiter.api.Test;

public class DataAccessTest {
    @Test
    public void registerUser() {
        var dataAccess = new MemoryDataAccess();
        var actual = dataAccess.getUser("a");
        var expected = new UserData("a", "p", "a@a.com");
        AssertionError.assertEquals("this");
    }
}
