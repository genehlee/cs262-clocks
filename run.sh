# List of ports
PORTS=("8000" "8001" "8002")

# Create three processes of Client, each on a unique port, passing in the port numbers of the other two clients
mvn exec:java -Dexec.mainClass="com.clocks.Client" -Dexec.args="${PORTS[0]} ${PORTS[1]} ${PORTS[2]}" &
mvn exec:java -Dexec.mainClass="com.clocks.Client" -Dexec.args="${PORTS[1]} ${PORTS[0]} ${PORTS[2]}" &
mvn exec:java -Dexec.mainClass="com.clocks.Client" -Dexec.args="${PORTS[2]} ${PORTS[0]} ${PORTS[1]}"