package ys.prototype.fmtaq4.domain.task;

import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class TaskService {

    private final CommandRepository commandRepository;
    private final CommandSendService commandSendService;

    public TaskService(CommandRepository commandRepository, CommandSendService commandSendService) {
        this.commandRepository = commandRepository;
        this.commandSendService = commandSendService;
    }

    public void handleResponse(CommandResponse commandResponse) {
        UUID commandId = commandResponse.getCommandId();
        Command command = commandRepository.findOne(commandId);

        if (command == null) {
            throw new NoSuchElementException("cannot find command by id: " + commandId);
        }

        Task task = command.getTask();
        task.handleResponse(commandResponse, command, commandRepository, commandSendService);
    }
}
