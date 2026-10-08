package com.aptech.s07;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * E06 — SQL injection: the same lookup written both wrong and right, run side by side.
 *
 * <p>This example is here to make one idea impossible to forget, not to teach an attack.
 * It uses a throwaway table in an in-memory database so nothing real is ever at risk.
 *
 * <p>The whole vulnerability is one habit: <strong>building SQL by gluing user input into a
 * string</strong>. The moment input can be part of the SQL, input can change what the SQL
 * <em>means</em> — and the database has no way to tell the difference, because by the time it
 * sees the statement, there is no difference.
 *
 * <p>A deliberately harmless payload is used: it makes the WHERE clause true for every row.
 * The point is not that data leaks. The point is that <em>a lookup changed its own meaning</em>.
 */
public class E06_SqlInjection {

    public static void main(String[] args) throws SQLException {

        System.out.println("E06 - the same lookup, written wrong and written right");

        try (Connection c = Db.connect("jdbc:h2:mem:injection;DB_CLOSE_DELAY=-1", "sa", "")) {

            createDemoTable(c);

            // A perfectly ordinary thing for a user to type.
            String typedName = "Ada Lovelace";
            // The payload. In a real app this arrives from a login box, a search field,
            // a URL parameter — anywhere a user (or a bot) can type.
            String payload   = "' OR '1'='1";

            System.out.println(Db.rule("THE WRONG WAY - input glued into the SQL"));

            wrongWay(c, typedName);
            wrongWay(c, payload);

            System.out.println(Db.rule("THE RIGHT WAY - the same input, as a parameter"));

            rightWay(c, typedName);
            rightWay(c, payload);

            System.out.println(Db.rule("why the two behave differently"));

            System.out.println("  WRONG: the database received");
            System.out.println("         SELECT ... WHERE name = '' OR '1'='1'");
            System.out.println("         — a WHERE that is true for every row. It was never told");
            System.out.println("           which characters were the query and which were the value.");

            System.out.println("  RIGHT: the database received");
            System.out.println("         SELECT ... WHERE name = ?   with one value bound to it.");
            System.out.println("         The ' OR '1'='1 is a STRING being compared to name.");
            System.out.println("         No row is called that, so no rows come back — correctly.");
        }

        System.out.println(Db.rule("the rule that follows"));
        System.out.println("  A parameter is data. It can never become code.");
        System.out.println("  So: every value goes through a ?. There is no clever case where");
        System.out.println("  concatenation is safe — \"it is only a number\" and \"it is from");
        System.out.println("  our own dropdown\" are both how this bug ships.");
    }

    // ------------------------------------------------------------------------

    private static void createDemoTable(Connection c) throws SQLException {
        try (Statement st = c.createStatement()) {
            st.execute("DROP TABLE IF EXISTS logins");
            st.execute("CREATE TABLE logins (id INT PRIMARY KEY, name VARCHAR(50), role VARCHAR(20))");
            st.execute("INSERT INTO logins VALUES "
                    + "(1, 'Ada Lovelace', 'student'), "
                    + "(2, 'Grace Hopper', 'student'), "
                    + "(3, 'The Examiner', 'examiner')");
        }
    }

    /** VULNERABLE ON PURPOSE — this is the mistake, shown so it can be recognised. */
    private static void wrongWay(Connection c, String typed) throws SQLException {
        String sql = "SELECT id, name, role FROM logins WHERE name = '" + typed + "'";
        System.out.println("  typed: " + typed);
        System.out.println("  SQL sent: " + sql);

        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int n = 0;
            while (rs.next()) {
                System.out.println("      -> " + rs.getString("name") + " (" + rs.getString("role") + ")");
                n++;
            }
            System.out.println("      " + n + " row(s) returned"
                    + (n > 1 ? "   <-- ONE lookup returned the whole table" : ""));
        } catch (SQLException e) {
            System.out.println("      SQLException: " + e.getMessage().split("\n")[0]);
        }
        System.out.println();
    }

    /** The same lookup, correctly written. */
    private static void rightWay(Connection c, String typed) throws SQLException {
        String sql = "SELECT id, name, role FROM logins WHERE name = ?";
        System.out.println("  typed: " + typed);
        System.out.println("  SQL sent: " + sql + "   (value bound separately)");

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, typed);
            try (ResultSet rs = ps.executeQuery()) {
                int n = 0;
                while (rs.next()) {
                    System.out.println("      -> " + rs.getString("name") + " (" + rs.getString("role") + ")");
                    n++;
                }
                System.out.println("      " + n + " row(s) returned"
                        + (n == 0 ? "   <-- nothing matched that name, which is correct" : ""));
            }
        }
        System.out.println();
    }
}
