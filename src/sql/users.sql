CREATE TABLE IF NOT EXISTS `marthelpr`.`users` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `user_name` VARCHAR(45) NULL DEFAULT NULL,
  `password` VARCHAR(45) NULL DEFAULT NULL,
  `created` DATETIME NULL DEFAULT NULL,
  `created_by` VARCHAR(45) NULL DEFAULT NULL,
  `last_updated` DATETIME NULL DEFAULT NULL,
  `last_updated_by` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `user_name_UNIQUE` (`user_name` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci