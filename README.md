# cs262-clocks
## Engineering Notebook
The engineering notebook can be found [here](https://docs.google.com/document/d/1YOrf8WIoCA7OLrn46ARJapOlARR92Ybnl2GgkMxo5LU/edit?usp=sharing)

## Getting started
> Prerequisite: Java gRPC is not supported on M1 Macs. You will need to use an Intel Mac or a virtual machine.
1. Install Maven, one of the Java build systems. On Mac, use homebrew: `brew install maven`.
> The `pom.xml` file in the project defines the Maven dependencies, which includes JUnit for unit/integration testing. Make sure Maven is installed by running `mvn -v`.
2. Clone this repository: `git clone`
3. Navigate to the root folder
4. Download the dependencies: `mvn clean install`
5. Compile the code: `mvn compile`

## Running the simulation
Run `./run.sh`. This will compile the code and run the simulation. The simulation launches three instances of `Client.java` on the three ports stored in the list variable in `run.sh`.

## Running the tests
Open VSCode, go to the testing tab, and run the tests from there.

## Generating the visualizations
1. Place the results of the simulation(s) into the proper directories under `experiments`
2. Run `python3 visualize.py` to generate the visualizations, which will be placed under `visualizations1` or `visualizations2`.