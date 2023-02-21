package TechStore.app.controller;

import TechStore.app.constant.ConstantApi;
import TechStore.app.dto.response.TestingResponseDto;
import TechStore.app.service.TestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/testing", produces = ConstantApi.CONTENT_TYPE)
public class TestingController {
    @Autowired
    private TestingService testingService;

    @GetMapping("env")
    public ResponseEntity<TestingResponseDto> testing() {
        return new ResponseEntity<>(testingService.testingEnvironment(), HttpStatus.OK);
    }
}
