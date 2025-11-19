import java.awt.*;
import javax.swing.*;

/**
 * LoginPanel now uses a two-step flow (CardLayout):
 *  - Step 1: enter account id and press Next (verifies account exists)
 *  - Step 2: enter PIN using virtual keypad and press Login (authenticates)
 */
public class LoginPanel extends JPanel {
    private GuiController controller;
    private MainFrame frame;

    private CardLayout cards;
    private JPanel cardPanel;

    // components for account step
    private JTextField accountField;
    private JButton accountNextBtn;

    // components for PIN step
    private JPasswordField pinField;
    private JButton pinBackBtn;
    private JButton pinLoginBtn;

    private int pendingAccount = -1;

    public LoginPanel(GuiController controller, MainFrame frame) {
        this.controller = controller;
        this.frame = frame;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(8,8));
        cards = new CardLayout();
        cardPanel = new JPanel(cards);

        // --- Account panel ---
        JPanel accountPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.gridx = 0; c.gridy = 0; accountPanel.add(new JLabel("Account ID:"), c);
        accountField = new JTextField(14);
        c.gridx = 1; accountPanel.add(accountField, c);
        accountNextBtn = new JButton("Next");
        c.gridx = 0; c.gridy = 1; c.gridwidth = 2; accountPanel.add(accountNextBtn, c);

        // --- PIN panel ---
        JPanel pinPanel = new JPanel(new BorderLayout(6,6));
        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints t = new GridBagConstraints();
        t.insets = new Insets(4,4,4,4);
        t.gridx = 0; t.gridy = 0; top.add(new JLabel("Enter PIN:"), t);
        pinField = new JPasswordField(12);
        t.gridx = 1; top.add(pinField, t);
        pinPanel.add(top, BorderLayout.NORTH);

        // virtual keypad attached to PIN field
        VirtualKeypad vkp = new VirtualKeypad(pinField, () -> doAuthenticate());
        pinPanel.add(vkp, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        pinBackBtn = new JButton("Back");
        pinLoginBtn = new JButton("Login");
        bottom.add(pinBackBtn);
        bottom.add(pinLoginBtn);
        pinPanel.add(bottom, BorderLayout.SOUTH);

        cardPanel.add(accountPanel, "ACCOUNT");
        cardPanel.add(pinPanel, "PIN");

        add(cardPanel, BorderLayout.CENTER);

        // actions
        accountNextBtn.addActionListener(e -> doAccountNext());
        pinBackBtn.addActionListener(e -> { cards.show(cardPanel, "ACCOUNT"); pendingAccount = -1; pinField.setText(""); });
        pinLoginBtn.addActionListener(e -> doAuthenticate());
    }

    private void doAccountNext() {
        String text = accountField.getText().trim();
        try {
            int acc = Integer.parseInt(text);
            if (!controller.accountExists(acc)) {
                JOptionPane.showMessageDialog(this, "Account not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            pendingAccount = acc;
            // move to PIN card
            cards.show(cardPanel, "PIN");
            pinField.requestFocusInWindow();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a numeric account ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void doAuthenticate() {
        if (pendingAccount < 0) {
            JOptionPane.showMessageDialog(this, "No account selected.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int pin = Integer.parseInt(new String(pinField.getPassword()).trim());
            if (controller.authenticate(pendingAccount, pin)) {
                frame.switchPanel(new MainMenuPanel(controller, frame));
            } else {
                JOptionPane.showMessageDialog(this, "Invalid PIN.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                pinField.setText("");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter numeric PIN.", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}
