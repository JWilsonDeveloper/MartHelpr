CREATE TABLE IF NOT EXISTS `marthelpr`.`items` (
  `item_id` INT NOT NULL AUTO_INCREMENT,
  `item_text` VARCHAR(90) NULL DEFAULT NULL,
  `price` DECIMAL(15,2) NULL DEFAULT NULL,
  PRIMARY KEY (`item_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci