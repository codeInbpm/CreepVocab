package com.creepvocab.controller;

import com.creepvocab.common.result.Result;
import com.creepvocab.entity.Word;
import com.creepvocab.service.WordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Word", description = "Word API")
@RestController
@RequestMapping("/word")
public class WordController {

    private final WordService wordService;

    public WordController(WordService wordService) {
        this.wordService = wordService;
    }

    @Operation(summary = "Get Word Books")
    @GetMapping("/books")
    public Result<List<Word>> getBooks() {
        return Result.success(wordService.list());
    }
}
