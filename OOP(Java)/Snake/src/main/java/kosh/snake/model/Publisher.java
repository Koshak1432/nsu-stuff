package kosh.snake.model;

public interface Publisher {
    void addSubscriber(Subscriber subscriber);
    void notifySubscribers();


}
