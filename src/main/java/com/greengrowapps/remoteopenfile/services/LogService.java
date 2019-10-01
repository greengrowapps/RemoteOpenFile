package com.greengrowapps.remoteopenfile.services;

import java.util.ArrayList;
import java.util.List;

public class LogService {
    public List<ROFLogger> loggers=new ArrayList<>();

    public void registerLogger(ROFLogger listener) {
        synchronized (loggers){
            loggers.add(listener);
        }
    }

    public void writeLine(String line){
        synchronized (loggers){
            for (ROFLogger l: loggers) {
                l.writeLine(line);
            }
        }
        System.out.print(line);
    }

    public interface ROFLogger{
        void writeLine(String line);
    }
}
