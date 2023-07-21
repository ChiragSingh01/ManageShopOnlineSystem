package OnlineShop;
import java .sql.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import userInfo.userInformation;

import javax.xml.stream.events.DTD;

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

    private static String CreateID(String TableName) {
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

    private static StringBuilder getUsername (String FirstName, String LastName){
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

    private static void insertTable(String ID, String... Values) {
        try {
            Connection con = makeConnection();
            List<String> Table = new ArrayList<>();
            Table.add("invoice");
            Table.add("invoiceproduct");
            Table.add("feedback");
            if (con != null) {
                Statement st = con.createStatement();
                StringBuilder column = new StringBuilder(String.format("'%s'", Values[1]));
                if(Values.length >2) {
                    for (int i = 2; i < Values.length; i++) {
                        column.append(String.format(",'%s'", Values[i]));
                    }
                }
                String date = null;
                String Query = null;
                if (Table.contains(Values[0])) {
                    date = getDate();
                    Query = String.format(" insert into %s values ('%s',%s,'%s') ", Values[0], ID, column, date);
                } else if (Values[0].equals("product")) {
                    Query = String.format(" insert into %s values ('%s','%s','%s',%s,%s) ", Values[0], ID, Values[1], Values[2], Values[3], Values[4]);
                }
                else{
                    Query = String.format(" insert into %s values ('%s',%s) ", Values[0], ID, column);
                }
                try {
                    st.execute(Query);
                    System.out.printf("Inserted in %s", Values[0]);
                    System.out.println();
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

    public static void insertProduct (){
        String PID = CreateID("Product");
        System.out.print("Enter the Name of Product : ");
        String Name = str.nextLine();
        System.out.print("Enter Description : ");
        String Desc = str.nextLine();
        System.out.print("Enter Price : ");
        int Price = str.nextInt();
        System.out.print("Enter Stocks Available : ");
        int stock = str.nextInt();
        str.nextLine();
        System.out.print("Enter Category by type (Phone, Watch, Laptop, Fan and etc) : ");
        String cate = str.nextLine();
        insertTable(PID,"product",Name,Desc,String.valueOf(Price),String.valueOf(stock));
        String CID = CreateID("Categories");
        insertTable(CID,"categories",cate,PID,Desc);
    }

    private static String getPrimeKey(String TableName) {
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

    public static void updateValues(String TableName, String... Column_Values) {
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
                        if (TableName.equals("product") && (Column_Values[i].equals("ProPrice") || Column_Values[i].equals("Stock"))) {
                            str.append(String.format("%s = %s,", Column_Values[i], Column_Values[i + 1]));
                        } else {
                            str.append(String.format("%s = '%s',", Column_Values[i], Column_Values[i + 1]));
                        }
                        str1.append(Column_Values[i]).append(",");
                    }
                    if (TableName.equals("product") && (Column_Values[Column_Values.length - 3].equals("ProPrice") || Column_Values[Column_Values.length - 3].equals("Stock"))) {
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

    public static void Alter(String TableName, String ColumnName) {
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

    public static void Drop(String TableName) {
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

    public static void Alter_column(String Tablename, String ColumnName) {
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

    public static void Alter_rename(String Tablename, String Oldcolumn, String Newcolumn) {
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

    public static void Truncate(String TableName) {
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

    public static void Display (String TableName, String ...Column) {
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
                        System.out.println();
                        for (int i = 1; i <= column_count; i++) {
                            System.out.print(rsmd.getColumnName(i) + ": ");
                            System.out.println(rs.getString(i));
                        }
                        System.out.println();
                        for(int i=0;i<100;i++)
                            System.out.print("*-");
                        System.out.println("*");
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

    public static void Display_Condition (String TableName, String Column_Name, String Condition, String ...Column) {
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
                        Query = String.format(" select %s from %s where %s=%s", Columns, TableName, Column_Name, Condition);
                    else
                        Query = String.format(" select %s from %s where %s='%s'", Columns, TableName, Column_Name, Condition);
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

    public static void printTables (String ...Tables){
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
    private static boolean Check (String Detail){
        try {
            Connection con = makeConnection();
            if (con != null) {
                Statement st = con.createStatement();
                String Query = "select * from CustomerAccounts";
                ResultSet rs = st.executeQuery(Query);
                List <String> list = new ArrayList<>();
                while(rs.next()){
                    list.add(rs.getString("AccEmail"));
                    list.add(rs.getString("AccPass"));
                    list.add(rs.getString("AccPhone"));
                }
                if(list.contains(Detail))
                    return true;
            } else
                System.out.println("Connection doesn't established");
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }
    public static String login (){
        System.out.print("Enter user details :-\nUser Mail : ");
        String Mail = str.next();
        String CusID = null;
        if(Check(Mail)) {
            System.out.print("Password : ");
            String Pass = str.next();
            if(Check(Pass)) {
                System.out.print("Welcome ");
                Display_Condition("customeraccounts","AccEmail",Mail,"AccName");
                Connection con = makeConnection();
                try{
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(String.format("selsct * from customeraccounts where AccEmail = '%s'",Mail));
                    rs.next();
                    CusID = rs.getString(1);
                }
                catch (SQLException e){
                    System.out.println(e.getMessage());
                }
                return CusID;
            }
            else{
                System.out.println("Please Check your Password :D");
                return null;
            }
        }
        else{
            System.out.println("Please Check your Mail ID :D");
            return null;
        }
    }
    public static void PassReset () {
        System.out.print("Enter your Phone Number : ");
        System.out.println();
        String phn = str.next();
        if(Check(phn)) {
            System.out.print("Enter New Password : ");
            String Pass = str.next();
            System.out.print("Enter Your CustomerID : ");
            String CusID = str.next();
            updateValues("customeraccounts","AccPass",Pass,CusID);
        }
        else{
            System.out.println("Phone Number is not registered please check or signup if you are new user.");
        }
    }
    public static String SignUp () {
        System.out.println("Enter your details :-");
        System.out.print("Name : ");
        String Name = str.nextLine();
        String mail = null,Pass = null,Pass1 = null,Phn = null,Address = null;
        while(true) {
            System.out.print("Mail : ");
            mail = str.nextLine();
            if (Check(mail)) {
                System.out.println("This Mail is already being use");
            }
            else {
                while (true) {
                    System.out.print("Password : ");
                    Pass = str.nextLine();
                    System.out.print("Confirm Password : ");
                    Pass1 = str.nextLine();
                    if(Pass.equals(Pass1)){
                        break;
                    }
                    else {
                        System.out.println("Entered Password is not same please re-enter");
                    }
                }
                break;
            }
        }
        while (true) {
            System.out.print("Phone Number : ");
            Phn = str.nextLine();
            if(Check(Phn)){
                System.out.println("This Phone Number is already being use. Enter a new Number or Login.");
            }
            else
                break;
        }
        String Username = null;
        try {
            System.out.print("Address : ");
            Address = str.nextLine();
            String[] str = Name.split(" ");
            Username = String.valueOf(getUsername(str[0], str[1]));
            insertTable(Username, "customeraccounts", Name, Pass, Phn, Address, mail, "activate");
            System.out.println();
            System.out.println("Please remember your Customer ID it will help u to reset your password if needed. Thank You :D");
            System.out.println();
            Display_Condition("customeraccounts", "AccPhone", Phn);
        }
        catch (Exception e){
            System.out.println("Enter your First name and Last name only separated by ' ' :)");
        }
        return Username;
    }

    static void order (String CusID) {
        System.out.print("Enter the Product ID of the Product you want to order : ");
        String ProID = str.nextLine();
        System.out.print("Enter the quantity of Product you want : ");
        int Q = str.nextInt();
        str.nextLine();
        System.out.println();
        System.out.println("Enter the Delivery Details : ");
        System.out.print("Enter name of Receiver : ");
        String name = str.nextLine();
        System.out.print("Enter the mail of Receiver : ");
        String mail = str.nextLine();
        System.out.print("Enter Receiver's full Address : ");
        String Address = str.nextLine();
        System.out.print("Enter Receiver's Phone Number : ");
        String phn = str.nextLine();
        insertTable(CusID,"deliverydetails",name,mail,Address,phn);
        System.out.println();
        System.out.println("Is this a gift for someone? \nplease answer in Y for yes and N for no.");
        String Answer = str.nextLine();
        if(Answer.equals("Y")){
            System.out.println("Do you want to add a text message with the gift? \nplease answer in Y for yes and N for no.");
            Answer = str.nextLine();
            if(Answer.equals("Y")){
                System.out.print("Message : ");
                String Message = str.nextLine();
                insertTable(CusID,Message);
            }
            else{
                insertTable(CusID);
            }
        }
        Connection con = makeConnection();
        if(con != null){
            try {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(String.format("select * from product where productProId = '%s'",ProID));
                rs.next();
                System.out.println("ok");
                int Stock = rs.getInt(5);
                System.out.println("ok");
                if(Stock > Q){
                    String S = String.valueOf(Stock-Q);
                    updateValues("product","Stock",S,ProID);
                    String ID = CreateID("Invoice");
                    insertTable(ID,"invoice",CusID);
                    insertTable(ID,"invoiceproduct",ProID,String.valueOf(Q));
                    insertTable(CusID,"feedback",ProID,"");
                }
                else{
                    System.out.println("Sorry we don't have enough Stocks :(");
                }
                System.out.print("Your Order has been placed and will be deliver in 4 days :)");
            }
            catch (SQLException e){
                System.out.println("This Product is not in our list :(");
            }
        }
    }

    public static void FeedBackNotify (String CusID) {
        Connection con = makeConnection();
        if(con != null){
            try{
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(String.format("select * from invoice,feedback where invoiceCusID = feedbackCusID and invoiceCusID = '%s'",CusID));
                while (rs.next()) {
                    String ProID = rs.getString("feedbackProID");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDate D = rs.getDate("Invoice_Date").toLocalDate();
                    LocalDate D1 = LocalDate.parse(getDate(), formatter);
                    Period Diff = Period.between(D, D1.plusDays(5));
                    if (Diff.getDays() >= 5 && rs.getString("comment").equals("")) {
                        System.out.println("Do you want to give feedback about your order :");
                        Display_Condition("product","productProID",ProID,"ProName");
                        System.out.println("Press Y for yes and N for no");
                        String Choice = str.nextLine();
                        if(Choice.equals("Y")){
                            System.out.print("Please type your feedback here : ");
                            String Comments = str.nextLine();
                            st.execute(String.format("update feedback set comment = '%s' where feedbackCusID = '%s' and feedbackProID = '%s'",Comments,CusID,ProID));
                        }
                        else{
                            st.execute(String.format("update feedback set comment = ' ' where feedbackCusID = '%s' and feedbackProID = '%s'",CusID,ProID));
                        }
                    }
                }
            }
            catch(SQLException e){
                System.out.print(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        FeedBackNotify("CSingh38");
    }
}
