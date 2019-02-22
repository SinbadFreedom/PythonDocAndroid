package com.dashidan;

import com.dashidan.data.Task;

import java.util.List;

public interface LoadTasksCallback {

    void onTasksLoaded(List<Task> tasks);

    void onDataNotAvailable();
}
