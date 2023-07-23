import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.mysql.cj.protocol.Resultset;

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
        int isOrdering = 1;
        int confirm = 2; //dummy value
        int lineNum = 0;

        LocalDateTime currentDate = LocalDateTime.now();
        String orderDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        ArrayList productCodes = new ArrayList<String>();
        ArrayList productNames = new ArrayList<String>();
        ArrayList quantities = new ArrayList<Integer>();
        ArrayList pricesEach = new ArrayList<Float>();
        
        Scanner sc = new Scanner(System.in);
        
        // i. Enter Customer Number
        System.out.print("Enter Customer Number: ");
        customerNumber = sc.nextInt();
        sc.nextLine();
        
        // ii. Enter Required Date
        System.out.print("Enter Required Date (in yyyy-mm-dd format): ");
        requiredDate = sc.nextLine();

        try {
            Connection conn; 
            // Change password every PULL !!
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsales?useTimezone=true&serverTimezone=UTC&user=root&password=12345");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);
            
            PreparedStatement pstmt = null;
            ResultSet rs = null;

            while (isOrdering == 1){
                // iv. Enter Product Code
                System.out.print("Enter Product Code: ");
                productCode = sc.nextLine();
                productCodes.add(productCode);	
                
                // System.out.print("Enter Price Each: "); //take from db?
                pstmt = conn.prepareStatement("SELECT productName, MSRP FROM products WHERE productCode=? FOR UPDATE");
                pstmt.setString(1, productCode);
                rs = pstmt.executeQuery();

                System.out.println("Press any key to continue");
                sc.nextLine();

                // v. Enter the quantity of the product
                System.out.print("Enter Quantity: ");
                quantityOrdered = sc.nextInt();
                quantities.add(quantityOrdered);	
                sc.nextLine();
            
                
                while(rs.next()) {
                    productNames.add(rs.getString("productName"));
                    pricesEach.add(rs.getFloat("MSRP"));
                }
                rs.close();

                System.out.println("Price of " + productCodes.get(productCodes.size()-1) + ": " + pricesEach.get(pricesEach.size()-1));
        
                System.out.println("Do you want to order another product? Enter [1] if YES [0] if NO");
                System.out.print("Input: ");
                isOrdering = sc.nextInt();  
                sc.nextLine();
            } //end of while

            // Confirm order //
            System.out.println("Here is a summary of your order: ");
            System.out.println("----------------------------------------");

            for (int i = 0; i < productCodes.size(); i++){
                System.out.println("ORDER ITEM #" + (i+1));
                System.out.println("Product Code:       " + productCodes.get(i));
                System.out.println("Product Name:       " + productNames.get(i));
                System.out.println("Price Each (MSRP):  " + pricesEach.get(i));
                System.out.println("----------------------------------------");
            }
            
            while (confirm != 1 && confirm != 0){
                System.out.println("Please confirm your order. Enter [1] for CONFIRM [0] for VOID");
                System.out.print("Input: ");
                confirm = sc.nextInt();
                sc.nextLine();
                
                if (confirm == 1){
                 //iii. Automtically Generates Order Number
                pstmt = conn.prepareStatement("SELECT MAX(orderNumber) + 1 AS newOrderNumber FROM orders");
                rs = pstmt.executeQuery();  
                rs.next();
                orderNumber = rs.getInt("newOrderNumber");   
								
                // Add order to database //
                pstmt = conn.prepareStatement("INSERT INTO orders (orderNumber, orderDate, requiredDate, shippedDate, status, comments, customerNumber) VALUES(?, ?, ?, NULL, 'In Process', NULL, ?)");
                pstmt.setInt(1, orderNumber);
                pstmt.setString(2, orderDate);
                pstmt.setString(3, requiredDate);
                pstmt.setInt(4, customerNumber);
                pstmt.executeUpdate();

                // Add orderDetails to database //
                for (int i = 0; i < productCodes.size(); i++){
                    lineNum = i+1;
                    pstmt = conn.prepareStatement("INSERT INTO orderdetails (orderNumber, productCode, quantityOrdered, priceEach, orderLineNumber) VALUES(?, ?, ?, ?, ?)");
                    pstmt.setInt(1, orderNumber);
                    pstmt.setString(2, (String) productCodes.get(i));
                    pstmt.setInt(3, (Integer) quantities.get(i));
                    pstmt.setFloat(4, (Float) pricesEach.get(i));
                    pstmt.setInt(5, lineNum);
                    pstmt.executeUpdate();
                }

                // Update product quantity //
                for (int i = 0; i < productCodes.size(); i++){
                    pstmt = conn.prepareStatement("UPDATE products SET quantityInStock=(quantityInStock-?) WHERE productCode=?");
                    pstmt.setInt(1, (Integer) quantities.get(i)); 
                    pstmt.setString(2, (String) productCodes.get(i));
                    pstmt.executeUpdate();
                }

                System.out.println("Order confirmed and placed.");
                } else if (confirm == 0) {
                    System.out.println("Current order voided.");
                } else {
                    System.out.println("Invalid input. Try again.");
                }
                rs.close();
                
            }

						System.out.println("Press any key to end transaction");
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
    
    //b. Inquire for Products
    public int getProductInfo()     {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Product Code: ");
        productCode = sc.nextLine();
        
        try {
            Connection conn; 
            // Change password every PULL !!
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsales?useTimezone=true&serverTimezone=UTC&user=root&password=12345");
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

    // c. Retrieve Info about the Order
    public int retrieveOrderInfo() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Order Number: ");
        orderNumber = sc.nextInt();

        try{
            Connection conn; 
            // Change password every PULL !!
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsales?useTimezone=true&serverTimezone=UTC&user=root&password=12345");
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
                System.out.println("Shipped Date:     -"); 
            }

            System.out.println("Status:           " + status);

            if (comments != null){
                System.out.println("Comments:         " + comments);
            }else{
                System.out.println("Comments:         -");
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
        ArrayList qtyOrdered = new ArrayList<>();
        ArrayList prices = new ArrayList<>();
        ArrayList orderLineNums = new ArrayList <>();

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Order ID: ");
        orderNumber = sc.nextInt();

        try {
            Connection conn;
            //CHANGE YOUR PASSWORD//
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsales?useTimezone=true&serverTimezone=UTC&user=root&password=12345");
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

            System.out.println("[ORDER INFO]");
            System.out.println("Order Date:       " + orderDate);
            System.out.println("Required Date:    " + requiredDate);
            System.out.println("Shipped Date:     " + shippedDate);
            System.out.println("Status:           " + status);
            System.out.println("Comments:         " + comments);
            System.out.println("Customer Number:  " + customerNumber);

            System.out.println("========================================");

            PreparedStatement pstmt2 = conn.prepareStatement("SELECT orderNumber, productCode, quantityOrdered, priceEach, orderLineNumber FROM orderdetails WHERE orderNumber=?");
            pstmt2.setInt(1, orderNumber);
            ResultSet rs2 = pstmt2.executeQuery();

            while (rs2.next()) {
                productcodes.add(rs2.getString("productCode")); 
                qtyOrdered.add(rs2.getInt("quantityOrdered")); 
                prices.add(rs2.getInt("priceEach"));  
                orderLineNums.add(rs2.getInt("orderLineNumber"));
            }
            rs2.close();

            System.out.println("[ORDER DETAILS INFO]");
            
            System.out.println("Order Number:       " + orderNumber);
            for(var i=0; i<productcodes.size(); i++) {
                System.out.println("Quantity Ordered:       " + productcodes.get(i)); 
                System.out.println("Quantity Ordered:       " + qtyOrdered.get(i)); 
                System.out.println("Price Each:             " + prices.get(i)); 
                System.out.println("Order Line Number:      " + orderLineNums.get(i)); 
                 System.out.println("-------------------");
            }
            
            System.out.println("Press any key to cancel");
            sc.nextLine();

            //SET STATUS TO CANCELLED//
            // Notes: Should not be able to cancel the following statuses:
            // 1. Shipped - An order already shipped cannot be canceled. (Source: https://feedvisor.com/university/canceling-orders/)
            // 2. Resolved - An order is already shipped and meets the terms of customer.
            // 3. Cancelled - An order is already cancelled to begin with.
            String str = "Shipped";
            if(!status.equals(str)){
                pstmt = conn.prepareStatement("UPDATE orders SET status = 'Cancelled', shippedDate = NULL WHERE orderNumber=? AND status IN ('Disputed', 'In Process', 'On Hold');");
                pstmt.setInt(1, orderNumber);
                pstmt.executeUpdate();

                //ADDS THE QUANTITY ORDERED BACK TO QUANTITY IN STOCK//
                pstmt = conn.prepareStatement("SELECT (p.quantityInStock + od.quantityOrdered) AS newQty, p.productCode FROM products p JOIN orderdetails od ON od.productCode = p.productCode WHERE od.orderNumber=? FOR UPDATE;");
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
            System.out.print("\nPress enter to end transaction");
            sc.nextLine();

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
        Scanner  sc     = new Scanner (System.in);
        int      choice = 0;
        products p      = new products();
        
        while(choice!=5){
            System.out.println("Enter [1] Create an Order [2] Inquire for Products [3] Retrieve Info about Order [4] Cancel Order [5] Exit");
            System.out.print("Input: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice){
                case 1:
                    p.orderProduct();
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
    }
    
}
