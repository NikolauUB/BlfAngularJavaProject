package wind.instrument.competitions.middle;


import java.util.*;

/**
 * This is an anti-bot question generator
 */
public class Question {
    /**
     * Error messages for wrong choice
     */
    private static ResourceBundle bundle = ResourceBundle.getBundle("Messages");
    /**
     * List of non-wind instruments
     */
    private ArrayList<String> nonWindInstruments = new ArrayList<String>();
    /**
     * List of wind instruments
     */
    private ArrayList<String> windInstruments = new ArrayList<String>();
    /**
     * Amount of wind instruments in current test (random number)
     */
    private Integer windInstAmount;
    /**
     * Correct answers for the test
     */
    private ArrayList<String> correctAnswers = new ArrayList<String>();
    /**
     * Maximum wind instruments in the test
     */
    private static int MAX_WIND_INSTRUMENTS = 6;
    /**
     * Amount of all answers
     */
    private static int MAX_ANSWERS = 8;
    /**
     * Range of answer sequence generation
     */
    private static int RANDOM_DIRECT_OR_ALTERNATIVE = 2;

    public Question() {
        this.createQuestion();
    }

    /**
     * @param replies - replies which come from client
     * @param correctReplies - replies saved in session of client
     * @throws BotCheckException
     */
    public static void  checkUserReply(LinkedList<String> replies, ArrayList<String> correctReplies) throws BotCheckException {
        if (replies == null || replies.size() == 0) {
            throw new BotCheckException(bundle.getString("AntiBotErrorNoReplies"));
        }
        if(correctReplies == null) {
            throw new BotCheckException(bundle.getString("AntiBotErrorEmtpySession"));
        }
        if (replies.size() < correctReplies.size() || replies.size() > correctReplies.size()) {
            throw new BotCheckException(bundle.getString("AntiBotErrorReplyCountWrong"));
        }
        for (String reply : replies) {
            if(!correctReplies.contains(reply)) {
                throw new BotCheckException(bundle.getString("AntiBotErrorWrongReplies"));
            }
        }
    }

    /**
     * Generate random wind instruments amount in current test
     * @param max
     */
    private void generateWindInstrumentAmount(int max) {
        Random  rand = new Random();
        this.windInstAmount = rand.nextInt(max) + 1;
    }

    /**
     * @return list of correct answers for current test
     */
    public ArrayList<String> getCorrectAnswers() {
        return this.correctAnswers;
    }

    /**
     * @return list of all answers for current test
     */
    public  ArrayList<String> getQuestionAnswers() {
        if(windInstruments.size() == 0 || nonWindInstruments.size() == 0) {
            return new ArrayList<String>();
        }
        ArrayList<String> result = new ArrayList<String>();
        this.generateWindInstrumentAmount(MAX_WIND_INSTRUMENTS);
        int directCounter = 0;
        int alternativeCounter = 0;
        HashSet<Integer> duplicateDirectControl = new HashSet<Integer>();
        HashSet<Integer> duplicateAlternativeControl = new HashSet<Integer>();
        Random  rand = new Random();
        while ((directCounter + alternativeCounter) < MAX_ANSWERS ) {
            if (rand.nextInt(RANDOM_DIRECT_OR_ALTERNATIVE) == 0 && directCounter < this.windInstAmount) {
                directCounter = addRandomInstrument(duplicateDirectControl,
                        directCounter, windInstruments, result, true);
            } else if (alternativeCounter < (MAX_ANSWERS - this.windInstAmount)) {
                alternativeCounter = addRandomInstrument(duplicateAlternativeControl,
                        alternativeCounter, nonWindInstruments, result, false);
            }
        }
        return result;
    }

    /**
     * direct - true answers
     * alternative  - false answers
     *
     * @param duplicateControl - direct or alternative hashset.
     * @param counter - direct or alternative answers counter.
     * @param instrumentCollection - direct or alternative (wind or non-wind) list
     * @param result - common list of all answers
     * @param isCorrect - true = direct(wind), false = alternative(non-wind)
     * @return counter direct or alternative depending on input parameters
     */
    private int addRandomInstrument( HashSet<Integer> duplicateControl,
                                            int counter,
                                            ArrayList<String> instrumentCollection,
                                            ArrayList<String> result,
                                            boolean isCorrect){
        Random  rand = new Random();
        Integer index = rand.nextInt(instrumentCollection.size() - 1);
        if (!duplicateControl.contains(index)) {
            result.add(instrumentCollection.get(index));
            if (isCorrect) correctAnswers.add(instrumentCollection.get(index));
            duplicateControl.add(index);
            counter++;
        }
        return counter;
    }



        nonWindInstruments = new ArrayList<String>(Arrays.asList(nonWind));
        windInstruments = new ArrayList<String>(Arrays.asList(wind));
    }
}
