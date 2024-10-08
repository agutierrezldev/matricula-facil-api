package com.agutierrezl.easy_enrollment_api.handler;

import com.agutierrezl.easy_enrollment_api.dto.StudentDTO;
import com.agutierrezl.easy_enrollment_api.model.Student;
import com.agutierrezl.easy_enrollment_api.service.IStudentService;
import com.agutierrezl.easy_enrollment_api.validator.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@RequiredArgsConstructor
public class StudentHandler {

    private final IStudentService studentService;
    @Qualifier("studentMapper")
    private final ModelMapper modelMapper;

    private final RequestValidator validator;

    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(studentService.findAll()
                        .map(this::convertToDTO), StudentDTO.class);
    }

    public Mono<ServerResponse> findById(ServerRequest request) {
        String id = request.pathVariable("id");
        return studentService.findById(id)
                .map(this::convertToDTO)
                .flatMap(e -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(e))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<StudentDTO> StudentDTO = request.bodyToMono(StudentDTO.class);
        return StudentDTO
                .flatMap(validator::validate)
                .flatMap(e -> studentService.save(convertToDocument(e)))
                .map(this::convertToDTO)
                .flatMap(e -> ServerResponse
                        .created(URI.create(request.uri().toString().concat("/").concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(e)));
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<StudentDTO> StudentDTO = request.bodyToMono(StudentDTO.class);
        return StudentDTO
                .flatMap(validator::validate)
                .map(c -> {
                    c.setId(id);
                    return c;
                })
                .flatMap(e -> studentService.update(id, convertToDocument(e)))
                .map(this::convertToDTO)
                .flatMap(e -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(e))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");
        return studentService.delete(id)
                .flatMap(result -> {
                    if (result) {
                        return ServerResponse.noContent().build();
                    } else {
                        return ServerResponse.notFound().build();
                    }
                });
    }

    public Mono<ServerResponse> findAllByOrderByYearAscOrDesc(ServerRequest request) {
        boolean asc = Boolean.parseBoolean(request.pathVariable("asc"));
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(studentService.findAllByOrderByYearAscOrDesc(asc)
                        .map(this::convertToDTO), StudentDTO.class);
    }


    private StudentDTO convertToDTO(Student model) {
        return modelMapper.map(model, StudentDTO.class);
    }

    private Student convertToDocument(StudentDTO dto) {
        return modelMapper.map(dto, Student.class);
    }

}
