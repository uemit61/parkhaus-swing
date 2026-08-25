-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema parkhaus
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema parkhaus
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `parkhaus` DEFAULT CHARACTER SET utf8mb3 ;
USE `parkhaus` ;

-- -----------------------------------------------------
-- Table `parkhaus`.`fahrzeug`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `parkhaus`.`fahrzeug` ;

CREATE TABLE IF NOT EXISTS `parkhaus`.`fahrzeug` (
  `nummernschild` VARCHAR(45) NOT NULL,
  `typ` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`nummernschild`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `parkhaus`.`parketage`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `parkhaus`.`parketage` ;

CREATE TABLE IF NOT EXISTS `parkhaus`.`parketage` (
  `etageNr` INT NOT NULL,
  `anzahlPlaetze` INT NULL DEFAULT NULL,
  PRIMARY KEY (`etageNr`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `parkhaus`.`garage`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `parkhaus`.`garage` ;

CREATE TABLE IF NOT EXISTS `parkhaus`.`garage` (
  `platzNr` INT NOT NULL,
  `Parketage_etageNr` INT NOT NULL,
  `Fahrzeug_nummernschild` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`platzNr`),
  CONSTRAINT `fk_Parkhaus_Fahrzeug1`
    FOREIGN KEY (`Fahrzeug_nummernschild`)
    REFERENCES `parkhaus`.`fahrzeug` (`nummernschild`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Parkhaus_Parketage`
    FOREIGN KEY (`Parketage_etageNr`)
    REFERENCES `parkhaus`.`parketage` (`etageNr`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

INSERT INTO `parkhaus`.`parketage` (`etageNr`, `anzahlPlaetze`) VALUES (0, 20);
INSERT INTO `parkhaus`.`parketage` (`etageNr`, `anzahlPlaetze`) VALUES (1, 120);
INSERT INTO `parkhaus`.`parketage` (`etageNr`, `anzahlPlaetze`) VALUES (2, 120);





