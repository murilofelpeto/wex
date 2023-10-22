SET NAMES utf8;
SET time_zone = '+00:00';
SET foreign_key_checks = 0;
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

SET NAMES utf8mb4;

DROP TABLE IF EXISTS `PurchaseEntity`;
CREATE TABLE `PurchaseEntity` (
  `amount` decimal(38,2) NOT NULL,
  `transaction_date` date NOT NULL,
  `id` bigint NOT NULL,
  `uuid` binary(16) NOT NULL,
  `description` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_purchase_uuid` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `PurchaseEntity_SEQ`;
CREATE TABLE `PurchaseEntity_SEQ` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `PurchaseEntity_SEQ` (`next_val`) VALUES
(1);