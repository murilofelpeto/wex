DROP TABLE IF EXISTS `PurchaseEntity`;
CREATE TABLE `PurchaseEntity` (
  `amount` decimal(38,2) NOT NULL,
  `transaction_date` date NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `uuid` binary(16) NOT NULL,
  `description` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_purchase_uuid` (`uuid`)
) ENGINE=InnoDB;