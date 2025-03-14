package Service;

import Model.Account;
import DAO.AccountDAO;

import java.util.List;


/**
 * The purpose of a Service class is to contain "business logic" that sits between the web layer (controller) and
 * persistence layer (DAO). That means that the Service class performs tasks that aren't done through the web or
 * SQL: programming tasks like checking that the input is valid, conducting additional security checks, or saving the
 * actions undertaken by the API to a logging file.
 *
 * It's perfectly normal to have Service methods that only contain a single line that calls a DAO method. An
 * application that follows best practices will often have unnecessary code, but this makes the code more
 * readable and maintainable in the long run!
 */
public class AccountService {
    private AccountDAO accountDAO;
    /**
     * no-args constructor for creating a new AccountService with a new AccountDAO.
     */
    public AccountService(){
        accountDAO = new AccountDAO();
    }
    /**
     * Constructor for a AccountService when a AccountDAO is provided.
     * @param accountDAO
     */
    public AccountService(AccountDAO accountDAO){
        this.accountDAO = accountDAO;
    }
    /**
     * Use the AccountDAO to retrieve all Accounts.
     *
     * @return all Accounts
     */
    public List<Account> getAllAccounts() {
        return accountDAO.getAllAccounts();
    }
    /**
     * The registration will be successful if and only if the username is not blank, the password is at least 4 characters long
     * and an Account with that username does not already exist. If all these conditions are met, 
     * the response body should contain a JSON of the Account, including its account_id.
     * The response status should be 200 OK, which is the default. The new account should be persisted to the database.
       If the registration is not successful, the response status should be 400. (Client error)
     * @param account an Account object.
     * @return The persisted Account if the persistence is successful.
     */
    public Account addAccount(Account account) {
        if(account.getUsername().length() == 0)
            return null;
        else if ( account.getPassword().length() < 4 )
            return null;
        else if (checkUsernameAvailable(account.getUsername()) == false)
            return null;

        return accountDAO.insertAccount(account);
    }
    /**
     * Checks database for an account
     * @param account
     * @return the account with matching username password in database. Null if doesn't exist
     */
    public Account getAccount(Account account) {
        return accountDAO.getAccount(account);
    }
    /**
     * Checks database for an account by id
     * @param account
     * @return the account with matching account id in database. Null if doesn't exist
     */
    public Account getAccountbyId(int account_id) {
        return accountDAO.getAccountById(account_id);
    }
    /**
     * Checks to see if the username is available
     * @param username
     * @return True if available
     */
    private boolean checkUsernameAvailable(String username) {
        List<Account> accounts = accountDAO.getAllAccounts();

        for(int i = 0; i < accounts.size(); i++)
        {
            if(accounts.get(i).getUsername() == username)
            {
                return false;
            }
        }
        return true;
    }
}