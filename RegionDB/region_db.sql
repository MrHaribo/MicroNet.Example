--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.1
-- Dumped by pg_dump version 9.5.1

-- Started on 2017-04-05 16:03:58

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 1 (class 3079 OID 12355)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2104 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

CREATE ROLE region_service LOGIN
  ENCRYPTED PASSWORD 'region1234'
  NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;

CREATE DATABASE region_db OWNER postgres;
  
\connect region_db

--
-- TOC entry 181 (class 1259 OID 16408)
-- Name: master_regions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE master_regions (
    region_id bigint NOT NULL,
    data json
);


ALTER TABLE master_regions OWNER TO postgres;

--
-- TOC entry 2096 (class 0 OID 16408)
-- Dependencies: 181
-- Data for Name: master_regions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY master_regions (region_id, data) FROM stdin;
0	{"id":{"types":[1],"values":[0]},"name":"Kashyyk","worldPosition":{"x":1.422,"y":5.22},"biomName":"Blue","seed":0.45844144,"threadLevel":40,"faction":"Neutral","pois":[]}
1	{"id":{"types":[1],"values":[1]},"name":"Mendes","worldPosition":{"x":1.058,"y":6.545},"biomName":"Blue","seed":0.6334152,"threadLevel":80,"faction":"Neutral","pois":[]}
2	{"id":{"types":[1],"values":[2]},"name":"Rigel","worldPosition":{"x":-0.714,"y":5.986},"biomName":"Blue","seed":0.75177354,"threadLevel":40,"faction":"Neutral","pois":[]}
3	{"id":{"types":[1],"values":[3]},"name":"Apollo","worldPosition":{"x":-2.181,"y":7.849},"biomName":"Blue","seed":0.9880761,"threadLevel":40,"faction":"Neutral","pois":[]}
4	{"id":{"types":[1],"values":[4]},"name":"Dantuine","worldPosition":{"x":-4.25,"y":7.84},"biomName":"Blue","seed":0.07203525,"threadLevel":40,"faction":"Neutral","pois":[]}
5	{"id":{"types":[1],"values":[5]},"name":"Prelmoid","worldPosition":{"x":-8.626,"y":7.393},"biomName":"Blue","seed":0.0031926632,"threadLevel":80,"faction":"Neutral","pois":[]}
6	{"id":{"types":[1],"values":[6]},"name":"Hismonia","worldPosition":{"x":-7.83,"y":8.2},"biomName":"Blue","seed":0.17834562,"threadLevel":40,"faction":"Neutral","pois":[]}
7	{"id":{"types":[1],"values":[7]},"name":"Kabal","worldPosition":{"x":-8.03,"y":6.11},"biomName":"Blue","seed":0.27968347,"threadLevel":40,"faction":"Neutral","pois":[]}
8	{"id":{"types":[1],"values":[8]},"name":"Timosero","worldPosition":{"x":-6.34,"y":4.45},"biomName":"Blue","seed":0.539098,"threadLevel":80,"faction":"Neutral","pois":[]}
9	{"id":{"types":[1],"values":[9]},"name":"Haital","worldPosition":{"x":-7.83,"y":2.66},"biomName":"Blue","seed":0.6175031,"threadLevel":40,"faction":"Neutral","pois":[]}
10	{"id":{"types":[1],"values":[10]},"name":"Flavour","worldPosition":{"x":-5.65,"y":-6.37},"biomName":"Green","seed":0.014817715,"threadLevel":50,"faction":"Neutral","pois":[]}
11	{"id":{"types":[1],"values":[11]},"name":"Salt","worldPosition":{"x":-7.11,"y":-3.65},"biomName":"Green","seed":0.077456474,"threadLevel":80,"faction":"Neutral","pois":[]}
12	{"id":{"types":[1],"values":[12]},"name":"Dimerion","worldPosition":{"x":-7.77,"y":-6.29},"biomName":"Green","seed":0.39890718,"threadLevel":50,"faction":"Neutral","pois":[]}
13	{"id":{"types":[1],"values":[13]},"name":"Heulion","worldPosition":{"x":-5.38,"y":-2.86},"biomName":"Green","seed":0.23177344,"threadLevel":50,"faction":"Neutral","pois":[]}
14	{"id":{"types":[1],"values":[14]},"name":"Ganja","worldPosition":{"x":-5.43,"y":-8.36},"biomName":"Green","seed":0.47859138,"threadLevel":50,"faction":"Neutral","pois":[]}
15	{"id":{"types":[1],"values":[15]},"name":"Memphis","worldPosition":{"x":-1.01,"y":-5.83},"biomName":"Purple","seed":0.32190615,"threadLevel":50,"faction":"Neutral","pois":[]}
16	{"id":{"types":[1],"values":[16]},"name":"Hexania","worldPosition":{"x":1.28,"y":-8.07},"biomName":"Purple","seed":0.38687402,"threadLevel":50,"faction":"Neutral","pois":[]}
17	{"id":{"types":[1],"values":[17]},"name":"Toph","worldPosition":{"x":1.55,"y":-6.75},"biomName":"Purple","seed":0.7056373,"threadLevel":80,"faction":"Neutral","pois":[]}
18	{"id":{"types":[1],"values":[18]},"name":"Teima","worldPosition":{"x":4.4,"y":-8.15},"biomName":"Purple","seed":0.53939927,"threadLevel":50,"faction":"Neutral","pois":[]}
19	{"id":{"types":[1],"values":[19]},"name":"Amarath","worldPosition":{"x":5.64,"y":-7.2},"biomName":"Purple","seed":0.789263,"threadLevel":80,"faction":"Neutral","pois":[]}
20	{"id":{"types":[1],"values":[20]},"name":"Keimora","worldPosition":{"x":7.68,"y":-8.08},"biomName":"Purple","seed":0.050593197,"threadLevel":50,"faction":"Neutral","pois":[]}
21	{"id":{"types":[1],"values":[21]},"name":"Sodom","worldPosition":{"x":6.58,"y":-3.11},"biomName":"Red","seed":0.14388382,"threadLevel":80,"faction":"Neutral","pois":[]}
22	{"id":{"types":[1],"values":[22]},"name":"Gomorrah","worldPosition":{"x":6.07,"y":-2.14},"biomName":"Red","seed":0.36938143,"threadLevel":80,"faction":"Neutral","pois":[]}
23	{"id":{"types":[1],"values":[23]},"name":"Springfield","worldPosition":{"x":8.25,"y":2.64},"biomName":"Red","seed":0.9245384,"threadLevel":80,"faction":"Neutral","pois":[]}
24	{"id":{"types":[1],"values":[24]},"name":"Takko","worldPosition":{"x":7.27,"y":4.64},"biomName":"Red","seed":0.9403297,"threadLevel":30,"faction":"Neutral","pois":[]}
25	{"id":{"types":[1],"values":[25]},"name":"Hakkonen","worldPosition":{"x":4.81,"y":-0.22},"biomName":"Red","seed":0.16230112,"threadLevel":30,"faction":"Neutral","pois":[]}
26	{"id":{"types":[1],"values":[26]},"name":"Lanzeloto","worldPosition":{"x":2.09,"y":-1.56},"biomName":"Red","seed":0.3336135,"threadLevel":30,"faction":"Neutral","pois":[]}
27	{"id":{"types":[1],"values":[27]},"name":"Salsa","worldPosition":{"x":7.51,"y":5.7},"biomName":"Red","seed":0.4814477,"threadLevel":30,"faction":"Neutral","pois":[]}
28	{"id":{"types":[1],"values":[28]},"name":"Biedermann","worldPosition":{"x":6.5,"y":8.15},"biomName":"Plain","seed":0.013850927,"threadLevel":0,"faction":"Rebel","pois":[]}
29	{"id":{"types":[1],"values":[29]},"name":"Oddland","worldPosition":{"x":4.21,"y":7.65},"biomName":"Plain","seed":0.049887598,"threadLevel":0,"faction":"Rebel","pois":[]}
30	{"id":{"types":[1],"values":[30]},"name":"Connec","worldPosition":{"x":1.83,"y":8.49},"biomName":"Plain","seed":0.44622833,"threadLevel":0,"faction":"Rebel","pois":[]}
31	{"id":{"types":[1],"values":[31]},"name":"Hylia","worldPosition":{"x":3.1,"y":4.84},"biomName":"Plain","seed":0.41776645,"threadLevel":0,"faction":"Rebel","pois":[]}
32	{"id":{"types":[1],"values":[32]},"name":"Fuelia","worldPosition":{"x":4.9,"y":5.28},"biomName":"Plain","seed":0.4691683,"threadLevel":0,"faction":"Rebel","pois":[]}
33	{"id":{"types":[1],"values":[33]},"name":"Central","worldPosition":{"x":-1.88,"y":1.8},"biomName":"Plain","seed":0.89407396,"threadLevel":0,"faction":"Confederate","pois":[]}
34	{"id":{"types":[1],"values":[34]},"name":"Crossroads","worldPosition":{"x":4.25,"y":3.44},"biomName":"Plain","seed":0.16732198,"threadLevel":0,"faction":"Rebel","pois":[]}
35	{"id":{"types":[1],"values":[35]},"name":"Narrow","worldPosition":{"x":6.0,"y":1.79},"biomName":"Plain","seed":0.95048165,"threadLevel":0,"faction":"Neutral","pois":[]}
36	{"id":{"types":[1],"values":[36]},"name":"Edge","worldPosition":{"x":7.65,"y":-0.21},"biomName":"Plain","seed":0.9243489,"threadLevel":0,"faction":"Neutral","pois":[]}
37	{"id":{"types":[1],"values":[37]},"name":"Tango","worldPosition":{"x":0.65,"y":2.88},"biomName":"Plain","seed":0.69915926,"threadLevel":0,"faction":"Confederate","pois":[]}
38	{"id":{"types":[1],"values":[38]},"name":"Dante","worldPosition":{"x":1.72,"y":1.19},"biomName":"Plain","seed":0.6572602,"threadLevel":0,"faction":"Confederate","pois":[]}
39	{"id":{"types":[1],"values":[39]},"name":"Saulon","worldPosition":{"x":-3.22,"y":5.47},"biomName":"Plain","seed":0.672292,"threadLevel":0,"faction":"Confederate","pois":[]}
40	{"id":{"types":[1],"values":[40]},"name":"Jaul","worldPosition":{"x":-5.72,"y":6.53},"biomName":"Plain","seed":0.48410094,"threadLevel":0,"faction":"Neutral","pois":[]}
41	{"id":{"types":[1],"values":[41]},"name":"Teardown","worldPosition":{"x":-5.19,"y":1.02},"biomName":"Plain","seed":0.47646874,"threadLevel":0,"faction":"Confederate","pois":[]}
42	{"id":{"types":[1],"values":[42]},"name":"Border","worldPosition":{"x":-7.25,"y":-1.14},"biomName":"Plain","seed":0.928969,"threadLevel":0,"faction":"Neutral","pois":[]}
43	{"id":{"types":[1],"values":[43]},"name":"Hammer","worldPosition":{"x":-2.85,"y":2.82},"biomName":"Plain","seed":0.6915619,"threadLevel":0,"faction":"Confederate","pois":[]}
44	{"id":{"types":[1],"values":[44]},"name":"Twink","worldPosition":{"x":-1.25,"y":-0.08},"biomName":"Plain","seed":0.124712646,"threadLevel":0,"faction":"Confederate","pois":[]}
45	{"id":{"types":[1],"values":[45]},"name":"Gazzlow","worldPosition":{"x":-3.45,"y":-1.57},"biomName":"Plain","seed":0.7692348,"threadLevel":0,"faction":"Confederate","pois":[]}
46	{"id":{"types":[1],"values":[46]},"name":"Himalaya","worldPosition":{"x":-7.66,"y":-6.9},"biomName":"Plain","seed":0.46816975,"threadLevel":80,"faction":"Neutral","pois":[]}
47	{"id":{"types":[1],"values":[47]},"name":"Worldsend","worldPosition":{"x":-1.96,"y":-8.17},"biomName":"Plain","seed":0.74604166,"threadLevel":0,"faction":"Neutral","pois":[]}
48	{"id":{"types":[1],"values":[48]},"name":"Chronius","worldPosition":{"x":-3.55,"y":-5.46},"biomName":"Plain","seed":0.23598689,"threadLevel":0,"faction":"Neutral","pois":[]}
49	{"id":{"types":[1],"values":[49]},"name":"Sandwich","worldPosition":{"x":-0.13,"y":-3.69},"biomName":"Plain","seed":0.41342968,"threadLevel":0,"faction":"Neutral","pois":[]}
50	{"id":{"types":[1],"values":[50]},"name":"Ying","worldPosition":{"x":3.5,"y":-3.56},"biomName":"Plain","seed":0.49703407,"threadLevel":0,"faction":"Neutral","pois":[]}
51	{"id":{"types":[1],"values":[51]},"name":"Yang","worldPosition":{"x":3.29,"y":-5.29},"biomName":"Plain","seed":0.89058983,"threadLevel":0,"faction":"Neutral","pois":[]}
52	{"id":{"types":[1],"values":[52]},"name":"Cornaria","worldPosition":{"x":5.49,"y":-5.36},"biomName":"Plain","seed":0.5461825,"threadLevel":0,"faction":"Neutral","pois":[]}
53	{"id":{"types":[1],"values":[53]},"name":"Yavin","worldPosition":{"x":7.76,"y":-5.72},"biomName":"Plain","seed":0.9232553,"threadLevel":0,"faction":"Neutral","pois":[]}
\.


--
-- TOC entry 1981 (class 2606 OID 24639)
-- Name: region_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY master_regions
    ADD CONSTRAINT region_pk PRIMARY KEY (region_id);


--
-- TOC entry 2103 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- TOC entry 2105 (class 0 OID 0)
-- Dependencies: 181
-- Name: master_regions; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE master_regions FROM PUBLIC;
REVOKE ALL ON TABLE master_regions FROM postgres;
GRANT ALL ON TABLE master_regions TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE master_regions TO region_service;


-- Completed on 2017-04-05 16:03:59

--
-- PostgreSQL database dump complete
--

