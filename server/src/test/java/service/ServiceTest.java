package service;

import dataaccess.DataAccess;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ServiceTest {
    static private DataAccess dataAccess;
    static private Service service;

    @BeforeAll
    public static void init() {
        dataAccess = new MemoryDataAccess();
        service = new Service(dataAccess);
    }
    @Test
    public void registerUser() {

    }
}
