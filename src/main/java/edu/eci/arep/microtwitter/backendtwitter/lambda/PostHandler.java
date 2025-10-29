package edu.eci.arep.microtwitter.backendtwitter.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

public class PostHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final DynamoDbClient dynamoDB = DynamoDbClient.create();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String TABLE_NAME = "Posts";

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            String method = request.getHttpMethod();

            if ("POST".equals(method)) {
                return createPost(request);
            } else if ("GET".equals(method)) {
                return getPosts(request);
            }

            return createResponse(405, "{\"error\":\"Method not allowed\"}");
        } catch (Exception e) {
            return createResponse(500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private APIGatewayProxyResponseEvent createPost(APIGatewayProxyRequestEvent request) throws Exception {
        Map<String, Object> body = mapper.readValue(request.getBody(), Map.class);
        String postId = UUID.randomUUID().toString();
        String text = (String) body.get("text");

        if (text == null || text.length() > 140) {
            return createResponse(400, "{\"error\":\"Invalid post text\"}");
        }

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(postId).build());
        item.put("text", AttributeValue.builder().s(text).build());
        item.put("createdAt", AttributeValue.builder().s(new Date().toString()).build());
        item.put("userId", AttributeValue.builder().s((String) body.getOrDefault("userId", "anonymous")).build());

        dynamoDB.putItem(PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build());

        return createResponse(201, mapper.writeValueAsString(Map.of("id", postId, "text", text)));
    }

    private APIGatewayProxyResponseEvent getPosts(APIGatewayProxyRequestEvent request) throws Exception {
        ScanResponse response = dynamoDB.scan(ScanRequest.builder()
                .tableName(TABLE_NAME)
                .limit(50)
                .build());

        List<Map<String, String>> posts = new ArrayList<>();
        for (Map<String, AttributeValue> item : response.items()) {
            posts.add(Map.of(
                    "id", item.get("id").s(),
                    "text", item.get("text").s(),
                    "createdAt", item.get("createdAt").s()
            ));
        }

        return createResponse(200, mapper.writeValueAsString(posts));
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
