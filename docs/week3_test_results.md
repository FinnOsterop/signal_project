# Week 3 Test Results

## Test Command

```sh
mvn clean test jacoco:report
```

## Unit Test Result

The unit tests passed successfully.

```text
Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Coverage Result

The JaCoCo report was generated at:

```text
target/site/jacoco/index.html
```

Coverage summary:

```text
Overall instruction coverage: 38.9%
Overall line coverage: 39.3%
Overall branch coverage: 45.3%
com.alerts instruction coverage: 85.5%
com.alerts line coverage: 86.8%
com.data_management instruction coverage: 84.8%
com.data_management line coverage: 84.0%
```

The overall coverage is lower because the report also includes simulator and output classes from
other parts of the project. The main Week 3 classes are in `com.alerts` and `com.data_management`,
and those packages have higher coverage.

## Parts Not Fully Tested

The real-time simulator classes, WebSocket output, TCP output, and console/file output strategies
are not fully tested in this week. I focused the tests on the required Week 3 functionality:

- reading file output with `FileDataReader`
- storing and retrieving patient records
- filtering records by timestamp
- triggering blood pressure alerts
- triggering blood saturation alerts
- triggering combined hypotensive hypoxemia alerts
- triggering ECG peak alerts
- handling triggered alert button records
