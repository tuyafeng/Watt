# Watt

English|[中文](https://github.com/tuyafeng/Watt/blob/master/README_zh_CN.md)

Watt is an open source android component management application. It uses IFW(Intent Firewall) to disable or enable components, **requires root permission**. (It is not recommended that you root your phone unless you know what you are doing.)

### Features

1. Disable or enable components(receivers/services/activities) easily
2. Block a broadcast so that no app can receive it to wake itself
3. Block the bad Keep-Alive services of some apps without errors
4. No data collection
5. Efficient and simple interface

### IFW Introduction

The Intent Firewall is a component of the Android framework which allows for the enforcement of intents based on rules defined in XML files. It was introduced in Android version 4.4.2 (API 19), and still works in the latest version 11 (API 29). The Intent Firewall is only accessible via system applications and root users since configuring the firewall requires being able to write directly to the device's filesystem.

The following is a description of how the IFW works:

> Every intent started in the Android framework, including intents created by the operating system, go through the Intent Firewall. This means that the Intent Firewall has the power to allow or deny any intent. The Intent Firewall is also able to dynamically update its rule set as XML files are written to and deleted from the Intent Firewall directory which makes it a very flexible system to configure on the fly.

You can learn more details [here](https://carteryagemann.com/pages/android-intent-firewall.html).

### Thanks

- [google/dagger](https://github.com/google/dagger), *Apache License, Version 2.0*
- [jaredrummler/AndroidShell](https://github.com/jaredrummler/AndroidShell), *Apache License, Version 2.0*
- [promeG/TinyPinyin](https://github.com/promeG/TinyPinyin), *Apache License, Version 2.0*

### License

```
Copyright (C) 2020 Tu Yafeng

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```

