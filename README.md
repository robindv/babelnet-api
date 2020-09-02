# Babelnet API
============

To use this API, you must first create an account on the BabelNet website and download a local BabelNet index (we specifically use BabelNet v4.0). Please follow the instructions on the [BabelNet website](https://babelnet.org/download) to how to download the index.

## Dependencies
- Java 
- Apache Maven

## Configure 

We assume the index is extracted into directory `/path/to/babelnet-v4.0-index`. You must then modify `babelnet-api/config/babelnet.var.properties` and set the variable `babelnet.dir` to the directory where you stored the index (e.g. `/path/to/babelnet-v4.0-index`). You can optionally also use the api directly without the index with BabelNet their api calls, which however do have a limited amount of requests per day, by setting the `babelnet.key` variable to your security key.  

## Compile 
Then you need to compile the code. 
`mvn compile`

## Testing 
If you want to check whether your offline indices work, run the following:
`mvn test`

## Start the webserver 
To start using this api yourself, start the webserver so you can query BabelNet. 
`mvn exec:java -Dexec.mainClass="nl.celp.App"` or `./run.sh`


This will load the index and serve it in the local machine under `localhost:8080`. Now you can continue with the creation of VisualSem with the use of this local requestable. 

