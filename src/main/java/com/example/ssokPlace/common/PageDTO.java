package com.example.ssokPlace.common;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

// 공통 페이지 응답 DTO

@JsonSerialize(using = PageDTO.Serializer.class)
public class PageDTO<T> extends PageImpl<T> {

    public PageDTO(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public static <T> PageDTO<T> of(Page<T> page){
        return new PageDTO<>(page.getContent(), page.getPageable(), page.getTotalElements());
    }

    @JsonComponent
    @SuppressWarnings("unchecked")
    public static class Serializer extends JsonSerializer<PageDTO<?>>{
        @Override
        public void serialize(PageDTO<?> value, com.fasterxml.jackson.core.JsonGenerator gen, com.fasterxml.jackson.databind.SerializerProvider serializers) throws java.io.IOException {
            gen.writeStartObject();
            gen.writeObjectField("content", value.getContent());
            gen.writeNumberField("page", value.getNumber());
            gen.writeNumberField("size", value.getSize());
            gen.writeNumberField("totalElements", value.getTotalElements());
            gen.writeNumberField("totalPages", value.getTotalPages());
            gen.writeBooleanField("last", value.isLast());
            gen.writeEndObject();

            // content
            gen.writeFieldName("content");
            gen.writeStartArray();
            for(Object content : value.getContent()){
                gen.writeObject(content);
            }
            gen.writeEndArray();

            gen.writeEndObject();
        }
    }
}
