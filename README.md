Babelnet API
============

Configureren
------------
Zorg dat het juiste pad staat ingesteld in `config/babelnet.var.properties`


Compileren
----------
`mvn compile`

Testen of de offline indices werken
------------------------------------
`mvn test`

Starten van de webserver
------------------------
`mvn exec:java -Dexec.mainClass="nl.celp.App"` of start `./run.sh`