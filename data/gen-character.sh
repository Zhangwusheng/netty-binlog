#!/usr/bin/env bash

#SELECT id, collation_name FROM information_schema.collations ORDER BY id;

cat ./character.list | awk '{printf("mapId2String[%s]=\"%s\";\n",$1,$2);}'