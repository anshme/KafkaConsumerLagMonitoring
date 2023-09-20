# Lag Calculator

The Lag Calculator is a Java application that calculates the lag between a Kafka consumer group and its corresponding Kafka producer. Lag in this context represents the difference between the latest message produced in a Kafka topic and the latest message consumed by a consumer group. This tool can be useful for monitoring the progress and performance of your Kafka consumers.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Usage](#usage)
- [How It Works](#how-it-works)
- [Contributing](#contributing)
- [License](#license)

## Prerequisites

Before using the Lag Calculator, make sure you have the following prerequisites installed and configured:

- Java 8 or later
- Apache Kafka
- Kafka client libraries

## Usage

To use the Lag Calculator, follow these steps:

1. Clone or download this repository to your local machine.

2. Compile the code if necessary:

   ```shell
   javac -cp path/to/kafka/libs/* LagCalculator.java
   ```

3. Run the Lag Calculator with the desired consumer group and Kafka bootstrap server configuration:

   ```shell
   java -cp .:path/to/kafka/libs/* org.simplethoughts.LagCalculator
   ```

   Replace `path/to/kafka/libs/*` with the path to your Kafka client libraries.

4. The application will calculate and display the total lag for the specified consumer group and Kafka server. The result will be printed to the console.

## How It Works

The Lag Calculator performs the following steps to calculate the lag for a given consumer group:

1. **Initialize Kafka Consumer and Admin Client:** The application sets up a Kafka consumer and an Admin Client with the provided Kafka bootstrap server configuration.

2. **Fetch Consumer Group Offsets:** It retrieves the consumer group offsets for all topic partitions from the Kafka cluster using the Admin Client.

3. **Fetch Producer Offsets:** The Lag Calculator determines the latest produced offsets for each topic partition by querying Kafka.

4. **Calculate Lag:** The application computes the lag for each topic partition by subtracting the consumer group offset from the producer offset. The lag is stored in a map.

5. **Total Lag Calculation:** Finally, the total lag is calculated by summing up the individual partition lags.

6. **Display Results:** The total lag is displayed on the console.

## Contributing

If you'd like to contribute to the Lag Calculator project, please follow these guidelines:

1. Fork the repository on GitHub.

2. Create a new branch with a descriptive name for your feature or bug fix.

3. Make your changes and ensure they adhere to the code style and quality standards.

4. Write tests if necessary to validate your changes.

5. Submit a pull request to the main repository, explaining the purpose and details of your changes.

6. The maintainers will review your pull request, and upon approval, your changes will be merged into the main codebase.

## License

This project is licensed under the MIT License - see the [LICENSE](https://opensource.org/license/mit/) file for details.