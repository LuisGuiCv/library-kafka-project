package com.learnkafka.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


public record Book(@NotNull Integer bookId, @NotEmpty String bookName, @NotEmpty String bookAuthor) {
}
