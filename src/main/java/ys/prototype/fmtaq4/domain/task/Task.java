package ys.prototype.fmtaq4.domain.task;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Entity
@Inheritance
public abstract class Task {

    @Id
    private UUID id;
    private LocalDateTime startTimestamp;
    private LocalDateTime statusTimestamp;
    private String account;
    private String serviceType;
    private TaskStatus taskStatus;

    @Version
    private Long version;

    @OneToMany(mappedBy = "task", cascade = CascadeType.PERSIST)
    private Set<Command> commands = new HashSet<>();

    protected Task() {
    }

    Task(final UUID id, final String account, final String serviceType) {
        LocalDateTime createTime = LocalDateTime.now();
        this.id = id;
        this.startTimestamp = createTime;
        this.statusTimestamp = createTime;
        this.account = account;
        this.serviceType = serviceType;
        this.taskStatus = TaskStatus.NEW;
    }

    public abstract void addCommand(String address, String body);

    public abstract void start(CommandSendService transport);

    public void handleResponse(final CommandResponse commandResponse, final Command command,
                               final CommandRepository commandRepository, final CommandSendService commandSendService) {
        switch (commandResponse.getStatus()) {
            case OK:
                command.setOkStatus();
                handleOkResponse(command, commandRepository, commandSendService);
                break;
            case ERROR:
                command.setErrorStatus();
                handleErrorResponse(command, commandRepository, commandSendService);
                break;
            default:
                throw new IllegalArgumentException("unknown command response status: " + commandResponse.getStatus());
        }
    }

    abstract void handleOkResponse(Command command, CommandRepository commandRepository, CommandSendService transport);

    abstract void handleErrorResponse(Command command, CommandRepository commandRepository, CommandSendService transport);

    Set<Command> getCommands() {
        return commands;
    }

    void addCommand(final Command command) {
        commands.add(command);
    }

    void setOkStatus() {
        statusTimestamp = LocalDateTime.now();
        taskStatus = TaskStatus.OK;
    }

    void setErrorStatus() {
        statusTimestamp = LocalDateTime.now();
        taskStatus = TaskStatus.ERROR;
    }

    Command findCommandById(final UUID commandId) {
        return commands.stream().filter(c -> c.getId().equals(commandId)).findFirst()
                .orElseThrow(() -> new NoSuchElementException("cannot find command by id: " + commandId));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "-" + id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Task task = (Task) o;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
