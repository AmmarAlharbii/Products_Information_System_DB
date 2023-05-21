import java.sql.*;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ProductsSystem {
     static Scanner scanner = new Scanner(System.in); //Scanner obj
     static Secure s1=new Secure(); // take obj from class secure to make connection

    private static final String PRODUCTS_TABLE_NAME = s1.getTableName(); //Table name
    private static final String[] PRODUCT_HEADER = {"ID", "Type", "Model", "Price", "Count", "DeliveryDate"}; // product columns

    public static void main(String[] args) {
        try {
            Connection connection = s1.secureConnection(); // make connection

            int choice; // choice is menu selector
            do {
                studentInformation(); // display my information
                displayMenu(); // display menu
                choice = scanner.nextInt(); // select number in menu
               scanner.nextLine(); // newline

                switch (choice) {
                    case 1:
                        addProduct(connection, scanner); //add proudct
                        break;
                    case 2:
                        searchProducts(connection, scanner);//search for proudct
                        break;
                    case 3:
                        deleteProduct(connection, scanner); //delete proudct
                        break;
                    case 4:
                        System.out.println("Finishing..."); // exit the system
                        break;
                    default:
                        System.out.println("Wrong number. Please try again."); // for wrong choice
                        break;
                }
            } while (choice != 4);//do while end

            connection.close(); // close connection
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void displayMenu() { // this function will print the menu
        System.out.println("Menu :");
        System.out.println("1. Add a product");
        System.out.println("2. Search for products by model or type");
        System.out.println("3. Delete a product by ID");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void addProduct(Connection connection, Scanner scanner) throws SQLException { // this methoed will add Product to table in sql database
        System.out.println("Enter product details:");

        System.out.print("Type: ");
        String type = scanner.nextLine();// assign type

        System.out.print("Model: ");
        String model = scanner.nextLine();//assign model

        System.out.print("Price: ");
        float price = scanner.nextFloat();//assign price
        while (price < 1) { //if the user enter wrong count
            System.out.println("Invalid number. Please enter a price greater than 0.");
            System.out.print("Price: ");
            price = scanner.nextInt();
        }
        System.out.print("Count: ");

        int count = scanner.nextInt();//assign count
        while (count < 1) { //if the user enter wrong count
            System.out.println("Invalid number. Please enter a number greater than 0.");
            System.out.print("Count: ");
            count = scanner.nextInt();
        }
        System.out.print("Delivery Date (YYYY-MM-DD): ");
        String deliveryDate = scanner.next();//assign date
        while (!dateFormat(deliveryDate)) { //check date
            System.out.println("Invalid date format. Please enter the date in correct format (YYYY-MM-DD).");
            System.out.print("Delivery Date (YYYY-MM-DD): ");
            deliveryDate = scanner.next();
        }

        String insertQuery = "INSERT INTO " + PRODUCTS_TABLE_NAME
                + " (Type, Model, Price, Count, DeliveryDate)"
                + " VALUES (?, ?, ?, ?, ?)"; //sql Query for adding

        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery); //declare preparedStatment
        preparedStatement.setString(1, type);
        preparedStatement.setString(2, model);
        preparedStatement.setFloat(3, price);
        preparedStatement.setInt(4, count);
        preparedStatement.setString(5, deliveryDate);

        int check = preparedStatement.executeUpdate(); //if update the value will be  value >0
        if (check > 0) {//check if the row insert or not
            System.out.println("Product added successfully.");
        }//if end
        else {
            System.out.println("Failed to add product.");
        }
    } // addProduct end

    private static void searchProducts(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter search criteria model or type (if you want to display all the records press enter direct): ");
        String searchText = scanner.nextLine(); // assign type or model

        String selectQuery = "SELECT * FROM " + PRODUCTS_TABLE_NAME
                + " WHERE Model LIKE '%" + searchText + "%' OR Type LIKE '%" + searchText + "%'"; //query for search

        PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);//preparedStatement declare
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            // Print table header
            for (int i = 0; i < PRODUCT_HEADER.length; i++) {
                System.out.printf("%-15s", PRODUCT_HEADER[i]);//display header
            } //for end
            System.out.println();// space

            // Print table rows
            do {
                for (int i = 0; i < PRODUCT_HEADER.length; i++) {
                    System.out.printf("%-15s", resultSet.getString(i + 1));// display the recored
                }
                System.out.println();
            } while (resultSet.next());//do while end
        }
        else { // if don't found any criteria in the table
            System.out.println("No records available for this search criteria.");
        }
        resultSet.close();
        preparedStatement.close();
//-----------------------------------------------------
        // Check if table is empty
        String countQuery = "SELECT COUNT(*) FROM " + PRODUCTS_TABLE_NAME; // to count number of rows in table
        PreparedStatement preparedStatement2  = connection.prepareStatement(countQuery);
        ResultSet countResult = preparedStatement2.executeQuery(countQuery);
        countResult.next();
        int rowCount = countResult.getInt(1);
        if (rowCount == 0) {
            System.out.println("No records in the table."); // this will print if no recoreds in all table
        }
        countResult.close();
        preparedStatement2.close();
    }// searchProducts() end



    private static void deleteProduct(Connection connection, Scanner scanner) throws SQLException {//search  Products from table in sql database
        System.out.print("Enter the ID of the product to delete: ");
        int productId = scanner.nextInt(); //assign id

        String selectQuery = "SELECT * FROM " + PRODUCTS_TABLE_NAME + " WHERE ID = ?"; //query to display the row which have this id
        PreparedStatement prepareStatement = connection.prepareStatement(selectQuery);
        prepareStatement.setInt(1, productId);
        ResultSet resultSet = prepareStatement.executeQuery();

        if (resultSet.next()) {
            System.out.println("Product found:");
            // Print table header colum
            for (int i = 0; i < PRODUCT_HEADER.length; i++) {
                System.out.printf("%-15s", PRODUCT_HEADER[i]); //print header
            }//for end
            System.out.println();//space
            System.out.println(resultSet.getString("ID") + "\t\t\t\t" +
                    resultSet.getString("Type") + "\t\t\t" +
                    resultSet.getString("Model") + "\t\t\t" +
                    resultSet.getString("Price") + "\t\t\t" +
                    resultSet.getString("Count") + "\t\t\t" +
                    resultSet.getString("DeliveryDate")); //print the row which have the id
            System.out.println();
            System.out.print("Are you sure you want to delete this product? (Y/N): ");
            String confirmation = scanner.next(); //assign y or n for confirm

            if (confirmation.equalsIgnoreCase("Y")) { // if press y
                String deleteQuery = "DELETE FROM " + PRODUCTS_TABLE_NAME + " WHERE ID = ?"; //delete query for the row
                PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery); //prepraedstatment
                deleteStatement.setInt(1, productId);
                int check = deleteStatement.executeUpdate();// check the query is work or not
                if (check > 0) {
                    System.out.println("Product deleted successfully.");
                } //third if for check update end
                else {
                    System.out.println("Failed to delete product.");
                }
                deleteStatement.close();
            } //nested if for [Y/N?] end
            else {
                System.out.println("Deletion with ID "+productId+" canceled.");



            }
        }//if end
        else {
            System.out.println("No product found with the specified ID.");
        }

        resultSet.close();
        prepareStatement.close();
    }
    private static boolean dateFormat(String date) {
        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    public static void studentInformation(){ // my information
        System.out.println("===============================");
        System.out.println(" Ammar abdulaziz alharbi ");
        System.out.println(" ID : 441001021");
        System.out.println(" Email :s441001021@st.uqu.edu.sa ");
        System.out.println(" Group : 1");
        System.out.println("===============================");
    }


}









