package io.heynow.mapperoperator;

import io.heynow.mapperoperator.service.MapperService;
import io.heynow.mapperoperator.service.ScriptService;
import io.heynow.stream.manager.client.EnableStreamManagerClient;
import io.heynow.stream.manager.client.model.Note;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.annotation.Router;

@Slf4j
@EnableBinding(Sink.class)
@EnableStreamManagerClient
@RequiredArgsConstructor(onConstructor = @___({@Autowired}))
@SpringBootApplication
public class Application {

    private final Sink channels;
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

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
