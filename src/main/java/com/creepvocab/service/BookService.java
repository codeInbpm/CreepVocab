package com.creepvocab.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.creepvocab.entity.*;
import com.creepvocab.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookCategoryMapper bookCategoryMapper;
    private final BookMapper bookMapper;
    private final BookArticleMapper bookArticleMapper;
    private final BookWordMapper bookWordMapper;
    private final UserBookMapper userBookMapper;
    private final WordMapper wordMapper;
    private final UserWordMapper userWordMapper;

    public List<BookCategory> getCategoryList() {
        return bookCategoryMapper.selectList(
                new LambdaQueryWrapper<BookCategory>().orderByAsc(BookCategory::getSortOrder)
        );
    }

    public List<Book> getBooksByCategory(Long categoryId) {
        return bookMapper.selectList(
                new LambdaQueryWrapper<Book>().eq(Book::getCategoryId, categoryId)
        );
    }
    
    public Map<String, Object> getBookDetail(Long userId, Long bookId) {
        Map<String, Object> result = new HashMap<>();
        // 1. Basic Book Info
        Book book = bookMapper.selectById(bookId);
        result.put("book", book);

        // 2. Articles List
        List<BookArticle> articles = bookArticleMapper.selectList(
                new LambdaQueryWrapper<BookArticle>()
                        .eq(BookArticle::getBookId, bookId)
                        .orderByAsc(BookArticle::getSortOrder)
        );
        result.put("articles", articles);

        // 3. User Favorite Status
        boolean isFavorited = false;
        if (userId != null) {
            Long count = userBookMapper.selectCount(
                    new LambdaQueryWrapper<UserBook>()
                            .eq(UserBook::getUserId, userId)
                            .eq(UserBook::getBookId, bookId)
            );
            isFavorited = count > 0;
        }
        result.put("isFavorited", isFavorited);

        return result;
    }

    public void toggleFavorite(Long userId, Long bookId) {
        UserBook existing = userBookMapper.selectOne(
                new LambdaQueryWrapper<UserBook>()
                        .eq(UserBook::getUserId, userId)
                        .eq(UserBook::getBookId, bookId)
        );
        if (existing != null) {
            userBookMapper.deleteById(existing.getId());
            // Decrement book view/favorite count conceptually if needed
        } else {
            UserBook ub = new UserBook();
            ub.setUserId(userId);
            ub.setBookId(bookId);
            ub.setCreateTime(LocalDateTime.now());
            userBookMapper.insert(ub);
        }
    }

    public Map<String, Object> getVocabStats(Long userId, Long bookId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 1. Total Words in Book
        Long totalVocab = bookWordMapper.selectCount(
                new LambdaQueryWrapper<BookWord>().eq(BookWord::getBookId, bookId)
        );
        stats.put("totalVocab", totalVocab);

        // 2. My Vocab (Intersection of user_word and book_word)
        Long myVocab = 0L;
        if (userId != null) {
            // Can be optimized with custom mapper mapping, doing it with wrapper queries for simplicity:
            // Find all word IDs in this book
            List<Long> bookWordIds = bookWordMapper.selectList(
                    new LambdaQueryWrapper<BookWord>().eq(BookWord::getBookId, bookId)
            ).stream().map(BookWord::getWordId).collect(Collectors.toList());
            
            if (!bookWordIds.isEmpty()) {
                myVocab = userWordMapper.selectCount(
                        new LambdaQueryWrapper<UserWord>()
                                .eq(UserWord::getUserId, userId)
                                .in(UserWord::getWordId, bookWordIds)
                );
            }
        }
        stats.put("myVocab", myVocab);

        // 3. Breakdown by Category (Mocks actual custom query returning Map)
        // Since doing a join through mybatis-plus wrappers is clunky, we will fetch words directly
        List<Long> bookWordIds = bookWordMapper.selectList(
                new LambdaQueryWrapper<BookWord>().eq(BookWord::getBookId, bookId)
        ).stream().map(BookWord::getWordId).collect(Collectors.toList());

        Map<String, Long> categoryBreakdown = new HashMap<>();
        if (!bookWordIds.isEmpty()) {
            List<Word> words = wordMapper.selectBatchIds(bookWordIds);
            categoryBreakdown = words.stream()
                .filter(w -> w.getCategory() != null)
                .collect(Collectors.groupingBy(Word::getCategory, Collectors.counting()));
        }
        stats.put("categories", categoryBreakdown);

        return stats;
    }
}
