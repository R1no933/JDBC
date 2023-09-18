package jdbc.util;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionManagerWithPool {
    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    public static final String POOL_SIZE_KEY = "db.pool.size";
    public static final Integer DEFAULT_POOL_SIZE = 10;
    public static BlockingQueue<Connection> pool;
    public static List<Connection> sourceConnection;

    static {
        loadDriver();
        initConnectionPool();
    }

    private ConnectionManagerWithPool() {}

    private static void initConnectionPool() {
        String poolsize = PropertiesUtil.getProperties(POOL_SIZE_KEY);
        int size = poolsize == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolsize);
        pool = new ArrayBlockingQueue<>(size);
        sourceConnection = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            Connection connection = openConncetion();
            Connection proxyConnection = (Connection) Proxy.newProxyInstance(ConnectionManagerWithPool.class.getClassLoader(), new Class[]{Connection.class},
                    ((proxy, method, args) -> method.getName().equals("close")
                    ? pool.add((Connection) proxy) : method.invoke(connection, args)));
            pool.add(proxyConnection);
            sourceConnection.add(connection);
        }
    }

    public static Connection getConnection() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeConnection() {
        try {
            for (Connection sourceConnection : sourceConnection) {
                sourceConnection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Connection openConncetion() {
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.getProperties(URL_KEY),
                    PropertiesUtil.getProperties(USERNAME_KEY),
                    PropertiesUtil.getProperties(PASSWORD_KEY)
            );
        } catch (SQLException error) {
            throw new RuntimeException(error);
        }
    }

    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException error) {
            throw new RuntimeException(error);
        }
    }
}
