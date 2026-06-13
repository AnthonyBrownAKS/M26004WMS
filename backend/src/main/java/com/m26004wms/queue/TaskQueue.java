package com.m26004wms.queue;

import com.m26004wms.entity.Task;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// 任务队列
@Component
public class TaskQueue {

    private final BlockingQueue<Task> queue = new LinkedBlockingQueue<>();

    // 入队
    public void addTask(Task task) {
        queue.offer(task);
    }

    // 取任务（阻塞）
    public Task takeTask() throws InterruptedException {
        return queue.take();
    }

}
