-- MySQL dump 10.13  Distrib 5.7.25, for Linux (x86_64)
--
-- Host: localhost    Database: Diary
-- ------------------------------------------------------
-- Server version	5.7.25-0ubuntu0.18.04.2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Equipment`
--

DROP TABLE IF EXISTS `Equipment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Equipment` (
  `EquipmentID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(64) NOT NULL,
  `Description` varchar(200) NOT NULL,
  PRIMARY KEY (`EquipmentID`),
  UNIQUE KEY `Equipment_UQ` (`Name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Equipment`
--

LOCK TABLES `Equipment` WRITE;
/*!40000 ALTER TABLE `Equipment` DISABLE KEYS */;
INSERT INTO `Equipment` VALUES (1,'Benkpress','Press på.'),(2,'Romaskin','Kan du ro?'),(3,'Bøylehest','Hopp'),(4,'Ergometersykkel','Du glømmer det aldri!');
/*!40000 ALTER TABLE `Equipment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `EquippedExercise`
--

DROP TABLE IF EXISTS `EquippedExercise`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `EquippedExercise` (
  `ExerciseID` int(11) NOT NULL,
  `EquipmentID` int(11) NOT NULL,
  `Weight` int(11) NOT NULL,
  `Sets` int(11) NOT NULL,
  KEY `EquippedExercise_FK1` (`ExerciseID`),
  KEY `EquippedExercise_FK2` (`EquipmentID`),
  CONSTRAINT `EquippedExercise_FK1` FOREIGN KEY (`ExerciseID`) REFERENCES `Exercise` (`ExerciseID`) ON UPDATE CASCADE,
  CONSTRAINT `EquippedExercise_FK2` FOREIGN KEY (`EquipmentID`) REFERENCES `Equipment` (`EquipmentID`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `EquippedExercise`
--

LOCK TABLES `EquippedExercise` WRITE;
/*!40000 ALTER TABLE `EquippedExercise` DISABLE KEYS */;
INSERT INTO `EquippedExercise` VALUES (1,1,30,20),(3,3,0,15),(5,2,5,200),(9,2,5,60);
/*!40000 ALTER TABLE `EquippedExercise` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Exercise`
--

DROP TABLE IF EXISTS `Exercise`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Exercise` (
  `ExerciseID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(40) NOT NULL,
  `Equipped` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`ExerciseID`),
  UNIQUE KEY `Exercise_UQ` (`Name`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Exercise`
--

LOCK TABLES `Exercise` WRITE;
/*!40000 ALTER TABLE `Exercise` DISABLE KEYS */;
INSERT INTO `Exercise` VALUES (1,'Pressing',1),(2,'Froskehopp',0),(3,'Assistert salto',1),(4,'Armheving',0),(5,'Ro',1),(8,'Slå hjul',0),(9,'Snurring',1);
/*!40000 ALTER TABLE `Exercise` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ExerciseGroup`
--

DROP TABLE IF EXISTS `ExerciseGroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ExerciseGroup` (
  `GroupID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(40) NOT NULL,
  PRIMARY KEY (`GroupID`),
  UNIQUE KEY `Group_UQ` (`Name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ExerciseGroup`
--

LOCK TABLES `ExerciseGroup` WRITE;
/*!40000 ALTER TABLE `ExerciseGroup` DISABLE KEYS */;
INSERT INTO `ExerciseGroup` VALUES (1,'Armtrening');
/*!40000 ALTER TABLE `ExerciseGroup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ExerciseInGroup`
--

DROP TABLE IF EXISTS `ExerciseInGroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ExerciseInGroup` (
  `ExerciseID` int(11) NOT NULL,
  `GroupID` int(11) NOT NULL,
  KEY `ExerciseInGroup_FK1` (`ExerciseID`),
  KEY `ExerciseInGroup_FK2` (`GroupID`),
  CONSTRAINT `ExerciseInGroup_FK1` FOREIGN KEY (`ExerciseID`) REFERENCES `Exercise` (`ExerciseID`) ON UPDATE CASCADE,
  CONSTRAINT `ExerciseInGroup_FK2` FOREIGN KEY (`GroupID`) REFERENCES `ExerciseGroup` (`GroupID`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ExerciseInGroup`
--

LOCK TABLES `ExerciseInGroup` WRITE;
/*!40000 ALTER TABLE `ExerciseInGroup` DISABLE KEYS */;
INSERT INTO `ExerciseInGroup` VALUES (1,1),(4,1),(5,1);
/*!40000 ALTER TABLE `ExerciseInGroup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ExerciseInWorkout`
--

DROP TABLE IF EXISTS `ExerciseInWorkout`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ExerciseInWorkout` (
  `ExerciseID` int(11) NOT NULL,
  `WorkoutID` int(11) NOT NULL,
  KEY `ExerciseInWorkout_FK1` (`ExerciseID`),
  KEY `ExerciseInWorkout_FK2` (`WorkoutID`),
  CONSTRAINT `ExerciseInWorkout_FK1` FOREIGN KEY (`ExerciseID`) REFERENCES `Exercise` (`ExerciseID`) ON UPDATE CASCADE,
  CONSTRAINT `ExerciseInWorkout_FK2` FOREIGN KEY (`WorkoutID`) REFERENCES `Workout` (`WorkoutID`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ExerciseInWorkout`
--

LOCK TABLES `ExerciseInWorkout` WRITE;
/*!40000 ALTER TABLE `ExerciseInWorkout` DISABLE KEYS */;
INSERT INTO `ExerciseInWorkout` VALUES (2,1),(4,1),(3,2),(5,2),(4,2);
/*!40000 ALTER TABLE `ExerciseInWorkout` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UnequippedExercise`
--

DROP TABLE IF EXISTS `UnequippedExercise`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UnequippedExercise` (
  `ExerciseID` int(11) NOT NULL,
  `Description` varchar(200) DEFAULT NULL,
  KEY `UnequippedExercise_FK` (`ExerciseID`),
  CONSTRAINT `UnequippedExercise_FK` FOREIGN KEY (`ExerciseID`) REFERENCES `Exercise` (`ExerciseID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UnequippedExercise`
--

LOCK TABLES `UnequippedExercise` WRITE;
/*!40000 ALTER TABLE `UnequippedExercise` DISABLE KEYS */;
INSERT INTO `UnequippedExercise` VALUES (2,'Hopp som ein frosk'),(4,'Hev deg opp frå bakken med berre armane'),(8,'Veit du kva dette er?');
/*!40000 ALTER TABLE `UnequippedExercise` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Workout`
--

DROP TABLE IF EXISTS `Workout`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Workout` (
  `WorkoutID` int(11) NOT NULL AUTO_INCREMENT,
  `Date` date NOT NULL,
  `Time` time NOT NULL,
  `Duration` int(11) NOT NULL,
  `PersonalShape` tinyint(4) DEFAULT NULL,
  `PersonalPerformance` tinyint(4) DEFAULT NULL,
  `Note` varchar(600) DEFAULT NULL,
  PRIMARY KEY (`WorkoutID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Workout`
--

LOCK TABLES `Workout` WRITE;
/*!40000 ALTER TABLE `Workout` DISABLE KEYS */;
INSERT INTO `Workout` VALUES (1,'2019-03-11','14:00:00',90,5,5,'Midt på treet'),(2,'2019-03-11','18:00:00',90,8,8,'Up in the trees!');
/*!40000 ALTER TABLE `Workout` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-03-14 23:04:51
