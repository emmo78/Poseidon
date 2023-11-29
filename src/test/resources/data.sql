CREATE TABLE `bidlist` (
  `BidListId` tinyint NOT NULL AUTO_INCREMENT,
  `account` varchar(30) NOT NULL,
  `type` varchar(30) NOT NULL,
  `bidQuantity` double DEFAULT NULL,
  `askQuantity` double DEFAULT NULL,
  `bid` double DEFAULT NULL,
  `ask` double DEFAULT NULL,
  `benchmark` varchar(125) DEFAULT NULL,
  `bidListDate` timestamp NULL DEFAULT NULL,
  `commentary` varchar(125) DEFAULT NULL,
  `security` varchar(125) DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  `trader` varchar(125) DEFAULT NULL,
  `book` varchar(125) DEFAULT NULL,
  `creationName` varchar(125) DEFAULT NULL,
  `creationDate` timestamp NULL DEFAULT NULL,
  `revisionName` varchar(125) DEFAULT NULL,
  `revisionDate` timestamp NULL DEFAULT NULL,
  `dealName` varchar(125) DEFAULT NULL,
  `dealType` varchar(125) DEFAULT NULL,
  `sourceListId` varchar(125) DEFAULT NULL,
  `side` varchar(125) DEFAULT NULL,
  PRIMARY KEY (`BidListId`)
);

CREATE TABLE `trade` (
  `TradeId` tinyint NOT NULL AUTO_INCREMENT,
  `account` varchar(30) NOT NULL,
  `type` varchar(30) NOT NULL,
  `buyQuantity` double DEFAULT NULL,
  `sellQuantity` double DEFAULT NULL,
  `buyPrice` double DEFAULT NULL,
  `sellPrice` double DEFAULT NULL,
  `tradeDate` timestamp NULL DEFAULT NULL,
  `security` varchar(125) DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  `trader` varchar(125) DEFAULT NULL,
  `benchmark` varchar(125) DEFAULT NULL,
  `book` varchar(125) DEFAULT NULL,
  `creationName` varchar(125) DEFAULT NULL,
  `creationDate` timestamp NULL DEFAULT NULL,
  `revisionName` varchar(125) DEFAULT NULL,
  `revisionDate` timestamp NULL DEFAULT NULL,
  `dealName` varchar(125) DEFAULT NULL,
  `dealType` varchar(125) DEFAULT NULL,
  `sourceListId` varchar(125) DEFAULT NULL,
  `side` varchar(125) DEFAULT NULL,
  PRIMARY KEY (`TradeId`)
);

CREATE TABLE `curvepoint` (
  `Id` tinyint NOT NULL AUTO_INCREMENT,
  `CurveId` tinyint NOT NULL UNIQUE,
  `asOfDate` timestamp NULL DEFAULT NULL,
  `term` double DEFAULT NULL,
  `value` double DEFAULT NULL,
  `creationDate` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`Id`)
);

CREATE TABLE `rating` (
  `Id` tinyint NOT NULL AUTO_INCREMENT,
  `moodysRating` varchar(125) DEFAULT NULL,
  `sandPRating` varchar(125) DEFAULT NULL,
  `fitchRating` varchar(125) DEFAULT NULL,
  `orderNumber` tinyint DEFAULT NULL,
  PRIMARY KEY (`Id`)
);

CREATE TABLE `rulename` (
  `Id` tinyint NOT NULL AUTO_INCREMENT,
  `name` varchar(125) DEFAULT NULL,
  `description` varchar(125) DEFAULT NULL,
  `json` varchar(125) DEFAULT NULL,
  `template` varchar(512) DEFAULT NULL,
  `sqlStr` varchar(125) DEFAULT NULL,
  `sqlPart` varchar(125) DEFAULT NULL,
  PRIMARY KEY (`Id`)
);

CREATE TABLE `users` (
  `Id` tinyint NOT NULL AUTO_INCREMENT,
  `username` varchar_ignorecase(125)NOT NULL UNIQUE ,
  `password` varchar(125) DEFAULT NULL,
  `fullname` varchar(125) DEFAULT NULL,
  `role` varchar(125) DEFAULT NULL,
  PRIMARY KEY (`Id`)
);