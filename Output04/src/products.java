import java.sql.*;
import java.util.*;

public class products {
   
    public int      customerNumber;
    public String   requiredDate;

    // Orders Table
    public int      orderNumber;
    public String   orderDate;
    public String   shippedDate;
    public String   status;
    public String   comments;
    
    // Order Details Table
    public int      quantityOrdered;
    public float    priceEach;
    public int      lineNum;

    // Products Table
    public String   productCode;
    public String   productName;
    public String   productLine;
    public int      quantityInStock;
    public float    buyPrice;
    public float    MSRP;
    
    //a. Create an Order (Ordering for Products) -- SKELETON CODE --
    public int orderProduct() { 
        try {
            return 1; 
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }
    
    //b. Inquire for Products
    public int getProductInfo()     {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Product Code: ");
        productCode = sc.nextLine();
        
        try {
            Connection conn; 
            // Change password every PULL !!
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsales?useTimezone=true&serverTimezone=UTC&user=root&password=ccapdev123");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);
            
            // PreparedStatement pstmt = conn.prepareStatement("LOCK TABLE products WRITE/READ");
            // pstmt.executeUpdate();
            
            PreparedStatement pstmt = conn.prepareStatement("SELECT productName, productLine, quantityInStock, buyPrice, MSRP FROM products WHERE productCode=? LOCK IN SHARE MODE");
            pstmt.setString(1, productCode);
            
            System.out.println("Press enter key to start retrieving the data");
            sc.nextLine();
            
            ResultSet rs = pstmt.executeQuery();   
            
            while (rs.next()) {
                productName     = rs.getString("productName");
                productLine     = rs.getString("productLine");
                quantityInStock = rs.getInt("quantityInStock");
                buyPrice        = rs.getFloat("buyPrice");
                MSRP            = rs.getFloat("MSRP");
            }
            
            rs.close();
            
            System.out.println("Product Name: " + productName);
            System.out.println("Product Line: " + productLine);
            System.out.println("Quantity:     " + quantityInStock);
            System.out.println("Buy Price:    " + buyPrice);
            System.out.println("MSRP:         " + MSRP);
            
            System.out.println("Press enter key to end transaction");
            sc.nextLine();

            pstmt.close();
            conn.commit();
            conn.close();
            return 1;
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    /*
    // Not Needed
    public int updateInfo() {
        
        float   incr;
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Product Code: ");
        productCode = sc.nextLine();
        
        try {
            Connection conn; 
            // Change password every PULL !!
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsales?useTimezone=true&serverTimezone=UTC&user=root&password=ccapdev123");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn.prepareStatement("SELECT productName, productLine, quantityInStock, buyPrice, MSRP FROM products WHERE productCode=? FOR UPDATE");
            pstmt.setString(1, productCode);
            
            System.out.println("Press enter key to start retrieving the data");
            sc.nextLine();
            
            ResultSet rs = pstmt.executeQuery();   
            
            while (rs.next()) {
                productName     = rs.getString("productName");
                productLine     = rs.getString("productLine");
                quantityInStock = rs.getInt("quantityInStock");
                buyPrice        = rs.getFloat("buyPrice");
                MSRP            = rs.getFloat("MSRP");
            }
            
            rs.close();
            
            System.out.println("Product Name: " + productName);
            System.out.println("Product Line: " + productLine);
            System.out.println("Quantity:     " + quantityInStock);
            System.out.println("Buy Price:    " + buyPrice);
            System.out.println("MSRP:         " + MSRP);
            
            System.out.println("Press enter key to enter new values for product");
            sc.nextLine();

            System.out.println("Enter percent increase in MSRP: ");
            incr = sc.nextFloat();
            
            MSRP = MSRP * (1+incr/100);
            
            pstmt = conn.prepareStatement ("UPDATE products SET MSRP=? WHERE productCode=?");
            pstmt.setFloat(1,  MSRP);
            pstmt.setString(2, productCode);
            pstmt.executeUpdate();
                        
            
            pstmt.close();
            conn.commit();
            conn.close();
            return 1;
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }        
    }
    */

    // c. Retrieve Info about the Order
    public int retrieveOrderInfo() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Order Number: ");
        orderNumber = sc.nextInt();

        try{
            Connection conn; 
            // Change password every PULL !!
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsales?useTimezone=true&serverTimezone=UTC&user=root&password=ccapdev123");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            PreparedStatement pstmt = conn.prepareStatement("SELECT orderNumber, orderDate, requiredDate, shippedDate, status, comments, customerNumber FROM orders WHERE orderNumber=? LOCK IN SHARE MODE");
            pstmt.setInt(1, orderNumber);

            System.out.println("Press enter key to start retrieving Order Details");
            sc.nextLine();
            sc.nextLine();
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                orderDate           = rs.getString("orderDate");
                requiredDate        = rs.getString("requiredDate");
                shippedDate         = rs.getString("shippedDate");
                status              = rs.getString("status");
                comments            = rs.getString("comments");
                customerNumber      = rs.getInt("customerNumber");

            }
            rs.close();

            System.out.println("Order Date:       " + orderDate);
            System.out.println("Required Date:    " + requiredDate);

            if (shippedDate != null){
                System.out.println("Shipped Date:     " + shippedDate); 
            }else{
                System.out.println("Shipped Date:     N/A"); 
            }

            System.out.println("Status:           " + status);

            if (comments != null){
                System.out.println("Comments:         " + comments);
            }else{
                System.out.println("Comments:         N/A");
            }

            System.out.println("Customer Number:  " + customerNumber); 
            System.out.println("----------------------------------------");

            pstmt = conn.prepareStatement("SELECT od.productCode, p.productName, od.quantityOrdered, od.priceEach, od.orderLineNumber FROM orderdetails od JOIN products p ON p.productCode = od.productCode WHERE od.orderNumber=? ORDER BY od.orderLineNumber LOCK IN SHARE MODE;");
            pstmt.setInt(1, orderNumber);

            rs = pstmt.executeQuery();
            float totalPrice = 0;

            while(rs.next()){
                productCode         = rs.getString("productCode");
                productName         = rs.getString("productName");
                quantityOrdered     = rs.getInt("quantityOrdered");
                priceEach           = rs.getFloat("priceEach");
                lineNum             = rs.getInt("orderLineNumber");

                System.out.println("[ LINE #" + lineNum + " ]");
                System.out.println("Product Code:      " + productCode);
                System.out.println("Product Name:      " + productName);
                System.out.println("Quantity Ordered:  " + quantityOrdered);
                System.out.println("Price Each:        " + priceEach);
                totalPrice += (quantityOrdered*priceEach);
                System.out.println("----------------------------------------");
            }
            rs.close();

            System.out.println("Order Total Price: " + totalPrice);

            System.out.println("\nPress enter key to end transaction");
            sc.nextLine();

            pstmt.close();
            conn.commit();
            conn.close();
            return 1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
     }

    //d. Cancel order
    public int cancelOrder(){
        ArrayList newQuantity = new ArrayList<>();
        ArrayList productcodes = new ArrayList<String>();

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Order ID: ");
        orderNumber = sc.nextInt();

        try {
            Connection conn;
            //CHANGE YOUR PASSWORD//
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsales?useTimezone=true&serverTimezone=UTC&user=root&password=ccapdev123");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            PreparedStatement pstmt = conn.prepareStatement("SELECT orderNumber, orderDate, requiredDate, shippedDate, status, comments, customerNumber FROM orders WHERE orderNumber=? FOR UPDATE");
            pstmt.setInt(1, orderNumber);

            System.out.println("Press enter key to start retrieving the data");
            sc.nextLine();
            sc.nextLine();

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                orderDate           = rs.getString("orderDate");
                requiredDate        = rs.getString("requiredDate");
                shippedDate         = rs.getString("shippedDate");
                status              = rs.getString("status");
                comments            = rs.getString("comments");
                customerNumber      = rs.getInt("customerNumber");
            }
            rs.close();

            System.out.println("Order Date:       " + orderDate);
            System.out.println("Required Date:    " + requiredDate);
            System.out.println("Shipped Date:     " + shippedDate);
            System.out.println("Status:           " + status);
            System.out.println("Comments:         " + comments);
            System.out.println("Customer Number:  " + customerNumber);

            System.out.println("Press any key to cancel");
            sc.nextLine();

            //SET STATUS TO CANCELLED//
            // Notes: Should not be able to cancel the following statuses:
            // 1. Shipped - An order already shipped cannot be canceled. (Source: https://feedvisor.com/university/canceling-orders/)
            String str = "Shipped";
            if(!status.equals(str)){
                pstmt = conn.prepareStatement("UPDATE orders SET status = 'Cancelled' WHERE orderNumber=? AND status IN ('Disputed', 'In Process', 'On Hold', 'Resolved');");
                pstmt.setInt(1, orderNumber);
                pstmt.executeUpdate();

                //ADDS THE QUANTITY ORDERED BACK TO QUANTITY IN STOCK//
                pstmt = conn.prepareStatement("SELECT (p.quantityInStock + od.quantityOrdered) AS newQty, p.productCode FROM products p JOIN orderdetails od ON od.productCode = p.productCode WHERE od.orderNumber=? LOCK IN SHARE MODE;");
                pstmt.setInt(1, orderNumber);

                rs = pstmt.executeQuery();

                //RETRIEVES THE VALUES TO UPDATE//
                while (rs.next()) {
                    newQuantity.add(rs.getInt("newQty"));
                    productcodes.add(rs.getString("productCode"));
                }
                rs.close();

                //UPDATES THE NEW QUANTITY//
                for(int i = 0; i < newQuantity.size(); i++) {
                    pstmt = conn.prepareStatement("UPDATE products SET quantityInStock=? WHERE productCode=?");
                    pstmt.setInt(1, (Integer) newQuantity.get(i));
                    pstmt.setString(2, (String) productcodes.get(i));
                    pstmt.executeUpdate();
                }

                System.out.println("Order Number " + orderNumber + " has successfully been cancelled.");

            }
            else{
                System.out.println("You cannot cancel an order that has already been shipped.");
            }

            pstmt.close();
            conn.commit();
            conn.close();
            return 1;
        }
            catch (Exception e) {
                System.out.println(e.getMessage());
                return 0;
        }
    }
    
    public static void main (String args[]) {
        Scanner sc     = new Scanner (System.in);
        int     choice = 0;
        products p = new products();
         while(choice!=5){
            System.out.println("Enter [1] Create an Order [2] Inquire for Products [3] Retrieve Info about Order [4] Cancel Order [5] Exit");
            System.out.print("Input: ");
            choice = sc.nextInt();
            
            switch (choice){
                case 1:
                    //p.orderProduct();
                    break;
                case 2:
                    p.getProductInfo();
                    break;
                case 3:
                    p.retrieveOrderInfo();
                    break;
                case 4:
                    p.cancelOrder();
                    break;
                case 5:
                    System.out.println("Thank you!");
                    break;
                default:
                    System.out.println("Invalid input. Try again.");
                    break;
            }
        }
        
        sc.nextLine();
    }
    
}