package com.task_management.Task.Management.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class TaskPageResponse<T> {
    List<T> data;
    int page;
    int size;
    int totalPages;
    long totalElements;
}
