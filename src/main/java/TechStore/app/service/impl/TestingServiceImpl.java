package TechStore.app.service.impl;

import TechStore.app.dto.response.TestingResponseDto;
import TechStore.app.service.TestingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestingServiceImpl implements TestingService {
    @Value("${environment.env}")
    private String env;
    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Override
    public TestingResponseDto testingEnvironment() {

        TestingResponseDto response = new TestingResponseDto();
        response.setEnv(env);
        response.setDatasourceUrl(datasourceUrl);

        return response;
    }
}
