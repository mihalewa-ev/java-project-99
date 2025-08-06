package hexlet.code.component;


import hexlet.code.mapper.UserMapper;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.CustomUserDetailsService;
import hexlet.code.utils.LabelUtils;
import hexlet.code.utils.TaskStatusUtils;
import hexlet.code.utils.UserUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "data.initializer.enabled", havingValue = "true", matchIfMissing = true)
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {


    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserMapper userMapper;

    @Autowired
    private TaskStatusUtils taskStatusUtils;
    @Autowired
    private final TaskStatusRepository taskStatusRepository;
    @Autowired
    private final LabelRepository labelRepository;
    @Autowired
    private final UserUtils userUtils;
    @Autowired
    private final LabelUtils labelUtils;
    @Autowired
    private final CustomUserDetailsService customUserDetailsService;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        User admin = userUtils.getAdminUser();
        if (userRepository.findByEmail(admin.getEmail()).isEmpty()) {
            customUserDetailsService.createUser(admin);
        }
        List<TaskStatus> defaultTaskStatuses = taskStatusUtils.getDefaultTaskStatuses();

        for (TaskStatus taskStatus : defaultTaskStatuses) {
            if (taskStatusRepository.findBySlug(taskStatus.getSlug()).isEmpty()) {
                taskStatusRepository.save(taskStatus);
            }
        }

        List<Label> defaultLabels = labelUtils.getDefaultLabels();
        for (Label label : defaultLabels) {
            if (labelRepository.findByName(label.getName()).isEmpty()) {
                labelRepository.save(label);
            }
        }
    }
}
