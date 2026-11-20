USE lila_joyeria;

ALTER TABLE pedidos
    ADD COLUMN nombre_contacto VARCHAR(100) NULL,
    ADD COLUMN direccion_entrega VARCHAR(255) NULL,
    ADD COLUMN telefono_whatsapp VARCHAR(30) NULL;

UPDATE pedidos p
JOIN usuarios u ON u.id_usuario = p.id_usuario
SET p.nombre_contacto = u.nombre,
    p.direccion_entrega = 'Dirección pendiente de actualizar',
    p.telefono_whatsapp = 'Pendiente de actualizar'
WHERE p.id_pedido > 0;

ALTER TABLE pedidos
    MODIFY COLUMN nombre_contacto VARCHAR(100) NOT NULL,
    MODIFY COLUMN direccion_entrega VARCHAR(255) NOT NULL,
    MODIFY COLUMN telefono_whatsapp VARCHAR(30) NOT NULL;
