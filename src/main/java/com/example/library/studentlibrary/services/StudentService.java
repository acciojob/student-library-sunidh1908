package com.example.library.studentlibrary.services;

import com.example.library.studentlibrary.models.Card;
import com.example.library.studentlibrary.models.CardStatus;
import com.example.library.studentlibrary.models.Student;
import com.example.library.studentlibrary.repositories.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {


    @Autowired
    CardService cardService4;

    @Autowired
    StudentRepository studentRepository4;

    public Student getDetailsByEmail(String email){
        Student student = studentRepository4.findByEmailId(email);
        return student;
    }

    public Student getDetailsById(int id){
        Student student = studentRepository4.findById(id).get();
        return student;
    }

    public void createStudent(Student student){
        Student.builder().id(student.getId()).emailId(student.getEmailId()).name(student.getName()).
                age(student.getAge()).country(student.getCountry()).createdOn(student.getCreatedOn()).
                updatedOn(student.getUpdatedOn()).build();
    }

    public void updateStudent(Student student){
        studentRepository4.updateStudentDetails(student);
    }

    public void deleteStudent(int id){
        studentRepository4.deleteCustom(id);
        cardService4.deactivateCard(id);
    }
}