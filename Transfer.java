public class Transfer extends Transaction {
   private Keypad keypad;           // reference to keypad  
   private double amount;           // transfer amount

   private static final int CANCELED = 0;
   public Transfer(int userAccountNumber, Screen atmScreen,     // Constructor to initialize Transfer with account number, atmscreen, 
   BankDatabase atmBankDatabase, Keypad atmKeypad) {            // bank database, and keypad
      super(userAccountNumber, atmScreen, atmBankDatabase);
      this.keypad = atmKeypad;
   }

   public void execute() {
     Screen screen = getScreen();                      //Get the screen object
     BankDatabase bankDatabase = getBankDatabase();    //Get the bank database object   
     while (true) {

     screen.displayMessage("\nEnter target account number, input 0 can cancel:");   // Prompt the user to input the target account
     int target = keypad.getInput();
     if(target == CANCELED) {                          // Cancel the progress if the user type cancel
        screen.displayMessageLine("\nCanceling transaction ");
        return;

     }
     if(target == getAccountNumber()){
      screen.displayMessageLine("\nTarget cannot be same as user's own account.");
      continue;
     }
     if(!bankDatabase.accountExists(target)){   // Verify if the target account exists in the bank database
      screen.displayMessageLine("\nThe account does not exist, cancel the transaction");
      continue;
     }
     screen.displayMessage ("Enter transfer amount in HKD , or 0 to cancel :");     // Prompt the user to input the transfer amount
     float inputAmount = (float) keypad.getInputDouble();
     if(inputAmount == CANCELED) {            // Check if the user chose to cancel the transaction
         screen.displayMessageLine("\nCanceling transaction");
         return;
      }
     if (inputAmount <= 0 ) {                 // Display error message and continue the loop if user input a negative amount to transfer
         screen.displayMessageLine("\nInvalid amount.Which must be a positive number. Cancel transaction");
         continue;
      }
      amount = inputAmount;
      double available = bankDatabase.getAvailableBalance(getAccountNumber());
      if (amount > available) {               // Display error message and continue the loop if user doesn't have enough funds to transfer
         screen.displayMessageLine("\nYour account does not have sufficient funds. The transaction has been canceled.");
         continue;
      }
      bankDatabase.debit(getAccountNumber(), amount);   // Debit transfer from user's account'
      bankDatabase.credit(target, amount);              // Credit transfer to target account

      screen.displayMessageLine("\nTransfer completed successfully.");      // Notify the user that the transfer is complete
      screen.displayMessage("Transferred: ");                               // Allow users to confirm transaction details
      screen.displayDollarAmount(amount);
      screen.displayMessageLine("");
      screen.displayMessage("To account: ");
      screen.displayMessageLine(String.valueOf(target));
      break;                                                               // Exit the loop
     }
  }

}                          