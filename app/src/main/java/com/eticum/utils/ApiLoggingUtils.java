package com.eticum.utils;

import com.eticum.api.EticumApiService;
import com.eticum.api.http.model.LogItem;

import java.util.ArrayList;
import java.util.List;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiLoggingUtils {
    private List<LogItem> logs = new ArrayList<>();

    public synchronized void add(LogItem item) {
        logs.add(item);
        if (logs.size() >= 500) {
            EticumApiService.doLog(logs);
            logs.clear();
        }
    }
}
