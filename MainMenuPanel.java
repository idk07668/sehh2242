import java.awt.*;
import javax.swing.*;

public class MainMenuPanel extends JPanel {
    private GuiController controller;
    private MainFrame frame;

    public MainMenuPanel(GuiController controller, MainFrame frame) {
        this.controller = controller;
        this.frame = frame;
        init();
    }

    private void init() {
        setLayout(new BorderLayout(8,8));
        JPanel btns = new JPanel(new GridLayout(3,1,6,6));
        JButton balanceBtn = new JButton("Balance Inquiry");
        JButton transferBtn = new JButton("Transfer");
        JButton logoutBtn = new JButton("Logout");
        btns.add(balanceBtn);
        btns.add(transferBtn);
        btns.add(logoutBtn);
        add(btns, BorderLayout.CENTER);

        balanceBtn.addActionListener(e -> {
            double avail = controller.getAvailableBalance();
            double total = controller.getTotalBalance();
            JOptionPane.showMessageDialog(this, String.format("Available: $%,.2f\nTotal: $%,.2f", avail, total), "Balance", JOptionPane.INFORMATION_MESSAGE);
        });

        transferBtn.addActionListener(e -> {
            frame.switchPanel(new TransferPanel(controller, frame));
        });

        logoutBtn.addActionListener(e -> {
            controller.logout();
            frame.switchPanel(new LoginPanel(controller, frame));
        });
    }

    // Inner TransferPanel keeps files minimal
    static class TransferPanel extends JPanel {
        private GuiController controller;
        private MainFrame frame;
        private JTextField targetField;
        private JTextField amountField;

        public TransferPanel(GuiController controller, MainFrame frame) {
            this.controller = controller;
            this.frame = frame;
            init();
        }

        private void init() {
            setLayout(new BorderLayout(8,8));
            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(4,4,4,4);

            c.gridx = 0; c.gridy = 0; form.add(new JLabel("Target Account:"), c);
            targetField = new JTextField(12);
            c.gridx = 1; form.add(targetField, c);

            c.gridx = 0; c.gridy = 1; form.add(new JLabel("Amount:"), c);
            amountField = new JTextField(12);
            c.gridx = 1; form.add(amountField, c);

            add(form, BorderLayout.NORTH);

            // attach keypad to amount field for numeric entry
            VirtualKeypad keypad = new VirtualKeypad(amountField, () -> performTransfer());
            add(keypad, BorderLayout.CENTER);

            JPanel bottom = new JPanel();
            JButton doTransfer = new JButton("Transfer");
            JButton cancel = new JButton("Cancel");
            bottom.add(doTransfer);
            bottom.add(cancel);
            add(bottom, BorderLayout.SOUTH);

            doTransfer.addActionListener(e -> performTransfer());
            cancel.addActionListener(e -> frame.switchPanel(new MainMenuPanel(controller, frame)));
        }

        private void performTransfer() {
            try {
                int target = Integer.parseInt(targetField.getText().trim());
                double amount = Double.parseDouble(amountField.getText().trim());
                String res = controller.transferTo(target, amount);
                JOptionPane.showMessageDialog(this, res);
                if (res.equals("Transfer successful.")) frame.switchPanel(new MainMenuPanel(controller, frame));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numeric values.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
