--
-- PostgreSQL database dump
--

\restrict na9UdpBFu03vGZLiWxj9ue5j99lNMROwuWjTixCF3gbUCdTHY3D35w6thojFeyT

-- Dumped from database version 18.4
-- Dumped by pg_dump version 18.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: modelos; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.modelos (id_modelo, estado_modelo, marca, nombre_modelo) FROM stdin;
1	t	Toyota	Corolla
\.


--
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.roles (id_rol, nombre_rol) FROM stdin;
1	ADMINISTRADOR
2	MECANICO
\.


--
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.usuarios (id_usuario, apellido, clave, email, estado_usuario, nombre, username, id_rol) FROM stdin;
1	General	$2a$10$Z3H9AY41Y5WSbEAJK4zZ/e3E0RS/FzuXp918/XHE/VVL84sg2/89i	admin@taller.com	t	Administrador	admin	1
2	Demo	$2a$10$dRKxPqQca.RyGxz4SavNqumTY/6vxpYNy5EWVH3vENcgFM6a8byna	mecanico@taller.com	t	Mecanico	mecanico	2
\.


--
-- Data for Name: vehiculos; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.vehiculos (id_vehiculo, color_placa, documento_dueno, estado_vehiculo, nombre_dueno, placa, telefono_dueno, id_modelo) FROM stdin;
1	AMARILLA	10203040	t	Juan Perez	ABC-123	3001234567	1
\.


--
-- Data for Name: reparaciones; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.reparaciones (id_reparacion, estado, fecha, observaciones, tipo, id_usuario_mecanico, id_vehiculo) FROM stdin;
1	TERMINADO	2026-11-02	Cambio de aceite y filtro	MANTENIMIENTO_PREVENTIVO	2	1
\.


--
-- Data for Name: repuestos; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.repuestos (id_repuesto, descripcion, ean, estado_repuesto, imagen_url, nombre, precio, stock) FROM stdin;
1	Filtro compatible	1234567890123	t	\N	Filtro de aceite	35000.00	15
\.


--
-- Data for Name: repuesto_modelo; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.repuesto_modelo (id_repuesto, id_modelo) FROM stdin;
1	1
\.


--
-- Name: modelos_id_modelo_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.modelos_id_modelo_seq', 1, true);


--
-- Name: reparaciones_id_reparacion_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.reparaciones_id_reparacion_seq', 1, true);


--
-- Name: repuestos_id_repuesto_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.repuestos_id_repuesto_seq', 1, true);


--
-- Name: roles_id_rol_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.roles_id_rol_seq', 2, true);


--
-- Name: usuarios_id_usuario_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.usuarios_id_usuario_seq', 2, true);


--
-- Name: vehiculos_id_vehiculo_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.vehiculos_id_vehiculo_seq', 1, true);


--
-- PostgreSQL database dump complete
--

\unrestrict na9UdpBFu03vGZLiWxj9ue5j99lNMROwuWjTixCF3gbUCdTHY3D35w6thojFeyT

