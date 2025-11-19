import java.awt.*;
import javax.swing.*;

public class LoginPanel extends JPanel {
    private GuiController controller;
    private MainFrame frame;
    private JTextField accountField;
    private JPasswordField pinField;

    public LoginPanel(GuiController controller, MainFrame frame) {
        this.controller = controller;
        this.frame = frame;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10,10));
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);

        c.gridx = 0; c.gridy = 0; form.add(new JLabel("Account:"), c);
        accountField = new JTextField(12);
        c.gridx = 1; form.add(accountField, c);

        c.gridx = 0; c.gridy = 1; form.add(new JLabel("PIN:"), c);
        pinField = new JPasswordField(12);
        c.gridx = 1; form.add(pinField, c);

        JButton loginBtn = new JButton("Login");
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2; form.add(loginBtn, c);

        add(form, BorderLayout.NORTH);

        // attach virtual keypad to PIN field
        VirtualKeypad keypad = new VirtualKeypad(pinField, () -> doLogin());
        add(keypad, BorderLayout.CENTER);

        loginBtn.addActionListener(e -> doLogin());
    }

    private void doLogin() {
        try {
            int acc = Integer.parseInt(accountField.getText().trim());
            int pin = Integer.parseInt(new String(pinField.getPassword()).trim());
            if (controller.authenticate(acc, pin)) {
                frame.switchPanel(new MainMenuPanel(controller, frame));
            } else {
                JOptionPane.showMessageDialog(this, "Invalid account or PIN", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter numeric account and PIN", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}
