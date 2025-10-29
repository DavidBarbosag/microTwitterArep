package edu.eci.arep.microtwitter.backendtwitter.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

public class UserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final DynamoDbClient dynamoDB = DynamoDbClient.create();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String TABLE_NAME = "Users";

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            String method = request.getHttpMethod();

            if ("GET".equals(method) && request.getPathParameters() != null) {
                return getUser(request);
            } else if ("POST".equals(method)) {
                return createUser(request);
            }

            return createResponse(405, "{\"error\":\"Method not allowed\"}");
        } catch (Exception e) {
            return createResponse(500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private APIGatewayProxyResponseEvent createUser(APIGatewayProxyRequestEvent request) throws Exception {
        Map<String, Object> body = mapper.readValue(request.getBody(), Map.class);
        String userId = UUID.randomUUID().toString();

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(userId).build());
        item.put("username", AttributeValue.builder().s((String) body.get("username")).build());
        item.put("email", AttributeValue.builder().s((String) body.get("email")).build());
        item.put("createdAt", AttributeValue.builder().s(new Date().toString()).build());

        dynamoDB.putItem(PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build());

        return createResponse(201, mapper.writeValueAsString(Map.of("id", userId, "username", body.get("username"))));
    }

    private APIGatewayProxyResponseEvent getUser(APIGatewayProxyRequestEvent request) throws Exception {
        String userId = request.getPathParameters().get("id");

        GetItemResponse response = dynamoDB.getItem(GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("id", AttributeValue.builder().s(userId).build()))
                .build());

        if (!response.hasItem()) {
            return createResponse(404, "{\"error\":\"User not found\"}");
        }

        return createResponse(200, mapper.writeValueAsString(response.item()));
    }

    private APIGatewayProxyResponseEvent createResponse(int statusCode, String body) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(statusCode);
        response.setBody(body);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        response.setHeaders(headers);

        return response;
    }
}

