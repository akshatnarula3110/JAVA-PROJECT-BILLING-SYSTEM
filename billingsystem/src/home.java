import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class home extends JFrame {
    private Connection con;
    private PreparedStatement pst;
    private ResultSet rs;

    private JTextField txtProductCode, txtProductName, txtPrice, txtQuantity;
    private JTextField txtCustomerId, txtCustomerName, txtCustomerEmail, txtCustomerPhone, txtCustomerAddress;
    private JButton btnAdd, btnPrint, btnAddCustomer, btnShowCustomers, btnShowProducts;
    private JTable table;
    private DefaultTableModel model;

    public home() {
        initComponents();
        connectToDatabase();
        displayProducts();
    }

    private void initComponents() {
        setTitle("Billing System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 240, 240));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(220, 220, 220));

        // Table
        model = new DefaultTableModel();
        model.addColumn("Product ID");
        model.addColumn("Product Name");
        model.addColumn("Price");
        model.addColumn("Quantity");
        table = new JTable(model);
        table.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Product details panel
        JPanel productPanel = new JPanel(new GridLayout(2, 4));
        productPanel.setBackground(new Color(200, 200, 200));
        JLabel lblProductCode = new JLabel("Product Code");
        JLabel lblProductName = new JLabel("Product Name");
        JLabel lblPrice = new JLabel("Price");
        JLabel lblQuantity = new JLabel("Quantity");
        txtProductCode = new JTextField();
        txtProductName = new JTextField();
        txtPrice = new JTextField();
        txtQuantity = new JTextField();
        productPanel.add(lblProductCode);
        productPanel.add(txtProductCode);
        productPanel.add(lblProductName);
        productPanel.add(txtProductName);
        productPanel.add(lblPrice);
        productPanel.add(txtPrice);
        productPanel.add(lblQuantity);
        productPanel.add(txtQuantity);

        // Customer details panel
        JPanel customerPanel = new JPanel(new GridLayout(2, 5));
        customerPanel.setBackground(new Color(200, 200, 200));
        JLabel lblCustomerId = new JLabel("Customer ID");
        JLabel lblCustomerName = new JLabel("Customer Name");
        JLabel lblCustomerEmail = new JLabel("Customer Email");
        JLabel lblCustomerPhone = new JLabel("Customer Phone");
        JLabel lblCustomerAddress = new JLabel("Customer Address");
        txtCustomerId = new JTextField();
        txtCustomerName = new JTextField();
        txtCustomerEmail = new JTextField();
        txtCustomerPhone = new JTextField();
        txtCustomerAddress = new JTextField();
        lblCustomerId.setFont(new Font("Arial", Font.BOLD, 14));
        lblCustomerName.setFont(new Font("Arial", Font.BOLD, 14));
        lblCustomerEmail.setFont(new Font("Arial", Font.BOLD, 14));
        lblCustomerPhone.setFont(new Font("Arial", Font.BOLD, 14));
        lblCustomerAddress.setFont(new Font("Arial", Font.BOLD, 14));
        txtCustomerId.setFont(new Font("Arial", Font.PLAIN, 14));
        txtCustomerName.setFont(new Font("Arial", Font.PLAIN, 14));
        txtCustomerEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        txtCustomerPhone.setFont(new Font("Arial", Font.PLAIN, 14));
        txtCustomerAddress.setFont(new Font("Arial", Font.PLAIN, 14));
        customerPanel.add(lblCustomerId);
        customerPanel.add(txtCustomerId);
        customerPanel.add(lblCustomerName);
        customerPanel.add(txtCustomerName);
        customerPanel.add(lblCustomerEmail);
        customerPanel.add(txtCustomerEmail);
        customerPanel.add(lblCustomerPhone);
        customerPanel.add(txtCustomerPhone);
        customerPanel.add(lblCustomerAddress);
        customerPanel.add(txtCustomerAddress);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(220, 220, 220));
        btnAdd = new JButton("Add Product");
        btnPrint = new JButton("Print Bill");
        btnAddCustomer = new JButton("Add Customer");
        btnShowCustomers = new JButton("Show Customers");
        btnShowProducts = new JButton("Show Products");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnPrint);
        buttonPanel.add(btnAddCustomer);
        buttonPanel.add(btnShowCustomers);
        buttonPanel.add(btnShowProducts);

        panel.add(productPanel, BorderLayout.NORTH);
        panel.add(customerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        // Action Listeners
        btnAdd.addActionListener(e -> addProduct());
        btnPrint.addActionListener(e -> printBill());
        btnAddCustomer.addActionListener(e -> addCustomer());
        btnShowCustomers.addActionListener(e -> showCustomers());
        btnShowProducts.addActionListener(e -> showProducts());
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                displaySelectedProduct();
            }
        });
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/billingsystem", "root", "Jatt3110@@@");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addProduct() {
        String productCode = txtProductCode.getText();
        String productName = txtProductName.getText();
        double price = Double.parseDouble(txtPrice.getText());
        int quantity = Integer.parseInt(txtQuantity.getText());

        try {
            String query = "INSERT INTO product (product_id, name, price, quantity) VALUES (?, ?, ?, ?)";
            pst = con.prepareStatement(query);
            pst.setString(1, productCode);
            pst.setString(2, productName);
            pst.setDouble(3, price);
            pst.setInt(4, quantity);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Product added successfully!");
            displayProducts();
            clearFields();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void displaySelectedProduct() {
        int row = table.getSelectedRow();
        if (row != -1) {
            txtProductCode.setText((String) table.getValueAt(row, 0));
            txtProductName.setText((String) table.getValueAt(row, 1));
            txtPrice.setText(String.valueOf(table.getValueAt(row, 2)));
            txtQuantity.setText(String.valueOf(table.getValueAt(row, 3)));
        }
    }

    private void displayProducts() {
        try {
            model.setRowCount(0);
            String query = "SELECT * FROM product";
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();
            while (rs.next()) {
                String productCode = rs.getString("product_id");
                String productName = rs.getString("name");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                model.addRow(new Object[]{productCode, productName, price, quantity});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void clearFields() {
        txtProductCode.setText("");
        txtProductName.setText("");
        txtPrice.setText("");
        txtQuantity.setText("");
    }

    private void addCustomer() {
        String customerId = txtCustomerId.getText();
        String customerName = txtCustomerName.getText();
        String customerEmail = txtCustomerEmail.getText();
        String customerPhone = txtCustomerPhone.getText();
        String customerAddress = txtCustomerAddress.getText();

        try {
            String query = "INSERT INTO customer (customer_id, name, email, phone, address) VALUES (?, ?, ?, ?, ?)";
            pst = con.prepareStatement(query);
            pst.setString(1, customerId);
            pst.setString(2, customerName);
            pst.setString(3, customerEmail);
            pst.setString(4, customerPhone);
            pst.setString(5, customerAddress);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Customer added successfully!");
            clearCustomerFields();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void printBill() {
        // Create a dialog to select products
        JPanel productSelectionPanel = new JPanel(new BorderLayout());
        JLabel lblSelectProducts = new JLabel("Select Products to Include in the Bill:");
        JList<String> productList = new JList<>();
        JScrollPane scrollPane = new JScrollPane(productList);
        productSelectionPanel.add(lblSelectProducts, BorderLayout.NORTH);
        productSelectionPanel.add(scrollPane, BorderLayout.CENTER);

        // Populate the product list with product names
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            String productName = (String) model.getValueAt(i, 1);
            listModel.addElement(productName);
        }
        productList.setModel(listModel);

        // Show the dialog
        int result = JOptionPane.showConfirmDialog(this, productSelectionPanel, "Select Products",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Get the selected product names
            List<String> selectedProducts = productList.getSelectedValuesList();

            // Generate the bill for selected products
            double totalBillAmount = 0.0;
            StringBuilder billDetails = new StringBuilder();
            billDetails.append("Billing ID: ").append(generateBillingID()).append("\n");
            billDetails.append("Date-Time: ").append(getCurrentDateTime()).append("\n\n");
            billDetails.append("Product Details:\n");
            billDetails.append("----------------------------------------------------\n");
            billDetails.append(String.format("%-15s%-25s%-15s%-15s%-15s\n", "Product ID", "Product Name", "Price", "Quantity", "Total Price"));
            for (int i = 0; i < model.getRowCount(); i++) {
                String productName = (String) model.getValueAt(i, 1);
                if (selectedProducts.contains(productName)) {
                    String productID = (String) model.getValueAt(i, 0);
                    double price = (double) model.getValueAt(i, 2);
                    int quantity = (int) model.getValueAt(i, 3);
                    double totalPrice = price * quantity;
                    totalBillAmount += totalPrice;
                    billDetails.append(String.format("%-15s%-25s%-15s%-15s%-15s\n", productID, productName, price, quantity, totalPrice));
                }
            }

            // Display the customer details
            billDetails.append("\nCustomer Details:\n");
            billDetails.append("----------------------------------------------------\n");
            billDetails.append("Customer ID: ").append(txtCustomerId.getText()).append("\n");
            billDetails.append("Name: ").append(txtCustomerName.getText()).append("\n");
            billDetails.append("Email: ").append(txtCustomerEmail.getText()).append("\n");
            billDetails.append("Phone: ").append(txtCustomerPhone.getText()).append("\n");
            billDetails.append("Address: ").append(txtCustomerAddress.getText()).append("\n");
            billDetails.append("----------------------------------------------------\n");

            // Display the total bill amount
            billDetails.append(String.format("%-55s%-15s\n", "Total", totalBillAmount));

            // Display the bill
            JOptionPane.showMessageDialog(this, billDetails.toString(), "Billing Details", JOptionPane.PLAIN_MESSAGE);
        }
    }

    private String generateBillingID() {
        // Logic to generate a unique billing ID
        return "BILL-" + System.currentTimeMillis();
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return sdf.format(date);
    }

    private void clearCustomerFields() {
        txtCustomerId.setText("");
        txtCustomerName.setText("");
        txtCustomerEmail.setText("");
        txtCustomerPhone.setText("");
        txtCustomerAddress.setText("");
    }

    private void showCustomers() {
        try {
            StringBuilder customerDetails = new StringBuilder();
            String query = "SELECT * FROM customer";
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();
            customerDetails.append("Customer Details:\n");
            customerDetails.append("----------------------------------------------------\n");
            while (rs.next()) {
                String customerId = rs.getString("customer_id");
                String customerName = rs.getString("name");
                String customerEmail = rs.getString("email");
                String customerPhone = rs.getString("phone");
                String customerAddress = rs.getString("address");
                customerDetails.append("Customer ID: ").append(customerId).append("\n");
                customerDetails.append("Name: ").append(customerName).append("\n");
                customerDetails.append("Email: ").append(customerEmail).append("\n");
                customerDetails.append("Phone: ").append(customerPhone).append("\n");
                customerDetails.append("Address: ").append(customerAddress).append("\n");
                customerDetails.append("----------------------------------------------------\n");
            }
            JOptionPane.showMessageDialog(this, customerDetails.toString(), "Customer Details", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void showProducts() {
        try {
            StringBuilder productDetails = new StringBuilder();
            String query = "SELECT * FROM product";
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();
            productDetails.append("Product Details:\n");
            productDetails.append("----------------------------------------------------\n");
            while (rs.next()) {
                String productCode = rs.getString("product_id");
                String productName = rs.getString("name");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                productDetails.append("Product ID: ").append(productCode).append("\n");
                productDetails.append("Name: ").append(productName).append("\n");
                productDetails.append("Price: ").append(price).append("\n");
                productDetails.append("Quantity: ").append(quantity).append("\n");
                productDetails.append("----------------------------------------------------\n");
            }
            JOptionPane.showMessageDialog(this, productDetails.toString(), "Product Details", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new home().setVisible(true);
        });
    }
}
