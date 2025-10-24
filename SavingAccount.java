
public class SavingAccount extends Account 
{
   private double interestRate; 
   public SavingAccount( int accountNumber, int pin, 
      double availableBalance, double totalBalance )
   {
      super( accountNumber, pin, availableBalance, totalBalance, "SAVING" );
      this.interestRate = 0.0025; 
   }
   public SavingAccount( int accountNumber, int pin, 
      double availableBalance, double totalBalance, double interestRate )
   {
      super( accountNumber, pin, availableBalance, totalBalance, "SAVING" );
      this.interestRate = interestRate;
   }
   
   public double getInterestRate() { 
      return interestRate; 
   }
   public void setInterestRate( double rate ) { 
      interestRate = rate; 
   }
}
