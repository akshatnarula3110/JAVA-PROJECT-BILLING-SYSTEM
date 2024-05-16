import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class signupform extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton signupButton;
    private JButton deleteButton;
    private JCheckBox showPasswordCheckBox;

    public signupform() {
        setTitle("Signup Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1));

        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new FlowLayout());
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);
        add(usernamePanel);

        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new FlowLayout());
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        add(passwordPanel);

        JPanel showPasswordPanel = new JPanel();
        showPasswordPanel.setLayout(new FlowLayout());
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordPanel.add(showPasswordCheckBox);
        add(showPasswordPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        signupButton = new JButton("Signup");
        deleteButton = new JButton("Delete");
        buttonPanel.add(signupButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel);

        showPasswordCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    passwordField.setEchoChar((char) 0);
                } else {
                    passwordField.setEchoChar('*');
                }
            }
        });

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                handleSignup(username, password);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usernameToDelete = usernameField.getText();
                handleDelete(usernameToDelete);
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    private void handleSignup(String username, String password) {
        insertUserSignup(username, password);
    }

    private void insertUserSignup(String username, String password) {
        String url = "jdbc:mysql://localhost:3306/billingsystem";
        String user = "root";
        String dbPassword = "Jatt3110@@@";

        try (Connection con = DriverManager.getConnection(url, user, dbPassword)) {
            String query = "INSERT INTO users_signup (username, password) VALUES (?, ?)";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, username);
                ps.setString(2, password);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Signup successful!");
                    dispose();
                    // Show the home page
                    SwingUtilities.invokeLater(() -> {
                        new home().setVisible(true);
                    });
                } else {
                    JOptionPane.showMessageDialog(this, "Signup failed.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void handleDelete(String username) {
        String url = "jdbc:mysql://localhost:3306/billingsystem";
        String user = "root";
        String dbPassword = "Jatt3110@@@";

        try (Connection con = DriverManager.getConnection(url, user, dbPassword)) {
            String query = "DELETE FROM users_signup WHERE username = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, username);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "User deleted successfully!");
                    // Clear the username and password fields
                    usernameField.setText("");
                    passwordField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "User not found.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                signupform sf = new signupform();
                sf.setVisible(true);
            }
        });
    }
}
