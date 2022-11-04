package com.javarush.task.task35.task3513;

import java.io.InputStream;
import java.util.*;
import java.util.stream.IntStream;

public class Model {
    private static final int FIELD_WIDTH = 4;
    private static final int NINETY_CLOCKWISE = 1;
    private static final int ONE_HUNDRED_EIGHTY_CLOCKWISE = 2;
    private static final int TWO_HUNDRED_SEVENTY_CLOCKWISE = 3;
    private Tile[][] gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
    private Stack<Tile[][]> previousStates = new Stack<>();
    private Stack<Integer> previousScores = new Stack<>();
    private boolean isSaveNeeded = true;
    int score;
    int maxTile;


    public Model() {
        score = 0;
        maxTile = 0;
        resetGameTiles();
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    private Tile[][] getCopyGameTiles(Tile[][] source){
        Tile[][] result = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                result[i][j] = new Tile(source[i][j].value);
            }
        }
        return result;
    }

    boolean canMove(){
        if(!getEmptyTiles().isEmpty()){
            return true;
        }
        for (Tile[] t :
                gameTiles) {
            for (int i = 0; i < FIELD_WIDTH - 1; i++) {
                if(!t[i].isEmpty() && t[i].value == t[i + 1].value){
                    return true;
                }
            }
        }
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH - 1; j++) {
                if(!gameTiles[j][i].isEmpty() && gameTiles[j][i].value == gameTiles[j + 1][i].value){
                    return true;
                }
            }
        }
        return false;
    }

    protected void resetGameTiles(){
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    private void addTile(){
        List<Tile> emptyTiles = getEmptyTiles();
        if(emptyTiles.size() > 0) {
            int randomTile = (int) (Math.random() * emptyTiles.size());
            emptyTiles.get(randomTile).value = Math.random() < 0.9 ? 2 : 4;
        }
    }

    private List<Tile> getEmptyTiles(){
        List<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if(gameTiles[i][j].value == 0){
                    tiles.add(gameTiles[i][j]);
                }
            }
        }
        return tiles;
    }

     private boolean compressTiles(Tile[] tiles){
        boolean isCompressed = false;
        int count = 0;
        for (int i = 0; i < tiles.length; i++) {
            if(!tiles[i].isEmpty()) {
                if (count != i){
                    tiles[count].value = tiles[i].value;
                    tiles[i].value = 0;
                    i--;
                    isCompressed = true;
                }
                count++;
            }

        }
        return isCompressed;
    }

     private boolean mergeTiles(Tile[] tiles){
        boolean isMerged = false;
        for (int i = 0; i < tiles.length - 1; i++) {
            if(!tiles[i].isEmpty() && tiles[i].value == tiles[i + 1].value){
                int mergeValue = tiles[i].value + tiles[i + 1].value;
                score += mergeValue;
                tiles[i].value = mergeValue;
                tiles[i + 1].value = 0;
                compressTiles(tiles);
                if(mergeValue > maxTile){
                    maxTile = mergeValue;
                }
                isMerged = true;
            }
        }
        return isMerged;
    }

    void left(){
        boolean isMoved = false;
        if(isSaveNeeded){
            saveState();
        }
        for (Tile[] t : gameTiles) {
            boolean isCompressed = compressTiles(t);
            boolean isMerged = mergeTiles(t);
            if(!isMoved){
                if(isCompressed || isMerged){
                    isMoved = true;
                }
            }
        }

        if(isMoved){
            addTile();
        }
        isSaveNeeded = true;
    }

    void down(){
        saveState();
        turnOnClockwise(NINETY_CLOCKWISE);
    }

    void right(){
        saveState();
        turnOnClockwise(ONE_HUNDRED_EIGHTY_CLOCKWISE);
    }

    void up(){
        saveState();
        turnOnClockwise(TWO_HUNDRED_SEVENTY_CLOCKWISE);
    }

    private void turnOnClockwise(int direction){
        for (int quarter = 1; quarter <= 4; quarter++) {
            Tile[][] copySource = getCopyGameTiles(gameTiles);
            for (int i = 0; i < FIELD_WIDTH; i++) {
                for (int j = 0; j < FIELD_WIDTH; j++) {
                    gameTiles[i][j] = new Tile(copySource[FIELD_WIDTH - 1 - j][i].value);
                }
            }
            if(quarter == direction){
                left();
            }

        }
    }

    private void saveState(){
        Tile[][] copyGameTiles = getCopyGameTiles(gameTiles);
        previousStates.push(copyGameTiles);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    public void rollback(){
        if(!previousStates.isEmpty() && !previousScores.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    void randomMove(){
        int n = ((int) (Math.random() * 100)) % 4;
        switch (n) {
            case 0 :
                left();
                break;
            case 1 :
                down();
                break;
            case 2 :
                right();
                break;
            case 3 :
                up();
                break;
        }
    }

    void autoMove(){
        PriorityQueue<MoveEfficiency> priorityQueue = new PriorityQueue<>(4, Collections.reverseOrder());
        priorityQueue.add(getMoveEfficiency(this::left));
        priorityQueue.add(getMoveEfficiency(this::down));
        priorityQueue.add(getMoveEfficiency(this::right));
        priorityQueue.add(getMoveEfficiency(this::up));
        assert priorityQueue.peek() != null;
        priorityQueue.peek().move.move();
    }

    private MoveEfficiency getMoveEfficiency(Move move){
        MoveEfficiency moveEfficiency = new MoveEfficiency(-1, 0, move);
        move.move();
        if(hasBoardChanged()){
            moveEfficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);
        }
        rollback();
        return moveEfficiency;
    }

    private boolean hasBoardChanged(){
        int currentSum = getSumValues(gameTiles);
        int backSum = getSumValues(previousStates.peek());
        return currentSum != backSum;
    }

    private int getSumValues(Tile[][] tiles){
        int sum = 0;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                sum += tiles[i][j].value;
            }
        }
        return sum;
    }
}
