package com.example.TaxiTelegramBot.services;

import com.example.TaxiTelegramBot.entities.DriverPhotos;
import com.example.TaxiTelegramBot.entities.Drivers;
import com.example.TaxiTelegramBot.repos.DriversRepository;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriversService {

    private static final String russianLettersInAutoNumber = "АВЕКМНОРСТУХ";

    private static final String[] russianRegions = {"01", "02", "102", "03", "04",
            "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "116",
    "17", "18", "118", "19", "20", "21", "121", "22", "23", "93", "24", "25", "125", "26",
    "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "138", "39",
    "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "90", "150", "51",
    "52", "53", "54", "154", "55", "56", "57", "58", "59", "159", "60", "61", "161", "62",
    "63", "163", "64", "65", "66", "96", "67", "68", "69", "70", "71", "72", "73", "173",
    "74", "174", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86",
    "87", "88", "89", "90", "95", "98", "99"};

    private final DriversRepository driversRepository;

    private final DriverPhotosService driverPhotosService;

    public boolean checkDriverLoginOrNot(long chatId){
        Drivers driver = getDriverByChatId(chatId);
        return driver != null;
    }

    public boolean checkCanWeRegisterToThisLogin(String login){
        Drivers driver = driversRepository.findByLogin(login);
        return driver == null;
    }

    private Drivers getDriverByChatId(long chatId){
        return driversRepository.findByChatId(chatId);
    }

    public String hashPassword(String notHashedPassword){
        return getArgon().hash(22, 65536, 1, notHashedPassword);
    }

    private Argon2 getArgon(){
        return Argon2Factory.create();
    }

    public boolean checkValidNumberOrNot(String number){
        if((number.length() < 8) || (number.length() > 9)){
            return false;
        }
        else if(!(russianLettersInAutoNumber.contains(number.substring(0, 1)) &&
                russianLettersInAutoNumber.contains(number.substring(4, 6)))){
            return false;
        }
        else if(!StringUtils.isNumeric(number.substring(1, 4))){
            return false;
        }
        else if(!containsRegion(number.substring(6))){
            return false;
        }
        return true;
    }

    private boolean containsRegion(String region){
        boolean result = false;

        for(int i = 0; i < russianRegions.length; i++){
            if(russianRegions[i].equals(region)){
                result = true;
                break;
            }
        }

        return result;
    }

    public boolean tryParseInt(String str){
        try {
            Integer.parseInt(str);
        }
        catch (Exception e){
            return false;
        }
        return true;
    }

    public void finishRegistration(Drivers driver, String wayToPhoto){
        DriverPhotos photo = driverPhotosService.addNewPhoto(wayToPhoto);
        List<DriverPhotos> photosList = new ArrayList<>();
        photosList.add(photo);
        driver.setDriverPhotos(photosList);
        driversRepository.save(driver);
    }
}
