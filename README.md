# TO-DO HTTP Server Application

## 1. About
This TO-DO app allows users to maintain a list of tasks to do. The app allows users to create, update and delete    
TO-DOs, with more functions listed below.  

#### The server will create two log files, in a dedicated logs folder:
* `requests.log`: In charge of logging each incoming request of any type to the server
* `todos.log`: In charge of logging information regarding the todo management  
<br />   

#### Options to use and test the TO-DO Server:
* You can download and import the following Postman files into Postman to see an example of some requests and responses from the server:
   * `Local-Server-Test Run.postman_collection.json` - for a local instance of the server that is running
   * `GCP-Deployed-Server-Test-Run.postman_collection.json` - to use the Google Cloud Platform deployed instance
<br />

* You can also use the `Dockerfile` to build and run a docker container with the todo-server
   > Notes and instructions on how to use the docker container are inside the Dockerfile
<br />
   
* Through GCP - enter the URL: `https://todo-server-2aj6ey6ugq-zf.a.run.app`, with the endpoints described below
<br />   
   
#### Each todo has the below properties:
* `Id:` a unique ID assigned for each TO-DO, Starting at 1
* `Title:` short title describing the essence of this TO-DO
* `Content:` the actual content/description describing what this TO-DO stands for
* `Due date:` a timestamp (in millisecs) denoting the target time for this TO-DO to be fulfilled
* `Status:` the status of the TO-DO as follows,
    * `PENDING` when it is created and before the due date
    * `LATE`    when it is not performed yet, and we are past the due date
    * `DONE`    when the TO-DO item processing is over
<br />

## 2. Frameworks Used
* `Java` - programming language
* `Spring-Boot` - web framework
* `Logback` - logging framework
* `SLF4J` - Simple Logging Facade for Java
* `Docker` - docker containers for ease of use
<br />

## 3. Server Behavior
### 3.0 Respose Format
endpoints will return a result in the Json format:
```yaml
{
  result: <result of operation>             (Template, depends on the context)
  errorMessage: <message in case of error>  (String)
}
```
<br />

### 3.1 Server Properties
The server is set to listen on port `9583`.  
This can be changed in the `application.properties` file, under the `server.port` property.
<br />   
<br />

### 3.2 Server Endpoints
#### 3.2.1 Get Health
This is a sanity endpoint used to check that the server is up and running.  
`Endpoint:` /todo/health  
`Method:` GET  

* The response code will be **200**
* The result will be the string **"OK"**.   
<br />

#### 3.2.2 Create New TO-DO:
Creates a new TO-DO item in the system.
`Endpoint:` /todo   
`Method:` POST   
`Body:` json object-   
```yaml
{
   title: <TO-DO title>                (String)
   content: <TO-DO description>        (String)
   dueDate: <timestamp in millisecs>   (long number)
}
```
The TO-DO is created with the status PENDING.   
When a new TO-DO is created, it is assigned by the server to the next id in turn.    

#### Upon processing the creation, the following will be checked:   
1. Is there already a TO-DO with this title (TO-DOs' titles are unique)
2. Is the dueDate in the future.   

#### If the operation can be invoked (all verification went OK): 
* The response code will be **200**
* The result will hold the newly assigned TO-DO number

#### If there is an error:   
*  The response will end with **409** (conflict)
*  The errorMessage will be set according to the error:
      * `TO-DO already exists:` "Error: TODO with the title [<TODO title>] already exists in the system"   
      * `due date is in the past:` “Error: Can’t create new TODO that its due date is in the past”
<br />  
 
#### 3.2.3 Get TO-DOs Count
Returns the total number of TO-DOs in the app, according to the given filter.   
`Endpoint:` /todo/size   
`Method:` GET   
`Query Parameter:` status, possible values- `ALL`, `PENDING`, `LATE`, `DONE`.   
   
* The response code will be **200**
* The result will hold the **number of TO-DOs** that have the given status   

If that status is not precisely the above four options (case sensitive) the result will be **400** (bad request).   
<br />   

#### 3.2.4 Get TO-DOs Data
Returns the content of the todos according to the given status.    
`Endpoint:` /todo/content   
`Method:` GET   
`Query Parameter:` status, possible values- `ALL`, `PENDING`, `LATE`, `DONE`   
`Query Parameter:` sortBy, optional, possible values- `ID`, `DUE_DATE`, `TITLE` (default value: `ID`).   
   
* The response will be a json array   
* The array will hold json objects that describe a single TO-DO  
* The array will be sorted according to the sortBy parameter, in an acending order
* If no TO-DOs are available the result is an empty array
   
Each Json object in the array holds:
```yaml
{
   id: Integer
   title: String
   content: String
   status: String
   dueDate: long (Timestamp in millisecs)
}
```

* The response code will be **200**
* The result will hold the json array as described above

In case status or sortBy are not **precisely** as the options mentioned above, case sensitive, the result is **400** (bad request).   
<br />

#### 3.2.5 Update TO-DO status
Updates a TO-DO's status property.   
`Endpoint:` /todo  
`Method:` PUT  
`Query Parameter:` id, The TO-DO ID  
`Query Parameter:` status, The status to update. possible values- `PENDING`, `LATE`, `DONE`  
   
#### If the TO-DO exists (according to the ID):
* The response code will be **200**   
* Its status will be updated
* The result is the name of the **OLD** state that this TO-DO was at (any option of PENDING, LATE, or DONE, case sensitive)

#### If no such TO-DO with that ID can be found
* The response code will be **404** (not found)
* The errorMessage will be: "Error: no such TODO with id <todo number>"
   
#### If the status is not exactly the above-mentioned options (case sensitive)
* The result and response code will be **400** (bad request)
<br />
  
#### 3.2.6 Delete TO-DO
Deletes a TO-DO object.   
`Endpoint:` /todo  
`Method:` DELETE  
`Query Parameter:` id, The TO-DO ID   
   
> Once deleted, its deleted id remains **empty**, so that the next TO-DO that will be created **will not** take this id  
   
#### If the operation can be invoked (the TO-DO exists):
* The response will end with **200**
* The result will hold the number of TO-DOs left in the app

#### If the operation cannot be invoked (TO-DO does not exist):
* The response will end with **404** (not found)
* The errorMessage will be: "Error: no such TODO with id <todo number>"
<br />   
   
## 4. Docker Containers
### 4.0 Pull The Docker Image From Docker-Hub
Use the docker image from Docker-Hub to simply use the server and test its capabilities without worrying about setting it up.   
      
#### Pull the image file from Docker-Hub:
* Docker-Hub link: **https://hub.docker.com/r/idansm/todo-server**
   
You can pull the docker image through Docker-Desktop by using the tag `idansm/todo-server:1.0`,   
or by using the following command in the terminal:
```
   docker pull idansm/todo-server:1.0
```   
> You need to install docker on your machine in order to run it properly
<br />   

### 4.1 Build and Run the Docker Container
#### 4.1.1 Build the image file:
```
   docker build -t todo-server:1.0 . --platform linux/amd64
```
**Flags used:**
* `-t` - used for the tag (name) of the docker container, where the number (1.0) refers to the version of the server (not required, but helps with version control)
* `--platform linux/amd64` - states the target platform of the docker container to be Linux or Windows, remove the flag to run it on MacOS
<br />
   
#### 4.1.2 Run the image file created:
```
   docker run --name todo-server -d -p 3769:9285 todo-server:1.0
```
**Flags used:**
* `-p` - makes the external exposed port of the server to be **3769**, and the internal port of the server (the port that the server actualy listens to, inside the docker container) to be **9285**
* `-d` - runs the docker container in the backgroud, enables use of the terminal after running the docker container (can be removed to see the logs of the server that were writen to the screen)
* `--name` - the name of the docker container created from the run command
* `todo-server:1.0` is the image tag of the image created by the build command
