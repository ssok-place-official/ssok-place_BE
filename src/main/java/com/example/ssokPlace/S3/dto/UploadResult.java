package com.example.ssokPlace.S3.dto;

public record UploadResult(String key, String url, String contentType, long size) {}
