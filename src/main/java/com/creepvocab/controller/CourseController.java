package com.creepvocab.controller;

import com.creepvocab.common.result.Result;
import com.creepvocab.entity.CoursePack;
import com.creepvocab.service.CourseService;
import com.creepvocab.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Course API")
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "Get All Course Packs")
    @PostMapping("/list")
    public Result<List<CoursePack>> list() {
        return Result.success(courseService.getAllCourses());
    }

    @Operation(summary = "Get My Courses")
    @PostMapping("/my")
    public Result<List<CoursePack>> myCourses() {
        Long userId = SecurityUtils.getUserId();
        return Result.success(courseService.getUserCourses(userId));
    }

    @Operation(summary = "Select/Add Course")
    @PostMapping("/select")
    public Result<?> select(@RequestBody Map<String, Long> params) {
        Long userId = SecurityUtils.getUserId();
        Long courseId = params.get("courseId");
        courseService.selectCourse(userId, courseId);
        return Result.success();
    }
}
