package ys.prototype.fmtaq4.domain.task;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Inheritance
public abstract class Command {

    @Id
    private UUID id;
    private LocalDateTime startTimestamp;
    private LocalDateTime statusTimestamp;
    private String address;
    private String body;
    private CommandStatus commandStatus;

    @Version
    private Long version;

    @ManyToOne
    private Task task;

    protected Command() {
    }

    Command(final UUID id, final String address, final String body, final Task task) {
        LocalDateTime createTime = LocalDateTime.now();
        this.id = id;
        this.startTimestamp = createTime;
        this.statusTimestamp = createTime;
        this.address = address;
        this.body = body;
        this.commandStatus = CommandStatus.REGISTERED;
        this.task = task;
    }

    public UUID getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getBody() {
        return body;
    }

    void setOkStatus() {
        statusTimestamp = LocalDateTime.now();
        commandStatus = CommandStatus.OK;
    }

    void setErrorStatus() {
        statusTimestamp = LocalDateTime.now();
        commandStatus = CommandStatus.ERROR;
    }

    Task getTask() {
        return task;
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

        Command command = (Command) o;
        return id.equals(command.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
