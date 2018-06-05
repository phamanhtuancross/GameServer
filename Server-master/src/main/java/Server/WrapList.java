package Server;

import Model.Dot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WrapList implements Serializable {

    public List<Dot> dots = new ArrayList<>();

    public void clear(){
        this.dots.clear();
    }

    public void ddd(Dot dot){
        this.dots.add(dot);
    }

    public void addAll(List<Dot> dots) {
        this.dots.addAll(dots);
    }

    public boolean contains(Dot dot){
        for(Dot _dot : dots){
            if(_dot.x == dot.x && _dot.y == dot.y){
                return true;
            }
        }
        return false;
    }

    public boolean contains(int x, int y){
       for(Dot dot : dots){
           if(dot.x == x & dot.y == y){
               return true;
           }
       }
       return false;
    }

    public void reamoveDot(Dot dot){
        System.out.println("Calling function removeDot()...");
        for(int i = 0; i < dots.size(); i ++){
            Dot temp = dots.get(i);
            if(temp.x == dot.x && temp.y == dot.y){
                dots.remove(i);
                return;
            }

        }
    }
}
