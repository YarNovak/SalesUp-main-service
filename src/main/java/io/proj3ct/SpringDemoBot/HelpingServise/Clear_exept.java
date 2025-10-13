package io.proj3ct.SpringDemoBot.HelpingServise;

import io.proj3ct.SpringDemoBot.DaO.BotCreating.Handlers.Wait_BotFather;
import io.proj3ct.SpringDemoBot.DaO.MenuManaging.Adding.AddMenu_handler;
import io.proj3ct.SpringDemoBot.DaO.MenuManaging.HEPLING_SERVICES.Description.MenuDescription_handler;
import io.proj3ct.SpringDemoBot.DaO.MenuManaging.HEPLING_SERVICES.Names.MenuName_handler;
import io.proj3ct.SpringDemoBot.DaO.MenuManaging.HEPLING_SERVICES.Photos.MenuPhoto_handler;
import io.proj3ct.SpringDemoBot.DaO.MenuManaging.HEPLING_SERVICES.Videos.MenuVideo_handler;
import io.proj3ct.SpringDemoBot.DaO.MessagEditing.BotFatherSettings.BotFatherHandlers.FatherSettings_handler;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.Change_Prod.NewLastShtrihs.ChangeOpys_handler;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.Change_Prod.Update_Handlers.Changename_handler;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.HandlerForProducts.AddCena_prod_handler;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.HandlerForProducts.AddKilkist_prod_handler;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.HandlerForProducts.AddProductVideo_handler;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.HandlerForProducts.Add_prod_handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class Clear_exept {


    @Autowired
    @Lazy
    private FatherSettings_handler fatherSettings_handler;

    @Autowired
    @Lazy
    private Wait_BotFather waitBotFather;

    @Autowired
    @Lazy
    private ChangeOpys_handler changeOpys_handler;

    @Autowired
    @Lazy
    private AddKilkist_prod_handler addKilkist_prod_handler;

    @Autowired
    @Lazy
    private Changename_handler changenameHandler;

    @Autowired
    @Lazy
    private AddCena_prod_handler addCenaProdHandler;

    @Autowired
    @Lazy
    private Add_prod_handler addProdHandler;

    @Autowired
    @Lazy
    private AddMenu_handler addMenuHandler;
    @Autowired
    private BotStateService botStateService;

    @Autowired
    @Lazy
    private AddProductVideo_handler addProductVideoHandler;

    @Autowired
    @Lazy
    private MenuName_handler menuNameHandler;

    @Autowired
    @Lazy
    private MenuPhoto_handler menuPhotoHandler;

    @Autowired
    @Lazy
    private MenuDescription_handler menuDescriptionHandler;

    @Autowired
    @Lazy
    private MenuVideo_handler menuVideoHandler;






    public void deleteAll_exept_for(Long userId){



            botStateService.clear(userId);
            fatherSettings_handler.clear(userId);
            waitBotFather.clear(userId);
            changeOpys_handler.clear(userId);
            addKilkist_prod_handler.clear(userId);
            changenameHandler.clear(userId);
            addCenaProdHandler.clear(userId);
            addProdHandler.clear(userId);
            addMenuHandler.clear(userId);
            addProductVideoHandler.clear(userId);
            menuDescriptionHandler.clear(userId);
            menuVideoHandler.clear(userId);
            menuNameHandler.clear(userId);
            menuPhotoHandler.clear(userId);

    }

}
