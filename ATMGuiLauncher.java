import javax.swing.SwingUtilities;

public class ATMGuiLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BankDatabase db = new BankDatabase();
            CashDispenser dispenser = new CashDispenser();
            GuiController controller = new GuiController(db, dispenser);
            new MainFrame(controller);
        });
    }
}
