package by.training.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The type Port.
 */
public class Port {
    private static Logger log = LogManager.getLogger(Port.class);

    /**
     * The constant DOCKS_NUMBER.
     */
    public static final int DOCKS_NUMBER = 10;
    private final Semaphore docksSemaphore = new Semaphore(DOCKS_NUMBER, true);
    private ArrayDeque<Ship> ships = new ArrayDeque<Ship>(DOCKS_NUMBER);

    private static boolean status = true;
    private static Port instance = null;
    private static ReentrantLock portLock = new ReentrantLock();

    private Lock lock = new ReentrantLock();
    private Lock lockAdd = new ReentrantLock();
    private Lock lockRemove = new ReentrantLock();

    private Port() {
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static Port getInstance() {
        if (status) {
            portLock.lock();
            try {
                if (instance == null) {
                    instance = new Port();
                    status = false;
                }
            } finally {
                portLock.unlock();
            }
        }
        return instance;
    }

    /**
     * Serve ship.
     *
     * @param ship the ship
     */
    public void serveShip(Ship ship) {
        try {
            lockAdd.lock();
            docksSemaphore.acquire();
            ships.addLast(ship);
            log.info("Ship took dock.");
        } catch (InterruptedException e) {
            log.error("Thread interrupted exception.", e);
        } finally {
            lockAdd.unlock();
        }
    }

    /**
     * Send ship.
     *
     * @param ship the ship
     */
    public void sendShip(Ship ship) {
        log.info("Ship left dock.");
        try {
            lockRemove.lock();
            ships.remove(ship);
            docksSemaphore.release();
        } finally {
            lockRemove.unlock();
        }
    }

    /**
     * Unload containers into ship int.
     *
     * @param number      the number
     * @param checkedShip the checked ship
     * @return the int
     */
    public int unloadContainersIntoShip(int number, Ship checkedShip) {
        int containersResidues = number;
        try {
            lock.lock();
            if (ships.isEmpty()) {
                return containersResidues;
            }
            Ship ship = ships.getFirst();
            if (!checkedShip.equals(ship)) {
                containersResidues = ship.loadContainers(number);
            }
            checkedShip.setContainersNumber(0);
        } finally {
            lock.unlock();
        }
        return containersResidues;
    }
}
