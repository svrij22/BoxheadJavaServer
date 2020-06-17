package nl.hu.bep.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class PerformanceLog implements Serializable {

    private HashMap<?, ?> values;
    private LocalDateTime date;
    private String dateStr;

    public PerformanceLog(HashMap<?, ?> values) {
        this.values = values;

        //Date
        this.date = LocalDateTime.now();
        this.dateStr = new SimpleDateFormat("MM-dd HH:mm").format(new Date());
    }

    public HashMap<?, ?> getValues() {
        return values;
    }

    public void setValues(HashMap<?, ?> values) {
        this.values = values;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }
}
