# use OpenJDK image with the latest Java version as the base image
FROM openjdk

# create a new directory for the server's docker files
RUN mkdir /server

# copy the server files from the host machine to the image filesystem
COPY out/artifacts/java_server_jar/java.server.jar /server/server.jar

# set the directory for excecuting future commands
WORKDIR /server

# expose port 3769
EXPOSE 3769

# run the jar file
CMD ["java", "-jar", "server.jar"]


# -------- COMMANDS TO USE THE DOCKER --------

# Command to build the image file:
# docker build -t idansm/todo-server:1.0 . --platform linux/amd64
# Note: remove the flag "--platform linux/amd64" in case of running on MacOS (if using Linux or Windows, keep the flag)

# Command to run the image file created:
# docker run --name todo-server -d -p 3769:9285 idansm/todo-server:1.0

# Flags used in the run command:
# -p flag: makes the external exposed port of the server to be 3769, and the internal port of the server (the port that the server actualy listens to, inside the docker container) to be 9285
# -d flag: runs the docker container in the backgroud, enables use of the terminal after running the docker container (can be removed to see the logs of the server that were writen to the screen)
# --name flag: the name of the docker container created from the run command
# "idansm/todo-server:1.0" is the image tag of the image created by the build command
