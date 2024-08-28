package com.bajaj;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class DestinationHashGenerator {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN Number> <Path to JSON file>");
            System.exit(1);
        }

        String prnNumber = args[0].trim().toLowerCase();
        String jsonFilePath = args[1].trim();

        try {
            // Read JSON file
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(new File(jsonFilePath));

            // Traverse JSON to find the first instance of "destination"
            String destinationValue = findDestinationValue(rootNode);
            if (destinationValue == null) {
                System.out.println("No 'destination' key found in the JSON file.");
                System.exit(1);
            }

            // Generate random alphanumeric string
            String randomString = generateRandomString(8);

            // Generate MD5 hash
            String concatenatedString = prnNumber + destinationValue + randomString;
            String md5Hash = generateMD5Hash(concatenatedString);

            // Output result
            System.out.println(md5Hash + ";" + randomString);

        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
            System.exit(1);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("MD5 algorithm not found: " + e.getMessage());
            System.exit(1);
        }
    }

    private static String findDestinationValue(JsonNode node) {
        if (node == null) {
            return null;
        }

        if (node.has("destination")) {
            return node.get("destination").asText();
        }

        if (node.isObject()) {
            for (JsonNode child : node) {
                String result = findDestinationValue(child);
                if (result != null) {
                    return result;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode element : node) {
                String result = findDestinationValue(element);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
