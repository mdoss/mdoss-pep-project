package Service;

import DAO.MessageDAO;
import Model.Message;
import Service.AccountService;
import Model.Account;

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
public class MessageService {
    public MessageDAO messageDAO;
    private AccountService accountService;

    /**
     * No-args constructor for MessageService which creates a MessageDAO.
     * There is no need to change this constructor.
     */
    public MessageService(){
        messageDAO = new MessageDAO();
        accountService = new AccountService();
    }
    /**
     * Constructor for a MessageService when a MessageDAO is provided.
     * This is used for when a mock MessageDAO that exhibits mock behavior is used in the test cases.
     * This would allow the testing of MessageService independently of MessageDAO.
     * There is no need to modify this constructor.
     * @param messageDAO
     */
    public MessageService(MessageDAO messageDAO){
        this.messageDAO = messageDAO;
    }
    /**
     * TODO: Use the MessageDAO to retrieve all Messages.
     * @return all Messages.
     */
    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    public Message getMessageById(int message_id) {
        return messageDAO.getMessageById(message_id);
    }

    public List<Message> getMessagesById(int account_id) {
        return messageDAO.getMessagesById(account_id);
    }

    /**
        Delete message by ID

     * The deletion of an existing message should remove an existing message from the database. 
     * If the message existed, the response body should contain the now-deleted message. 
     * The response status should be 200, which is the default.
        - If the message did not exist, the response status should be 200, but the response body should be empty. 
        This is because the DELETE verb is intended to be idempotent, ie, multiple calls to the DELETE endpoint should respond 
        with the same type of response.
        @return Message if it was successfully persisted, null if it was not successfully persisted (eg if the Message primary
     * key was already in use.)
     */
    public Message deleteMessageById(int message_id) {
        Message message = messageDAO.getMessageById(message_id);
        if(message != null)
        {
            messageDAO.deleteMessageById(message_id);
            return message;
        }
        return null;
            
    }

    /**
        Update message by ID

     * The update of a message should be successful if and only if the message 
     * id already exists and the new message_text is not blank and is not over 255 characters
     * 
     * @return Message if it was successfully persisted, null if it was not successfully persisted (eg if the Message primary
     * key was already in use.)
     */
    public Message updateMessageById(int message_id, String updatedText) {
        Message message = messageDAO.getMessageById(message_id);
        if(message == null || updatedText.length() == 0 || updatedText.length() > 255)
        {
            return null;
        }
        return messageDAO.updateMessageById(new Message(message.getMessage_id(), message.getPosted_by(), updatedText, message.getTime_posted_epoch()));
    }


    /**
     * 
     * The creation of the message will be successful if and only if the message_text is not blank, is not over 255 characters,
     *  and posted_by refers to a real, existing user. If successful, the response body should contain a JSON of the message, 
     * including its message_id. The response status should be 200, which is the default. 
     * The new message should be persisted to the database.
     * @param message a Message object.
     * @return Message if it was successfully persisted, null if it was not successfully persisted (eg if the Message primary
     * key was already in use.)
     */
    public Message addMessage(Message message) {
        if(message.getMessage_text().length() == 0 || message.getMessage_text().length() > 255 || messageDAO.getMessageById(message.getMessage_id()) == message)
            return null;

        if(accountService.getAccountbyId(message.getPosted_by()) == null) //Check if account exists
            return null;
        
        return messageDAO.insertMessage(message);
    }
    /**
     * TODO: Use the MessageDAO to retrieve a list of all Messages that have a MessageCount above 0.
     * @return all available Messages (MessageCount over zero)
     */
    public List<Message> getAllAvailableMessages() {
        return messageDAO.getMessagesWithMessageCountOverZero();
    }

}