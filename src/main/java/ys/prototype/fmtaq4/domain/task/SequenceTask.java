package ys.prototype.fmtaq4.domain.task;

import javax.persistence.Entity;
import java.util.NoSuchElementException;
import java.util.UUID;

@Entity
class SequenceTask extends Task {

    private UUID firstCommandId;

    protected SequenceTask() {
    }

    SequenceTask(final UUID id, final String account, final String serviceType) {
        super(id, account, serviceType);
    }

    @Override
    public void addCommand(final String address, final String body) {
        UUID newCommandId = UUID.randomUUID();

        if (isFirstCommand()) {
            setFirstCommandId(newCommandId);
        } else {
            SequenceCommand lastCommand = getLastCommand();
            lastCommand.setNextCommandId(newCommandId);
        }

        addCommand(new SequenceCommand(newCommandId, null, address, body, this));
    }

    @Override
    public void start(final CommandSendService transport) {
        Command command = findCommandById(firstCommandId);
        transport.send(command);
    }

    @Override
    void handleOkResponse(final Command command, final CommandRepository commandRepository,
                          final CommandSendService commandSendService) {
        SequenceCommand sequenceCommand = castToSequenceCommand(command);

        if (sequenceCommand.ifLast()) {
            setOkStatus();
        } else {
            Command nextCommand = getNextCommand(sequenceCommand, commandRepository);
            commandSendService.send(nextCommand);
        }
    }

    private Command getNextCommand(final SequenceCommand command, final CommandRepository commandRepository) {
        UUID nextCommandId = command.getNextCommandId();
        SequenceCommand nextCommand = castToSequenceCommand(commandRepository.findOne(nextCommandId));

        if (nextCommand == null) {
            throw new NoSuchElementException("cannot find command by id: " + nextCommandId);
        }

        return nextCommand;
    }

    @Override
    void handleErrorResponse(final Command command, final CommandRepository commandRepository,
                             final CommandSendService commandSendService) {
        setErrorStatus();
    }

    private boolean isFirstCommand() {
        return getCommands().isEmpty();
    }

    private SequenceCommand getLastCommand() {
        return getCommands().stream().map(this::castToSequenceCommand).filter(this::ifLastCommand).findFirst()
                .orElseThrow(this::cannotFindLastCommand);
    }

    private SequenceCommand castToSequenceCommand(final Command command) {
        return (SequenceCommand) command;
    }

    private boolean ifLastCommand(final SequenceCommand command) {
        return command.getNextCommandId() == null;
    }

    private RuntimeException cannotFindLastCommand() {
        return new NoSuchElementException("cannot find last command. task: " + this);
    }

    private void setFirstCommandId(final UUID firstCommandId) {
        this.firstCommandId = firstCommandId;
    }
}

@Entity
class SequenceCommand extends Command {

    private UUID nextCommandId;

    protected SequenceCommand() {
    }

    SequenceCommand(final UUID id, final UUID nextCommandId, final String address, final String body, final Task task) {
        super(id, address, body, task);
        this.nextCommandId = nextCommandId;
    }

    boolean ifLast() {
        return nextCommandId == null;
    }

    void setNextCommandId(final UUID nextCommandId) {
        this.nextCommandId = nextCommandId;
    }

    UUID getNextCommandId() {
        return nextCommandId;
    }
}

class SequenceTaskFactory implements TaskFactory {

    @Override
    public Task createTask(final String account, final String serviceType) {
        return new SequenceTask(UUID.randomUUID(), account, serviceType);
    }
}
