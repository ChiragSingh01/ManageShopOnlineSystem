import OnlineShop.*;

import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        Scanner str = new Scanner(System.in);
        String CusID = null;
        OnlineShopManagement Obj = new OnlineShopManagement();
        System.out.println("Welcome to Jain Confectionary");
        while(true) {
            System.out.println("1. Login\n2. Signup\n3. Login as guest");
            System.out.print("Input : ");
            int Ch = str.nextInt();
            str.nextLine();
            if (Ch == 1) {
                CusID = Obj.login();
                if(CusID.equals("null")){
                    continue;
                }
                else
                    break;
            }
            else if (Ch == 2){
                CusID = Obj.SignUp();
                if(CusID.equals("null"))
                    continue;
                else
                    break;
            } else if (Ch == 3) {
                break;
            } else
                System.out.println("Invalid Input");
        }
        Obj.Display("product");
        if(CusID != null)
            Obj.FeedBackNotify(CusID);
        while(true){
            System.out.println("1. Order\n2. Check previous Invoice\n3. Your Profile\n4. Exit");
            System.out.print("Input : ");
            int Ch = str.nextInt();
            str.nextLine();
            switch (Ch) {
                case 1 -> {
                    if(CusID == null){
                        System.out.print("Please login to order something :) and if you want to exit press 'N' or press 'Y'");
                        String CHH = str.nextLine();
                        if(CHH.equals("N"))
                            Ch =4;
                        else {
                            CusID = Obj.login();
                            Obj.order(CusID);
                        }
                    }
                    else {
                        Obj.order(CusID);
                    }
                }
                case 2 -> Obj.printTables("invoice,invoiceproduct");
                case 3 -> {
                    Obj.Display_Condition("customeraccounts", "customeraccountsCusID", CusID);
                    System.out.println("Do you want to edit something ? Y/N");
                    String ch = str.nextLine();
                    if (ch.equals("Y")) {
                        System.out.print("Type What do you want to edit : ");
                        String edit = str.nextLine();
                        System.out.print("Enter the update : ");
                        String update = str.nextLine();
                        Obj.updateValues("customeraccounts", edit, update, CusID);
                    }
                }
            }
            if(Ch == 4) {
                System.out.println("Thanks for your Visit See you soon :)");
                break;
            }
        }
    }
}
