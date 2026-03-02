CREATE DATABASE IF NOT EXISTS `creep_vocab` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `creep_vocab`;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `openid` varchar(64) DEFAULT NULL COMMENT 'WeChat OpenID',
  `wx_session_key` varchar(255) DEFAULT NULL COMMENT 'WeChat Session Key',
  `phone` varchar(20) DEFAULT NULL COMMENT 'Phone Number',
  `nickname` varchar(64) DEFAULT NULL COMMENT 'Nickname',
  `avatar` varchar(255) DEFAULT NULL COMMENT 'Avatar URL',
  `coins` int(11) DEFAULT '0' COMMENT 'Coins',
  `streak` int(11) DEFAULT '0' COMMENT 'Check-in Streak',
  `level` int(11) DEFAULT '1' COMMENT 'Level',
  `word_power` int(11) DEFAULT '0' COMMENT 'Word Power',
  `challenge_high_score` int(11) DEFAULT '0',
  `total_battles` int(11) DEFAULT '0',
  `win_count` int(11) DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User';

-- ----------------------------
-- Table structure for word
-- ----------------------------
DROP TABLE IF EXISTS `word`;
CREATE TABLE `word` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `word` varchar(100) NOT NULL COMMENT 'Word',
  `meaning` text COMMENT 'Meaning',
  `phonetic` varchar(100) DEFAULT NULL COMMENT 'Phonetic Symbol',
  `example` text COMMENT 'Example Sentence',
  `category` varchar(50) DEFAULT NULL COMMENT 'Category (CET4, CET6, etc)',
  PRIMARY KEY (`id`),
  KEY `idx_word` (`word`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Vocabulary';

-- ----------------------------
-- Table structure for battle_record
-- ----------------------------
DROP TABLE IF EXISTS `battle_record`;
CREATE TABLE `battle_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user1_id` bigint(20) NOT NULL COMMENT 'User 1 ID',
  `user2_id` bigint(20) DEFAULT NULL COMMENT 'User 2 ID (Null for AI)',
  `winner_id` bigint(20) DEFAULT NULL COMMENT 'Winner ID',
  `score1` int(11) DEFAULT '0' COMMENT 'User 1 Score',
  `score2` int(11) DEFAULT '0' COMMENT 'User 2 Score',
  `battle_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Battle Time',
  `mode` varchar(20) DEFAULT 'random' COMMENT 'Battle Mode',
  PRIMARY KEY (`id`),
  KEY `idx_user1` (`user1_id`),
  KEY `idx_user2` (`user2_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Battle Records';

-- ----------------------------
-- Table structure for user_word
-- ----------------------------
DROP TABLE IF EXISTS `user_word`;
CREATE TABLE `user_word` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT 'User ID',
  `word_id` bigint(20) NOT NULL COMMENT 'Word ID',
  `mastery` int(11) DEFAULT '0' COMMENT 'Mastery Level',
  `review_time` datetime DEFAULT NULL COMMENT 'Last Review Time',
  `next_review_time` datetime DEFAULT NULL COMMENT 'Next Review Time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_word` (`user_id`,`word_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User Wrong/Star Words';

-- ----------------------------
-- Table structure for daily_checkin
-- ----------------------------
DROP TABLE IF EXISTS `daily_checkin`;
CREATE TABLE `daily_checkin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT 'User ID',
  `checkin_date` date NOT NULL COMMENT 'Check-in Date',
  `reward` int(11) DEFAULT '0' COMMENT 'Reward Coins',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`,`checkin_date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Daily Check-in';

-- ----------------------------
-- Table structure for battle_room
-- ----------------------------
DROP TABLE IF EXISTS `battle_room`;
CREATE TABLE `battle_room` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `room_code` varchar(20) DEFAULT NULL COMMENT 'Share Code',
  `creator_id` bigint(20) NOT NULL,
  `player1_id` bigint(20) DEFAULT NULL,
  `player2_id` bigint(20) DEFAULT NULL,
  `word_book` varchar(50) DEFAULT 'random' COMMENT 'Word Book Type',
  `question_count` int(11) DEFAULT '10',
  `status` varchar(20) DEFAULT 'waiting' COMMENT 'waiting/playing/finished/leave',
  `current_index` int(11) DEFAULT '0',
  `questions` json DEFAULT NULL COMMENT 'Question IDs',
  `ai_mode` tinyint(1) DEFAULT '0' COMMENT 'AI Mode',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `start_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_room_code` (`room_code`),
  KEY `idx_status_book` (`status`,`word_book`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Battle Room';

-- ----------------------------
-- Table structure for feedback
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `content` text,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Feedback';

-- ----------------------------
-- Update User Table
-- ----------------------------
-- Note: Manually running this on existing DB:
-- ALTER TABLE `user` ADD COLUMN `word_power` int DEFAULT 0 COMMENT 'Word Power';
-- ALTER TABLE `user` ADD COLUMN `challenge_high_score` int DEFAULT 0;
-- ALTER TABLE `user` ADD COLUMN `total_battles` int DEFAULT 0;
-- ALTER TABLE `user` ADD COLUMN `win_count` int DEFAULT 0;

-- ----------------------------
-- Initial Data
-- ----------------------------
INSERT INTO `word` (`word`, `meaning`, `phonetic`, `example`, `category`) VALUES 
('abandon', 'v. 放弃，遗弃；n. 放任，狂热', '/əˈbændən/', 'He abandoned his plan.', 'CET4'),
('ability', 'n. 能力，本领；才能', '/əˈbɪləti/', 'She has the ability to do the job.', 'CET4'),
('absent', 'adj. 缺席的，不在的', '/ˈæbsənt/', 'He was absent from work.', 'CET4');

-- ----------------------------
-- Table structure for course_pack
-- ----------------------------
DROP TABLE IF EXISTS `course_pack`;
CREATE TABLE `course_pack` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(64) NOT NULL COMMENT 'Title',
  `sub_title` varchar(64) DEFAULT NULL COMMENT 'Subtitle/Tag',
  `cover_color` varchar(64) DEFAULT NULL COMMENT 'CSS Color Gradient',
  `category` varchar(50) DEFAULT 'General' COMMENT 'Category',
  `word_count` int(11) DEFAULT '0',
  `price` int(11) DEFAULT '0' COMMENT 'Price in coins',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Course Pack';

INSERT INTO `course_pack` (`title`, `sub_title`, `cover_color`, `category`) VALUES
('考研必考词汇', '试卷真题总结', 'linear-gradient(135deg, #64B5F6 0%, #42A5F5 100%)', 'Postgraduate'),
('小学大纲词汇', '人教版 · 单词', 'linear-gradient(135deg, #7986CB 0%, #5C6BC0 100%)', 'Primary'),
('中考必备词汇', '考纲词汇大全', 'linear-gradient(135deg, #FFB74D 0%, #FFA726 100%)', 'Middle'),
('随机词汇', '', 'linear-gradient(135deg, #FF8A65 0%, #FF7043 100%)', 'General'),
('高考必备词汇', '考纲词汇大全', 'linear-gradient(135deg, #BA68C8 0%, #AB47BC 100%)', 'High'),
('六级真题词汇', '核心词汇精选', 'linear-gradient(135deg, #81C784 0%, #66BB6A 100%)', 'CET6'),
('四级真题词汇', '核心词汇精选', 'linear-gradient(135deg, #FF8A65 0%, #FF7043 100%)', 'CET4'),
('四级大纲词汇', '四级必背词汇', 'linear-gradient(135deg, #7986CB 0%, #5C6BC0 100%)', 'CET4');

-- ----------------------------
-- Table structure for user_course
-- ----------------------------
DROP TABLE IF EXISTS `user_course`;
CREATE TABLE `user_course` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `course_id` bigint(20) NOT NULL,
  `progress` int(11) DEFAULT '0' COMMENT 'Learned words count',
  `is_active` tinyint(1) DEFAULT '0' COMMENT 'Is currently selected',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_course` (`user_id`,`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User My Courses';

-- ----------------------------
-- Table structure for book_category
-- ----------------------------
DROP TABLE IF EXISTS `book_category`;
CREATE TABLE `book_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL COMMENT 'Category Name',
  `sort_order` int(11) DEFAULT '0' COMMENT 'Order in Sidebar',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Book Category';

INSERT INTO `book_category` (`id`, `name`, `sort_order`) VALUES
(1, '听单词', 1),
(2, '综合听力', 2),
(3, '公开课', 3),
(4, '有声读物', 4),
(5, 'Podcast', 5),
(6, '发音口语', 6),
(7, '娱乐影音', 7),
(8, '职场英语', 8),
(9, '双语精读', 9),
(10, '词汇', 10),
(11, '英语教材', 11),
(12, '入门英语', 12),
(13, 'VIP专属', 13);


-- ----------------------------
-- Table structure for book
-- ----------------------------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_id` bigint(20) NOT NULL,
  `title` varchar(128) NOT NULL,
  `cover_image` varchar(255) DEFAULT NULL COMMENT 'URL or color gradient or path',
  `tags` varchar(255) DEFAULT NULL COMMENT 'Comma separated tags (e.g. 全部,四六级词汇)',
  `article_count` int(11) DEFAULT '0' COMMENT 'Number of articles/lessons',
  `view_count` int(11) DEFAULT '0' COMMENT 'Total views in ten thousands (万)',
  `author` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Study Book';

INSERT INTO `book` (`category_id`, `title`, `cover_image`, `tags`, `article_count`, `view_count`, `author`) VALUES
(10, 'TIME单挑1000', 'linear-gradient(135deg, #FFB74D 0%, #FFA726 100%)', '全部,四六级词汇,VOA词汇', 39, 9, 'TIME'),
(10, 'Maddy老师教你实用词汇', 'linear-gradient(135deg, #FFD54F 0%, #FFCA28 100%)', '全部,闭眼飘单词,华研词汇', 129, 111, 'Maddy老师'),
(10, '15篇文章贯通六级词汇', 'linear-gradient(135deg, #4FC3F7 0%, #29B6F6 100%)', '全部,四六级词汇', 28, 32, '四六级教研组'),
(10, '英语词汇测评小站', 'linear-gradient(135deg, #7986CB 0%, #5C6BC0 100%)', '全部,刘毅词汇', 35, 20, '英语小站'),
(10, '词汇分类速记', 'linear-gradient(135deg, #FF8A65 0%, #FF7043 100%)', '全部,四六级词汇,新概念', 50, 48, '速记专家'),
(10, '牛津英语词汇 (初级) (第二版)', 'linear-gradient(135deg, #81C784 0%, #66BB6A 100%)', '全部,入门', 42, 34, '牛津大学出版社'),
(10, '牛津英语词汇 (中级) (第二版)', 'linear-gradient(135deg, #BA68C8 0%, #AB47BC 100%)', '全部,进阶', 45, 10, '牛津大学出版社');


-- ----------------------------
-- Table structure for book_article
-- ----------------------------
DROP TABLE IF EXISTS `book_article`;
CREATE TABLE `book_article` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `book_id` bigint(20) NOT NULL,
  `title` varchar(255) NOT NULL COMMENT 'e.g. 01 用来描述心情的...',
  `duration_str` varchar(20) DEFAULT NULL COMMENT 'e.g. 11:59',
  `size_str` varchar(20) DEFAULT NULL COMMENT 'e.g. 40.59MB',
  `publish_date` date DEFAULT NULL COMMENT 'e.g. 2022-04-08',
  `sort_order` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_book` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Book Articles/Chapters';

INSERT INTO `book_article` (`book_id`, `title`, `duration_str`, `size_str`, `publish_date`, `sort_order`) VALUES
(1, '01 用来描述心情的20个形容词 20 adjectives to describe feelings!', '11:59', '40.59MB', '2022-04-08', 1),
(1, '02 用来询问和谈论某人的个性的15个形容词 15 English adjectives to...', '12:07', '36.67MB', '2022-04-15', 2),
(1, '03 用来谈论受伤的10个短语和词汇 10 words to talk about injuries...', '09:34', '30.02MB', '2022-04-22', 3),
(1, '04 如何通过电影学习英语 How To Learn English With Movies...', '24:34', '77.93MB', '2022-04-29', 4),
(1, '05 关于食物的20个短语和词汇 20 Words To Talk About Food...', '11:23', '31.40MB', '2022-05-06', 5);


-- ----------------------------
-- Table structure for book_word
-- ----------------------------
DROP TABLE IF EXISTS `book_word`;
CREATE TABLE `book_word` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `book_id` bigint(20) NOT NULL,
  `word_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_book_word` (`book_id`,`word_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Words contained in a book';

-- Mock data: Link book 1 (TIME单挑) to word 1 (abandon), 2 (ability), 3 (absent)
INSERT INTO `book_word` (`book_id`, `word_id`) VALUES
(1, 1),
(1, 2),
(1, 3);


-- ----------------------------
-- Table structure for user_book
-- ----------------------------
DROP TABLE IF EXISTS `user_book`;
CREATE TABLE `user_book` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `book_id` bigint(20) NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Subscribed/Favorited Time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_book` (`user_id`,`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User Subscribed/Favorited Books';

