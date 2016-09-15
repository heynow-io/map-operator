package io.heynow.mapoperator.operator;

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
public class MapOperator {

    private final MapperService mapperService;
    private final ScriptService scriptService;

    @Router(inputChannel = Sink.INPUT)
    public final String route(Note note) {
        process(note);
        return note.proceed().getName();
    }

    private void process(Note note) {
        String script = scriptService.getScript(note.getProcessingModel().getCurrent().getId());
        mapperService.map(script, note.getPayload());
    }
}
