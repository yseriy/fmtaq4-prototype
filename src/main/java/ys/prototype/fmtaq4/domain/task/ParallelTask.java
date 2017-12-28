package ys.prototype.fmtaq4.domain.task;

import javax.persistence.Entity;
import java.util.UUID;

@Entity
class ParallelTask extends Task {

    private Integer commandCounter;

    protected ParallelTask() {
    }

    ParallelTask(final UUID id, final String account, final String serviceType) {
        super(id, account, serviceType);
        this.commandCounter = 0;
    }

    @Override
    public void start(final CommandSendService commandSendService) {
        getCommands().forEach(commandSendService::send);
    }

    @Override
    void handleOkResponse(final Command command, final CommandRepository commandRepository,
                          final CommandSendService transport) {
        decreaseCommandCounter();

        if (isLastCommand()) {
            setOkStatus();
        }
    }

    @Override
    void handleErrorResponse(final Command command, final CommandRepository commandRepository,
                             final CommandSendService transport) {
        decreaseCommandCounter();

        if (isLastCommand()) {
            setErrorStatus();
        }
    }

    @Override
    public void addCommand(final String address, final String body) {
        increaseCommandCounter();
        getCommands().add(new ParallelCommand(UUID.randomUUID(), address, body, this));
    }

    private void increaseCommandCounter() {
        commandCounter++;
    }

    private void decreaseCommandCounter() {
        commandCounter--;
        assert commandCounter < 0 : "command counter cannot be less that zero";
    }

    private boolean isLastCommand() {
        return commandCounter == 0;
    }
}

@Entity
class ParallelCommand extends Command {

    protected ParallelCommand() {
    }

    ParallelCommand(final UUID id, final String address, final String body, final Task task) {
        super(id, address, body, task);
    }
}

class ParallelTaskFactory implements TaskFactory {

    @Override
    public Task createTask(String account, String serviceType) {
        return new ParallelTask(UUID.randomUUID(), account, serviceType);
    }
}
