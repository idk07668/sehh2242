public class GuiController {
    private BankDatabase bankDatabase;
    private CashDispenser cashDispenser;
    private int currentAccount = -1;

    public GuiController(BankDatabase db, CashDispenser dispenser) {
        this.bankDatabase = db;
        this.cashDispenser = dispenser;
    }

    public boolean authenticate(int account, int pin) {
        boolean ok = bankDatabase.authenticateUser(account, pin);
        if (ok) currentAccount = account;
        return ok;
    }

    public void logout() {
        currentAccount = -1;
    }

    public double getAvailableBalance() {
        if (currentAccount < 0) return 0.0;
        return bankDatabase.getAvailableBalance(currentAccount);
    }

    public double getTotalBalance() {
        if (currentAccount < 0) return 0.0;
        return bankDatabase.getTotalBalance(currentAccount);
    }

    public String transferTo(int targetAccount, double amount) {
        if (currentAccount < 0) return "No user logged in.";
        if (!bankDatabase.accountExists(targetAccount)) return "Target account not found.";
        if (amount <= 0) return "Amount must be positive.";
        double avail = bankDatabase.getAvailableBalance(currentAccount);
        if (amount > avail) return "Insufficient funds.";
        bankDatabase.debit(currentAccount, amount);
        bankDatabase.credit(targetAccount, amount);
        return "Transfer successful.";
    }

    public String withdraw(double amount) {
        if (currentAccount < 0) return "No user logged in.";
        if (amount <= 0) return "Amount must be positive.";
        double avail = bankDatabase.getAvailableBalance(currentAccount);
        if (amount > avail) return "Insufficient funds.";
        if (!cashDispenser.isSufficientCashAvailable((int)amount)) return "ATM has insufficient cash.";
        bankDatabase.debit(currentAccount, amount);
        cashDispenser.dispenseCash((int)amount);
        return "Withdrawal complete.";
    }

    // helper: check whether an account exists (used by Login two-step flow)
    public boolean accountExists(int accountNumber) {
        return bankDatabase.accountExists(accountNumber);
    }
}
