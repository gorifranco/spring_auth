package com.example.demo.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        File poolsData = new File(poolsDataFile);
        if(poolsData.exists() && poolsData.length() > 0){

        }
    }

    public static void saveData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(poolsDataFile))) {
            for (CustomPool pool : pools) {
                bw.write(pool.getDatabaseConfig().toString());
            }
        } catch(IOException e){
            logger.error(e.getMessage());
        }
    }

    public static void createPool(PoolConfig dbconf) {
        pools.add(new CustomPool(dbconf));
    }

    public static ArrayList<CustomPool> getPools() {
        return pools;
    }

    public static void updatePoolConfig(int id, PoolConfig poolConfig){
        pools.get(id).updateConf(poolConfig);
        saveData();
    }

}