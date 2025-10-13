package io.proj3ct.SpringDemoBot.HelpingServise;

import org.springframework.stereotype.Service;

@Service
public class LinkDecider {

   public static String getPhotoMessageLink(String key){

       if (key.equals("greeting")){
           return "https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/start1DSPV.png";
       }
       else if (key.equals("menu")){
           return "https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/start2DSPV.png";
       }
       else if (key.equals("catalog")){
           return "https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/katalogDSPV.png";
       }
       else if (key.equals("delivery")){
           return "https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/adresDSPTV.png";
       }
       else if (key.equals("accept")){
           return "https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/acceptDS.png";
       }
       else if (key.equals("congrat")){
           return "https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/doneDSPTV.png";
       }
       else if (key.equals("deny")){
           return "https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/denyDSPTV.png";
       }
       else if (key.equals("phone_thanks")){
           return "https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/thanksDST.png";
       }
       else if (key.equals("clearing")){
           return "https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/koshykDSPTV.png";
       }
       else if (key.equals("send_money")){
            return "https://github.com/YarNovak/BOT12500Photos/blob/main/paycartDSPTV.png";
       }
       else if (key.equals("phone")){
           return "https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/contaktDSPTV.png";
       }

       return "https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png";
   }

   public static String getTextMessageLink(String key){
       if (key.equals("greeting")){
           return "https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/start1DST.png";
       }
       else if (key.equals("menu")){
           return "https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/start2DST.png";
       }
       else if (key.equals("catalog")){
           return "https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/katalogDST.png";
       }
       else if (key.equals("delivery")){
           return "https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/adresDS.png";
       }
       else if (key.equals("accept")){
           return "https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/acceptDSPTV.png";
       }
       else if (key.equals("congrat")){
           return "https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/doneDS.png";
       }
       else if (key.equals("deny")){
           return "https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/denyDST.png";
       }
       else if (key.equals("phone_thanks")){
           return "https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/thanksDSPTV.png";
       }
       else if (key.equals("clearing")){
           return "https://github.com/YarNovak/BOT12500Photos/blob/main/koshykDST.png?raw=true";
       }
       else if (key.equals("send_money")){
           return "https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/paycartDST.png";
       }
       else if (key.equals("phone")){

           return "https://github.com/YarNovak/BOT12500Photos/blob/main/ContacktDS.png?raw=true";
       }
       return "https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png";
   }



}
