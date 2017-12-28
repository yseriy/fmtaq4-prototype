package ys.prototype.fmtaq4.domain.task;

import org.springframework.stereotype.Service;

@Service
public interface CommandSendService {

    void send(Command command);
}
