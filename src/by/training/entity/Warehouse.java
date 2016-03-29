package by.training.entity;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.locks.ReentrantLock;

/**
 * The type Warehouse.
 */
public class Warehouse {
    private static Logger log = LogManager.getLogger(Warehouse.class);

    private int containersNumber;
    /**
     * The constant CONTAINERS_CAPACITY.
     */
    public static final int CONTAINERS_CAPACITY = 120;
    private static boolean status = true;

    private static Warehouse instance = null;
    private static ReentrantLock warehouseLock = new ReentrantLock();
    private static ReentrantLock loadLock = new ReentrantLock();

    private Warehouse() {
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static Warehouse getInstance() {
        if (status) {
            warehouseLock.lock();
            try {
                if (instance == null) {
                    instance = new Warehouse();
                    status = false;
                }
            } finally {
                warehouseLock.unlock();
            }
        }

        return instance;
    }

    /**
     * Load containers boolean.
     *
     * @param containersNumber the containers number
     * @return the boolean
     */
    public boolean loadContainers(int containersNumber) {
        try {
            loadLock.lock();
            if ((this.containersNumber + containersNumber) > CONTAINERS_CAPACITY) {
                log.info("Warehouse is full. Begins unloading. Please expect.");
                this.containersNumber = 0;
                return false;
            }
            this.containersNumber += containersNumber;
        } finally {
            loadLock.unlock();
        }
        return true;
    }
}
