-- phpMyAdmin SQL Dump
-- version 3.4.10.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Apr 17, 2012 at 07:44 PM
-- Server version: 5.0.95
-- PHP Version: 5.2.6

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `djcharon_ecommerce`
--

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
CREATE TABLE IF NOT EXISTS `products` (
  `pid` int(11) NOT NULL auto_increment,
  `pName` varchar(50) NOT NULL,
  `pTitle` varchar(100) NOT NULL,
  `pPrice` varchar(10) NOT NULL,
  `pDescription` text NOT NULL,
  `pCategory` varchar(50) NOT NULL,
  `pIcon` varchar(32) NOT NULL,
  PRIMARY KEY  (`pid`),
  UNIQUE KEY `pName` (`pName`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=9 ;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`pid`, `pName`, `pTitle`, `pPrice`, `pDescription`, `pCategory`, `pIcon`) VALUES
(1, 'ST1000DM003 ', 'HDD Seagate 1TB, 7200RPM, 64MB, SATA3', '150$', '', 'HDD', 'img202138-27122011162947-0_150x1'),
(2, 'WD5000AZRX', 'Compara cu alte produse HDD Western Digital 500GB, 5400rpm, 64MB, SATA 3', '99$', '', 'HDD', 'img217627-07032012144434-0_150x1'),
(3, 'WD3200AZDX ', 'Hard Disk-uri Compara cu alte produse HDD Western Digital Caviar GP 320GB, 5400RPM, 32MB, SATA3', '85$', '', 'HDD', 'img217627-07032012144434-0_150x1'),
(4, 'ST2000DL003', 'HDD Seagate Barracuda Green 2TB, 5900rpm, 64MB,', '180$', '', 'HDD', 'img161394-28012011170951_150x150'),
(5, 'WD1002FAEX', 'HDD Western Digital 1TB, 7200RPM, 64MB, SATA3 ', '180$', '', 'HDD', 'img133729-10032010161200_150x150'),
(6, 'ST1000DL002 ', 'HDD Seagate 500GB, 7200rpm, 16MB, SATA3 ', '105$', '', 'HDD', 'img188278-11082011092231-0_150x1'),
(7, 'WD20EURS', 'HDD Western Digital AV-GP 2TB, 5400RPM, 64MB, SATA2 ', '180$', '', 'HDD', 'img153426-11112010165556_150x150'),
(8, 'WD1600AAJB', 'HDD Western Digital 160GB, 7200RPM, IDE ', '100$', '', 'HDD', 'img43234-10032010163345_150x150c');

-- --------------------------------------------------------

--
-- Table structure for table `sales`
--

DROP TABLE IF EXISTS `sales`;
CREATE TABLE IF NOT EXISTS `sales` (
  `sid` int(11) NOT NULL auto_increment,
  `sProduct` varchar(32) NOT NULL,
  `sUser` varchar(32) NOT NULL,
  `sQty` int(10) NOT NULL,
  PRIMARY KEY  (`sid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `uid` int(100) NOT NULL auto_increment,
  `username` varchar(32) NOT NULL,
  `password` varchar(64) NOT NULL,
  `email` varchar(32) NOT NULL,
  `address` varchar(100) NOT NULL,
  `city` varchar(32) NOT NULL,
  `country` varchar(32) NOT NULL,
  `phone` int(16) NOT NULL,
  PRIMARY KEY  (`uid`),
  UNIQUE KEY `uid` (`uid`,`username`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=6 ;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`uid`, `username`, `password`, `email`, `address`, `city`, `country`, `phone`) VALUES
(1, 'djcharon', '9814120a24c5dc7a1e81e525f77a58cecd269c8ed68c96e13a3c199a6b9dc65e', '', '', '', '', 0),
(2, 'djcharon_admin', '', '', '', '', '', 0),
(3, 'djcharon_admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '', '', '', '', 0),
(4, 'etien', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'djcharon@yahoo.com', 'dsd', 'Braila', 'Romania', 745041373),
(5, '', '', '', '', '', '', 0);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
