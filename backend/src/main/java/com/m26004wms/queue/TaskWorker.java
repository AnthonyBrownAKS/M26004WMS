package com.m26004wms.queue;

import com.m26004wms.entity.Task;
import com.m26004wms.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class TaskWorker {


    @Autowired
    private TaskQueue taskQueue;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ThreadPoolExecutor taskExecutor;

    @PostConstruct
    public void start() {

        // 启动多个消费线程
        for (int i = 0; i < 3; i++) {

            taskExecutor.execute(() -> {

                while (true) {
                    try {
                        Task task = taskQueue.takeTask();

                        taskService.executeSingleTask(task);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


}

