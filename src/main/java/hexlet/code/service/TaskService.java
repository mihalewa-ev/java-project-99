package hexlet.code.service;


import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TaskSpecifications taskSpecifications;

    public List<TaskDTO> getAll(TaskParamsDTO params) {
        var specification = taskSpecifications.build(params);
        return taskRepository.findAll(specification).stream()
                .map(taskMapper::map)
                .toList();
    }

}
