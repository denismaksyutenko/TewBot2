package without.TewBot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import without.TewBot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import without.TewBot.model.MessageResponse;
import without.TewBot.model.TrueOrFalseResponse;
import without.TewBot.repository.WordsRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class BotService extends TelegramLongPollingBot {

    BotConfig config;
    @Autowired
    WordsRepository repository;
    TaskService taskService = new TaskService();
    private boolean TMP;
    private int RIGHT_INDEX_OF_WORD;
    private boolean boolValueForTask = true;

    static final String HELP_TEXT = """
            This bot is created to learning english.
            You can execute commands from the main menu on the left or by typing a command:
            Type /start to see a welcome message
            Type /task_to_ua to see task
            Type /task_to_en to see task
            Type /fast_task to see task for equal
            Type /exit to delete last task
            Type /help to see this message again""";

    public BotService (BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/task_to_ua", "task for translate en - ua"));
        listOfCommands.add(new BotCommand("/task_to_en", "task for translate ua - en"));
        listOfCommands.add(new BotCommand("/fast_task", "task true/false"));
        listOfCommands.add(new BotCommand("/exit", "delete last message"));
        listOfCommands.add(new BotCommand("/help", "list of command and description"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText) {
                case "/start" -> startBot(chatId, update.getMessage().getChat().getUserName());
                case "/help" -> prepareAndSendMessage(chatId, HELP_TEXT);
                case "/task_to_ua" -> {
                    boolValueForTask = true;
                    OneOfFourTask(chatId, boolValueForTask);
                }
                case "/task_to_en" -> {
                    boolValueForTask = false;
                    OneOfFourTask(chatId, boolValueForTask);
                }
                case "/fast_task" -> chooseRightAnswerTask(chatId);
                case "/exit" -> textAfterPressButton(chatId, "message deleted", update.getMessage().getMessageId()-1);

                default -> prepareAndSendMessage(chatId, "Sorry, command was not recognized");
                /*{     // block for translation input words by user
                    try {
                        TranslateService translateS = new TranslateService();
                        String tmp3 = translateS.translate("ua", update.getMessage().getText());
                        System.out.println(tmp3);
                        prepareAndSendMessage(chatId, tmp3);
                    }
                    catch (Exception e){
                        log.error("error translate exception: " + e);
                    }
                }*/
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();// чтоб удалить сообщение
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            for (int i=0;i<4;i++)
            {
                if((callbackData.equals("button" + i)) && (RIGHT_INDEX_OF_WORD == i)){
                    textAfterPressButton(chatId, "This is right answer", messageId);
                    OneOfFourTask(chatId, boolValueForTask);
                }
                else if(callbackData.equals("button" + i)){
                    textAfterPressButton(chatId, "You choose wrong answer", messageId);
                    OneOfFourTask(chatId, boolValueForTask);
                }
            }
            if(callbackData.equals("buttonTrue") && TMP){
                textAfterPressButton(chatId, "You right, this is true answer", messageId);
                chooseRightAnswerTask(chatId);
            }
            else if(callbackData.equals("buttonTrue")){
                textAfterPressButton(chatId, "This is false", messageId);
                chooseRightAnswerTask(chatId);
            }
            else if(callbackData.equals("buttonFalse") && !TMP){
                textAfterPressButton(chatId, "You right", messageId);
                chooseRightAnswerTask(chatId);
            }
            else if(callbackData.equals("buttonFalse")){
                textAfterPressButton(chatId, "No, that's was true", messageId);
                chooseRightAnswerTask(chatId);
            }
        }
    }

    private void startBot(long chatId, String userName) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Hello, " + userName + "! I'm a Telegram bot.");
        executeMessage(message);
        log.info("info user: ");
    }
    private void textAfterPressButton(long chatId, String callbackData, long messageId){
        EditMessageText messageText = new EditMessageText();
        messageText.setChatId(String.valueOf(chatId));
        messageText.setText(callbackData);
        messageText.setMessageId((int) messageId);
        try {
            execute(messageText);
        } catch (TelegramApiException e) {
            log.error("Error button: " + e.getMessage());
        }

    }

    public void OneOfFourTask(long chatId, boolean boolValueForTranslate){
        MessageResponse response;
        response = taskService.listOfWords(repository, boolValueForTranslate);
        RIGHT_INDEX_OF_WORD = response.rightIndexOfWord;

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(!boolValueForTranslate ? "choose right translate for word: " + response.getWordForQuestion() :
                "виберіть правильний переклад: " + response.getWordForQuestion());

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        List<String> buttonList = response.getListOfWords();
        for(int i=0;i<4;i++){
            var button = new InlineKeyboardButton();
            button.setText(buttonList.get(i));
            button.setCallbackData("button"+i);
            rowInline.add(button);
        }
        rowsInline.add(rowInline);
        markup.setKeyboard(rowsInline);
        message.setReplyMarkup(markup);

        executeMessage(message);
    }

    public void chooseRightAnswerTask(long chatId){
        TrueOrFalseResponse response;
        response = taskService.TrueOrFalseTask(repository);
        TMP = response.isValueBoolean();

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(response.getFirstWord() + " = " +response.getSecondWord() + "?"); // refactor

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        var button1 = new InlineKeyboardButton();
        button1.setText("True");
        button1.setCallbackData("buttonTrue");
        rowInline.add(button1);
        var button2 = new InlineKeyboardButton();
        button2.setText("False");
        button2.setCallbackData("buttonFalse");
        rowInline.add(button2);

        rowsInline.add(rowInline);
        markup.setKeyboard(rowsInline);
        message.setReplyMarkup(markup);

        executeMessage(message);
    }

    public void executeMessage(SendMessage message){
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error: " + e.getMessage());
        }
    }

    private void prepareAndSendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }
}
