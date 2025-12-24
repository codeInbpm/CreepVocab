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
