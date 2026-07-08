package com.task_management.Task.Management.services;

import com.task_management.Task.Management.dtos.TaskUpdateEventDto;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TaskEventPublisher {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void send(TaskUpdateEventDto event){
        simpMessagingTemplate.convertAndSend("/topic/task" , event);
    }
}
