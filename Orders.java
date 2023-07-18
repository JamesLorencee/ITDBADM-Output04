import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;

public class Orders {
    public int  orderNumber;
    public String orderDate;
    public String requiredDate;
    public String shippedDate;
    public String status;
    public String comments;
    public int customerNumber;

    public Orders() {}
    public int cancelOrder() {
        ArrayList newQuantity = new ArrayList<>();
        ArrayList productcodes = new ArrayList<String>();

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Order Number:");
        orderNumber = sc.nextInt();

        try {
            Connection conn;
            //CHANGE YOUR PASSWORD//
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsales?useTimezone=true&serverTimezone=UTC&user=root&password=123456");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            PreparedStatement pstmt = conn.prepareStatement("SELECT orderNumber, orderDate, requiredDate, shippedDate, status, comments, customerNumber FROM orders WHERE orderNumber=?");
            pstmt.setInt(1, orderNumber);

            System.out.println("Press enter key to start retrieving the data");
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
            pstmt = conn.prepareStatement("UPDATE orders SET status = 'Cancelled' WHERE orderNumber=? AND status = 'Shipped';");
            pstmt.setInt(1, orderNumber);
            pstmt.executeUpdate();

            //ADDS THE QUANTITY ORDERED BACK TO QUANTITY IN STOCK//
            pstmt = conn.prepareStatement("SELECT (p.quantityInStock + od.quantityOrdered) AS newQty, p.productCode FROM products p JOIN orderdetails od ON od.productCode = p.productCode WHERE od.orderNumber=?;");
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

        Orders o = new Orders();
        o.cancelOrder();

        System.out.println("Press enter key to continue....");
        sc.nextLine();
    }
}
