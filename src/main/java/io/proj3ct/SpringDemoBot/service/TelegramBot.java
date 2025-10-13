package io.proj3ct.SpringDemoBot.service;


import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DB_entities.PlatformUser;
import io.proj3ct.SpringDemoBot.DaO.BotCreating.Handlers.LastShtrifBotFather.BotFatherLastShtrih;
import io.proj3ct.SpringDemoBot.DaO.ButtonEditing.RenameButtonMessageHandler;
import io.proj3ct.SpringDemoBot.DaO.MenuManaging.Adding.AddVapecompony_LastShtrich;
import io.proj3ct.SpringDemoBot.DaO.MenuManaging.HEPLING_SERVICES.Description.MenuDescriptionSettings;
import io.proj3ct.SpringDemoBot.DaO.MenuManaging.HEPLING_SERVICES.Names.MenuNameSetting;
import io.proj3ct.SpringDemoBot.DaO.MenuManaging.PodMenu_changeszczegoly.Media.PhotoM.PhotoMenu_LastShtrih;
import io.proj3ct.SpringDemoBot.DaO.MenuManaging.PodMenu_changeszczegoly.Media.VideoM.VideoMenu_LastShtrih;
import io.proj3ct.SpringDemoBot.DaO.MessagEditing.BotFatherSettings.FatherSettingsLastShtrihs.BotName_LastShtrih;
import io.proj3ct.SpringDemoBot.DaO.MessagEditing.MyMessages.PhotoEditing.PhotoLastShtrih;
import io.proj3ct.SpringDemoBot.DaO.MessagEditing.RenameMessageHandler;
import io.proj3ct.SpringDemoBot.DaO.MessagEditing.MyMessages.VideoEditing.VideoLastShtrih;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.Change_Prod.NewLastShtrihs.ChangeName_LastShrih;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.Change_Prod.NewLastShtrihs.ChangeOpys_LastShtrih;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.*;
import io.proj3ct.SpringDemoBot.Dispetchers.CallbackDispatcher;
import io.proj3ct.SpringDemoBot.Dispetchers.CommandDispatcher;
import io.proj3ct.SpringDemoBot.HelpingServise.CleanBot.CleanTheBot;
import io.proj3ct.SpringDemoBot.HelpingServise.Clear_exept;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.model.*;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import io.proj3ct.SpringDemoBot.repository.PlatformUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;

    private final Map<Long, Boolean> pendingAdd = new HashMap<>();
    private final Map<Long, Boolean> pendingAdd2 = new HashMap<>();
    private final Map<Long, Boolean> pendingDelete = new HashMap<>();

    private final Map<Long, Boolean> pendingAdd_product = new HashMap<>();
    private final Map<Long, Boolean> pendingAdd2_product = new HashMap<>();
    private final Map<Long, Boolean> pendingAdd3_product = new HashMap<>();
    private final Map<Long, Boolean> pendingDelete_product = new HashMap<>();
    private final Map<Long, Boolean> add_DELIVERY = new HashMap<>();
    private final Map<Long, Boolean> wait_photo = new HashMap<>();
    private final Map<Long, Boolean> sent = new HashMap<>();
    private final Map<Long, Boolean> wait_id = new HashMap<>();
    private final Map<Long, Contact> media = new HashMap<>();
    private final Map<Long, Boolean> advertisement = new HashMap<>();
    private final Map<Long, String> adres = new HashMap<>();


    private Vapecompony_katalog levyproduct = new Vapecompony_katalog();
    private Vapecompony levybutton = new Vapecompony();


    static final String HELP_TEXT = "Ella Spot была создана чтобы упростить и ускорить процес покупки выдающегося \n" +
            "товара SPOT.LAB\uD83D\uDE2E\u200D\uD83D\uDCA8\n" +
            "\n" +
            "\uD83D\uDFE2 № 1 По жидкостям\n" +
            "\uD83D\uDFE2 топ ассортимент\n" +
            "\uD83D\uDFE2 быстро отвечаем\n" +
            "\n" +
            "Используй  /start и начни обозревать лабораторию\uD83D\uDE1C";

    static final String YES_BUTTON = "YES_BUTTON";
    static final String ALL_KATALOG_BUTTON = "ALL_KATALOG_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";
    static final String ERROR_TEXT = "Error occurred: ";
    static final String CHASER_BUTTON = "CHASER_BUTTON";
    static final String WOZOL_BUTTON = "WOZOL_BUTTON";
    static final String SIGINAH_BUTTON = "SIGINAH_BUTTON";
    static final String PODONKI_BUTTON = "PODONKI_BUTTON";
    static final String NOVA_BUTTON = "NOVA_BUTTON";
    static final String TOLKINAH_BUTTON = "TOLKINAH_BUTTON";
    static final String CART_CLEAR = "CART_CLEAR";
    static final String CART_CHANGE = "CART_CHANGE";
    static final String SEE_CART = "SEE_CART";
    static final String PAY = "PAY";
    static final String CARD = "CARD";
    static final String CASH = "CASH";
    private static final String CURRENCY = "PLN";
    private static final String PLN = "PLN";
    private static final String GRN = "GRN";
    private static final String ACCEPT = "ACCEPT";
    private static final String DENY = "DENY";
    private static boolean work = true;

    @Autowired
    private final CommandDispatcher commandDispatcher;
    @Autowired
    private CallbackDispatcher callbackDispatcher;
    @Autowired
    private RenameButtonMessageHandler renameButtonMessageHandler;
    @Autowired
    private RenameMessageHandler renameMessageHandler;
    @Autowired
    private MenuNameSetting menuNameSetting;

    @Autowired
    private MenuDescriptionSettings menuDescriptionSettings;
    @Autowired
    private PhotoLastShtrih photoLastShtrih;
    @Autowired
    private VideoLastShtrih videoLastShtrih;

    @Autowired
    private PhotoMenu_LastShtrih photoMenuLastShtrih;

    @Autowired
    private PhotoForProd_LastShtrih photoForProd;

    @Autowired
    private VideoMenu_LastShtrih videoMenu_lastShtrih;

    @Autowired
    private VideoForProd_LastShtrih videoForProdLastShtrih;

    @Autowired
    private AddVapecompony_LastShtrich addVapecompony_LastShtrich;

    @Autowired
    private NameForProd_LastShtrih nameForProd_LastShtrih;

    @Autowired
    private ChangeName_LastShrih changeNameLastShrih;

    @Autowired
    private CenaForProd_LastShtrih cenaForProdLastShtrih;

    @Autowired
    private KilkistForProd_LastShtrih kilkistForProdLastShtrih;

    @Autowired
    private ChangeOpys_LastShtrih changeOpys_lastShtrih;

    @Autowired
    private BotFatherLastShtrih botFatherLastShtrih;

    @Autowired
    private BotName_LastShtrih botNameLastShtrih;


    @Autowired
    private MessageRegistry messageRegistry;
    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;
    @Autowired
    private Clear_exept clear_exept;
    @Autowired
    private PlatformUserRepository platformUserRepository;
    @Autowired
    private BotRepository botRepository;
    @Autowired
    private CleanTheBot cleanTheBot;

    public TelegramBot(BotConfig config, CommandDispatcher commandDispatcher) {
        this.config = config;
        this.commandDispatcher = commandDispatcher;
        this.callbackDispatcher = callbackDispatcher;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "Тапни Ella Spot"));
        // listofCommands.add(new BotCommand("/mydata", "get your data stored"));
        //  listofCommands.add(new BotCommand("/deletedata", "delete my data"));
        listofCommands.add(new BotCommand("/help", "информация о нас)"));
        // listofCommands.add(new BotCommand("/settings", "set your preferences"));
        //  listofCommands.add(new BotCommand("/register", "goood"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
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

        if(update.hasMessage()){

            messageRegistry.addMessage(update.getMessage().getChatId(), update.getMessage().getMessageId());

        }
        else if(update.hasCallbackQuery()){
            messageRegistry.addMessage(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());


        }

        if(update.hasMessage() && update.getMessage().hasText()) {

            if(renameButtonMessageHandler.support(update.getMessage())){
                renameButtonMessageHandler.handle(update.getMessage(), this);

            }
            else if(botFatherLastShtrih.support(update.getMessage())){
               botFatherLastShtrih.handle(update.getMessage(), this);
            }
            else if(botNameLastShtrih.support(update.getMessage())){
                botNameLastShtrih.handle(update.getMessage(), this);
            }

            else if(menuDescriptionSettings.support(update.getMessage())){
                menuDescriptionSettings.handle(update.getMessage(), this);
            }

            else if(menuNameSetting.support(update.getMessage())){
                menuNameSetting.handle(update.getMessage(), this);
            }
            else if(renameMessageHandler.support(update.getMessage())){
                renameMessageHandler.handle(update.getMessage(), this);
            }
            else if(addVapecompony_LastShtrich.support(update.getMessage())){
                addVapecompony_LastShtrich.handle(update.getMessage(), this);
            }

            else if(changeNameLastShrih.support(update.getMessage())){
                changeNameLastShrih.handle(update.getMessage(), this);
            }

            else if(nameForProd_LastShtrih.support(update.getMessage())){
                nameForProd_LastShtrih.handle(update.getMessage(), this);
            }
            else if(cenaForProdLastShtrih.support(update.getMessage())){
                cenaForProdLastShtrih.handle(update.getMessage(), this);
            }
            else if(kilkistForProdLastShtrih.support(update.getMessage())){
                kilkistForProdLastShtrih.handle(update.getMessage(), this);
            }

            else if(changeOpys_lastShtrih.support(update.getMessage())){
                changeOpys_lastShtrih.handle(update.getMessage(), this);
            }

            else{
                commandDispatcher.dispatch(update.getMessage(), this);
            }

        }
        else if(update.hasCallbackQuery()){

            callbackDispatcher.dispatch(update.getCallbackQuery(), this);

        }
        else if (update.hasMessage() && update.getMessage().hasPhoto()){

            if(photoLastShtrih.support(update.getMessage())){

                photoLastShtrih.handle(update.getMessage(), this);
            }
            else if(botNameLastShtrih.support(update.getMessage())){
                botNameLastShtrih.handle(update.getMessage(), this);
            }
            else if(photoMenuLastShtrih.support(update.getMessage())){
                photoMenuLastShtrih.handle(update.getMessage(), this);
            }
            else if(photoForProd.support(update.getMessage())){
                photoForProd.handle(update.getMessage(), this);
            }
        }
        else if (update.hasMessage() && update.getMessage().hasVideo()){

            if(videoLastShtrih.support(update.getMessage())){
                System.out.println("666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666");
                videoLastShtrih.handle(update.getMessage(), this);
            }
            else if(videoMenu_lastShtrih.support(update.getMessage())){
                videoMenu_lastShtrih.handle(update.getMessage(), this);
            }
            else if(videoForProdLastShtrih.support(update.getMessage())){
                videoForProdLastShtrih.handle(update.getMessage(), this);
            }

        }
        else if(update.hasMessage() && update.getMessage().hasAnimation() ){

            if(botNameLastShtrih.support(update.getMessage())){
                botNameLastShtrih.handle(update.getMessage(), this);
            }
        }

    }

    @Scheduled(fixedRate = 15 * 60 * 1000)
    public void clear_handlers (){

        List<Vapecompony_katalog> vp = vapecomponyKatalogRepository.findByVapecompony(null);
        vapecomponyKatalogRepository.deleteAll(vp);

        for (
                PlatformUser pl : platformUserRepository.findAll()
        ){
            clear_exept.deleteAll_exept_for(pl.getId());
        }



    }

    @Scheduled(fixedRate = 24* 60* 60 * 1000)
    public void PaymentOverthinking(){

        System.out.println("UAAAA");
        LocalDateTime now = LocalDateTime.now();
        List<Bot> bots = botRepository.findAll();

        for (Bot b: bots){

            if(b.getOwner() == null) continue;
            System.out.println("POLI");


            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(b.getOwner().getTelegramId()));

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            sendMessage.setParseMode("HTML");
            sendMessage.setText("<b>❕УВЕДОМЛЕНИЕ</b>\n" +
                    "\n" +
                    "<blockquote>"+
                    "Подписка на "+b.getName()+" не была продлена \uD83D\uDEAB\n" +
                    "\n" +
                    "<i>Бот и его содержимое были удалены</i> \uD83D\uDDD1</blockquote>"+"\n" +
                    "\n" +
                    "Будь с SalesUp и возвращайся, когда захочешь\uD83E\uDD29");
            LocalDateTime due = b.getPaymentDue();

                // якщо дедлайн вже настав (прострочено)
                if ((b.getPaymentDue() != null) && (b.getPaymentDue().isBefore(now))) {
                    System.out.println("LOLIPOP");
                    cleanTheBot.base_settingsForBot(b);
                    try {
                      Message msg =  execute(sendMessage);
                      messageRegistry.addMessage(msg.getChatId(), msg.getMessageId());
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }


            else if (due != null) {

                // різниця між дедлайном і поточним часом
                    //
                    String link = "https://t.me/" +b.getBotusername();

                // long hours = diff.toHours();
                // якщо залишилось <= 24 години, але ще не прострочено
                if (due.isBefore(now.plusDays(1L))) {
                    System.out.println("POIZDA");
                    sendMessage.setText(
                            "<b>❕НАПОМИНАНИЕ\nПродление — 15 $/мес 🚀</b>\n\n" +
                                    "<blockquote>" +
                                    "Подписка на " + b.getName() + " заканчивается через 24 часа 🚫\n\nПосле этого бот и его содержимое будут очищены\uD83D\uDDD1" +

                                    "</blockquote>\n\n" +
                                    link + "\n\n" +
                                    "Оставайся с SalesUp 🤩"
                    );
                   link ="https://yaroslavnovak.gumroad.com/l/goixa"+"?user_id="+b.getOwner().getId()+"&bot_id="+b.getId();

                    InlineKeyboardButton payButton = new InlineKeyboardButton();
                    payButton.setText("💳 Оплатить");
                    payButton.setUrl(link); // при натисканні відкриється сайт Gumroad

                    InlineKeyboardButton denyButton= new InlineKeyboardButton();
                    denyButton.setText("\uD83D\uDED1 Отписаться");
                    denyButton.setCallbackData("PayDeny:"+b.getId());



                    rows.add(List.of(payButton));
                    rows.add(List.of(denyButton));

                    markup.setKeyboard(rows);
                    sendMessage.setReplyMarkup(markup);

                    try {
                      Message msg = execute(sendMessage);
                        messageRegistry.addMessage(msg.getChatId(), msg.getMessageId());
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }


        }



    }

}

