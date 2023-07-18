import java.sql.*;
import java.util.*;

public class products {
   
    public int      customerNumber;
    public String   requiredDate;

    // Orders Table
    public String   orderNumber;
    public String   orderDate;
    public String   shippedDate;
    public String   status;
    public String   comments;
    

    public String   productCode;
    public String   productName;
    public String   productLine;
    public int      quantityInStock;
    public float    buyPrice;
    public float    MSRP;
    
  
    public products() {}
    
    //b. Inquire for Products
    public int getInfo()     {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Product Code:");
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

    // Not Needed
    public int updateInfo() {
        
        float   incr;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Product Code:");
        productCode = sc.nextLine();
        
        try {
            Connection conn; 
            // Change password every PULL !!
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsales?useTimezone=true&serverTimezone=UTC&user=root&password=12345");
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

    // c. Retrieve Info about the Order --- SKELETON CODE ---
    public int retrieveOrderInfo() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Order Number:");
        orderNumber = sc.nextLine();

        try{
            Connection conn; 
            // Change password every PULL !!
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsales?useTimezone=true&serverTimezone=UTC&user=root&password=12345");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            // PreparedStatement pstmt = conn.prepareStatement("SELECT orderNumber, orderDate, requiredDate, shippedDate, status, comments, customerNumber FROM orders WHERE orderNumber=?");
            pstmt.setInt(1, orderNumber);

            System.out.println("Press enter key to start retrieving Order Details");
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

            System.out.println("Order Date:            " + orderDate);
            System.out.println("Required Date:         " + requiredDate);
            System.out.println("Shipped Date:          " + orderDate); 
            System.out.println("Status:                " + status);
            System.out.println("Comments:              " + comments);
            System.out.println("Customer Number:       " + customerNumber); 

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

    //d. Cancel order --- SKELETON CODE --- 
    public int cancelOrder(){
        try{
            
            return 1;
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    
    public static void main (String args[]) {
        Scanner sc     = new Scanner (System.in);
        int     choice = 0;
        // Letting the use choose between the two functions
        System.out.println("Enter [1] Get product Info  [2] Order Product [3] Retrieve Info  [4] Cancel Order");
        choice = sc.nextInt();
        products p = new products();
        if (choice==1) p.getInfo();
        // if (choice==2) p.orderProduct();
        if (choice==3) p.retrieveOrderInfo();
        // if (choice==4) p.cancelOrder();
        
        System.out.println("Press enter key to continue....");
        sc.nextLine();
    }
    
}