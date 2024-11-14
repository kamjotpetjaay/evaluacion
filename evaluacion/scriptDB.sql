CREATE TABLE `empleado` (
  `id_empleado` varchar(8) NOT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `apellido1` varchar(255) DEFAULT NULL,
  `apellido2` varchar(255) DEFAULT NULL,
  `sexo` varchar(255) DEFAULT NULL,
  `f_nacimiento` datetime(6) DEFAULT NULL,
  `tel_celular` varchar(255) DEFAULT NULL,
  `id_puesto` int DEFAULT NULL,
  PRIMARY KEY (`id_empleado`),
  KEY `id_puesto` (`id_puesto`),
  CONSTRAINT `empleado_ibfk_1` FOREIGN KEY (`id_puesto`) REFERENCES `salario` (`id_puesto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `salario` (
  `id_puesto` int NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `salario` decimal(38,2) DEFAULT NULL,
  PRIMARY KEY (`id_puesto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `seguridad` (
  `id_usuario` varchar(10) NOT NULL,
  `password` varchar(12) NOT NULL,
  `activo` char(1) DEFAULT NULL,
  `id_empleado` varchar(8) DEFAULT NULL,
  `usuario` varchar(15) NOT NULL,
  PRIMARY KEY (`id_usuario`),
  KEY `id_empleado` (`id_empleado`),
  CONSTRAINT `seguridad_ibfk_1` FOREIGN KEY (`id_empleado`) REFERENCES `empleado` (`id_empleado`),
  CONSTRAINT `seguridad_chk_1` CHECK ((`activo` in (_utf8mb4'S',_utf8mb4'N')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO Salario (id_puesto , descripcion , salario) VALUES
(1, 'Gerente', 5000.00),
(2, 'Subgerente', 4000.00),
(3, 'Analista', 3000.00),
(4, 'Desarrollador', 3500.00),
(5, 'Soporte', 2500.00);

INSERT INTO Empleado (id_empleado , nombre , apellido1 , apellido2 , sexo , f_nacimiento , tel_celular , id_puesto) VALUES
('E001', 'Juan', 'Perez', 'Lopez', 'M', '1990-05-10', 5551234567, 1),
('E002', 'Ana', 'Gomez', 'Martinez', 'F', '1985-07-20', 5552345678, 2),
('E003', 'Luis', 'Ramirez', NULL, 'M', '1992-10-15', 5553456789, 3),
('E004', 'Maria', 'Diaz', 'Santos', 'F', '1993-01-22', 5554567890, 4),
('E005', 'Carlos', 'Fernandez', 'Ruiz', 'M', '1988-03-30', 5555678901, 5);

INSERT INTO Seguridad (id_usuario , password , activo , id_empleado, usuario) VALUES
('ID1', 'pass1234', 'S', 'E001','user1'),
('ID2', 'pass5678', 'S', 'E002','user2'),
('ID3', 'pass9101', 'N', 'E003','user3'),
('ID4', 'pass1122', 'S', 'E004','user4'),
('ID5', 'pass3344', 'N', 'E005','user4');