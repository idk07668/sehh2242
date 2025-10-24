class ChequeAccount extends Account {
    private double chequeLimit; 
    public ChequeAccount(int accountNumber, int pin, double availableBalance, double totalBalance) {
    super(accountNumber, pin, availableBalance, totalBalance, null);
        chequeLimit = 50000;
    }
    public void debit(double amount){
    super.totalBalance = totalBalance - amount;
    System.out.println("get out " + amount + " new totalBalance is " + super.totalBalance);
    }
    
    public void writeCheque(double amount) {
        if (amount > chequeLimit) {
            System.out.println("cannot");
        } else if (amount > availableBalance) {
            System.out.println("cannot");
        } else {
            debit(amount);

        }
    }
}
