package wind.instrument.competitions.middle;

/**
 * Special exception for wrong user replies on anti-bot question
 */
public class BotCheckException extends Exception{
    public BotCheckException(){
        super();
    }

    public BotCheckException(String message){
        super(message);
    }
}
