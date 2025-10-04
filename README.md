# Can Be Var

Simple IntelliJ plugin that generates a warning when an explicit type declaration can be changed to ***var***.

## Table of Contents
- [Requirements](#requirements)
- [Development](#development)
- [Installation](#installation)
- [Features](#features)
- [Contributing](#contributing)
- [License](#license)

## Requirements
- JDK 24   
- InteliJ Community Edition or Ultimate
- Gradle

## Development
* Clone the repository

* Run
```bash
./gradlew runIde 
```

* Build
```bash
./gradlew buildPlugin
```

## Installation
* Download the latest release.
* In InteliJ: Settings → Plugins → Install Plugin from Disk… → select zip.

## Features
- Generate warning when var can be used
- Provide quick fix allowing to replace the explicit type with var

## Contributing
If you find any bugs or have ideas for new features contributions are welcomed.

## License

[MIT](LICENSE)
