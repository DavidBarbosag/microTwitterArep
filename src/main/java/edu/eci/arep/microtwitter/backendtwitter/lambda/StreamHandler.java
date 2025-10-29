package edu.eci.arep.microtwitter.backendtwitter.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

public class StreamHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final DynamoDbClient dynamoDB = DynamoDbClient.create();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String TABLE_NAME = "Streams";

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            String method = request.getHttpMethod();

            if ("GET".equals(method) && request.getPathParameters() != null) {
                return getStream(request);
            } else if ("GET".equals(method)) {
                return getAllStreams();
            } else if ("POST".equals(method)) {
                return createStream(request);
            }

            return createResponse(405, "{\"error\":\"Method not allowed\"}");
        } catch (Exception e) {
            return createResponse(500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private APIGatewayProxyResponseEvent createStream(APIGatewayProxyRequestEvent request) throws Exception {
        Map<String, Object> body = mapper.readValue(request.getBody(), Map.class);
        String streamId = UUID.randomUUID().toString();

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(streamId).build());
        item.put("name", AttributeValue.builder().s((String) body.get("name")).build());
        item.put("createdAt", AttributeValue.builder().s(new Date().toString()).build());

        dynamoDB.putItem(PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build());

        return createResponse(201, mapper.writeValueAsString(Map.of("id", streamId)));
    }

    private APIGatewayProxyResponseEvent getStream(APIGatewayProxyRequestEvent request) throws Exception {
        String streamId = request.getPathParameters().get("id");

        GetItemResponse response = dynamoDB.getItem(GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("id", AttributeValue.builder().s(streamId).build()))
                .build());

        if (!response.hasItem()) {
            return createResponse(404, "{\"error\":\"Stream not found\"}");
        }

        return createResponse(200, mapper.writeValueAsString(response.item()));
    }

    private APIGatewayProxyResponseEvent getAllStreams() throws Exception {
        ScanResponse response = dynamoDB.scan(ScanRequest.builder()
                .tableName(TABLE_NAME)
                .build());

        return createResponse(200, mapper.writeValueAsString(response.items()));
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

