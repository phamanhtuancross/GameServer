package Server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class Helper {

    public static Object getObjectFromJsonString(int objectType,String jsonString){
        Gson gson = new Gson();
        switch (objectType){

            case ObjectType.SEVER_MESSAGE_OBJECT:{
                Type collectionType = new TypeToken<ServerMessage>(){}.getType();
                ServerMessage serverMessage = gson.fromJson(jsonString,collectionType);
                return serverMessage;
            }
        }
        return null;
    }

    public static String getJsonStringFromObject(Object object){
        Gson gson = new Gson();
        return gson.toJson(object);
    }
}
