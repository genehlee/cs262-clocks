# List of ports
PORTS=("8000" "8001" "8002")

INDICES=(0 1 2)

# create three processes of Client, each on a different port, passing in the port numbers of the other two clients
# for i in "${PORTS[@]}"
# do
#     if [ $i != ${PORTS[2]} ]; then
#         mvn exec:java -Dexec.mainClass="com.clocks.Client" -Dexec.args="$i ${PORTS[@]/$i}" &
#     else
#         mvn exec:java -Dexec.mainClass="com.clocks.Client" -Dexec.args="$i ${PORTS[@]/$i}"
#     fi
# done

for i in "${INDICES[@]}"
do
    # if [ $i != 2 ]; then
    #     mvn exec:java -Dexec.mainClass="com.clocks.Client" -Dexec.args="${PORTS[i]} ${PORTS[i]} ${PORTS[i]}" &
    # else
    #     mvn exec:java -Dexec.mainClass="com.clocks.Client" -Dexec.args="${PORTS[i]} ${PORTS[i]} ${PORTS[i]}"
    # fi
    if [$i == $2]; then
        echo "i is 2"
    fi
done

# Hard coded version
# mvn exec:java -Dexec.mainClass="com.clocks.Client" -Dexec.args="8000 8001 8002" &
# mvn exec:java -Dexec.mainClass="com.clocks.Client" -Dexec.args="8001 8000 8002" &
# mvn exec:java -Dexec.mainClass="com.clocks.Client" -Dexec.args="8002 8000 8001"
