package Model;

import Server.WrapList;

import java.util.ArrayList;
import java.util.List;

public class Snake {
    
    public long Id;
    public List <Dot> dots;
    public String name;
    public String code;
    public boolean isRemoved;
    
    public Snake(){
        this.dots = new ArrayList<Dot>();
        this.isRemoved = false;
    }

    public Snake(List<Dot> dots) {
        this.dots = dots;
        this.isRemoved = false;
    }
    
    public void updateState(List<Dot> dots){
        this.dots = dots;
    }

    public List<Dot> update(WrapList titles, List<Snake> fullCharacters){
        List<Dot> dots = new ArrayList<>();
        for(Snake character : fullCharacters){
            if(character.Id == this.Id){
                continue;
            }


            if(checkIsCollisionWithAnotherSnake(character)){

                this.isRemoved = true;
            }
            if(!character.isRemoved) {
                dots.addAll(character.dots);
            }
        }

        dots.addAll(titles.dots);
        return dots;
    }


    public boolean checkIsCollisionWithAnotherSnake(Snake snake){
        if(this.dots.size() < 1 || snake.dots.size() < 1){
            return false;
        }

        Dot head = this.dots.get(0);
        for(int indexDot = 1; indexDot < snake.dots.size(); indexDot++){
            Dot dot = snake.dots.get(indexDot);
            if(distance(head.x, head.y,dot.x,dot.y)  < 16){
                return true;
            }
        }
        return false;
    }

    public float distance(float xFirstPoint, float yFirstPoint, float  xSecondPoint, float ySecondPoint){
        return (float) Math.sqrt(Math.pow(xFirstPoint - xSecondPoint, 2) + Math.pow(yFirstPoint - ySecondPoint, 2));
    }

    public Dot checkIsCollisionWithTitle(WrapList title){
        Dot head = this.dots.get(0);
        for(Dot dot : title.dots){
            int rightLocation = head.x + 16;
            int leftLocation = head.x - 16;
            int aboveLocation = head.y + 16;
            int belowLocation = head.y - 16;

            System.out.println("left :" + leftLocation + "-" + dot.x + "- rightLocation :" + rightLocation);
            System.out.println("above:" + aboveLocation + "-" + dot.y + "- below : " + belowLocation);

            if(leftLocation < dot.x && dot.x < rightLocation &&
               belowLocation < dot.y && dot.y < aboveLocation){
                return dot;
            }
        }
        return null;
    }
}


