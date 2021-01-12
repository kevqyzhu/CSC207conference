package Entities;

import java.time.LocalDateTime;
import java.util.ArrayList;

public interface IndividualScheduleInterface {
    ArrayList<ArrayList<LocalDateTime>> schedule = new ArrayList<>();

    boolean addTime(LocalDateTime startTime, LocalDateTime endTime);

    boolean removeTime(LocalDateTime startTime, LocalDateTime endTime);

    boolean isAvailable(LocalDateTime startTime, LocalDateTime endTime);
}