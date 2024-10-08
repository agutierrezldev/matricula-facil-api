package com.agutierrezl.easy_enrollment_api.service.impl;

import com.agutierrezl.easy_enrollment_api.dto.StudentDTO;
import com.agutierrezl.easy_enrollment_api.model.Student;
import com.agutierrezl.easy_enrollment_api.repository.IGenericRepository;
import com.agutierrezl.easy_enrollment_api.repository.IStudentRepository;
import com.agutierrezl.easy_enrollment_api.service.IStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends CRUDImpl<Student, String> implements IStudentService {

    private final IStudentRepository studentRepository;

    @Override
    protected IGenericRepository<Student, String> getRepository() {
        return studentRepository;
    }


    @Override
    public Flux<Student> findAllByOrderByYearAscOrDesc(boolean order) {
        if (order) {
            return studentRepository.findAllByOrderByYearAsc();
        }
        return studentRepository.findAllByOrderByYearDesc();
    }
}
