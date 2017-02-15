-- INSERT DE LOS MODULOS DISPONIBLES
INSERT INTO T_MODULO_TERMINAL (P_ID, A_CLASE, a_NOMBRE) VALUES(1, 'main.acciones.VerDialogoOrdenarPiezasODTAction', 'Cosido');
INSERT INTO T_MODULO_TERMINAL (P_ID, A_CLASE, a_NOMBRE) VALUES(2, 'main.acciones.VerLectorODTAction', 'Asignar metros piezas ODT');
INSERT INTO T_MODULO_TERMINAL (P_ID, A_CLASE, a_NOMBRE) VALUES(3, 'main.acciones.VerEntregaReingresoDocumentosAction', 'Entrega/Reingreso documentos');
INSERT INTO T_MODULO_TERMINAL (P_ID, A_CLASE, a_NOMBRE) VALUES(4, 'main.acciones.VerDialogoEntregarMercaderiaRemitoSalidaAction', 'Control de Mercader�a');


-- INSERT DE LAS TERMINALES
INSERT INTO T_TERMINAL VALUES(1, 'TERMINAL COSIDO', 'por definir', 1);
INSERT INTO T_TERMINAL VALUES(2, 'TERMINAL ENTREGA', 'por definir', NULL);
INSERT INTO T_TERMINAL VALUES(3, 'TERMINAL ASIGNACION METROS', 'por definir', 2);
INSERT INTO T_TERMINAL VALUES(4, 'TERMINAL CONTROL DE SALIDA', 'por definir', 3);


-- INSERT DEL MODULO DE ASIGNACION DE TERMINALES
INSERT INTO T_MODULO VALUES (114,'ar.com.textillevel.gui.modulos.abm.GuiABMTerminal','Terminal - Administrar m�dulos de terminales',0,0);
