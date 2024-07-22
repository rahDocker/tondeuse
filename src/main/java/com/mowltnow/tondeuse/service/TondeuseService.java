package com.mowltnow.tondeuse.service;

import org.springframework.stereotype.Service;

import com.mowltnow.tondeuse.batch.BatchConfiguration;
import com.mowltnow.tondeuse.entity.Position;
import com.mowltnow.tondeuse.entity.Tondeuse;

@Service
public class TondeuseService {

    public Position getFinalPosition(Tondeuse tondeuse,int height,int width) throws Exception {
        if (tondeuse.getInstruction().isEmpty()) {
            return null;
        }

        Position currentPosition = tondeuse.getPosition();
        char currentOrientation = currentPosition.getOrientation();

        for (char instruction : tondeuse.getInstruction().toCharArray()) {
            switch (instruction) {
                case 'G':
                    currentOrientation = goLeft(currentOrientation); 
                    break;
                case 'D':
                    currentOrientation = goRight(currentOrientation); 
                    break;
                case 'A':
                    currentPosition = go(currentPosition, currentOrientation,height,width); 
                    break;
                default:
                    throw new IllegalArgumentException("Invalid instruction: " + instruction); 
            }
        }

        return new Position(currentPosition.getX(), currentPosition.getY(), currentOrientation); 
    }

    private char goLeft(char direction) {
        switch (direction) {
            case 'N': return 'W';
            case 'W': return 'S';
            case 'S': return 'E';
            case 'E': return 'N';
            default: throw new IllegalArgumentException("Invalid direction: " + direction); 
        }
    }

    private char goRight(char direction) {
        switch (direction) {
            case 'N': return 'E';
            case 'E': return 'S';
            case 'S': return 'W';
            case 'W': return 'N';
            default: throw new IllegalArgumentException("Invalid direction: " + direction); 
        }
    }

    private Position go(Position position, char orientation,int height,int width) {
        int x = position.getX();
        int y = position.getY();

        switch (orientation) {
            case 'N':
                if (y < height) y++; 
                break;
            case 'E':
                if (x < width) x++; 
                break;
            case 'S':
                if (y > 0) y--; 
                break;
            case 'W':
                if (x > 0) x--; 
                break;
        }

        return new Position(x, y, orientation); 
    }
}
