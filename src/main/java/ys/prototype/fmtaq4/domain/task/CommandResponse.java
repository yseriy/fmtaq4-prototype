package ys.prototype.fmtaq4.domain.task;

import java.util.Objects;
import java.util.UUID;

public class CommandResponse {

    private final UUID commandId;
    private final CommandResponseStatus status;
    private final String body;

    public CommandResponse(final UUID commandId, final CommandResponseStatus status, final String body) {
        this.commandId = commandId;
        this.status = status;
        this.body = body;
    }

    public UUID getCommandId() {
        return commandId;
    }

    public CommandResponseStatus getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "CommandResponse{commandId=" + commandId + ", status=" + status + ", body='" + body + "\'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CommandResponse that = (CommandResponse) o;

        return Objects.equals(commandId, that.commandId) && status == that.status && Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandId, status, body);
    }
}
