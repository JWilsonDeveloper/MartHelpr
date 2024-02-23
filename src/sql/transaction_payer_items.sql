CREATE TABLE IF NOT EXISTS `marthelpr`.`transaction_item_payers` (
  `transaction_id` INT NOT NULL,
  `item_id` INT NOT NULL,
  `payer_id` INT NOT NULL,
  PRIMARY KEY (`transaction_id`, `item_id`, `payer_id`),
  INDEX `fk_item_id_idx` (`item_id` ASC) VISIBLE,
  INDEX `fk_payer_id_idx` (`payer_id` ASC) VISIBLE,
  CONSTRAINT `fk_item_id`
    FOREIGN KEY (`item_id`)
    REFERENCES `marthelpr`.`items` (`item_id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_payer_id`
    FOREIGN KEY (`payer_id`)
    REFERENCES `marthelpr`.`payers` (`payer_id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_transaction_id`
    FOREIGN KEY (`transaction_id`)
    REFERENCES `marthelpr`.`transactions` (`transaction_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci