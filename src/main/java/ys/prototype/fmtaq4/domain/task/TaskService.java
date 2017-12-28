package ys.prototype.fmtaq4.domain.task;

import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class TaskService {

    public void handleResponse(final CommandResponse commandResponse, final CommandRepository commandRepository,
                               final CommandSendService commandSendService) {
        UUID commandId = commandResponse.getCommandId();
        Command command = commandRepository.findOne(commandId);

        if (command == null) {
            throw new NoSuchElementException("cannot find command by id: " + commandId);
        }

        Task task = command.getTask();
        task.handleResponse(commandResponse, command, commandRepository, commandSendService);
    }
}
