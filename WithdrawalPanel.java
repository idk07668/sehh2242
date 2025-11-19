import java.awt.*;
import javax.swing.*;

public class WithdrawalPanel extends JPanel {
    private GuiController controller;
    private MainFrame frame;
    private JTextField amountField;

    public WithdrawalPanel(GuiController controller, MainFrame frame) {
        this.controller = controller;
        this.frame = frame;
        init();
    }

    private void init() {
        setLayout(new BorderLayout(8,8));
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);

        c.gridx = 0; c.gridy = 0; form.add(new JLabel("Amount (HKD):"), c);
        amountField = new JTextField(12);
        c.gridx = 1; form.add(amountField, c);

        add(form, BorderLayout.NORTH);

        // attach virtual keypad for amount entry
        VirtualKeypad keypad = new VirtualKeypad(amountField, () -> doWithdraw());
        add(keypad, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton withdrawBtn = new JButton("Withdraw");
        JButton cancel = new JButton("Cancel");
        bottom.add(withdrawBtn);
        bottom.add(cancel);
        add(bottom, BorderLayout.SOUTH);

        withdrawBtn.addActionListener(e -> doWithdraw());
        cancel.addActionListener(e -> frame.switchPanel(new MainMenuPanel(controller, frame)));
    }

    private void doWithdraw() {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // require multiples of 100
            if (((int)amount) % 100 != 0) {
                JOptionPane.showMessageDialog(this, "Amount must be a multiple of 100.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String res = controller.withdraw(amount);
            JOptionPane.showMessageDialog(this, res);
            if (res.equals("Withdrawal complete.")) {
                frame.switchPanel(new MainMenuPanel(controller, frame));
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric amount.", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}
