/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Model.Snake;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author phamanhtuan
 */
public class RoomGameObject {
    public int IDs = 0;
    public String code;
    public List<Snake> fullCharacter;
    public WrapList map;
    public WrapList gamePlay;
    public boolean isRunning;
    public boolean isFinished;
    private int numberIndex  = 0;


    public RoomGameObject() {
    }

    public int getIDs() {
        return IDs++;
    }



    public RoomGameObject(String code, List<Snake> fullCharacter){
        this.code  = code;
        this.fullCharacter = fullCharacter;
    }

    public int getNumberIndex() {
        return numberIndex;
    }

    public RoomGameObject(String code){
        this.code = code;
        this.fullCharacter = new ArrayList<>();
        this.gamePlay = new WrapList();
        this.map = new WrapList();
        this.isFinished = false;
        this.isRunning = false;
    }

    public boolean addCharacter(Snake character){
        if(fullCharacter.size() < 4){
            character.Id = numberIndex;
            this.fullCharacter.add(character);
            numberIndex++;

            return true;
        }
        
        return false;
    }
    
    public void removeCharacterById(int Id){
        for(int index = 0; index < fullCharacter.size(); index++){
            if(fullCharacter.get(index).Id == Id){
                fullCharacter.remove(index);
                return ;
            }
        }
    }


}
