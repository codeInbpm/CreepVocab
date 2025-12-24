package com.creepvocab.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creepvocab.entity.CoursePack;
import com.creepvocab.entity.UserCourse;
import com.creepvocab.mapper.CoursePackMapper;
import com.creepvocab.mapper.UserCourseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CoursePackMapper coursePackMapper;
    private final UserCourseMapper userCourseMapper;

    /**
     * Get all available course packs
     */
    public List<CoursePack> getAllCourses() {
        return coursePackMapper.selectList(null);
    }

    /**
     * Get user's active/owned courses
     */
    public List<CoursePack> getUserCourses(Long userId) {
        // Query user_course table for course IDs
        List<UserCourse> userCourses = userCourseMapper.selectList(
                new LambdaQueryWrapper<UserCourse>().eq(UserCourse::getUserId, userId)
        );

        if (userCourses.isEmpty()) {
            return List.of();
        }

        List<Long> courseIds = userCourses.stream()
                .map(UserCourse::getCourseId)
                .collect(Collectors.toList());

        // Fetch course details
        return coursePackMapper.selectBatchIds(courseIds);
    }

    /**
     * User selects/adds a course
     */
    @Transactional
    public void selectCourse(Long userId, Long courseId) {
        // Check if already added
        UserCourse exists = userCourseMapper.selectOne(
                new LambdaQueryWrapper<UserCourse>()
                        .eq(UserCourse::getUserId, userId)
                        .eq(UserCourse::getCourseId, courseId)
        );

        if (exists != null) {
            // Already owns it, just set active
            setActive(userId, courseId);
            return;
        }

        // Create new record
        UserCourse userCourse = new UserCourse();
        userCourse.setUserId(userId);
        userCourse.setCourseId(courseId);
        userCourse.setProgress(0);
        userCourse.setIsActive(true); // Default to active when added
        userCourse.setCreateTime(LocalDateTime.now());
        
        // Deactivate others first
        deactivateAll(userId);
        
        userCourseMapper.insert(userCourse);
    }

    private void setActive(Long userId, Long courseId) {
        deactivateAll(userId);
        UserCourse update = new UserCourse();
        update.setIsActive(true);
        userCourseMapper.update(update, 
                new LambdaQueryWrapper<UserCourse>()
                        .eq(UserCourse::getUserId, userId)
                        .eq(UserCourse::getCourseId, courseId));
    }

    private void deactivateAll(Long userId) {
        UserCourse update = new UserCourse();
        update.setIsActive(false);
        userCourseMapper.update(update, 
                new LambdaQueryWrapper<UserCourse>().eq(UserCourse::getUserId, userId));
    }
}
