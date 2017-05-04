drop table if exists user;
CREATE TABLE `toutiao`.`user` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(64) NOT NULL,
  `password` VARCHAR(128) NOT NULL,
  `salt` VARCHAR(32) NOT NULL,
  `head_url` VARCHAR(256) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_bin;
drop table if exists news;
CREATE TABLE `toutiao`.`news` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(128) NOT NULL,
  `link` VARCHAR(256) NOT NULL,
  `image` VARCHAR(256) NOT NULL,
  `like_count` INT NOT NULL,
  `comment_count` INT NOT NULL,
  `created_date` DATETIME NOT NULL,
  `user_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_bin;
