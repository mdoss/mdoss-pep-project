package Controller;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
/**
 *  The included endpoints:
 *
 *  GET localhost:8080/Messages : retrieve all Messages
 *
 *  POST localhost:8080/Messages : post a new Message.  example:
 *      {
 *          "posted_by":1,
 *          "message_text":"my favorite Message",
 *          "time_posted_epoch":1
 *      }
 *
 *  GET localhost:8080/Messages/available : retrieve all Messages with a copies_available of at least 1
 *
 *  GET localhost:8080/Accounts : retrieve all Accounts
 *
 *  POST localhost:8080/Accounts : post a new Account. a new Account should be contained in the body of the request as a
 *  JSON representation. It should not include the Account_id field as this will be automatically generated. example:
 *      {
 *          "name":"mrs writer"
 *      }
 */
public class SocialMediaController {
    MessageService messageService;
    AccountService accountService;

    public SocialMediaController(){
        this.messageService = new MessageService();
        this.accountService = new AccountService();
    }

    /**
     * Method defines the structure of the Javalin Library API. Javalin methods will use handler methods
     * to manipulate the Context object, which is a special object provided by Javalin which contains information about
     * HTTP requests and can generate responses.
     */
    public Javalin startAPI(){
        Javalin app = Javalin.create();
        app.get("/accounts/{account_id}/messages", this::getMessagesByAccountIdHandler);


        app.post("/register", this::postAccountHandler);
        app.post("/login", this::postAccountLoginHandler);
        app.post("/messages", this::postMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageByIdHandler);
        app.patch("/messages/{message_id}", this::updateMessageByIdHandler);


        


        return app;
        //app.start(8080);
    }

    /**
     * Handler to create a new Account.
     * The Jackson ObjectMapper will automatically convert the JSON of the POST request into an Account object.
     * If AccountService returns a null Account (meaning posting an Account was unsuccessful), the API will return a 400
     * message (client error). There is no need to change anything in this method.
     * @param ctx the context object handles information HTTP requests and generates responses within Javalin. It will
     *            be available to this method automatically thanks to the app.post method.
     * @throws JsonProcessingException will be thrown if there is an issue converting JSON into an object.
     */
    private void postAccountHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        Account addedAccount = accountService.addAccount(account);
        if(addedAccount!=null){
            ctx.json(mapper.writeValueAsString(addedAccount));
        }else{
            ctx.status(400);
        }
    }

    /**
     * Handler to login to an account.
     * The Jackson ObjectMapper will automatically convert the JSON of the POST request into an Account object.
     * If AccountService returns a null Account (meaning posting an Account was unsuccessful), the API will return a 400
     * message (client error). There is no need to change anything in this method.
     * @param ctx the context object handles information HTTP requests and generates responses within Javalin. It will
     *            be available to this method automatically thanks to the app.post method.
     * @throws JsonProcessingException will be thrown if there is an issue converting JSON into an object.
     */
    private void postAccountLoginHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        Account returnedAccount = accountService.getAccount(account);
        if(returnedAccount!=null){
            ctx.json(mapper.writeValueAsString(returnedAccount));
        }else{
            ctx.status(401);
        }
    }
    /**
     * Handler to retrieve all Accounts. 
     * @param ctx the context object handles information HTTP requests and generates responses within Javalin. It will
     *            be available to this method automatically thanks to the app.put method.
     */
    private void getAllAccountsHandler(Context ctx) {
        List<Account> accounts = accountService.getAllAccounts();
        ctx.json(accounts);
    }
    /**
     * Handler to post a new Message. 
     * The Jackson ObjectMapper will automatically convert the JSON of the POST request into a Message object.
     * If MessageService returns a null Message (meaning posting a Message was unsuccessful), the API will return a 400
     * message (client error).
     * @param ctx the context object handles information HTTP requests and generates responses within Javalin. It will
     *            be available to this method automatically thanks to the app.post method.
     * @throws JsonProcessingException will be thrown if there is an issue converting JSON into an object.
     */
    private void postMessageHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(), Message.class);
        Message addedMessage = messageService.addMessage(message);
        if(addedMessage!=null){
            ctx.json(mapper.writeValueAsString(addedMessage));
        }else{
            ctx.status(400);
        }
    }
    /**
     * Handler to retrieve all Messages. 
     * @param ctx the context object handles information HTTP requests and generates responses within Javalin. It will
     *            be available to this method automatically thanks to the app.put method.
     */
    public void getAllMessagesHandler(Context ctx){
        List<Message> messages = messageService.getAllMessages();
        ctx.json(messages);
    }
    /**
     * Retrieve message by ID
     * The response body should contain a JSON representation of the message identified by the message_id. 
     * It is expected for the response body to simply be empty if there is no such message. 
     * The response status should always be 200, which is the default
     * @param ctx the context object handles information HTTP requests and generates responses within Javalin. It will
     *            be available to this method automatically thanks to the app.put method.
     */
    public void getMessageByIdHandler(Context ctx){
        int message_id = Integer.valueOf(ctx.pathParam("message_id"));
        Message message = messageService.getMessageById(message_id);
        if(message == null)
            ctx.json("");
        else
            ctx.json(message);
    }
    /**
     * delete message by ID
     * The deletion of an existing message should remove an existing message from the database. If the message existed, 
     * the response body should contain the now-deleted message. The response status should be 200, which is the default.
        - If the message did not exist, the response status should be 200, but the response body should be empty. 
        This is because the DELETE verb is intended to be idempotent, ie, multiple calls to the DELETE endpoint 
        should respond with the same type of response.
     * @param ctx the context object handles information HTTP requests and generates responses within Javalin. It will
     *            be available to this method automatically thanks to the app.put method.
     */
    public void deleteMessageByIdHandler(Context ctx){
        int message_id = Integer.valueOf(ctx.pathParam("message_id"));
        Message message = messageService.deleteMessageById(message_id);
        if(message == null)
            ctx.json("");
        else
            ctx.json(message);
    }
    /**
     * update message by ID
     * If the update is successful, the response body should contain the full updated message (including message_id, posted_by, message_text, and time_posted_epoch), and the response status should be 200, which is the default. The message existing on the database should have the updated message_text.
    - If the update of the message is not successful for any reason, the response status should be 400. (Client error)
     * @param ctx the context object handles information HTTP requests and generates responses within Javalin. It will
     *            be available to this method automatically thanks to the app.put method.
     */ 
    public void updateMessageByIdHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message messag = mapper.readValue(ctx.body(), Message.class);
        
        int message_id = Integer.valueOf(ctx.pathParam("message_id"));
        String text = messag.getMessage_text();

        Message message = messageService.updateMessageById(message_id, text);
        if(message!=null){
            ctx.json(message);
        }else{
            ctx.status(400);
        }
    }

    public void getMessagesByAccountIdHandler(Context ctx){
        int account_id = Integer.valueOf(ctx.pathParam("account_id"));
        List<Message> messages = messageService.getMessagesById(account_id);
        if(messages == null)
            ctx.json("");
        else
            ctx.json(messages);
    }
    
}