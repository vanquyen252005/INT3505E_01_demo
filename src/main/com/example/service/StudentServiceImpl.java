package com.example.service;

import com.example.model.StudentResult;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.HashMap;
import java.util.Map;

@WebService(
        targetNamespace = "http://service.student.example.com/"
)
public class StudentServiceImpl {
    private static final Map<String, StudentResult> data = new HashMap<>();

    static {
        data.put("SV001", new StudentResult(3.85f, 95, "Xuất sắc"));
        data.put("SV002", new StudentResult(3.3f, 85, "Giỏi"));
        data.put("SV003", new StudentResult(2.5f, 70, "Trung bình"));
    }

    @WebMethod
    public StudentResult getStudentResult(@WebParam(name = "mssv") String mssv) {
        return data.getOrDefault(mssv, new StudentResult(0.0f, 0, "Không tìm thấy"));
    }
}
