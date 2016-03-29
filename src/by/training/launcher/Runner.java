package by.training.launcher;

import by.training.entity.Ship;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class Runner {
    private static Logger log = LogManager.getLogger(Runner.class);

    public static void main(String[] args) {
        log.info("Start main function.");
        final int SHIPS_NUMBER = 200;

        Random random = new Random();

        for (int i = 1; i < SHIPS_NUMBER; i++) {
            Ship ship = new Ship(random.nextInt(Ship.CONTAINERS_CAPACITY), "Ship - " + i);
            ship.start();
        }

        log.info("Finish main function.");
    }
}
