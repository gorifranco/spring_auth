package com.example.demo.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.models.PoolConfig;

public class MainService {

    private static final Logger logger = LoggerFactory.getLogger(MainService.class);

    private static final String poolsDataFile = "src\\main\\java\\com\\example\\demo\\config\\pools.data";
    private static ArrayList<CustomPool> pools;

    public static CustomPool getPool(int id) {
        return pools.get(id);
    }

    public static void init() {
        pools = new ArrayList<>();
        loadData();
    }

    private static void loadData() {
        File tmp = new File(poolsDataFile);
        if (!tmp.exists() || tmp.length() == 0)
            return;

        try (
                FileInputStream fi = new FileInputStream(new File(poolsDataFile));
                ObjectInputStream oi = new ObjectInputStream(fi);) {
            while (true) {
                pools.add(new CustomPool((PoolConfig) oi.readObject()));
            }

        } catch (IOException e) {
            logger.error("IOException carregant connexions: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.error("ClassNotFound carregant connexions: " + e.getMessage());
        }
    }

    private static void saveData() {
        try (FileOutputStream f = new FileOutputStream(new File(poolsDataFile));
                ObjectOutputStream o = new ObjectOutputStream(f);) {

            for (CustomPool pool : pools) {
                o.writeObject(pool.getDatabaseConfig());
            }
        } catch (IOException e) {
            logger.error("Error guardant configuracions a l'arxiu: " + e.getMessage());
        }
    }

    public static void createPool(PoolConfig dbconf) {
        pools.add(new CustomPool(dbconf));
        saveData();
    }

    public static ArrayList<CustomPool> getPools() {
        return pools;
    }

    public static void updatePoolConfig(int id, PoolConfig poolConfig) {
        pools.get(id).updateConf(poolConfig);
        saveData();
    }

    public static int poolSize() {
        return pools.size();
    }

    public static boolean deletePool(int id) {
        if (id >= poolSize()) {
            logger.error("Intentent borra una connexió inexistent");
            return false;
        }
        pools.remove(id);
        saveData();
        logger.info("Connexió eliminada amb èxit");
        return true;
    }
}