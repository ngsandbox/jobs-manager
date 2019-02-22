package org.jobs.manager.backend.controllers;

import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.backend.models.TaskInfo;
import org.jobs.manager.common.schedulers.Schedulers;
import org.jobs.manager.common.services.TaskService;
import org.jobs.manager.common.shared.TaskStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class JobsManagerController {

    private final TaskService taskService;
    private final List<TaskStrategy> strategies;

    @Autowired
    public JobsManagerController(TaskService taskService, List<TaskStrategy> strategies) {
        this.taskService = taskService;
        this.strategies = strategies;
    }


//    @GetMapping("/")
//    public ModelAndView redirect() {
//        return new ModelAndView("redirect:/manager");
//    }

    @GetMapping("/jobs")
    public String displayTasks(Model model) {
        Iterable<TaskInfo> taskInfos = taskService.getTasks().map(TaskInfo::from)
                .toIterable();
        Map<String, String> strategies = this.strategies.stream().collect(Collectors.toMap(TaskStrategy::getCode, TaskStrategy::getDescription));

        model.addAttribute("tasks", taskInfos);
        model.addAttribute("strategies", strategies);
        model.addAttribute("schedulers", Schedulers.SCHEDULERS);
        return "index";
    }
}
