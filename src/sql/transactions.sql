CREATE TABLE IF NOT EXISTS `marthelpr`.`transactions` (
  `transaction_id` INT NOT NULL AUTO_INCREMENT,
  `total` DECIMAL(15,2) NULL DEFAULT NULL,
  `no_payers` INT NULL DEFAULT NULL,
  `transaction_date` DATE NULL DEFAULT NULL,
  `last_updated` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`transaction_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci