package com.javarush.task.task35.task3513;

public class MoveEfficiency implements Comparable<MoveEfficiency>{
    int numberOfEmptyTiles;
    int score;
    Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public int compareTo(MoveEfficiency o) {
        if(this.numberOfEmptyTiles > o.numberOfEmptyTiles) {
            return 1;
        }
        else if(this.numberOfEmptyTiles < o.numberOfEmptyTiles) {
            return -1;
        }
        else {
            return Integer.compare(this.score, o.score);
        }
    }
}
