package kosh.snake.model;

import kosh.snake.model.ICoordinates;
import kosh.snake.model.IField;

import java.util.List;

public interface Subscriber {
    void handleEvent(IField field, List<ICoordinates> coordsToRedraw, int score);
}
