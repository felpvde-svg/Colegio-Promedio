
CREATE TABLE `Promedios` (
  `idPromedio` int NOT NULL AUTO_INCREMENT,
  `idEstudiante` int NOT NULL,
  `idMateria` int NOT NULL,
  `nota1` double NOT NULL,
  `nota2` double NOT NULL,
  `nota3` double NOT NULL,
  `promedio` double NOT NULL,
  PRIMARY KEY (`idPromedio`),
  KEY `idEstudiante` (`idEstudiante`),
  KEY `fk_promedios_materias` (`idMateria`),
  CONSTRAINT `fk_promedios_materias` FOREIGN KEY (`idMateria`) REFERENCES `Materias` (`idMateria`) ON DELETE CASCADE,
  CONSTRAINT `Promedios_ibfk_1` FOREIGN KEY (`idEstudiante`) REFERENCES `Estudiantes` (`idEstudiante`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;