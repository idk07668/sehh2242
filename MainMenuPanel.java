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
        JPanel btns = new JPanel(new GridLayout(4,1,6,6));
        JButton balanceBtn = new JButton("Balance Inquiry");
        JButton transferBtn = new JButton("Transfer");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton logoutBtn = new JButton("Logout");
        btns.add(balanceBtn);
        btns.add(transferBtn);
        btns.add(withdrawBtn);
        btns.add(logoutBtn);
        add(btns, BorderLayout.CENTER);

        balanceBtn.addActionListener(e -> {
            frame.switchPanel(new BalancePanel(controller, frame));
        });

        transferBtn.addActionListener(e -> {
            frame.switchPanel(new TransferPanel(controller, frame));
        });

        withdrawBtn.addActionListener(e -> {
            frame.switchPanel(new WithdrawalPanel(controller, frame));
        });

        logoutBtn.addActionListener(e -> {
            controller.logout();
            // show login with eject message
            frame.switchPanel(new LoginPanel(controller, frame, "Please eject your card."));
        });
    }

    // Inner TransferPanel: two-step flow (Target -> Amount) using one shared keypad
    static class TransferPanel extends JPanel {
        private GuiController controller;
        private MainFrame frame;
        private JTextField targetField;
        private JTextField amountField;
        private CardLayout cards;
        private JPanel cardPanel;
        private int pendingTarget = -1;

        public TransferPanel(GuiController controller, MainFrame frame) {
            this.controller = controller;
            this.frame = frame;
            init();
        }

        private void init() {
            setLayout(new BorderLayout(8,8));

            cards = new CardLayout();
            cardPanel = new JPanel(cards);

            // --- Target card ---
            JPanel targetCard = new JPanel(new GridBagLayout());
            GridBagConstraints tc = new GridBagConstraints();
            tc.insets = new Insets(8,8,8,8);
            tc.gridx = 0; tc.gridy = 0; targetCard.add(new JLabel("Enter target account:"), tc);
            targetField = new JTextField(14);
            tc.gridx = 0; tc.gridy = 1; targetCard.add(targetField, tc);

            // --- Amount card ---
            JPanel amountCard = new JPanel(new GridBagLayout());
            GridBagConstraints ac = new GridBagConstraints();
            ac.insets = new Insets(8,8,8,8);
            ac.gridx = 0; ac.gridy = 0; amountCard.add(new JLabel("Enter amount:"), ac);
            amountField = new JTextField(14);
            ac.gridx = 0; ac.gridy = 1; amountCard.add(amountField, ac);

            cardPanel.add(targetCard, "TARGET");
            cardPanel.add(amountCard, "AMOUNT");

            add(cardPanel, BorderLayout.CENTER);

            // instruction label
            JLabel instr = new JLabel("Enter target account and press Next", SwingConstants.CENTER);
            add(instr, BorderLayout.NORTH);

            // single keypad shared between cards
            final VirtualKeypad[] keypadHolder = new VirtualKeypad[1];
            VirtualKeypad keypad = new VirtualKeypad(() -> {
                // Enter behavior: on target card move to amount, on amount card perform transfer
                if (isShowingTarget()) {
                    doTargetNext(instr, keypadHolder);
                } else {
                    doPerformTransfer();
                }
            });
            keypadHolder[0] = keypad;

            // place keypad in south and button row below it
            JPanel south = new JPanel(new BorderLayout());
            south.add(keypad, BorderLayout.CENTER);

            JPanel buttonRow = new JPanel();
            JButton nextBtn = new JButton("Next");
            JButton backBtn = new JButton("Back");
            JButton transferBtn = new JButton("Transfer");
            JButton cancelBtn = new JButton("Cancel");
            buttonRow.add(nextBtn);
            buttonRow.add(backBtn);
            buttonRow.add(transferBtn);
            buttonRow.add(cancelBtn);
            south.add(buttonRow, BorderLayout.SOUTH);

            add(south, BorderLayout.SOUTH);

            // focus behavior: default to target field
            keypadHolder[0].setTarget(targetField);
            targetField.requestFocusInWindow();

            // buttons
            nextBtn.addActionListener(e -> doTargetNext(instr, keypadHolder));
            backBtn.addActionListener(e -> {
                // go back to target entry
                cards.show(cardPanel, "TARGET");
                keypadHolder[0].setTarget(targetField);
                instr.setText("Enter target account and press Next");
            });
            transferBtn.addActionListener(e -> doPerformTransfer());
            cancelBtn.addActionListener(e -> frame.switchPanel(new MainMenuPanel(controller, frame)));
        }

        private boolean isShowingTarget() {
            // crude check by seeing which field has focus or by checking pendingTarget
            return targetField.hasFocus() || pendingTarget < 0;
        }

        private void doTargetNext(JLabel instr, VirtualKeypad[] keypadHolder) {
            String text = targetField.getText().trim();
            try {
                int acc = Integer.parseInt(text);
                if (!controller.accountExists(acc)) {
                    JOptionPane.showMessageDialog(this, "Target account not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                pendingTarget = acc;
                // switch to amount card
                cards.show(cardPanel, "AMOUNT");
                keypadHolder[0].setTarget(amountField);
                amountField.requestFocusInWindow();
                instr.setText("Enter amount and press Transfer (or Back to change account)");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a numeric account ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        }

        private void doPerformTransfer() {
            try {
                if (pendingTarget < 0) {
                    JOptionPane.showMessageDialog(this, "No target account selected.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                double amount = Double.parseDouble(amountField.getText().trim());
                String res = controller.transferTo(pendingTarget, amount);
                JOptionPane.showMessageDialog(this, res);
                if (res.equals("Transfer successful.")) frame.switchPanel(new MainMenuPanel(controller, frame));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric amount.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // Balance panel shows balances on its own page instead of a dialog
    static class BalancePanel extends JPanel {
        private GuiController controller;
        private MainFrame frame;

        public BalancePanel(GuiController controller, MainFrame frame) {
            this.controller = controller;
            this.frame = frame;
            init();
        }

        private void init() {
            setLayout(new BorderLayout(8,8));
            JPanel info = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(6,6,6,6);
            c.gridx = 0; c.gridy = 0; c.anchor = GridBagConstraints.WEST;
            double avail = controller.getAvailableBalance();
            double total = controller.getTotalBalance();
            info.add(new JLabel(String.format("Available Balance: $%,.2f", avail)), c);
            c.gridy = 1; info.add(new JLabel(String.format("Total Balance:     $%,.2f", total)), c);

            add(info, BorderLayout.CENTER);

            JPanel bottom = new JPanel();
            JButton back = new JButton("Back");
            bottom.add(back);
            add(bottom, BorderLayout.SOUTH);

            back.addActionListener(e -> frame.switchPanel(new MainMenuPanel(controller, frame)));
        }
    }
}
