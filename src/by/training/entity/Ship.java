package by.training.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The type Ship.
 */
public class Ship extends Thread {
    /**
     * The Log.
     */
    private static Logger log = LogManager.getLogger(Ship.class);

    private AtomicInteger containersNumber = new AtomicInteger();
    private Lock lock = new ReentrantLock();

    /**
     * The constant CONTAINERS_CAPACITY.
     */
    public static final int CONTAINERS_CAPACITY = 30;
    /**
     * The constant RANDOM_RANGE.
     */
    public static final int RANDOM_RANGE = 100;

    /**
     * Instantiates a new Ship.
     *
     * @param containersNumber the containers number
     * @param name             the name
     */
    public Ship(int containersNumber, String name) {
        super(name);
        this.containersNumber.set(containersNumber);
    }

    @Override
    public void run() {
        Port.getInstance().serveShip(this);
        Random random = new Random();
        log.info("Initial number of containers = " + containersNumber.get());

        try {
            if (containersNumber.get() == 0 || random.nextInt(RANDOM_RANGE) > random.nextInt(RANDOM_RANGE)) {

                int containersResidues = loadContainers(random.nextInt(CONTAINERS_CAPACITY));
                if (containersResidues > 0) {
                    log.info("Volume was exceeded. Residues unloaded at the warehouse.");
                    unloadContainersIntoWarehouse(containersResidues);                }
            } else if (random.nextInt(RANDOM_RANGE) < random.nextInt(RANDOM_RANGE)) {
                unloadContainersIntoWarehouse(this.containersNumber.get());
                containersNumber.set(0);
            } else {
                int containersResidues = unloadContainersIntoOtherShip();
                if (containersResidues > 0) {
                    log.info("Volume was exceeded. Residues unloaded at the warehouse.");
                    unloadContainersIntoWarehouse(containersResidues);
                }
                containersNumber.set(0);
            }
            Thread.sleep(1000);
            Port.getInstance().sendShip(this);
            log.info("Final number of containers = " + containersNumber.get());
        } catch (InterruptedException e) {
            log.error("Thread sleep exception.", e);
        }
    }

    /**
     * Sets containers number.
     *
     * @param containersNumber the containers number
     */
    public void setContainersNumber(int containersNumber) {
        this.containersNumber.set(containersNumber);
    }

    /**
     * Unload containers into other ship int.
     *
     * @return the int
     */
    public int unloadContainersIntoOtherShip() {
        log.info("Unloading of containers onto another ship");
        int containersResidues = 0;
        containersResidues = Port.getInstance().unloadContainersIntoShip(containersNumber.get(), this);
        return containersResidues;
    }

    /**
     * Unload containers into warehouse.
     *
     * @param number the number
     */
    public void unloadContainersIntoWarehouse(int number) {
        log.info("Unloading containers at the warehouse");
        while (!Warehouse.getInstance().loadContainers(number)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("Thread sleep exception.", e);
            }
        }
    }

    /**
     * Load containers int.
     *
     * @param containersNumber the containers number
     * @return the int
     */
    public int loadContainers(int containersNumber) {
        log.info("Loading of containers onto the ship");
        int containersResidues = 0;
        try {
            lock.lock();
            if ((this.containersNumber.get() + containersNumber) > CONTAINERS_CAPACITY) {
                containersResidues = (containersNumber + this.containersNumber.get()) - CONTAINERS_CAPACITY;
                this.containersNumber.set(CONTAINERS_CAPACITY);
            } else {
                this.containersNumber.addAndGet(containersNumber);
            }
        } finally {
            lock.unlock();
        }
        return containersResidues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ship ship = (Ship) o;

        if (containersNumber != null ? !containersNumber.equals(ship.containersNumber) : ship.containersNumber != null)
            return false;
        if (lock != null ? !lock.equals(ship.lock) : ship.lock != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = containersNumber != null ? containersNumber.hashCode() : 0;
        result = 31 * result + CONTAINERS_CAPACITY;
        result = 31 * result + (lock != null ? lock.hashCode() : 0);
        return result;
    }
}
