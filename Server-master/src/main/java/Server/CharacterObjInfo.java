/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.Serializable;

/**
 *
 * @author phamanhtuan
 */
public class CharacterObjInfo implements Serializable{
    public int Id;
    public String name;
    public String code;

    public CharacterObjInfo() {
        this.Id = -1;
        this.name = "";
    }

    public CharacterObjInfo(int Id, String name){
        this.Id = Id;
        this.name = name;
    }
    
}
