package org.hrf.demo.test;

import org.hrf.gateway.core.annotation.MappingApi;
import org.springframework.stereotype.Component;

@Component
public class TestService {

    @MappingApi("org.hrf.user")
    public String getUser(String id) {
        return "id:" + id;
    }

}
