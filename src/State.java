public class State implements Comparable<State>{
    private int x, y;
    private int g; 
    private int f;
    private String steps; 
    private int curDigit; 
    private boolean usedTargetLastMove;

    public State(int x, int y, int g, int f, String steps, int curDigit, boolean usedTargetLastMove){
        this.x = x;
        this.y = y;
        this.g = g; 
        this.f = f;
        this.steps = steps;
        this.curDigit = curDigit;
        this.usedTargetLastMove = usedTargetLastMove;
    }

    public int getX(){ return x;}
    public int getY(){ return y;}
    public int getG(){ return g;}
    public int getF(){ return f;}
    public String getSteps(){ return steps;}
    public int getCurDigit(){ return curDigit;}
    public boolean getUsedTargetLastMove(){ return usedTargetLastMove;}


    public int compareTo(State other){
        return Integer.compare(this.f, other.f);
    }
}