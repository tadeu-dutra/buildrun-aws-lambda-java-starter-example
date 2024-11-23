package tech.buildrun.lambda;

import java.io.UncheckedIOException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tech.buildrun.lambda.request.LoginRequest;
import tech.buildrun.lambda.response.LoginResponse;

public class Handler implements
        RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request,
                                                      Context context) {
        var logger = context.getLogger();

        logger.log("Request received - " + request.getBody());

        try {
            var loginRequest = objectMapper.readValue(request.getBody(), LoginRequest.class);

            boolean isAuthorized = isAuthorizedUser(loginRequest.username(), loginRequest.password());

            var loginResponse = new LoginResponse(isAuthorized);

            return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                // .withBody("""
                //         {
                //             "sucesso": true
                //         }
                //         """)
                .withBody(objectMapper.writeValueAsString(loginResponse))
                .withIsBase64Encoded(false);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }

        
    }

    private boolean isAuthorizedUser(String username, String password) {
        if (username.equals("admin") && password.equals("admin")) {
            return true;
        }

        return false;
    }

}
