package OnlineShop;
import java .sql.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import userInfo.userInformation;

public class OnlineShopManagement {
    static Scanner str = new Scanner(System.in);

    static String getCondition(List <String> Tables){
        if ((Tables.contains("customeraccounts") || Tables.contains("deliverydetails")) && Tables.contains("product")){
            return "ProID,CusID";
        }
        else if (Tables.contains("invoice") && Tables.contains("product")){
            return "ProID,InvoiceID";
        }
        else if(Tables.contains("product")){
            return "ProID";
        }
        else if(Tables.contains("customeraccounts") || Tables.contains("deliverydetails")){
            return "CusID";
        }
        else if (Tables.contains("invoice")){
            return "InvoiceID";
        }
        else
            return "CusID";
    }
    static Connection makeConnection() {
        userInformation user = new userInformation();
        try {
            return (DriverManager.getConnection(user.getUrl(), user.getUser(), user.getPass()));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static String getDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date today = (Date) Calendar.getInstance()
                .getTime();
        return (df.format(today));
    }

    static String CreateID(String TableName) {
        Random rnd = new Random();
        String UserID = null;
        int i=0,bond=99;
        try {
            Connection con = makeConnection();
            List<String> ID = new ArrayList<>();
            if (con != null) {
                Statement st = con.createStatement();

                ResultSet rs = st.executeQuery(String.format("select %s from %s", getPrimeKey(TableName), TableName));
                while (rs.next()) {
                    ID.add(rs.getString(getPrimeKey(TableName)));
                }
                do {
                    UserID = TableName.substring(0, 3) + rnd.nextInt(bond);
                    i++;
                    if(i==50){
                        bond = (bond*10) + 9;
                        i=0;
                    }
                } while (ID.contains(UserID));
            } else {
                System.out.println("Connection doesn't Established");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return UserID;
    }

    static StringBuilder getUsername (String FirstName, String LastName){
        Random rnd = new Random();
        int Bond = 99,i=0;
        StringBuilder UserName = new StringBuilder();
        try {
            Connection con = makeConnection();
            List<StringBuilder> userName = new ArrayList<>();
            if (con != null) {
                Statement st = con.createStatement();

                ResultSet rs = st.executeQuery(String.format("select %s from %s", getPrimeKey("customeraccounts"), "customeraccounts"));
                while (rs.next()) {
                    userName.add(new StringBuilder(rs.getString(getPrimeKey("customeraccounts"))));
                }
                do {
                    if (rnd.nextInt(0,6) == 0) {
                        UserName = new StringBuilder(Character.toString(FirstName.charAt(0)));
                        if (LastName.length() > 5)
                            UserName.append(LastName, 0, 5);
                        else
                            UserName.append(LastName);
                        UserName.append(Integer.toString(rnd.nextInt(Bond)));
                    } else if (rnd.nextInt(0,6) == 1) {
                        UserName = new StringBuilder(Character.toString(LastName.charAt(0)));
                        if (FirstName.length() > 5)
                            UserName.append(FirstName, 0, 5);
                        else
                            UserName.append(FirstName);
                        UserName.append(Integer.toString(rnd.nextInt(Bond)));
                    } else if (rnd.nextInt(0,6) == 2) {
                        UserName = new StringBuilder(LastName + FirstName);
                        UserName.append(Integer.toString(rnd.nextInt(Bond)));
                    } else if (rnd.nextInt(0,6) == 3) {
                        UserName = new StringBuilder(FirstName + LastName);
                        UserName.append(Integer.toString(rnd.nextInt(Bond)));
                    } else if (rnd.nextInt(0,6) == 4) {
                        UserName = new StringBuilder(Character.toString(FirstName.charAt(0)) + Character.toString(LastName.charAt(0)));
                        UserName.append(Integer.toString(rnd.nextInt(Bond)));
                    } else if (rnd.nextInt(0,6) == 5) {
                        UserName = new StringBuilder(Character.toString(LastName.charAt(0)) + Character.toString(FirstName.charAt(0)));
                        UserName.append(Integer.toString(rnd.nextInt(Bond)));
                    } else {
                        UserName = null;
                    }
                    i++;
                    if(i>50){
                        Bond = (Bond * 10) + 9;
                        i=0;
                    }
                }while (userName.contains(UserName) || UserName == null);
            }
            else {
                System.out.println("Connection doesn't Established");
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return (UserName);
    }

    static void insertTable(String... Values) {
        try {
            Connection con = makeConnection();
            List<String> Table = new ArrayList<>();
            Table.add("invoice");
            Table.add("invoiceProduct");
            Table.add("feedback");
            if (con != null) {
                Statement st = con.createStatement();
                StringBuilder column = new StringBuilder(String.format("'%s',", Values[1]));
                for (int i = 2; i < Values.length - 1; i++) {
                    column.append(String.format("'%s',", Values[i]));
                }
                column.append(String.format("'%s'", Values[Values.length - 1]));
                String date = null;
                String Query = null;
                if (Table.contains(Values[0])) {
                    date = getDate();
                    Query = String.format(" insert into %s values ('%S',%s,'%s') ", Values[0], CreateID(Values[0]), column, date);
                } else if (Values[0].equals("product")) {
                    Query = String.format(" insert into %s values ('%s','%s','%s',%s,%s) ", Values[0], CreateID(Values[0]), Values[1], Values[2], Values[3], Values[4]);
                }
                try {
                    st.execute(Query);
                    System.out.printf("Inserted in %s", Values[0]);
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("Connection doesn't Established");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static String getPrimeKey(String TableName) {
        try {
            Connection con = makeConnection();
            if (con != null) {
                DatabaseMetaData meta = con.getMetaData();
                ResultSet rs1 = meta.getPrimaryKeys(null, null, TableName);
                rs1.next();
                return rs1.getString("COLUMN_NAME");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    static void updateValues(String TableName, String... Column_Values) {
        List<String> Tables = new ArrayList<>();
        Tables.add("product");
        Tables.add("categories");
        if (Tables.contains(TableName)) {
            try {
                Connection con = makeConnection();
                if (con != null) {
                    Statement st = con.createStatement();
                    StringBuilder str = new StringBuilder(), str1 = new StringBuilder();
                    for (int i = 0; i < Column_Values.length - 3; i = i + 2) {
                        if (TableName.equals("product")) {
                            str.append(String.format("%s = %s,", Column_Values[i], Column_Values[i + 1]));
                        } else {
                            str.append(String.format("%s = '%s',", Column_Values[i], Column_Values[i + 1]));
                        }
                        str1.append(Column_Values[i]).append(",");
                    }
                    if (TableName.equals("product")) {
                        str.append(String.format("%s = %s", Column_Values[Column_Values.length - 3], Column_Values[Column_Values.length - 2]));
                    } else {
                        str.append(String.format("%s = '%s'", Column_Values[Column_Values.length - 3], Column_Values[Column_Values.length - 2]));
                    }
                    str1.append(Column_Values[Column_Values.length - 3]);
                    String Query = String.format("update %s set %s where %s = '%s'", TableName, str, getPrimeKey(TableName), Column_Values[Column_Values.length - 1]);
                    try {
                        st.execute(Query);
                        System.out.printf("Values are updated in Table : %s in Column : %s%n", TableName, str1);
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    System.out.println("Connection doesn't Established");
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("You Can't Update this Table");
        }
    }

    static void Alter(String TableName, String ColumnName) {
        try {
            Connection con = makeConnection();
            if (con != null) {
                Statement st = con.createStatement();
                String Query = String.format("alter table %s add column %s ", TableName, ColumnName);
                try {
                    st.execute(Query);
                    System.out.println("Executed and column added");
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("Connection doesn't Established");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void Drop(String TableName) {
        try {
            Connection con = makeConnection();
            if (con != null) {
                Statement st = con.createStatement();
                String Query = String.format("Drop table %s ", TableName);
                try {
                    st.execute(Query);
                    System.out.println("Executed and table dropped");
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("Connection doesn't Established");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void Alter_column(String Tablename, String ColumnName) {
        try {
            Connection con = makeConnection();
            if (con != null) {
                Statement st = con.createStatement();
                String Query = String.format("Alter table %s Drop column %s ", Tablename, ColumnName);
                try {
                    st.execute(Query);
                    System.out.println("Executed and  column dropped");
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("Connection doesn't Established");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void Alter_rename(String Tablename, String Oldcolumn, String Newcolumn) {
        try {
            Connection con = makeConnection();
            if (con != null) {
                Statement st = con.createStatement();
                String Query = String.format("Alter table %s Rename column %s to %s ", Tablename, Oldcolumn, Newcolumn);
                try {
                    st.execute(Query);
                    System.out.println("Executed and  column renamed");
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("Connection doesn't Established");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void Truncate(String TableName) {
        try {
            Connection con = makeConnection();
            if (con != null) {
                Statement st = con.createStatement();
                String Query = String.format("Truncate  table %s ", TableName);
                try {
                    st.execute(Query);
                    System.out.println("Executed and all rows deleted");
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("Connection doesn't Established");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void Display (String TableName, String ...Column) {
        try{
            Connection con = makeConnection();
            if (con != null) {
                Statement st = con.createStatement();
                String Query = null;
                StringBuilder Columns = new StringBuilder();
                if(Column.length == 0)
                    Query = String.format(" select * from %s", TableName);
                else {
                    for (int i = 0; i < Column.length - 1; i++) {
                        Columns.append(Column[i]).append(",");
                    }
                    Columns.append(Column[Column.length - 1]);
                    Query = String.format(" select %s from %s ", TableName, Columns);
                }
                try {
                    ResultSet rs = st.executeQuery(Query);
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int column_count = rsmd.getColumnCount();
                    while(rs.next()) {
                        for (int i = 1; i <= column_count; i++) {
                            System.out.print(rsmd.getColumnName(i) + ":  ");
                            System.out.println(rs.getString(i));
                        }
                        System.out.println();
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("Connection doesn't Established");
            }
        }
        catch ( SQLException e ){
            System.out.println(e.getMessage());
        }
    }

    static void Display_Condition (String TableName, String Column_Name, String Condition, String ...Column) {
        try{
            Connection con = makeConnection();
            if (con != null) {
                Statement st = con.createStatement();
                String Query = null;
                StringBuilder Columns = new StringBuilder();
                if(Column.length == 0) {
                    if (Column_Name.equals("ProPrice") || Column_Name.equals("Stock"))
                        Query = String.format(" select * from %s where %s=%s", TableName, Column_Name, Condition);
                    else
                        Query = String.format(" select * from %s where %s='%s'", TableName, Column_Name, Condition);
                }
                else {
                    for (int i = 0; i < Column.length - 1; i++) {
                        Columns.append(Column[i]).append(",");
                    }
                    Columns.append(Column[Column.length - 1]);
                    if (Column_Name.equals("ProPrice") || Column_Name.equals("Stock"))
                        Query = String.format(" select * from %s where %s=%s", TableName, Column_Name, Condition);
                    else
                        Query = String.format(" select * from %s where %s='%s'", TableName, Column_Name, Condition);
                }
                try {
                    ResultSet rs = st.executeQuery(Query);
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int column_count = rsmd.getColumnCount();
                    while(rs.next()) {
                        for (int i = 1; i <= column_count; i++) {
                            System.out.print(rsmd.getColumnName(i) + ":  ");
                            System.out.println(rs.getString(i));
                        }
                        System.out.println();
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("Connection doesn't Established");
            }
        }
        catch ( SQLException e ){
            System.out.println(e.getMessage());
        }
    }

    static void printTables (String ...Tables){
        try {
            Connection con = makeConnection();
            if (con != null) {
                Statement st = con.createStatement();
                String Query = null;
                String Columns = null;
                StringBuilder Conn = new StringBuilder();
                StringBuilder Table = new StringBuilder();
                List<String> L = new ArrayList<>(Arrays.asList(Tables));
                String Condition = getCondition(L);
                if (Tables.length == 1) {
                    System.out.println("Please Enter more then one Table :D");
                } else {
                    for (int i = 0; i < Tables.length - 1; i++) {
                        Table.append(Tables[i]).append(",");
                    }
                    Table.append(Tables[Tables.length - 1]);
                    System.out.println("If you want to print specific column then write them using (,) otherwise type null.");
                    Columns = str.next();
                    try {
                        if (Condition.equals("ProID,CusID")) {
                            L.remove("product");
                            if (L.contains("customeraccounts")) {
                                L.remove("customeraccounts");
                                String[] parts = Condition.split(",");
                                for (int i = 0; i < L.size() - 1; i++) {
                                    Conn.append(String.format("(%s%s=product%s or %s%s=customeraccounts%s) && ", L.get(i), parts[1], parts[1], L.get(i), parts[2], parts[2]));
                                }
                                Conn.append(String.format("(%s%s=product%s or %s%s=customeraccounts%s)", L.get(L.size() - 1), parts[1], parts[1], L.get(L.size() - 1), parts[2], parts[2]));
                            } else {
                                L.remove("deliverydetails");
                                String[] parts = Condition.split(",");
                                for (int i = 0; i < L.size() - 1; i++) {
                                    Conn.append(String.format("(%s%s=product%s or %s%s=deliverydetails%s) && ", L.get(i), parts[1], parts[1], L.get(i), parts[2], parts[2]));
                                }
                                Conn.append(String.format("(%s%s=product%s or %s%s=deliverydetails%s)", L.get(L.size() - 1), parts[1], parts[1], L.get(L.size() - 1), parts[2], parts[2]));
                            }

                        } else if (Condition.equals("ProID,InvoiceID")) {
                            L.remove("product");
                            L.remove("invoice");
                            String[] parts = Condition.split(",");
                            for (int i = 0; i < L.size() - 1; i++) {
                                Conn.append(String.format("(%s%s=product%s or %s%s=invoice%s) && ", L.get(i), parts[1], parts[1], L.get(i), parts[2], parts[2]));
                            }
                            Conn.append(String.format("(%s%s=product%s or %s%s=invoice%s)", L.get(L.size() - 1), parts[1], parts[1], L.get(L.size() - 1), parts[2], parts[2]));
                        } else if (Condition.equals("ProID")) {
                            L.remove("product");
                            for (int i = 0; i < L.size() - 1; i++) {
                                Conn.append(String.format("%s%s=product%s && ", L.get(i), Condition, Condition));
                            }
                            Conn.append(String.format("%s%s=product%s ", L.get(L.size() - 1), Condition, Condition));
                        } else if (Condition.equals("InvoiceID")) {
                            L.remove("invoice");
                            for (int i = 0; i < L.size() - 1; i++) {
                                Conn.append(String.format("%s%s=invoice%s && ", L.get(i), Condition, Condition));
                            }
                            Conn.append(String.format("%s%s=invoice%s ", L.get(L.size() - 1), Condition, Condition));
                        } else if (Condition.equals("CusID")) {
                            for (int i = 0; i < L.size() - 2; i++) {
                                Conn.append(String.format("%s%s=%s%s && ", L.get(i), Condition, L.get(L.size() - 1), Condition));
                            }
                            Conn.append(String.format("%s%s=%s%s ", L.get(L.size() - 2), Condition, L.get(L.size() - 1), Condition));
                        }
                        if (Columns.equals("null")) {
                            Query = String.format("select * from %s where %s", Table, Conn);
                        } else {
                            Query = String.format("select %s form %s where %s", Columns, Table, Conn);
                        }
                        try {
                            ResultSet rs = st.executeQuery(Query);
                            ResultSetMetaData rsmd = rs.getMetaData();
                            int column_count = rsmd.getColumnCount();
                            while (rs.next()) {
                                for (int i = 1; i <= column_count; i++) {
                                    System.out.print(rsmd.getColumnName(i) + ":  ");
                                    System.out.println(rs.getString(i));
                                }
                                System.out.println();
                            }
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    catch (Exception e){
                        System.out.println("This combination is invalid");
                    }
                }
            }
            else {
                System.out.println("Connection doesn't Established");
            }
        }
        catch ( SQLException e ){
            System.out.println(e.getMessage());
        }
    }
}
