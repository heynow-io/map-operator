package io.heynow.mapoperator.service.impl;

import io.heynow.mapoperator.service.MapperService;
import io.heynow.mapoperator.service.ScriptService;
import io.heynow.stream.manager.client.model.Note;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.annotation.Router;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class DefaultRoutingService {

    private final MapperService mapperService;
    private final ScriptService scriptService;

    @Router(inputChannel = Sink.INPUT)
    public final String route(Note note) {
        process(note);
        note.setProcessingModel(note.getProcessingModel().getNextProcessingModel());
        return note.getProcessingModel().getCurrent().getName();
    }

    private void process(Note note) {
        String script = scriptService.getScript(note.getProcessingModel().getCurrent().getId());
        mapperService.map(script, note.getPayload());
    }
}
