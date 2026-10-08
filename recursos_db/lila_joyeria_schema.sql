CREATE DATABASE IF NOT EXISTS lila_joyeria;
USE lila_joyeria;

CREATE TABLE categorias (
                            id_categoria INT AUTO_INCREMENT PRIMARY KEY,
                            nombre VARCHAR(50) NOT NULL
);

INSERT INTO categorias (nombre) VALUES ('Anillos'), ('Collares'), ('Pulseras'), ('Aretes');

CREATE TABLE usuarios (
                          id_usuario INT AUTO_INCREMENT PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          email VARCHAR(100) UNIQUE NOT NULL,
                          password VARCHAR(255) NOT NULL,
                          rol ENUM('ADMIN', 'CLIENTE') DEFAULT 'CLIENTE'
);

CREATE TABLE joyas (
                       id_joya INT AUTO_INCREMENT PRIMARY KEY,
                       nombre VARCHAR(100) NOT NULL,
                       descripcion TEXT,
                       material VARCHAR(50) NOT NULL,
                       quilates DECIMAL(4,2),
                       precio DECIMAL(10,2) NOT NULL,
                       stock INT NOT NULL DEFAULT 0,
                       imagen VARCHAR(150) DEFAULT 'sin-imagen.jpg',
                       id_categoria INT,
                       FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria)
);

INSERT INTO joyas
    (nombre, descripcion, material, quilates, precio, stock, id_categoria)
VALUES
    (
        'Anillo Elegante',
        'Anillo clásico para ocasiones especiales.',
        'Oro',
        18.00,
        120.00,
        8,
        (SELECT id_categoria FROM categorias WHERE nombre = 'Anillos' LIMIT 1)
    ),
    (
        'Collar Clásico',
        'Collar de diseño atemporal.',
        'Plata',
        NULL,
        85.00,
        12,
        (SELECT id_categoria FROM categorias WHERE nombre = 'Collares' LIMIT 1)
    ),
    (
        'Pulsera Moderna',
        'Pulsera ligera con acabado moderno.',
        'Plata',
        NULL,
        60.00,
        10,
        (SELECT id_categoria FROM categorias WHERE nombre = 'Pulseras' LIMIT 1)
    ),
    (
        'Aretes Brillantes',
        'Aretes ideales para complementar cualquier look.',
        'Oro',
        14.00,
        95.00,
        6,
        (SELECT id_categoria FROM categorias WHERE nombre = 'Aretes' LIMIT 1)
    );

CREATE TABLE pedidos (
                         id_pedido INT AUTO_INCREMENT PRIMARY KEY,
                         id_usuario INT NOT NULL,
                         fecha_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         total DECIMAL(10,2) NOT NULL,
                         estado ENUM('PENDIENTE', 'PAGADO', 'ENVIADO') DEFAULT 'PENDIENTE',
                         FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

CREATE TABLE detalles_pedido (
                                 id_detalle INT AUTO_INCREMENT PRIMARY KEY,
                                 id_pedido INT NOT NULL,
                                 id_joya INT NOT NULL,
                                 cantidad INT NOT NULL,
                                 precio_unitario DECIMAL(10,2) NOT NULL,
                                 FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido),
                                 FOREIGN KEY (id_joya) REFERENCES joyas(id_joya)
);
