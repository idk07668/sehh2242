class ChequeAccount extends Account {
    private double chequeLimit; 
    public ChequeAccount(int accountNumber, int pin, double availableBalance, 
                         double totalBalance) {
        super(accountNumber, pin, availableBalance, totalBalance,"CHEQUE");
        chequeLimit = 50000;
    }
    public double getChequeLimit(){
    
    return chequeLimit;
    
    }
    public void setChequeLimit(double limit){
    
    chequeLimit = limit;
    
    }
}